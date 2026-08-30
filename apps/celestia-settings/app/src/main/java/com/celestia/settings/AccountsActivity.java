package com.celestia.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Switch;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class AccountsActivity extends AppCompatActivity {

    private Switch autoSyncSwitch;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_accounts);

        prefs = getSharedPreferences("celestia_accounts", MODE_PRIVATE);

        initViews();
        loadState();
        setupListeners();
    }

    private void initViews() {
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        autoSyncSwitch = findViewById(R.id.autoSyncSwitch);
    }

    private void loadState() {
        autoSyncSwitch.setChecked(prefs.getBoolean("auto_sync", true));
    }

    private void setupListeners() {
        autoSyncSwitch.setOnCheckedChangeListener((b, checked) ->
                prefs.edit().putBoolean("auto_sync", checked).apply());

        findViewById(R.id.addAccountItem).setOnClickListener(v ->
                showAddAccountDialog());
    }

    private void showAddAccountDialog() {
        String[] accountTypes = {"Google", "GitHub", "Microsoft", "Apple"};
        new AlertDialog.Builder(this, R.style.DialogTheme)
                .setTitle("Add account")
                .setItems(accountTypes, (dialog, which) ->
                        Toast.makeText(this, "Add " + accountTypes[which] + " account", Toast.LENGTH_SHORT).show())
                .show();
    }
}
