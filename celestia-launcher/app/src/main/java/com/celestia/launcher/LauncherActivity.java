package com.celestia.launcher;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class LauncherActivity extends AppCompatActivity {

    private RecyclerView appGrid;
    private LinearLayout dockBar;
    private TextView txtTime, txtDate;
    private List<AppItem> allApps = new ArrayList<>();
    private AppAdapter adapter;
    private Handler timeHandler = new Handler();
    private Runnable timeRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );

        setContentView(R.layout.activity_launcher);

        initViews();
        loadApps();
        setupDock();
        startTimeUpdate();
    }

    private void initViews() {
        appGrid = findViewById(R.id.app_grid);
        dockBar = findViewById(R.id.dock_bar);
        txtTime = findViewById(R.id.txt_time);
        txtDate = findViewById(R.id.txt_date);

        appGrid.setLayoutManager(new GridLayoutManager(this, 4));
        adapter = new AppAdapter(allApps, app -> {
            Intent launchIntent = getPackageManager()
                .getLaunchIntentForPackage(app.getPackageName());
            if (launchIntent != null) {
                startActivity(launchIntent);
            }
        });
        appGrid.setAdapter(adapter);
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

        adapter.notifyDataSetChanged();
    }

    private void setupDock() {
        String[] dockPackages = {
            "com.celestia.settings",
            "com.celestia.camera",
            "com.android.dialer",
            "com.android.messaging"
        };

        PackageManager pm = getPackageManager();
        dockBar.removeAllViews();

        for (String pkg : dockPackages) {
            try {
                Drawable icon = pm.getApplicationIcon(pkg);
                CharSequence label = pm.getApplicationLabel(
                    pm.getApplicationInfo(pkg, 0));

                ImageView dockItem = new ImageView(this);
                dockItem.setImageDrawable(icon);
                dockItem.setLayoutParams(new LinearLayout.LayoutParams(
                    dpToPx(52), dpToPx(52)));
                dockItem.setPadding(dpToPx(8), dpToPx(8), dpToPx(8), dpToPx(8));
                dockItem.setScaleType(ImageView.ScaleType.FIT_CENTER);

                dockItem.setOnClickListener(v -> {
                    Intent intent = pm.getLaunchIntentForPackage(pkg);
                    if (intent != null) startActivity(intent);
                });

                dockBar.addView(dockItem);
            } catch (PackageManager.NameNotFoundException e) {
                // Skip if app not found
            }
        }
    }

    private void startTimeUpdate() {
        timeRunnable = new Runnable() {
            @Override
            public void run() {
                SimpleDateFormat timeFormat =
                    new SimpleDateFormat("HH:mm", Locale.getDefault());
                SimpleDateFormat dateFormat =
                    new SimpleDateFormat("EEEE, MMMM d", Locale.getDefault());

                txtTime.setText(timeFormat.format(new Date()));
                txtDate.setText(dateFormat.format(new Date()));

                timeHandler.postDelayed(this, 1000);
            }
        };
        timeRunnable.run();
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadApps();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timeHandler.removeCallbacks(timeRunnable);
    }
}
