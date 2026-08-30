package com.celestia.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Switch;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class SystemActivity extends AppCompatActivity {

    private Switch autoTimeSwitch, oneHandSwitch;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_system);

        prefs = getSharedPreferences("celestia_system", MODE_PRIVATE);

        initViews();
        loadState();
        setupListeners();
    }

    private void initViews() {
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        autoTimeSwitch = findViewById(R.id.autoTimeSwitch);
        oneHandSwitch = findViewById(R.id.oneHandSwitch);
    }

    private void loadState() {
        autoTimeSwitch.setChecked(prefs.getBoolean("auto_time", true));
        oneHandSwitch.setChecked(prefs.getBoolean("one_hand_mode", false));
    }

    private void setupListeners() {
        autoTimeSwitch.setOnCheckedChangeListener((b, checked) ->
                prefs.edit().putBoolean("auto_time", checked).apply());

        oneHandSwitch.setOnCheckedChangeListener((b, checked) ->
                prefs.edit().putBoolean("one_hand_mode", checked).apply());

        findViewById(R.id.datetimeItem).setOnClickListener(v ->
                Toast.makeText(this, "Date & Time settings", Toast.LENGTH_SHORT).show());

        findViewById(R.id.developerLink).setOnClickListener(v ->
                startActivity(new android.content.Intent(this, DeveloperOptionsActivity.class)));
    }
}
