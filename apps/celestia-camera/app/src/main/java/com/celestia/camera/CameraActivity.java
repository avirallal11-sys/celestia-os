package com.celestia.camera;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.graphics.ImageFormat;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CameraActivity extends AppCompatActivity {
    private static final String TAG = "CelestiaCamera";
    private static final int REQUEST_CAMERA_PERMISSION = 200;

    private TextureView textureView;
    private ImageButton btnCapture, btnSwitch, btnFlash, btnTimer, btnGrid, btnGallery;
    private TextView txtMode, txtTimer, txtCountdown, txtRecordingTime;
    private TextView zoom1x, zoom2x, zoom5x;
    private ImageView galleryThumbnail;
    private View gridOverlay;

    private CameraDevice cameraDevice;
    private CameraCaptureSession captureSession;
    private CaptureRequest.Builder captureRequestBuilder;
    private CameraManager cameraManager;
    private String cameraId;
    private Size imageDimension;
    private ImageReader imageReader;
    private HandlerThread backgroundThread;
    private Handler backgroundHandler;
    private File photoFile;
    private SharedPreferences prefs;

    private boolean isFrontCamera = false;
    private boolean isFlashOn = false;
    private boolean isVideoMode = false;
    private boolean isGridVisible = false;
    private int timerSeconds = 0;
    private float currentZoom = 1.0f;
    private MediaRecorder mediaRecorder;
    private boolean isRecording = false;
    private File videoFile;
    private CountDownTimer recordingTimer;
    private long recordingStartTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );

        setContentView(R.layout.activity_camera);

        prefs = getSharedPreferences("celestia_camera", MODE_PRIVATE);

        textureView = findViewById(R.id.texture_view);
        btnCapture = findViewById(R.id.btn_capture);
        btnSwitch = findViewById(R.id.btn_switch);
        btnFlash = findViewById(R.id.btn_flash);
        btnTimer = findViewById(R.id.btn_timer);
        btnGrid = findViewById(R.id.btn_grid);
        btnGallery = findViewById(R.id.btn_gallery);
        txtMode = findViewById(R.id.txt_mode);
        txtTimer = findViewById(R.id.txt_timer);
        txtCountdown = findViewById(R.id.txt_countdown);
        txtRecordingTime = findViewById(R.id.txt_recording_time);
        zoom1x = findViewById(R.id.zoom_1x);
        zoom2x = findViewById(R.id.zoom_2x);
        zoom5x = findViewById(R.id.zoom_5x);
        galleryThumbnail = findViewById(R.id.btn_gallery);
        gridOverlay = findViewById(R.id.grid_overlay);

        cameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);

        btnCapture.setOnClickListener(v -> {
            if (isVideoMode) {
                toggleRecording();
            } else {
                if (timerSeconds > 0) {
                    startTimerCountdown();
                } else {
                    takePicture();
                }
            }
        });

        btnSwitch.setOnClickListener(v -> switchCamera());
        btnFlash.setOnClickListener(v -> toggleFlash());
        btnTimer.setOnClickListener(v -> cycleTimer());
        btnGrid.setOnClickListener(v -> toggleGrid());
        btnGallery.setOnClickListener(v -> openGallery());

        zoom1x.setOnClickListener(v -> setZoom(1.0f));
        zoom2x.setOnClickListener(v -> setZoom(2.0f));
        zoom5x.setOnClickListener(v -> setZoom(5.0f));

        btnMode.setOnClickListener(v -> {
            isVideoMode = !isVideoMode;
            txtMode.setText(isVideoMode ? "VIDEO" : "PHOTO");
            btnCapture.setBackgroundResource(isVideoMode ?
                R.drawable.btn_record : R.drawable.btn_capture);
        });

        loadLastPhoto();

        textureView.setSurfaceTextureListener(surfaceTextureListener);

        if (checkPermissions()) {
            openCamera();
        } else {
            requestPermissions();
        }
    }

    private final TextureView.SurfaceTextureListener surfaceTextureListener =
        new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {
                openCamera();
            }

            @Override
            public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {}

            @Override
            public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
                return true;
            }

            @Override
            public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {}
        };

    private void openCamera() {
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            cameraId = isFrontCamera ? getFrontCameraId() : getBackCameraId();

            CameraCharacteristics characteristics =
                cameraManager.getCameraCharacteristics(cameraId);
            StreamConfigurationMap map =
                characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

            if (map != null) {
                imageDimension = map.getOutputSizes(SurfaceTexture.class)[0];
            }

            cameraManager.openCamera(cameraId, stateCallback, backgroundHandler);

        } catch (CameraAccessException e) {
            Log.e(TAG, "Camera access error", e);
        }
    }

    private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            cameraDevice = camera;
            createCameraPreview();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            cameraDevice.close();
            cameraDevice = null;
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            cameraDevice.close();
            cameraDevice = null;
            Toast.makeText(CameraActivity.this,
                "Camera error: " + error, Toast.LENGTH_SHORT).show();
        }
    };

    private void createCameraPreview() {
        try {
            if (cameraDevice == null || !textureView.isAvailable()) return;

            SurfaceTexture texture = textureView.getSurfaceTexture();
            texture.setDefaultBufferSize(imageDimension.getWidth(), imageDimension.getHeight());
            Surface surface = new Surface(texture);

            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(surface);

            cameraDevice.createCaptureSession(
                Collections.singletonList(surface),
                new CameraCaptureSession.StateCallback() {
                    @Override
                    public void onConfigured(@NonNull CameraCaptureSession session) {
                        if (cameraDevice == null) return;
                        captureSession = session;
                        updatePreview();
                    }

                    @Override
                    public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                        Toast.makeText(CameraActivity.this,
                            "Camera config failed", Toast.LENGTH_SHORT).show();
                    }
                }, backgroundHandler
            );

        } catch (CameraAccessException e) {
            Log.e(TAG, "Create preview error", e);
        }
    }

    private void updatePreview() {
        if (cameraDevice == null) return;

        try {
            captureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);

            if (isFlashOn) {
                captureRequestBuilder.set(CaptureRequest.FLASH_MODE,
                    CaptureRequest.FLASH_MODE_TORCH);
            }

            captureSession.setRepeatingRequest(
                captureRequestBuilder.build(), null, backgroundHandler);
        } catch (CameraAccessException e) {
            Log.e(TAG, "Update preview error", e);
        }
    }

    private void takePicture() {
        if (cameraDevice == null) return;

        try {
            CameraCharacteristics characteristics =
                cameraManager.getCameraCharacteristics(cameraId);
            StreamConfigurationMap map =
                characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

            if (map == null) return;

            ImageReader reader = ImageReader.newInstance(
                map.getOutputSizes(ImageFormat.JPEG)[0].getWidth(),
                map.getOutputSizes(ImageFormat.JPEG)[0].getHeight(),
                ImageFormat.JPEG, 1);

            List<Surface> outputSurfaces = new ArrayList<>();
            outputSurfaces.add(reader.getSurface());

            final CaptureRequest.Builder captureBuilder =
                cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(reader.getSurface());
            captureBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);

            if (isFlashOn) {
                captureBuilder.set(CaptureRequest.FLASH_MODE,
                    CaptureRequest.FLASH_MODE_SINGLE);
            }

            photoFile = getPhotoFile();

            ImageReader.OnImageAvailableListener readerListener = reader1 -> {
                Image image = null;
                try {
                    image = reader1.acquireLatestImage();
                    if (image != null) {
                        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                        byte[] bytes = new byte[buffer.capacity()];
                        buffer.get(bytes);
                        saveImage(bytes, photoFile);
                    }
                } finally {
                    if (image != null) image.close();
                }
            };

            reader.setOnImageAvailableListener(readerListener, backgroundHandler);

            cameraDevice.createCaptureSession(outputSurfaces,
                new CameraCaptureSession.StateCallback() {
                    @Override
                    public void onConfigured(@NonNull CameraCaptureSession session) {
                        try {
                            session.capture(captureBuilder.build(), null, backgroundHandler);
                        } catch (CameraAccessException e) {
                            Log.e(TAG, "Capture error", e);
                        }
                    }

                    @Override
                    public void onConfigureFailed(@NonNull CameraCaptureSession session) {}
                }, backgroundHandler);

        } catch (CameraAccessException e) {
            Log.e(TAG, "Take picture error", e);
        }
    }

    private void saveImage(byte[] bytes, File file) {
        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(bytes);
            outputStream.close();

            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            runOnUiThread(() -> {
                Toast.makeText(CameraActivity.this,
                    "Photo saved: " + file.getName(), Toast.LENGTH_SHORT).show();
                loadLastPhoto();
            });

        } catch (IOException e) {
            Log.e(TAG, "Save image error", e);
        }
    }

    private File getPhotoFile() {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return new File(storageDir, "CELESTIA_" + timestamp + ".jpg");
    }

    private void switchCamera() {
        isFrontCamera = !isFrontCamera;
        if (cameraDevice != null) {
            cameraDevice.close();
            cameraDevice = null;
        }
        openCamera();
    }

    private void toggleFlash() {
        isFlashOn = !isFlashOn;
        btnFlash.setImageResource(isFlashOn ?
            R.drawable.ic_flash_on : R.drawable.ic_flash_off);
        updatePreview();
    }

    private void cycleTimer() {
        if (timerSeconds == 0) {
            timerSeconds = 3;
            Toast.makeText(this, "Timer: 3s", Toast.LENGTH_SHORT).show();
        } else if (timerSeconds == 3) {
            timerSeconds = 5;
            Toast.makeText(this, "Timer: 5s", Toast.LENGTH_SHORT).show();
        } else if (timerSeconds == 5) {
            timerSeconds = 10;
            Toast.makeText(this, "Timer: 10s", Toast.LENGTH_SHORT).show();
        } else {
            timerSeconds = 0;
            Toast.makeText(this, "Timer: Off", Toast.LENGTH_SHORT).show();
        }
        txtTimer.setText(timerSeconds > 0 ? timerSeconds + "s" : "");
    }

    private void startTimerCountdown() {
        txtCountdown.setVisibility(View.VISIBLE);
        new CountDownTimer(timerSeconds * 1000L, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                txtCountdown.setText(String.valueOf(millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                txtCountdown.setVisibility(View.GONE);
                takePicture();
            }
        }.start();
    }

    private void toggleGrid() {
        isGridVisible = !isGridVisible;
        gridOverlay.setVisibility(isGridVisible ? View.VISIBLE : View.GONE);
        Toast.makeText(this, isGridVisible ? "Grid: On" : "Grid: Off", Toast.LENGTH_SHORT).show();
    }

    private void setZoom(float zoom) {
        currentZoom = zoom;
        zoom1x.setTextColor(zoom == 1.0f ? 0xFFFFFFFF : 0x80FFFFFF);
        zoom2x.setTextColor(zoom == 2.0f ? 0xFFFFFFFF : 0x80FFFFFF);
        zoom5x.setTextColor(zoom == 5.0f ? 0xFFFFFFFF : 0x80FFFFFF);
        Toast.makeText(this, "Zoom: " + zoom + "x", Toast.LENGTH_SHORT).show();
    }

    private void toggleRecording() {
        if (isRecording) {
            stopRecording();
        } else {
            startRecording();
        }
    }

    private void startRecording() {
        try {
            if (cameraDevice == null) return;

            videoFile = getVideoFile();

            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mediaRecorder.setOutputFile(videoFile.getAbsolutePath());
            mediaRecorder.setVideoEncodingBitRate(10000000);
            mediaRecorder.setVideoFrameRate(30);
            mediaRecorder.setVideoSize(imageDimension.getWidth(), imageDimension.getHeight());
            mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

            mediaRecorder.prepare();

            Surface recordingSurface = mediaRecorder.getSurface();
            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);
            captureRequestBuilder.addTarget(recordingSurface);

            cameraDevice.createCaptureSession(
                Collections.singletonList(recordingSurface),
                new CameraCaptureSession.StateCallback() {
                    @Override
                    public void onConfigured(@NonNull CameraCaptureSession session) {
                        try {
                            captureSession = session;
                            captureSession.setRepeatingRequest(
                                captureRequestBuilder.build(), null, backgroundHandler);
                            mediaRecorder.start();
                            isRecording = true;
                            recordingStartTime = System.currentTimeMillis();

                            runOnUiThread(() -> {
                                btnCapture.setBackgroundResource(R.drawable.btn_recording_active);
                                txtRecordingTime.setVisibility(View.VISIBLE);
                                startRecordingTimer();
                                Toast.makeText(CameraActivity.this,
                                    "Recording started", Toast.LENGTH_SHORT).show();
                            });
                        } catch (CameraAccessException e) {
                            Log.e(TAG, "Recording start error", e);
                        }
                    }

                    @Override
                    public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                        Toast.makeText(CameraActivity.this,
                            "Recording setup failed", Toast.LENGTH_SHORT).show();
                    }
                }, backgroundHandler
            );

        } catch (Exception e) {
            Log.e(TAG, "Start recording error", e);
            Toast.makeText(this, "Cannot start recording", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopRecording() {
        try {
            if (mediaRecorder != null && isRecording) {
                mediaRecorder.stop();
                mediaRecorder.release();
                mediaRecorder = null;

                ContentValues values = new ContentValues();
                values.put(MediaStore.Video.Media.DATA, videoFile.getAbsolutePath());
                values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
                getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);

                isRecording = false;

                if (recordingTimer != null) {
                    recordingTimer.cancel();
                }

                runOnUiThread(() -> {
                    btnCapture.setBackgroundResource(R.drawable.btn_record);
                    txtRecordingTime.setVisibility(View.GONE);
                    Toast.makeText(CameraActivity.this,
                        "Video saved: " + videoFile.getName(), Toast.LENGTH_SHORT).show();
                    createCameraPreview();
                });
            }
        } catch (Exception e) {
            Log.e(TAG, "Stop recording error", e);
            Toast.makeText(this, "Error saving video", Toast.LENGTH_SHORT).show();
        }
    }

    private void startRecordingTimer() {
        recordingTimer = new CountDownTimer(Long.MAX_VALUE, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long elapsed = System.currentTimeMillis() - recordingStartTime;
                int seconds = (int) (elapsed / 1000);
                int mins = seconds / 60;
                int secs = seconds % 60;
                txtRecordingTime.setText(String.format(Locale.US,
                    "REC %02d:%02d", mins, secs));
            }

            @Override
            public void onFinish() {}
        }.start();
    }

    private File getVideoFile() {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_MOVIES);
        return new File(storageDir, "CELESTIA_" + timestamp + ".mp4");
    }

    private void openGallery() {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setType("image/*");
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "No gallery app found", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadLastPhoto() {
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (storageDir != null) {
            File[] files = storageDir.listFiles();
            if (files != null && files.length > 0) {
                galleryThumbnail.setImageURI(Uri.fromFile(files[files.length - 1]));
            }
        }
    }

    private String getBackCameraId() throws CameraAccessException {
        for (String id : cameraManager.getCameraIdList()) {
            CameraCharacteristics chars = cameraManager.getCameraCharacteristics(id);
            Integer facing = chars.get(CameraCharacteristics.LENS_FACING);
            if (facing != null && facing == CameraCharacteristics.LENS_FACING_BACK) {
                return id;
            }
        }
        return cameraManager.getCameraIdList()[0];
    }

    private String getFrontCameraId() throws CameraAccessException {
        for (String id : cameraManager.getCameraIdList()) {
            CameraCharacteristics chars = cameraManager.getCameraCharacteristics(id);
            Integer facing = chars.get(CameraCharacteristics.LENS_FACING);
            if (facing != null && facing == CameraCharacteristics.LENS_FACING_FRONT) {
                return id;
            }
        }
        return cameraManager.getCameraIdList()[0];
    }

    private boolean checkPermissions() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this,
            new String[]{Manifest.permission.CAMERA,
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE},
            REQUEST_CAMERA_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Camera permission required", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startBackgroundThread();
        if (textureView.isAvailable()) {
            openCamera();
        } else {
            textureView.setSurfaceTextureListener(surfaceTextureListener);
        }
    }

    @Override
    protected void onPause() {
        stopBackgroundThread();
        super.onPause();
    }

    private void startBackgroundThread() {
        backgroundThread = new HandlerThread("Camera Background");
        backgroundThread.start();
        backgroundHandler = new Handler(backgroundThread.getLooper());
    }

    private void stopBackgroundThread() {
        if (backgroundThread != null) {
            backgroundThread.quitSafely();
            try {
                backgroundThread.join();
                backgroundThread = null;
                backgroundHandler = null;
            } catch (InterruptedException e) {
                Log.e(TAG, "Stop background thread error", e);
            }
        }
    }
}
