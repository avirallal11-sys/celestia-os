package com.celestia.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class StorageActivity extends AppCompatActivity {

    private ProgressBar storageBar;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_storage);

        prefs = getSharedPreferences("celestia_storage", MODE_PRIVATE);

        initViews();
    }

    private void initViews() {
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        storageBar = findViewById(R.id.storageBar);

        String[] categories = {"Apps", "Images", "Videos", "Audio", "Documents"};
        String[] sizes = {"18.3 GB", "5.2 GB", "3.8 GB", "2.1 GB", "1.5 GB"};
        int[] ids = {R.id.storage_apps, R.id.storage_images, R.id.storage_videos, R.id.storage_audio, R.id.storage_documents};

        for (int i = 0; i < ids.length; i++) {
            final String cat = categories[i];
            final String size = sizes[i];
            View item = findViewById(ids[i]);
            if (item != null) {
                item.setOnClickListener(v -> showStorageDetail(cat, size));
            }
        }
    }

    private void showStorageDetail(String category, String size) {
        new AlertDialog.Builder(this, R.style.DialogTheme)
                .setTitle(category)
                .setMessage("Size: " + size + "\n\nClear cache and data to free up space.")
                .setPositiveButton("Clear cache", (dialog, which) ->
                        Toast.makeText(this, "Cache cleared for " + category, Toast.LENGTH_SHORT).show())
                .setNegativeButton("Cancel", null)
                .show();
    }
}
