package com.celestia.settings;

import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class SoundActivity extends AppCompatActivity {

    private SeekBar mediaVolume, ringtoneVolume, notificationVolume, alarmVolume, systemVolume;
    private Switch dndSwitch, vibrationSwitch, touchSoundSwitch;
    private TextView ringtoneName;
    private AudioManager audioManager;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_sound);

        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        prefs = getSharedPreferences("celestia_sound", MODE_PRIVATE);

        initViews();
        loadState();
        setupListeners();
    }

    private void initViews() {
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        mediaVolume = findViewById(R.id.mediaVolume);
        ringtoneVolume = findViewById(R.id.ringtoneVolume);
        notificationVolume = findViewById(R.id.notificationVolume);
        alarmVolume = findViewById(R.id.alarmVolume);
        systemVolume = findViewById(R.id.systemVolume);
        dndSwitch = findViewById(R.id.dndSwitch);
        vibrationSwitch = findViewById(R.id.vibrationSwitch);
        touchSoundSwitch = findViewById(R.id.touchSoundSwitch);
        ringtoneName = findViewById(R.id.ringtoneName);
    }

    private void loadState() {
        mediaVolume.setProgress(prefs.getInt("vol_media", 70));
        ringtoneVolume.setProgress(prefs.getInt("vol_ringtone", 80));
        notificationVolume.setProgress(prefs.getInt("vol_notification", 60));
        alarmVolume.setProgress(prefs.getInt("vol_alarm", 90));
        systemVolume.setProgress(prefs.getInt("vol_system", 50));
        dndSwitch.setChecked(prefs.getBoolean("dnd", false));
        vibrationSwitch.setChecked(prefs.getBoolean("vibration", true));
        touchSoundSwitch.setChecked(prefs.getBoolean("touch_sound", false));
        ringtoneName.setText(prefs.getString("ringtone", "Andromeda"));
    }

    private void setupListeners() {
        setupVolumeSlider(mediaVolume, "vol_media", AudioManager.STREAM_MUSIC);
        setupVolumeSlider(ringtoneVolume, "vol_ringtone", AudioManager.STREAM_RING);
        setupVolumeSlider(notificationVolume, "vol_notification", AudioManager.STREAM_NOTIFICATION);
        setupVolumeSlider(alarmVolume, "vol_alarm", AudioManager.STREAM_ALARM);
        setupVolumeSlider(systemVolume, "vol_system", AudioManager.STREAM_SYSTEM);

        dndSwitch.setOnCheckedChangeListener((b, checked) ->
                prefs.edit().putBoolean("dnd", checked).apply());
        vibrationSwitch.setOnCheckedChangeListener((b, checked) ->
                prefs.edit().putBoolean("vibration", checked).apply());
        touchSoundSwitch.setOnCheckedChangeListener((b, checked) ->
                prefs.edit().putBoolean("touch_sound", checked).apply());

        findViewById(R.id.ringtoneItem).setOnClickListener(v -> showRingtoneDialog());
    }

    private void setupVolumeSlider(SeekBar seekBar, String prefKey, int streamType) {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                prefs.edit().putInt(prefKey, progress).apply();
                if (fromUser && audioManager != null) {
                    int maxVol = audioManager.getStreamMaxVolume(streamType);
                    int vol = progress * maxVol / 100;
                    audioManager.setStreamVolume(streamType, vol, 0);
                }
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void showRingtoneDialog() {
        String[] ringtones = {"Andromeda", "Orion", "Nebula", "Saturn", "Jupiter"};
        int checked = 0;
        String current = prefs.getString("ringtone", "Andromeda");
        for (int i = 0; i < ringtones.length; i++) {
            if (ringtones[i].equals(current)) { checked = i; break; }
        }

        new AlertDialog.Builder(this, R.style.DialogTheme)
                .setTitle("Phone ringtone")
                .setSingleChoiceItems(ringtones, checked, (dialog, which) -> {
                    ringtoneName.setText(ringtones[which]);
                    prefs.edit().putString("ringtone", ringtones[which]).apply();
                    dialog.dismiss();
                })
                .show();
    }
}
