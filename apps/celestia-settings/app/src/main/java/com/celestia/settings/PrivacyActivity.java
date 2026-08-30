package com.celestia.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Switch;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class PrivacyActivity extends AppCompatActivity {

    private Switch activityControlsSwitch, deleteActivitySwitch;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_privacy);

        prefs = getSharedPreferences("celestia_privacy", MODE_PRIVATE);

        initViews();
        loadState();
        setupListeners();
    }

    private void initViews() {
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        activityControlsSwitch = findViewById(R.id.activityControlsSwitch);
        deleteActivitySwitch = findViewById(R.id.deleteActivitySwitch);
    }

    private void loadState() {
        activityControlsSwitch.setChecked(prefs.getBoolean("activity_controls", false));
        deleteActivitySwitch.setChecked(prefs.getBoolean("delete_activity", false));
    }

    private void setupListeners() {
        activityControlsSwitch.setOnCheckedChangeListener((b, checked) ->
                prefs.edit().putBoolean("activity_controls", checked).apply());

        deleteActivitySwitch.setOnCheckedChangeListener((b, checked) ->
                prefs.edit().putBoolean("delete_activity", checked).apply());

        findViewById(R.id.permissionManagerItem).setOnClickListener(v ->
                Toast.makeText(this, "Permission manager", Toast.LENGTH_SHORT).show());
    }
}
