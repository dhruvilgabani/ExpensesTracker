package com.dhruv.expensesapp;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class StatsActivity extends AppCompatActivity {
    private TextView tvIncome, tvExpenses, tvBalance;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        // Enable ActionBar back button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Statistics");
        }

        tvIncome = findViewById(R.id.tvIncome);
        tvExpenses = findViewById(R.id.tvExpenses);
        tvBalance = findViewById(R.id.tvBalance);
        dbHelper = new DatabaseHelper(this);

        updateStats();
    }

    private void updateStats() {
        double totalIncome = dbHelper.getTotalIncome();
        double totalExpenses = dbHelper.getTotalExpenses();
        double balance = totalIncome - totalExpenses;

        tvIncome.setText(String.format("Total Income: $%.2f", totalIncome));
        tvExpenses.setText(String.format("Total Expenses: $%.2f", totalExpenses));
        tvBalance.setText(String.format("Balance: $%.2f", balance));
        tvBalance.setTextColor(balance >= 0 ? getResources().getColor(android.R.color.holo_green_dark) : getResources().getColor(android.R.color.holo_red_dark));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}