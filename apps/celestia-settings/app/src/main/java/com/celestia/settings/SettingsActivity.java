package com.celestia.settings;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    private EditText searchBar;
    private LinearLayout itemNetwork, itemBluetooth, itemDisplay, itemSound;
    private LinearLayout itemBattery, itemStorage, itemPrivacy, itemLocation;
    private LinearLayout itemSecurity, itemAccounts, itemAccessibility, itemSystem;
    private LinearLayout itemDeveloper, itemAbout, itemCelestia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_settings);

        initViews();
        setupClickListeners();
        setupSearch();
    }

    private void initViews() {
        searchBar = findViewById(R.id.searchBar);
        itemNetwork = findViewById(R.id.item_network);
        itemBluetooth = findViewById(R.id.item_bluetooth);
        itemDisplay = findViewById(R.id.item_display);
        itemSound = findViewById(R.id.item_sound);
        itemBattery = findViewById(R.id.item_battery);
        itemStorage = findViewById(R.id.item_storage);
        itemPrivacy = findViewById(R.id.item_privacy);
        itemLocation = findViewById(R.id.item_location);
        itemSecurity = findViewById(R.id.item_security);
        itemAccounts = findViewById(R.id.item_accounts);
        itemAccessibility = findViewById(R.id.item_accessibility);
        itemSystem = findViewById(R.id.item_system);
        itemDeveloper = findViewById(R.id.item_developer);
        itemAbout = findViewById(R.id.item_about);
        itemCelestia = findViewById(R.id.item_celestia);
    }

    private void setupClickListeners() {
        itemNetwork.setOnClickListener(v -> startActivity(new Intent(this, WifiActivity.class)));
        itemBluetooth.setOnClickListener(v -> startActivity(new Intent(this, BluetoothActivity.class)));
        itemDisplay.setOnClickListener(v -> startActivity(new Intent(this, DisplayActivity.class)));
        itemSound.setOnClickListener(v -> startActivity(new Intent(this, SoundActivity.class)));
        itemBattery.setOnClickListener(v -> startActivity(new Intent(this, BatteryActivity.class)));
        itemStorage.setOnClickListener(v -> startActivity(new Intent(this, StorageActivity.class)));
        itemPrivacy.setOnClickListener(v -> startActivity(new Intent(this, PrivacyActivity.class)));
        itemLocation.setOnClickListener(v -> startActivity(new Intent(this, LocationActivity.class)));
        itemSecurity.setOnClickListener(v -> startActivity(new Intent(this, SecurityActivity.class)));
        itemAccounts.setOnClickListener(v -> startActivity(new Intent(this, AccountsActivity.class)));
        itemAccessibility.setOnClickListener(v -> startActivity(new Intent(this, AccessibilityActivity.class)));
        itemSystem.setOnClickListener(v -> startActivity(new Intent(this, SystemActivity.class)));
        itemDeveloper.setOnClickListener(v -> startActivity(new Intent(this, DeveloperOptionsActivity.class)));
        itemAbout.setOnClickListener(v -> startActivity(new Intent(this, AboutActivity.class)));
        itemCelestia.setOnClickListener(v -> startActivity(new Intent(this, CelestiaActivity.class)));
    }

    private void setupSearch() {
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterItems(s.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void filterItems(String query) {
        itemNetwork.setVisibility(matchesQuery(itemNetwork, query) ? View.VISIBLE : View.GONE);
        itemBluetooth.setVisibility(matchesQuery(itemBluetooth, query) ? View.VISIBLE : View.GONE);
        itemDisplay.setVisibility(matchesQuery(itemDisplay, query) ? View.VISIBLE : View.GONE);
        itemSound.setVisibility(matchesQuery(itemSound, query) ? View.VISIBLE : View.GONE);
        itemBattery.setVisibility(matchesQuery(itemBattery, query) ? View.VISIBLE : View.GONE);
        itemStorage.setVisibility(matchesQuery(itemStorage, query) ? View.VISIBLE : View.GONE);
        itemPrivacy.setVisibility(matchesQuery(itemPrivacy, query) ? View.VISIBLE : View.GONE);
        itemLocation.setVisibility(matchesQuery(itemLocation, query) ? View.VISIBLE : View.GONE);
        itemSecurity.setVisibility(matchesQuery(itemSecurity, query) ? View.VISIBLE : View.GONE);
        itemAccounts.setVisibility(matchesQuery(itemAccounts, query) ? View.VISIBLE : View.GONE);
        itemAccessibility.setVisibility(matchesQuery(itemAccessibility, query) ? View.VISIBLE : View.GONE);
        itemSystem.setVisibility(matchesQuery(itemSystem, query) ? View.VISIBLE : View.GONE);
        itemDeveloper.setVisibility(matchesQuery(itemDeveloper, query) ? View.VISIBLE : View.GONE);
        itemAbout.setVisibility(matchesQuery(itemAbout, query) ? View.VISIBLE : View.GONE);
        itemCelestia.setVisibility(matchesQuery(itemCelestia, query) ? View.VISIBLE : View.GONE);
    }

    private boolean matchesQuery(LinearLayout item, String query) {
        if (query.isEmpty()) return true;
        LinearLayout textContainer = (LinearLayout) item.getChildAt(1);
        if (textContainer == null || textContainer.getChildCount() == 0) return false;
        TextView titleView = (TextView) textContainer.getChildAt(0);
        if (titleView == null) return false;
        return titleView.getText().toString().toLowerCase().contains(query);
    }
}
