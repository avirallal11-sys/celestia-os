package com.celestia.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Switch;
import androidx.appcompat.app.AppCompatActivity;

public class AccessibilityActivity extends AppCompatActivity {

    private Switch colorCorrectionSwitch, colorInversionSwitch, highContrastSwitch;
    private Switch magnificationSwitch, talkbackSwitch, selectToSpeakSwitch, switchAccessSwitch;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_accessibility);

        prefs = getSharedPreferences("celestia_accessibility", MODE_PRIVATE);

        initViews();
        loadState();
        setupListeners();
    }

    private void initViews() {
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        colorCorrectionSwitch = findViewById(R.id.colorCorrectionSwitch);
        colorInversionSwitch = findViewById(R.id.colorInversionSwitch);
        highContrastSwitch = findViewById(R.id.highContrastSwitch);
        magnificationSwitch = findViewById(R.id.magnificationSwitch);
        talkbackSwitch = findViewById(R.id.talkbackSwitch);
        selectToSpeakSwitch = findViewById(R.id.selectToSpeakSwitch);
        switchAccessSwitch = findViewById(R.id.switchAccessSwitch);
    }

    private void loadState() {
        colorCorrectionSwitch.setChecked(prefs.getBoolean("color_correction", false));
        colorInversionSwitch.setChecked(prefs.getBoolean("color_inversion", false));
        highContrastSwitch.setChecked(prefs.getBoolean("high_contrast", false));
        magnificationSwitch.setChecked(prefs.getBoolean("magnification", false));
        talkbackSwitch.setChecked(prefs.getBoolean("talkback", false));
        selectToSpeakSwitch.setChecked(prefs.getBoolean("select_to_speak", false));
        switchAccessSwitch.setChecked(prefs.getBoolean("switch_access", false));
    }

    private void setupListeners() {
        colorCorrectionSwitch.setOnCheckedChangeListener((b, checked) ->
                prefs.edit().putBoolean("color_correction", checked).apply());
        colorInversionSwitch.setOnCheckedChangeListener((b, checked) ->
                prefs.edit().putBoolean("color_inversion", checked).apply());
        highContrastSwitch.setOnCheckedChangeListener((b, checked) ->
                prefs.edit().putBoolean("high_contrast", checked).apply());
        magnificationSwitch.setOnCheckedChangeListener((b, checked) ->
                prefs.edit().putBoolean("magnification", checked).apply());
        talkbackSwitch.setOnCheckedChangeListener((b, checked) ->
                prefs.edit().putBoolean("talkback", checked).apply());
        selectToSpeakSwitch.setOnCheckedChangeListener((b, checked) ->
                prefs.edit().putBoolean("select_to_speak", checked).apply());
        switchAccessSwitch.setOnCheckedChangeListener((b, checked) ->
                prefs.edit().putBoolean("switch_access", checked).apply());

        findViewById(R.id.hearingAidsItem).setOnClickListener(v -> {
            // Navigate to hearing aids settings
        });
    }
}
