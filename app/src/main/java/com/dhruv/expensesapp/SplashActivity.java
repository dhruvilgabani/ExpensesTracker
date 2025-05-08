package com.dhruv.expensesapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
    private static final String TAG = "SplashActivity";
    private static final int SPLASH_DURATION = 3000; // 3 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_splash);
        } catch (Exception e) {
            Log.e(TAG, "Error setting splash screen layout", e);
            Toast.makeText(this, "Failed to load splash screen", Toast.LENGTH_LONG).show();
            proceedToMainActivity();
            return;
        }

        // Delay for 3 seconds, then start MainActivity
        new Handler(Looper.getMainLooper()).postDelayed(this::proceedToMainActivity, SPLASH_DURATION);
    }

    private void proceedToMainActivity() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        finish(); // Close SplashActivity to prevent back navigation
    }
}