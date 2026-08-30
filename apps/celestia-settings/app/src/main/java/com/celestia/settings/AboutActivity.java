package com.celestia.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AboutActivity extends AppCompatActivity {

    private int buildTapCount = 0;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_about);

        prefs = getSharedPreferences("celestia_about", MODE_PRIVATE);

        initViews();
    }

    private void initViews() {
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        findViewById(R.id.buildNumberItem).setOnClickListener(v -> {
            buildTapCount++;
            if (buildTapCount >= 7) {
                Toast.makeText(this, "You are now a developer!", Toast.LENGTH_LONG).show();
                prefs.edit().putBoolean("developer_mode", true).apply();
                buildTapCount = 0;
            } else if (buildTapCount >= 3) {
                int remaining = 7 - buildTapCount;
                Toast.makeText(this, "You are " + remaining + " steps away from being a developer", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
