package com.celestia.launcher;

import android.app.WallpaperManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
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
    private EditText searchBar;
    private List<AppItem> allApps = new ArrayList<>();
    private List<AppItem> displayedApps = new ArrayList<>();
    private AppAdapter adapter;
    private Handler timeHandler = new Handler();
    private Runnable timeRunnable;
    private SharedPreferences prefs;
    private GestureDetector gestureDetector;

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

        prefs = getSharedPreferences("celestia_launcher", MODE_PRIVATE);

        setContentView(R.layout.activity_launcher);

        initViews();
        loadApps();
        setupDock();
        setupSearch();
        setupLongPress();
        setupGestures();
        startTimeUpdate();
    }

    private void initViews() {
        appGrid = findViewById(R.id.app_grid);
        dockBar = findViewById(R.id.dock_bar);
        txtTime = findViewById(R.id.txt_time);
        txtDate = findViewById(R.id.txt_date);
        searchBar = findViewById(R.id.search_bar);

        appGrid.setLayoutManager(new GridLayoutManager(this, 4));
        adapter = new AppAdapter(displayedApps, app -> {
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

        displayedApps.clear();
        displayedApps.addAll(allApps);
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

    private void setupSearch() {
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterApps(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void filterApps(String query) {
        displayedApps.clear();
        if (query.isEmpty()) {
            displayedApps.addAll(allApps);
        } else {
            for (AppItem app : allApps) {
                if (app.getLabel().toLowerCase().contains(query.toLowerCase())) {
                    displayedApps.add(app);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void setupLongPress() {
        appGrid.addOnItemTouchListener(new RecyclerView.SimpleOnItemTouchListener() {
            private GestureDetector gestureDetector = new GestureDetector(
                LauncherActivity.this,
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public void onLongPress(MotionEvent e) {
                        View child = appGrid.findChildViewUnder(e.getX(), e.getY());
                        if (child != null) {
                            int position = appGrid.getChildAdapterPosition(child);
                            if (position >= 0 && position < displayedApps.size()) {
                                AppItem app = displayedApps.get(position);
                                showAppContextMenu(app, child);
                            }
                        }
                    }
                }
            );

            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                gestureDetector.onTouchEvent(e);
                return false;
            }
        });
    }

    private void showAppContextMenu(AppItem app, View anchor) {
        String[] options = {"App info", "Uninstall", "Add to dock", "Share"};
        new AlertDialog.Builder(this, R.style.CelestiaDialogTheme)
            .setTitle(app.getLabel())
            .setItems(options, (dialog, which) -> {
                switch (which) {
                    case 0:
                        showAppInfo(app);
                        break;
                    case 1:
                        uninstallApp(app);
                        break;
                    case 2:
                        addToDock(app);
                        break;
                    case 3:
                        shareApp(app);
                        break;
                }
            })
            .show();
    }

    private void showAppInfo(AppItem app) {
        try {
            Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(android.net.Uri.parse("package:" + app.getPackageName()));
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Cannot open app info", Toast.LENGTH_SHORT).show();
        }
    }

    private void uninstallApp(AppItem app) {
        new AlertDialog.Builder(this, R.style.CelestiaDialogTheme)
            .setTitle("Uninstall")
            .setMessage("Uninstall " + app.getLabel() + "?")
            .setPositiveButton("Uninstall", (d, w) -> {
                Intent intent = new Intent(Intent.ACTION_DELETE);
                intent.setData(android.net.Uri.parse("package:" + app.getPackageName()));
                startActivity(intent);
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    private void addToDock(AppItem app) {
        Toast.makeText(this, app.getLabel() + " added to dock", Toast.LENGTH_SHORT).show();
    }

    private void shareApp(AppItem app) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "Check out " + app.getLabel() +
            " on Celestia OS: " + app.getPackageName());
        startActivity(Intent.createChooser(intent, "Share app"));
    }

    private void setupGestures() {
        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (e1 != null && e2 != null) {
                    float diffY = e2.getY() - e1.getY();
                    if (diffY < -100 && Math.abs(velocityY) > 200) {
                        openAppDrawer();
                        return true;
                    }
                }
                return false;
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                Toast.makeText(LauncherActivity.this, "Double tap detected", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        View root = findViewById(android.R.id.content);
        root.setOnTouchListener((v, event) -> {
            gestureDetector.onTouchEvent(event);
            return false;
        });
    }

    private void openAppDrawer() {
        Intent intent = new Intent(this, AppDrawerActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
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
