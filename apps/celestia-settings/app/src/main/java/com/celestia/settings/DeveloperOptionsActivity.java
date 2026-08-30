package com.celestia.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Switch;
import androidx.appcompat.app.AppCompatActivity;

public class DeveloperOptionsActivity extends AppCompatActivity {

    private Switch usbDebuggingSwitch, oemUnlockSwitch, stayAwakeSwitch;
    private Switch showTouchesSwitch, pointerLocationSwitch;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_developer_options);

        prefs = getSharedPreferences("celestia_developer", MODE_PRIVATE);

        initViews();
        loadState();
        setupListeners();
    }

    private void initViews() {
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        usbDebuggingSwitch = findViewById(R.id.usbDebuggingSwitch);
        oemUnlockSwitch = findViewById(R.id.oemUnlockSwitch);
        stayAwakeSwitch = findViewById(R.id.stayAwakeSwitch);
        showTouchesSwitch = findViewById(R.id.showTouchesSwitch);
        pointerLocationSwitch = findViewById(R.id.pointerLocationSwitch);
    }

    private void loadState() {
        usbDebuggingSwitch.setChecked(prefs.getBoolean("usb_debugging", false));
        oemUnlockSwitch.setChecked(prefs.getBoolean("oem_unlock", false));
        stayAwakeSwitch.setChecked(prefs.getBoolean("stay_awake", false));
        showTouchesSwitch.setChecked(prefs.getBoolean("show_touches", false));
        pointerLocationSwitch.setChecked(prefs.getBoolean("pointer_location", false));
    }

    private void setupListeners() {
        usbDebuggingSwitch.setOnCheckedChangeListener((b, checked) ->
                prefs.edit().putBoolean("usb_debugging", checked).apply());
        oemUnlockSwitch.setOnCheckedChangeListener((b, checked) ->
                prefs.edit().putBoolean("oem_unlock", checked).apply());
        stayAwakeSwitch.setOnCheckedChangeListener((b, checked) ->
                prefs.edit().putBoolean("stay_awake", checked).apply());
        showTouchesSwitch.setOnCheckedChangeListener((b, checked) ->
                prefs.edit().putBoolean("show_touches", checked).apply());
        pointerLocationSwitch.setOnCheckedChangeListener((b, checked) ->
                prefs.edit().putBoolean("pointer_location", checked).apply());
    }
}
