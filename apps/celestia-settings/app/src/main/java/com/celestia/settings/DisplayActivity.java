package com.celestia.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class DisplayActivity extends AppCompatActivity {

    private SeekBar brightnessSeekBar, fontSizeSeekBar, zoomSeekBar;
    private Switch darkModeSwitch, eyeComfortSwitch, autoRotateSwitch;
    private TextView fontSizeLabel, timeoutValue;

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_display);

        prefs = getSharedPreferences("celestia_display", MODE_PRIVATE);

        initViews();
        loadState();
        setupListeners();
    }

    private void initViews() {
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        brightnessSeekBar = findViewById(R.id.brightnessSeekBar);
        fontSizeSeekBar = findViewById(R.id.fontSizeSeekBar);
        zoomSeekBar = findViewById(R.id.zoomSeekBar);
        darkModeSwitch = findViewById(R.id.darkModeSwitch);
        eyeComfortSwitch = findViewById(R.id.eyeComfortSwitch);
        autoRotateSwitch = findViewById(R.id.autoRotateSwitch);
        fontSizeLabel = findViewById(R.id.fontSizeLabel);
        timeoutValue = findViewById(R.id.timeoutValue);
    }

    private void loadState() {
        brightnessSeekBar.setProgress(prefs.getInt("brightness", 60));
        fontSizeSeekBar.setProgress(prefs.getInt("font_size", 1));
        zoomSeekBar.setProgress(prefs.getInt("zoom", 2));
        darkModeSwitch.setChecked(prefs.getBoolean("dark_mode", false));
        eyeComfortSwitch.setChecked(prefs.getBoolean("eye_comfort", false));
        autoRotateSwitch.setChecked(prefs.getBoolean("auto_rotate", true));
        timeoutValue.setText(prefs.getString("screen_timeout", "30 seconds"));
        updateFontLabel(prefs.getInt("font_size", 1));
    }

    private void setupListeners() {
        brightnessSeekBar.setOnSeekBarChangeListener(new SimpleSeekBarListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                prefs.edit().putInt("brightness", progress).apply();
                try {
                    android.provider.Settings.System.putInt(getContentResolver(),
                            android.provider.Settings.System.SCREEN_BRIGHTNESS, progress * 255 / 100);
                } catch (Exception e) { /* permission denied */ }
            }
        });

        fontSizeSeekBar.setOnSeekBarChangeListener(new SimpleSeekBarListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                prefs.edit().putInt("font_size", progress).apply();
                updateFontLabel(progress);
            }
        });

        zoomSeekBar.setOnSeekBarChangeListener(new SimpleSeekBarListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                prefs.edit().putInt("zoom", progress).apply();
            }
        });

        darkModeSwitch.setOnCheckedChangeListener((b, checked) ->
                prefs.edit().putBoolean("dark_mode", checked).apply());

        eyeComfortSwitch.setOnCheckedChangeListener((b, checked) ->
                prefs.edit().putBoolean("eye_comfort", checked).apply());

        autoRotateSwitch.setOnCheckedChangeListener((b, checked) ->
                prefs.edit().putBoolean("auto_rotate", checked).apply());

        findViewById(R.id.screenTimeoutItem).setOnClickListener(v -> showTimeoutDialog());
        findViewById(R.id.wallpaperItem).setOnClickListener(v ->
                Toast.makeText(this, "Wallpaper selector coming soon", Toast.LENGTH_SHORT).show());
    }

    private void updateFontLabel(int size) {
        String[] labels = {"Small", "Default", "Large", "Extra large"};
        fontSizeLabel.setText(labels[Math.min(size, labels.length - 1)]);
    }

    private void showTimeoutDialog() {
        String[] options = {"15 seconds", "30 seconds", "1 minute", "2 minutes", "5 minutes", "10 minutes", "Never"};
        new AlertDialog.Builder(this, R.style.DialogTheme)
                .setTitle("Screen timeout")
                .setItems(options, (dialog, which) -> {
                    timeoutValue.setText(options[which]);
                    prefs.edit().putString("screen_timeout", options[which]).apply();
                })
                .show();
    }

    private static abstract class SimpleSeekBarListener implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {}
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {}
    }
}
