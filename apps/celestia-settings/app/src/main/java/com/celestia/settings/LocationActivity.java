package com.celestia.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Switch;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class LocationActivity extends AppCompatActivity {

    private Switch locationSwitch, gpsSwitch, wifiScanSwitch, btScanSwitch, locationAccuracySwitch;
    private TextView locationStatusText;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_location);

        prefs = getSharedPreferences("celestia_location", MODE_PRIVATE);

        initViews();
        loadState();
        setupListeners();
    }

    private void initViews() {
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        locationSwitch = findViewById(R.id.locationSwitch);
        locationStatusText = findViewById(R.id.locationStatusText);
        gpsSwitch = findViewById(R.id.gpsSwitch);
        wifiScanSwitch = findViewById(R.id.wifiScanSwitch);
        btScanSwitch = findViewById(R.id.btScanSwitch);
        locationAccuracySwitch = findViewById(R.id.locationAccuracySwitch);
    }

    private void loadState() {
        locationSwitch.setChecked(prefs.getBoolean("location_enabled", true));
        gpsSwitch.setChecked(prefs.getBoolean("gps_enabled", true));
        wifiScanSwitch.setChecked(prefs.getBoolean("wifi_scan", true));
        btScanSwitch.setChecked(prefs.getBoolean("bt_scan", false));
        locationAccuracySwitch.setChecked(prefs.getBoolean("location_accuracy", true));
        updateLocationStatus(prefs.getBoolean("location_enabled", true));
    }

    private void setupListeners() {
        locationSwitch.setOnCheckedChangeListener((b, checked) -> {
            prefs.edit().putBoolean("location_enabled", checked).apply();
            updateLocationStatus(checked);
        });
        gpsSwitch.setOnCheckedChangeListener((b, checked) ->
                prefs.edit().putBoolean("gps_enabled", checked).apply());
        wifiScanSwitch.setOnCheckedChangeListener((b, checked) ->
                prefs.edit().putBoolean("wifi_scan", checked).apply());
        btScanSwitch.setOnCheckedChangeListener((b, checked) ->
                prefs.edit().putBoolean("bt_scan", checked).apply());
        locationAccuracySwitch.setOnCheckedChangeListener((b, checked) ->
                prefs.edit().putBoolean("location_accuracy", checked).apply());
    }

    private void updateLocationStatus(boolean enabled) {
        locationStatusText.setText(enabled ? "On — using GPS, Wi-Fi" : "Off");
    }
}
