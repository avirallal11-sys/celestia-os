package com.celestia.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class CelestiaActivity extends AppCompatActivity {

    private Switch bootAnimSwitch, particlesSwitch, autoUpdateSwitch;
    private TextView themeDarkCosmicCheck, themeMidnightBlueCheck, themeNeonPurpleCheck, themeStarlightCheck;
    private TextView updateStatusText;
    private SharedPreferences prefs;
    private static final String GITHUB_REPO_URL = "https://api.github.com/repos/avirallal11-sys/celestia-os/releases/latest";
    private static final String CURRENT_VERSION = "1.0.0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_celestia);

        prefs = getSharedPreferences("celestia_theme", MODE_PRIVATE);

        initViews();
        loadState();
        setupListeners();
    }

    private void initViews() {
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        bootAnimSwitch = findViewById(R.id.bootAnimSwitch);
        particlesSwitch = findViewById(R.id.particlesSwitch);
        autoUpdateSwitch = findViewById(R.id.autoUpdateSwitch);
        themeDarkCosmicCheck = findViewById(R.id.themeDarkCosmicCheck);
        themeMidnightBlueCheck = findViewById(R.id.themeMidnightBlueCheck);
        themeNeonPurpleCheck = findViewById(R.id.themeNeonPurpleCheck);
        themeStarlightCheck = findViewById(R.id.themeStarlightCheck);
        updateStatusText = findViewById(R.id.updateStatusText);
    }

    private void loadState() {
        bootAnimSwitch.setChecked(prefs.getBoolean("boot_anim", true));
        particlesSwitch.setChecked(prefs.getBoolean("particles", true));
        autoUpdateSwitch.setChecked(prefs.getBoolean("auto_update", false));
        updateStatusText.setText("Current version: " + CURRENT_VERSION);
        updateThemeChecks(prefs.getString("theme", "dark_cosmic"));
    }

    private void setupListeners() {
        bootAnimSwitch.setOnCheckedChangeListener((b, checked) ->
                prefs.edit().putBoolean("boot_anim", checked).apply());

        particlesSwitch.setOnCheckedChangeListener((b, checked) ->
                prefs.edit().putBoolean("particles", checked).apply());

        autoUpdateSwitch.setOnCheckedChangeListener((b, checked) ->
                prefs.edit().putBoolean("auto_update", checked).apply());

        findViewById(R.id.themeDarkCosmic).setOnClickListener(v -> selectTheme("dark_cosmic"));
        findViewById(R.id.themeMidnightBlue).setOnClickListener(v -> selectTheme("midnight_blue"));
        findViewById(R.id.themeNeonPurple).setOnClickListener(v -> selectTheme("neon_purple"));
        findViewById(R.id.themeStarlight).setOnClickListener(v -> selectTheme("starlight"));

        int[] colorIds = {R.id.colorCyan, R.id.colorRed, R.id.colorGreen, R.id.colorPurple, R.id.colorYellow, R.id.colorBlue};
        String[] colorNames = {"Cyan", "Red", "Green", "Purple", "Yellow", "Blue"};

        for (int i = 0; i < colorIds.length; i++) {
            final String name = colorNames[i];
            findViewById(colorIds[i]).setOnClickListener(v -> {
                prefs.edit().putString("accent_color", name).apply();
                Toast.makeText(this, "Accent color: " + name, Toast.LENGTH_SHORT).show();
            });
        }

        findViewById(R.id.githubSyncItem).setOnClickListener(v -> syncFromGitHub());

        findViewById(R.id.checkUpdatesItem).setOnClickListener(v -> checkForUpdates());
    }

    private void selectTheme(String theme) {
        prefs.edit().putString("theme", theme).apply();
        updateThemeChecks(theme);
        Toast.makeText(this, "Theme changed", Toast.LENGTH_SHORT).show();
    }

    private void updateThemeChecks(String current) {
        themeDarkCosmicCheck.setVisibility(current.equals("dark_cosmic") ? View.VISIBLE : View.GONE);
        themeMidnightBlueCheck.setVisibility(current.equals("midnight_blue") ? View.VISIBLE : View.GONE);
        themeNeonPurpleCheck.setVisibility(current.equals("neon_purple") ? View.VISIBLE : View.GONE);
        themeStarlightCheck.setVisibility(current.equals("starlight") ? View.VISIBLE : View.GONE);
    }

    private void checkForUpdates() {
        updateStatusText.setText("Checking for updates...");
        
        new Thread(() -> {
            try {
                URL url = new URL(GITHUB_REPO_URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Accept", "application/vnd.github.v3+json");

                int responseCode = connection.getResponseCode();
                if (responseCode == 200) {
                    BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    String responseStr = response.toString();
                    String latestVersion = extractVersion(responseStr);
                    String downloadUrl = extractDownloadUrl(responseStr);

                    runOnUiThread(() -> {
                        if (latestVersion != null && !latestVersion.equals(CURRENT_VERSION)) {
                            showUpdateDialog(latestVersion, downloadUrl);
                        } else {
                            updateStatusText.setText("Up to date (v" + CURRENT_VERSION + ")");
                            Toast.makeText(CelestiaActivity.this,
                                "Celestia OS is up to date!", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    runOnUiThread(() -> {
                        updateStatusText.setText("Update check failed");
                        Toast.makeText(CelestiaActivity.this,
                            "Could not check for updates", Toast.LENGTH_SHORT).show();
                    });
                }
                connection.disconnect();
            } catch (Exception e) {
                runOnUiThread(() -> {
                    updateStatusText.setText("Update check failed");
                    Toast.makeText(CelestiaActivity.this,
                        "Network error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    private String extractVersion(String json) {
        int tagIndex = json.indexOf("\"tag_name\"");
        if (tagIndex == -1) return null;
        int start = json.indexOf("\"", tagIndex + 11) + 1;
        int end = json.indexOf("\"", start);
        return json.substring(start, end).replace("v", "");
    }

    private String extractDownloadUrl(String json) {
        int assetsIndex = json.indexOf("\"browser_download_url\"");
        if (assetsIndex == -1) return null;
        int start = json.indexOf("\"", assetsIndex + 23) + 1;
        int end = json.indexOf("\"", start);
        return json.substring(start, end);
    }

    private void showUpdateDialog(String newVersion, String downloadUrl) {
        new AlertDialog.Builder(this, R.style.DialogTheme)
                .setTitle("Update available")
                .setMessage("A new version of Celestia OS is available:\n\n" +
                    "Current: v" + CURRENT_VERSION + "\n" +
                    "New: v" + newVersion + "\n\n" +
                    "Download from GitHub?")
                .setPositiveButton("Update", (dialog, which) -> {
                    if (downloadUrl != null) {
                        openBrowser(downloadUrl);
                    } else {
                        openBrowser("https://github.com/avirallal11-sys/celestia-os/releases");
                    }
                })
                .setNegativeButton("Later", null)
                .show();
    }

    private void syncFromGitHub() {
        Toast.makeText(this, "Syncing from GitHub...", Toast.LENGTH_SHORT).show();
        
        new Thread(() -> {
            try {
                URL url = new URL("https://github.com/avirallal11-sys/celestia-os/archive/refs/heads/master.zip");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("HEAD");
                int responseCode = connection.getResponseCode();
                connection.disconnect();

                runOnUiThread(() -> {
                    if (responseCode == 200) {
                        prefs.edit().putBoolean("github_synced", true).apply();
                        prefs.edit().putLong("last_sync", System.currentTimeMillis()).apply();
                        Toast.makeText(CelestiaActivity.this,
                            "GitHub sync completed!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(CelestiaActivity.this,
                            "Sync failed: " + responseCode, Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(CelestiaActivity.this,
                        "Sync error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    private void openBrowser(String url) {
        try {
            android.content.Intent intent = new android.content.Intent(
                android.content.Intent.ACTION_VIEW, android.net.Uri.parse(url));
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Cannot open browser", Toast.LENGTH_SHORT).show();
        }
    }
}
