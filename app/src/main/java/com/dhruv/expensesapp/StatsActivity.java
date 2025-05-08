package com.dhruv.expensesapp;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StatsActivity extends AppCompatActivity {
    private TextView totalExpensesText;
    private TextView categoryStatsText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        // Enable ActionBar back button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Expense Statistics");
        }

        // Initialize views
        totalExpensesText = findViewById(R.id.totalExpensesText);
        categoryStatsText = findViewById(R.id.categoryStatsText);

        // Get expense list
        ArrayList<MainActivity.Expense> expenseList = getIntent().getParcelableArrayListExtra("expenseList");
        if (expenseList != null) {
            displayStats(expenseList);
        }
    }

    private void displayStats(ArrayList<MainActivity.Expense> expenseList) {
        // Calculate total
        double total = 0;
        Map<String, Double> categoryTotals = new HashMap<>();
        for (MainActivity.Expense expense : expenseList) {
            total += expense.getAmount();
            categoryTotals.put(expense.getCategory(),
                    categoryTotals.getOrDefault(expense.getCategory(), 0.0) + expense.getAmount());
        }

        // Display total
        totalExpensesText.setText(String.format("Total Expenses: $%.2f", total));

        // Display category stats
        StringBuilder categoryStats = new StringBuilder("Expenses by Category:\n");
        for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
            categoryStats.append(String.format("%s: $%.2f\n", entry.getKey(), entry.getValue()));
        }
        categoryStatsText.setText(categoryStats.toString());
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish(); // Close the activity and return to MainActivity
        return true;
    }
}