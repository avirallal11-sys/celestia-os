package com.celestia.camera;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.provider.MediaStore;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
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
    private static final int REQUEST_STORAGE_PERMISSION = 201;

    private TextureView textureView;
    private ImageButton btnCapture, btnSwitch, btnFlash, btnMode;
    private TextView txtMode, txtTimer;

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

    private boolean isFrontCamera = false;
    private boolean isFlashOn = false;
    private boolean isVideoMode = false;
    private MediaRecorder mediaRecorder;
    private boolean isRecording = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Fullscreen immersive mode
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

        textureView = findViewById(R.id.texture_view);
        btnCapture = findViewById(R.id.btn_capture);
        btnSwitch = findViewById(R.id.btn_switch);
        btnFlash = findViewById(R.id.btn_flash);
        btnMode = findViewById(R.id.btn_mode);
        txtMode = findViewById(R.id.txt_mode);
        txtTimer = findViewById(R.id.txt_timer);

        cameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);

        btnCapture.setOnClickListener(v -> {
            if (isVideoMode) {
                toggleRecording();
            } else {
                takePicture();
            }
        });

        btnSwitch.setOnClickListener(v -> switchCamera());
        btnFlash.setOnClickListener(v -> toggleFlash());

        btnMode.setOnClickListener(v -> {
            isVideoMode = !isVideoMode;
            txtMode.setText(isVideoMode ? "VIDEO" : "PHOTO");
            btnCapture.setBackgroundResource(isVideoMode ?
                R.drawable.btn_record : R.drawable.btn_capture);
        });

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

            cameraId = isFrontCamera ?
                getFrontCameraId() : getBackCameraId();

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

            // Add to gallery
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            runOnUiThread(() -> {
                Toast.makeText(CameraActivity.this,
                    "Photo saved: " + file.getName(), Toast.LENGTH_SHORT).show();
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

    private void toggleRecording() {
        if (isRecording) {
            stopRecording();
        } else {
            startRecording();
        }
    }

    private void startRecording() {
        // Video recording implementation
        isRecording = true;
        btnCapture.setBackgroundResource(R.drawable.btn_recording_active);
        Toast.makeText(this, "Recording started", Toast.LENGTH_SHORT).show();
    }

    private void stopRecording() {
        isRecording = false;
        btnCapture.setBackgroundResource(R.drawable.btn_record);
        Toast.makeText(this, "Recording stopped", Toast.LENGTH_SHORT).show();
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
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
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
