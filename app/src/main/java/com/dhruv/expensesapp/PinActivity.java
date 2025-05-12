package com.dhruv.expensesapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class PinActivity extends AppCompatActivity {
    private EditText etPin;
    private Button btnSubmit;
    private TextView tvMessage;
    private SharedPreferences prefs;
    private boolean isSettingPin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin);

        // Enable ActionBar back button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("PIN Authentication");
        }

        etPin = findViewById(R.id.etPin);
        btnSubmit = findViewById(R.id.btnSubmit);
        tvMessage = findViewById(R.id.tvMessage);
        prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);

        isSettingPin = prefs.getString("pin", null) == null;

        if (isSettingPin) {
            tvMessage.setText("Set a 4-digit PIN");
        } else {
            tvMessage.setText("Enter your PIN");
        }

        btnSubmit.setOnClickListener(v -> verifyOrSetPin());
    }

    private void verifyOrSetPin() {
        String pin = etPin.getText().toString().trim();
        if (pin.length() != 4 || !pin.matches("\\d+")) {
            etPin.setError("Enter a 4-digit PIN");
            return;
        }

        if (isSettingPin) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("pin", pin);
            editor.apply();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            String savedPin = prefs.getString("pin", null);
            if (pin.equals(savedPin)) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                etPin.setError("Incorrect PIN");
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}