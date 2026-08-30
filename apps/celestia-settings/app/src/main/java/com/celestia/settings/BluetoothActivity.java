package com.celestia.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class BluetoothActivity extends AppCompatActivity {

    private Switch btSwitch;
    private TextView btStatusText, deviceNameText;
    private View btOffMessage;
    private LinearLayout pairedList, availableList;

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_bluetooth);

        prefs = getSharedPreferences("celestia_bluetooth", MODE_PRIVATE);

        initViews();
        loadState();
        setupListeners();
    }

    private void initViews() {
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        btSwitch = findViewById(R.id.btSwitch);
        btStatusText = findViewById(R.id.btStatusText);
        deviceNameText = findViewById(R.id.deviceNameText);
        btOffMessage = findViewById(R.id.btOffMessage);
        pairedList = findViewById(R.id.pairedList);
        availableList = findViewById(R.id.availableList);
    }

    private void loadState() {
        boolean enabled = prefs.getBoolean("bt_enabled", true);
        btSwitch.setChecked(enabled);
        deviceNameText.setText(prefs.getString("device_name", "Celestia 1.0"));
        updateUI(enabled);
    }

    private void setupListeners() {
        btSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("bt_enabled", isChecked).apply();
            updateUI(isChecked);
        });

        findViewById(R.id.deviceNameItem).setOnClickListener(v -> showDeviceNameDialog());

        int[] deviceIds = {R.id.bt_galaxy_buds, R.id.bt_sony, R.id.bt_watch, R.id.bt_miband};
        String[] deviceNames = {"Galaxy Buds", "Sony WH-1000XM5", "Celestia Watch", "Mi Band 7"};

        for (int i = 0; i < deviceIds.length; i++) {
            final String name = deviceNames[i];
            findViewById(deviceIds[i]).setOnClickListener(v -> showPairingDialog(name));
        }
    }

    private void updateUI(boolean enabled) {
        btStatusText.setText(enabled ? "On" : "Off");
        int visibility = enabled ? View.VISIBLE : View.GONE;
        btOffMessage.setVisibility(enabled ? View.GONE : View.VISIBLE);
        availableList.setVisibility(visibility);
    }

    private void showPairingDialog(String deviceName) {
        new AlertDialog.Builder(this, R.style.DialogTheme)
                .setTitle("Bluetooth pairing request")
                .setMessage("Pair with " + deviceName + "?")
                .setPositiveButton("Pair", (dialog, which) -> {
                    Toast.makeText(this, "Paired with " + deviceName, Toast.LENGTH_SHORT).show();
                    prefs.edit().putBoolean("paired_" + deviceName, true).apply();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showDeviceNameDialog() {
        EditText input = new EditText(this);
        input.setText(deviceNameText.getText().toString());
        input.setTextColor(getResources().getColor(R.color.text_primary));

        new AlertDialog.Builder(this, R.style.DialogTheme)
                .setTitle("Device name")
                .setView(input)
                .setPositiveButton("OK", (dialog, which) -> {
                    String newName = input.getText().toString();
                    if (!newName.isEmpty()) {
                        deviceNameText.setText(newName);
                        prefs.edit().putString("device_name", newName).apply();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
