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

public class WifiActivity extends AppCompatActivity {

    private Switch wifiSwitch;
    private TextView wifiStatusText;
    private View wifiOffMessage;
    private View availableNetworksHeader, connectedNetwork, networkList;
    private LinearLayout connectedNet;

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_wifi);

        prefs = getSharedPreferences("celestia_wifi", MODE_PRIVATE);

        initViews();
        loadState();
        setupListeners();
    }

    private void initViews() {
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        wifiSwitch = findViewById(R.id.wifiSwitch);
        wifiStatusText = findViewById(R.id.wifiStatusText);
        wifiOffMessage = findViewById(R.id.wifiOffMessage);
        availableNetworksHeader = findViewById(R.id.availableNetworksHeader);
        connectedNetwork = findViewById(R.id.connectedNetwork);
        networkList = findViewById(R.id.networkList);
    }

    private void loadState() {
        boolean enabled = prefs.getBoolean("wifi_enabled", true);
        wifiSwitch.setChecked(enabled);
        updateUI(enabled);
    }

    private void setupListeners() {
        wifiSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("wifi_enabled", isChecked).apply();
            updateUI(isChecked);
        });

        int[] networkIds = {R.id.network_office, R.id.network_celestia, R.id.network_neighbor, R.id.network_cafe};
        String[] networkNames = {"Office-5G", "Celestia-Net", "Neighbor_WiFi", "Cafe_Free"};
        String[] networkInfo = {"5 GHz · WPA3-Personal", "2.4 GHz · WPA2-Personal", "2.4 GHz · WPA2-Personal", "2.4 GHz · Open"};

        for (int i = 0; i < networkIds.length; i++) {
            final String name = networkNames[i];
            final String info = networkInfo[i];
            findViewById(networkIds[i]).setOnClickListener(v -> showPasswordDialog(name, info));
        }
    }

    private void updateUI(boolean enabled) {
        wifiStatusText.setText(enabled ? "On" : "Off");
        int visibility = enabled ? View.VISIBLE : View.GONE;
        wifiOffMessage.setVisibility(enabled ? View.GONE : View.VISIBLE);
        availableNetworksHeader.setVisibility(visibility);
        connectedNetwork.setVisibility(visibility);
        networkList.setVisibility(visibility);
    }

    private void showPasswordDialog(String networkName, String info) {
        EditText input = new EditText(this);
        input.setHint("Enter password");
        input.setTextColor(getResources().getColor(R.color.text_primary));
        input.setHintTextColor(getResources().getColor(R.color.text_hint));
        input.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);

        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(64, 32, 64, 0);

        TextView label = new TextView(this);
        label.setText("Connect to " + networkName + "\n" + info);
        label.setTextColor(getResources().getColor(R.color.text_secondary));
        label.setTextSize(13);
        container.addView(label);
        container.addView(input);

        new AlertDialog.Builder(this, R.style.DialogTheme)
                .setTitle("Enter password")
                .setView(container)
                .setPositiveButton("Connect", (dialog, which) -> {
                    String password = input.getText().toString();
                    if (!password.isEmpty()) {
                        Toast.makeText(this, "Connected to " + networkName, Toast.LENGTH_SHORT).show();
                        prefs.edit().putString("connected_network", networkName).apply();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
