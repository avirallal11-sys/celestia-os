package com.celestia.emulator;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class BootActivity extends AppCompatActivity {

    private ProgressBar bootProgress;
    private TextView bootText;
    private TextView bootVersion;
    private ImageView bootLogo;
    private int progress = 0;

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

        setContentView(R.layout.activity_boot);

        bootProgress = findViewById(R.id.boot_progress);
        bootText = findViewById(R.id.boot_text);
        bootVersion = findViewById(R.id.boot_version);
        bootLogo = findViewById(R.id.boot_logo);

        startBootSequence();
    }

    private void startBootSequence() {
        // Animate logo
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setDuration(1000);
        bootLogo.startAnimation(fadeIn);

        // Animate text
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Animation textFade = new AlphaAnimation(0, 1);
            textFade.setDuration(500);
            bootText.startAnimation(textFade);
            bootVersion.startAnimation(textFade);
        }, 500);

        // Progress bar animation
        final Handler handler = new Handler(Looper.getMainLooper());
        final Runnable progressRunnable = new Runnable() {
            @Override
            public void run() {
                if (progress < 100) {
                    progress += 2;
                    bootProgress.setProgress(progress);
                    handler.postDelayed(this, 50);
                } else {
                    // Boot complete, launch emulator
                    handler.postDelayed(() -> {
                        Intent intent = new Intent(BootActivity.this, EmulatorActivity.class);
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        finish();
                    }, 500);
                }
            }
        };
        handler.postDelayed(progressRunnable, 1000);
    }
}
