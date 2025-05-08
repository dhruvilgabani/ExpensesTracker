package com.dhruv.expensesapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddExpenseActivity extends AppCompatActivity {
    private EditText editTextAmount, editTextCategory;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        // Initialize views
        editTextAmount = findViewById(R.id.editTextAmount);
        editTextCategory = findViewById(R.id.editTextCategory);
        Button saveExpenseButton = findViewById(R.id.saveExpenseButton);

        // Initialize database
        dbHelper = new DatabaseHelper(this);

        // Set up save button listener
        saveExpenseButton.setOnClickListener(v -> saveExpense());
    }

    private void saveExpense() {
        String amountStr = editTextAmount.getText().toString();
        String category = editTextCategory.getText().toString();

        if (!amountStr.isEmpty() && !category.isEmpty()) {
            double amount = Double.parseDouble(amountStr);
            String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            String description = "Expense on " + date; // Simple description

            MainActivity.Expense expense = new MainActivity.Expense(0, description, amount, date, category);
            dbHelper.addExpense(expense);

            setResult(RESULT_OK);
            finish();
        }
    }
}