package com.celestia.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class SecurityActivity extends AppCompatActivity {

    private Switch fingerprintSwitch, faceSwitch, findDeviceSwitch;
    private TextView screenLockValue;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_security);

        prefs = getSharedPreferences("celestia_security", MODE_PRIVATE);

        initViews();
        loadState();
        setupListeners();
    }

    private void initViews() {
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        fingerprintSwitch = findViewById(R.id.fingerprintSwitch);
        faceSwitch = findViewById(R.id.faceSwitch);
        findDeviceSwitch = findViewById(R.id.findDeviceSwitch);
        screenLockValue = findViewById(R.id.screenLockValue);
    }

    private void loadState() {
        fingerprintSwitch.setChecked(prefs.getBoolean("fingerprint", false));
        faceSwitch.setChecked(prefs.getBoolean("face_recognition", false));
        findDeviceSwitch.setChecked(prefs.getBoolean("find_device", true));
        screenLockValue.setText(prefs.getString("screen_lock", "Pattern"));
    }

    private void setupListeners() {
        fingerprintSwitch.setOnCheckedChangeListener((b, checked) ->
                prefs.edit().putBoolean("fingerprint", checked).apply());
        faceSwitch.setOnCheckedChangeListener((b, checked) ->
                prefs.edit().putBoolean("face_recognition", checked).apply());
        findDeviceSwitch.setOnCheckedChangeListener((b, checked) ->
                prefs.edit().putBoolean("find_device", checked).apply());

        findViewById(R.id.screenLockItem).setOnClickListener(v -> showScreenLockDialog());
    }

    private void showScreenLockDialog() {
        String[] options = {"None", "Swipe", "Pattern", "PIN", "Password"};
        String current = prefs.getString("screen_lock", "Pattern");
        int checked = 0;
        for (int i = 0; i < options.length; i++) {
            if (options[i].equals(current)) { checked = i; break; }
        }

        new AlertDialog.Builder(this, R.style.DialogTheme)
                .setTitle("Screen lock")
                .setSingleChoiceItems(options, checked, (dialog, which) -> {
                    screenLockValue.setText(options[which]);
                    prefs.edit().putString("screen_lock", options[which]).apply();
                    dialog.dismiss();
                })
                .show();
    }
}
