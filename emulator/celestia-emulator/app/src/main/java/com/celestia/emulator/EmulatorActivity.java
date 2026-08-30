package com.celestia.emulator;

import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EmulatorActivity extends AppCompatActivity {

    private LinearLayout homeScreen;
    private LinearLayout lockScreen;
    private GridLayout appGrid;
    private LinearLayout dockBar;
    private TextView homeTime, homeDate, lockTime, lockDate;
    private TextView statusTime;
    private ScrollView appContainer;
    private LinearLayout notificationPanel;
    private Handler timeHandler;
    private boolean isLocked = true;
    private boolean panelOpen = false;

    private ExecutorService executor;
    private Handler mainHandler;

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

        setContentView(R.layout.activity_emulator);

        executor = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());

        initViews();
        setupApps();
        setupDock();
        startTime();
        setupGestures();
    }

    private void initViews() {
        homeScreen = findViewById(R.id.home_screen);
        lockScreen = findViewById(R.id.lock_screen);
        appGrid = findViewById(R.id.app_grid);
        dockBar = findViewById(R.id.dock_bar);
        homeTime = findViewById(R.id.home_time);
        homeDate = findViewById(R.id.home_date);
        lockTime = findViewById(R.id.lock_time);
        lockDate = findViewById(R.id.lock_date);
        statusTime = findViewById(R.id.status_time);
        appContainer = findViewById(R.id.app_container);
        notificationPanel = findViewById(R.id.notification_panel);

        // Lock screen click to unlock
        lockScreen.setOnClickListener(v -> unlock());
    }

    private void setupApps() {
        String[][] apps = {
            {"📱", "Celestia Camera", "camera"},
            {"⚙️", "Settings", "settings"},
            {"📁", "Files", "files"},
            {"🔢", "Calculator", "calculator"},
            {"🕐", "Clock", "clock"},
            {"📝", "Notes", "notes"},
            {"🌐", "Browser", "browser"},
            {"🎵", "Music", "music"},
            {"🖼️", "Gallery", "gallery"},
            {"📞", "Phone", "phone"},
            {"💬", "Messages", "messages"},
            {"📧", "Email", "email"},
            {"📍", "Maps", "maps"},
            {"🎮", "Games", "games"},
            {"🔧", "Terminal", "terminal"},
            {"⭐", "GitHub Sync", "github"}
        };

        for (String[] app : apps) {
            View itemView = getLayoutInflater().inflate(R.layout.item_app, appGrid, false);
            TextView icon = itemView.findViewById(R.id.app_icon);
            TextView name = itemView.findViewById(R.id.app_name);

            icon.setText(app[0]);
            name.setText(app[1]);

            String appId = app[2];
            itemView.setOnClickListener(v -> openApp(appId));

            appGrid.addView(itemView);
        }
    }

    private void setupDock() {
        String[][] dockApps = {
            {"📱", "Camera", "camera"},
            {"⚙️", "Settings", "settings"},
            {"📞", "Phone", "phone"},
            {"💬", "Messages", "messages"}
        };

        for (String[] app : dockApps) {
            View dockItem = getLayoutInflater().inflate(R.layout.item_dock, dockBar, false);
            TextView icon = dockItem.findViewById(R.id.dock_icon);
            icon.setText(app[0]);

            String appId = app[2];
            dockItem.setOnClickListener(v -> openApp(appId));

            dockBar.addView(dockItem);
        }
    }

    private void openApp(String appId) {
        homeScreen.setVisibility(View.GONE);
        lockScreen.setVisibility(View.GONE);
        appContainer.setVisibility(View.VISIBLE);
        appContainer.removeAllViews();

        View appView;
        switch (appId) {
            case "camera":
                appView = createCameraApp();
                break;
            case "settings":
                appView = createSettingsApp();
                break;
            case "calculator":
                appView = createCalculatorApp();
                break;
            case "terminal":
                appView = createTerminalApp();
                break;
            case "github":
                appView = createGithubApp();
                break;
            case "clock":
                appView = createClockApp();
                break;
            default:
                appView = createGenericApp(appId);
                break;
        }

        appContainer.addView(appView);

        // Animate in
        TranslateAnimation slideIn = new TranslateAnimation(
            Animation.RELATIVE_TO_SELF, 1f,
            Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, 0f
        );
        slideIn.setDuration(300);
        appContainer.startAnimation(slideIn);
    }

    private View createCameraApp() {
        View view = getLayoutInflater().inflate(R.layout.app_camera, null);
        setupCameraApp(view);
        return view;
    }

    private void setupCameraApp(View view) {
        View captureBtn = view.findViewById(R.id.capture_btn);
        captureBtn.setOnClickListener(v -> {
            // Flash effect
            View flash = new View(this);
            flash.setBackgroundColor(0xFFFFFFFF);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);
            addContentView(flash, params);

            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                ((FrameLayout) flash.getParent()).removeView(flash);
                Toast.makeText(this, "📸 Photo captured!", Toast.LENGTH_SHORT).show();
            }, 200);
        });

        View backBtn = view.findViewById(R.id.camera_back);
        backBtn.setOnClickListener(v -> closeApp());
    }

    private View createSettingsApp() {
        View view = getLayoutInflater().inflate(R.layout.app_settings, null);
        setupSettingsApp(view);
        return view;
    }

    private void setupSettingsApp(View view) {
        View backBtn = view.findViewById(R.id.settings_back);
        backBtn.setOnClickListener(v -> closeApp());

        // Setup settings items
        LinearLayout settingsList = view.findViewById(R.id.settings_list);
        String[][] settings = {
            {"📶", "Network & Internet", "Wi-Fi, mobile, data usage"},
            {"🔵", "Bluetooth", "Pair device, connections"},
            {"🖥️", "Display", "Brightness, wallpaper, sleep"},
            {"🔊", "Sound & Vibration", "Volume, Do Not Disturb"},
            {"🔋", "Battery", "100% - About 12 hr left"},
            {"💾", "Storage", "32 GB total, 18 GB free"},
            {"🔒", "Privacy", "Permissions, activity controls"},
            {"📍", "Location", "On - 3 apps have access"},
            {"🛡️", "Security", "Screen lock, fingerprint"},
            {"👤", "Accounts", "Celestia Account, Google"},
            {"♿", "Accessibility", "Screen readers, display"},
            {"⚙️", "System", "Language, gestures, backup"},
            {"ℹ️", "About Celestia", "Device info, version 1.0"},
            {"⭐", "Celestia Settings", "Custom themes, cosmic UI"}
        };

        for (String[] setting : settings) {
            View item = getLayoutInflater().inflate(R.layout.item_settings, settingsList, false);
            ((TextView) item.findViewById(R.id.setting_icon)).setText(setting[0]);
            ((TextView) item.findViewById(R.id.setting_title)).setText(setting[1]);
            ((TextView) item.findViewById(R.id.setting_subtitle)).setText(setting[2]);
            settingsList.addView(item);
        }
    }

    private View createCalculatorApp() {
        View view = getLayoutInflater().inflate(R.layout.app_calculator, null);
        setupCalculator(view);
        return view;
    }

    private void setupCalculator(View view) {
        TextView display = view.findViewById(R.id.calc_display);
        View backBtn = view.findViewById(R.id.calc_back);
        backBtn.setOnClickListener(v -> closeApp());

        int[] buttons = {
            R.id.calc_c, R.id.calc_open, R.id.calc_close, R.id.calc_percent,
            R.id.calc_7, R.id.calc_8, R.id.calc_9, R.id.calc_div,
            R.id.calc_4, R.id.calc_5, R.id.calc_6, R.id.calc_mul,
            R.id.calc_1, R.id.calc_2, R.id.calc_3, R.id.calc_sub,
            R.id.calc_0, R.id.calc_dot, R.id.calc_backspace, R.id.calc_equals
        };

        String[] labels = {"C", "(", ")", "%", "7", "8", "9", "÷", "4", "5", "6", "×", "1", "2", "3", "-", "0", ".", "⌫", "="};

        for (int i = 0; i < buttons.length; i++) {
            View btn = view.findViewById(buttons[i]);
            String label = labels[i];
            btn.setOnClickListener(v -> {
                if (label.equals("C")) {
                    display.setText("0");
                } else if (label.equals("⌫")) {
                    String current = display.getText().toString();
                    if (current.length() > 1) {
                        display.setText(current.substring(0, current.length() - 1));
                    } else {
                        display.setText("0");
                    }
                } else if (label.equals("=")) {
                    try {
                        String expr = display.getText().toString()
                            .replace("×", "*").replace("÷", "/");
                        double result = eval(expr);
                        display.setText(String.valueOf(result));
                    } catch (Exception e) {
                        display.setText("Error");
                    }
                } else {
                    if (display.getText().toString().equals("0")) {
                        display.setText(label);
                    } else {
                        display.append(label);
                    }
                }
            });
        }
    }

    private double eval(String expr) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = ++pos < expr.length() ? expr.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) { nextChar(); return true; }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                return x;
            }

            double parseExpression() {
                double x = parseTerm();
                for (;;) {
                    if (eat('+')) x += parseTerm();
                    else if (eat('-')) x -= parseTerm();
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (;;) {
                    if (eat('×')) x *= parseFactor();
                    else if (eat('÷')) x /= parseFactor();
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return +parseFactor();
                if (eat('-')) return -parseFactor();

                double x;
                int startPos = this.pos;
                if (eat('(')) {
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') {
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(expr.substring(startPos, this.pos));
                } else {
                    throw new RuntimeException("Unexpected: " + (char)ch);
                }
                return x;
            }
        }.parse();
    }

    private View createTerminalApp() {
        View view = getLayoutInflater().inflate(R.layout.app_terminal, null);
        setupTerminal(view);
        return view;
    }

    private void setupTerminal(View view) {
        TextView terminalOutput = view.findViewById(R.id.terminal_output);
        EditText terminalInput = view.findViewById(R.id.terminal_input);
        View sendBtn = view.findViewById(R.id.terminal_send);
        View backBtn = view.findViewById(R.id.terminal_back);

        backBtn.setOnClickListener(v -> closeApp());

        StringBuilder output = new StringBuilder();
        output.append("Celestia OS Terminal v1.0\n");
        output.append("Type 'help' for commands\n");
        output.append("$ ");
        terminalOutput.setText(output.toString());

        sendBtn.setOnClickListener(v -> {
            String input = terminalInput.getText().toString().trim();
            if (!input.isEmpty()) {
                output.append(input + "\n");
                String result = processCommand(input);
                output.append(result + "\n$ ");
                terminalOutput.setText(output.toString());
                terminalInput.setText("");
            }
        });
    }

    private String processCommand(String cmd) {
        switch (cmd.toLowerCase()) {
            case "help":
                return "Available commands:\n  help - Show this help\n  uname - System info\n  date - Current date/time\n  ls - List files\n  whoami - Current user\n  clear - Clear terminal\n  neofetch - System info\n  echo - Echo text";
            case "uname":
                return "CelestiaOS 1.0 Nebula (Linux 5.15.0-celestia x86_64)";
            case "date":
                return new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US).format(new Date());
            case "ls":
                return "bin  boot  dev  etc  home  lib  media  mnt  opt  proc  root  sbin  sys  tmp  usr  var";
            case "whoami":
                return "celestia";
            case "clear":
                return "\n";
            case "neofetch":
                return "       ___           celestia@virtual\n      /   \\          OS: Celestia OS 1.0 Nebula\n     / * * \\         Host: Celestia Virtual Device\n    /  ___  \\        Kernel: 5.15.0-celestia\n   / /   \\ \\ \\       Shell: sh\n  /_/     \\_\\_\\      Terminal: Celestia Terminal\n                      Memory: 4096MB / 8192MB\n                      CPU: Virtual ARM64";
            default:
                if (cmd.startsWith("echo ")) {
                    return cmd.substring(5);
                }
                return "command not found: " + cmd;
        }
    }

    private View createGithubApp() {
        View view = getLayoutInflater().inflate(R.layout.app_github, null);
        setupGithubApp(view);
        return view;
    }

    private void setupGithubApp(View view) {
        View backBtn = view.findViewById(R.id.github_back);
        View syncBtn = view.findViewById(R.id.github_sync);
        TextView syncStatus = view.findViewById(R.id.github_status);

        backBtn.setOnClickListener(v -> closeApp());

        syncBtn.setOnClickListener(v -> {
            syncBtn.setEnabled(false);
            syncStatus.setText("🔄 Connecting to GitHub...");

            executor.execute(() -> {
                try {
                    Thread.sleep(1500);
                    mainHandler.post(() -> syncStatus.setText("⬇️ Downloading updates..."));

                    Thread.sleep(2000);
                    mainHandler.post(() -> syncStatus.setText("📦 Extracting files..."));

                    Thread.sleep(1000);
                    mainHandler.post(() -> {
                        syncStatus.setText("✅ Sync complete! Celestia OS updated.");
                        syncBtn.setEnabled(true);
                        Toast.makeText(this, "🎉 System updated!", Toast.LENGTH_SHORT).show();
                    });
                } catch (InterruptedException e) {
                    mainHandler.post(() -> {
                        syncStatus.setText("❌ Sync failed");
                        syncBtn.setEnabled(true);
                    });
                }
            });
        });
    }

    private View createClockApp() {
        View view = getLayoutInflater().inflate(R.layout.app_clock, null);
        TextView clockBig = view.findViewById(R.id.clock_big);
        View backBtn = view.findViewById(R.id.clock_back);

        backBtn.setOnClickListener(v -> closeApp());

        timeHandler.post(new Runnable() {
            @Override
            public void run() {
                clockBig.setText(new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date()));
                timeHandler.postDelayed(this, 1000);
            }
        });

        return view;
    }

    private View createGenericApp(String appId) {
        View view = getLayoutInflater().inflate(R.layout.app_generic, null);
        TextView title = view.findViewById(R.id.generic_title);
        TextView icon = view.findViewById(R.id.generic_icon);
        View backBtn = view.findViewById(R.id.generic_back);

        backBtn.setOnClickListener(v -> closeApp());

        String[][] appInfo = {
            {"files", "📁", "Files"},
            {"notes", "📝", "Notes"},
            {"browser", "🌐", "Browser"},
            {"music", "🎵", "Music"},
            {"gallery", "🖼️", "Gallery"},
            {"phone", "📞", "Phone"},
            {"messages", "💬", "Messages"},
            {"email", "📧", "Email"},
            {"maps", "📍", "Maps"},
            {"games", "🎮", "Games"}
        };

        for (String[] info : appInfo) {
            if (info[0].equals(appId)) {
                icon.setText(info[1]);
                title.setText(info[2]);
                break;
            }
        }

        return view;
    }

    private void closeApp() {
        appContainer.setVisibility(View.GONE);
        appContainer.removeAllViews();
        homeScreen.setVisibility(View.VISIBLE);

        TranslateAnimation slideIn = new TranslateAnimation(
            Animation.RELATIVE_TO_SELF, -0.3f,
            Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, 0f
        );
        slideIn.setDuration(200);
        homeScreen.startAnimation(slideIn);
    }

    private void unlock() {
        lockScreen.setVisibility(View.GONE);
        homeScreen.setVisibility(View.VISIBLE);
        isLocked = false;

        TranslateAnimation slideUp = new TranslateAnimation(
            Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, 1f,
            Animation.RELATIVE_TO_SELF, 0f
        );
        slideUp.setDuration(300);
        homeScreen.startAnimation(slideUp);
    }

    private void setupGestures() {
        // Swipe down for notifications
        final float[] startY = new float[1];
        homeScreen.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case android.view.MotionEvent.ACTION_DOWN:
                    startY[0] = event.getRawY();
                    break;
                case android.view.MotionEvent.ACTION_UP:
                    float diff = event.getRawY() - startY[0];
                    if (diff > 100 && startY[0] < 200) {
                        toggleNotificationPanel();
                    }
                    break;
            }
            return false;
        });
    }

    private void toggleNotificationPanel() {
        if (panelOpen) {
            notificationPanel.setVisibility(View.GONE);
            panelOpen = false;
        } else {
            notificationPanel.setVisibility(View.VISIBLE);
            panelOpen = true;
        }
    }

    private void startTime() {
        timeHandler = new Handler(Looper.getMainLooper());
        final Runnable timeRunnable = new Runnable() {
            @Override
            public void run() {
                String time = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
                String fullDate = new SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()).format(new Date());

                homeTime.setText(time);
                homeDate.setText(fullDate);
                lockTime.setText(time);
                lockDate.setText(fullDate);
                statusTime.setText(time);

                timeHandler.postDelayed(this, 1000);
            }
        };
        timeRunnable.run();
    }

    @Override
    public void onBackPressed() {
        if (appContainer.getVisibility() == View.VISIBLE) {
            closeApp();
        } else if (panelOpen) {
            toggleNotificationPanel();
        } else if (!isLocked) {
            // Show recent apps or do nothing
        } else {
            super.onBackPressed();
        }
    }
}
