package com.celestia.launcher;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AppDrawerActivity extends AppCompatActivity {

    private RecyclerView appGrid;
    private EditText searchInput;
    private AppAdapter adapter;
    private List<AppItem> allApps = new ArrayList<>();
    private List<AppItem> filteredApps = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        setContentView(R.layout.activity_app_drawer);

        appGrid = findViewById(R.id.drawer_app_grid);
        searchInput = findViewById(R.id.search_input);

        appGrid.setLayoutManager(new GridLayoutManager(this, 4));
        adapter = new AppAdapter(filteredApps, app -> {
            Intent launchIntent = getPackageManager()
                .getLaunchIntentForPackage(app.getPackageName());
            if (launchIntent != null) {
                startActivity(launchIntent);
                finish();
            }
        });
        appGrid.setAdapter(adapter);

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterApps(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        loadApps();
    }

    private void loadApps() {
        PackageManager pm = getPackageManager();
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> appList = pm.queryIntentActivities(mainIntent, 0);
        allApps.clear();

        for (ResolveInfo info : appList) {
            ApplicationInfo appInfo = info.activityInfo.applicationInfo;
            AppItem item = new AppItem(
                info.loadLabel(pm).toString(),
                info.loadIcon(pm),
                appInfo.packageName
            );
            allApps.add(item);
        }

        Collections.sort(allApps, (a, b) ->
            a.getLabel().compareToIgnoreCase(b.getLabel()));

        filteredApps.clear();
        filteredApps.addAll(allApps);
        adapter.notifyDataSetChanged();
    }

    private void filterApps(String query) {
        filteredApps.clear();
        if (query.isEmpty()) {
            filteredApps.addAll(allApps);
        } else {
            for (AppItem app : allApps) {
                if (app.getLabel().toLowerCase().contains(query.toLowerCase()) ||
                    app.getPackageName().toLowerCase().contains(query.toLowerCase())) {
                    filteredApps.add(app);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }
}
