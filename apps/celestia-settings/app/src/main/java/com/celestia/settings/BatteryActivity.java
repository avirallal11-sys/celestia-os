package com.celestia.settings;

import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Switch;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class BatteryActivity extends AppCompatActivity {

    private TextView batteryPercent, batteryStatusText;
    private View batteryFill;
    private Switch powerSavingSwitch, adaptiveBatterySwitch;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_battery);

        prefs = getSharedPreferences("celestia_battery", MODE_PRIVATE);

        initViews();
        loadState();
        setupListeners();
        updateBatteryInfo();
    }

    private void initViews() {
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        batteryPercent = findViewById(R.id.batteryPercent);
        batteryFill = findViewById(R.id.batteryFill);
        powerSavingSwitch = findViewById(R.id.powerSavingSwitch);
        adaptiveBatterySwitch = findViewById(R.id.adaptiveBatterySwitch);
    }

    private void loadState() {
        powerSavingSwitch.setChecked(prefs.getBoolean("power_saving", false));
        adaptiveBatterySwitch.setChecked(prefs.getBoolean("adaptive_battery", true));
    }

    private void setupListeners() {
        powerSavingSwitch.setOnCheckedChangeListener((b, checked) ->
                prefs.edit().putBoolean("power_saving", checked).apply());
        adaptiveBatterySwitch.setOnCheckedChangeListener((b, checked) ->
                prefs.edit().putBoolean("adaptive_battery", checked).apply());
    }

    private void updateBatteryInfo() {
        android.content.IntentFilter filter = new android.content.IntentFilter();
        android.content.Intent batteryIntent = registerReceiver(null, filter);

        int level = 87;
        if (batteryIntent != null) {
            level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, 87);
        }

        batteryPercent.setText(level + "%");

        int fillHeight = (int) (114 * level / 100.0f);
        android.view.ViewGroup.LayoutParams params = batteryFill.getLayoutParams();
        params.height = fillHeight;
        batteryFill.setLayoutParams(params);

        int color;
        if (level > 60) color = getResources().getColor(R.color.safe_green);
        else if (level > 20) color = getResources().getColor(R.color.battery_yellow);
        else color = getResources().getColor(R.color.battery_red);
        batteryFill.setBackgroundColor(color);
    }
}
