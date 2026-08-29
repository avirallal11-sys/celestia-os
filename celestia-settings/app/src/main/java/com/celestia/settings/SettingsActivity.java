package com.celestia.settings;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SettingsAdapter adapter;
    private List<SettingsItem> settingsItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        setContentView(R.layout.activity_settings);

        recyclerView = findViewById(R.id.settings_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        setupSettingsItems();
        adapter = new SettingsAdapter(settingsItems, this);
        recyclerView.setAdapter(adapter);
    }

    private void setupSettingsItems() {
        settingsItems = new ArrayList<>();

        // Network & Internet
        settingsItems.add(new SettingsItem(
            "Network & Internet", "Wi-Fi, mobile, data usage, hotspot",
            R.drawable.ic_wifi, "network"));

        // Bluetooth
        settingsItems.add(new SettingsItem(
            "Bluetooth", "Pair device, connection preferences",
            R.drawable.ic_bluetooth, "bluetooth"));

        // Display
        settingsItems.add(new SettingsItem(
            "Display", "Brightness, wallpaper, sleep, font size",
            R.drawable.ic_display, "display"));

        // Sound & Vibration
        settingsItems.add(new SettingsItem(
            "Sound & Vibration", "Volume, vibration, Do Not Disturb",
            R.drawable.ic_sound, "sound"));

        // Battery
        settingsItems.add(new SettingsItem(
            "Battery", "100% - About 12 hr left",
            R.drawable.ic_battery, "battery"));

        // Storage
        settingsItems.add(new SettingsItem(
            "Storage", "Internal shared storage - 32 GB",
            R.drawable.ic_storage, "storage"));

        // Privacy
        settingsItems.add(new SettingsItem(
            "Privacy", "Permissions, account activity controls",
            R.drawable.ic_privacy, "privacy"));

        // Location
        settingsItems.add(new SettingsItem(
            "Location", "On - 3 apps have access",
            R.drawable.ic_location, "location"));

        // Security
        settingsItems.add(new SettingsItem(
            "Security", "Screen lock, fingerprint, encryption",
            R.drawable.ic_security, "security"));

        // Accounts
        settingsItems.add(new SettingsItem(
            "Accounts", "Celestia Account, Google, sync",
            R.drawable.ic_accounts, "accounts"));

        // Accessibility
        settingsItems.add(new SettingsItem(
            "Accessibility", "Screen readers, display, controls",
            R.drawable.ic_accessibility, "accessibility"));

        // System
        settingsItems.add(new SettingsItem(
            "System", "Language, gestures, time, backup, reset",
            R.drawable.ic_system, "system"));

        // About Phone
        settingsItems.add(new SettingsItem(
            "About Celestia", "Device info, build number, version",
            R.drawable.ic_info, "about"));

        // Celestia Settings (Custom)
        settingsItems.add(new SettingsItem(
            "Celestia Settings", "Custom themes, boot animation, cosmic UI",
            R.drawable.ic_celestia, "celestia"));
    }
}
