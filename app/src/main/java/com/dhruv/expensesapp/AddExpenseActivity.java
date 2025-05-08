package com.dhruv.expensesapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddExpenseActivity extends AppCompatActivity {
    private EditText editTextDescription;
    private EditText editTextAmount;
    private EditText editTextCategory;
    private EditText editTextDate;
    private MainActivity.Expense existingExpense;
    private boolean isEditing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        // Enable ActionBar back button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Add Expense");
        }

        // Initialize views
        editTextDescription = findViewById(R.id.editTextDescription);
        editTextAmount = findViewById(R.id.editTextAmount);
        editTextCategory = findViewById(R.id.editTextCategory);
        editTextDate = findViewById(R.id.editTextDate);
        Button saveExpenseButton = findViewById(R.id.saveExpenseButton);

        // Check if editing an existing expense
        Intent intent = getIntent();
        if (intent.hasExtra("expense")) {
            existingExpense = intent.getParcelableExtra("expense");
            isEditing = true;
            if (existingExpense != null) {
                editTextDescription.setText(existingExpense.getDescription());
                editTextAmount.setText(String.format(Locale.US, "%.2f", existingExpense.getAmount()));
                editTextCategory.setText(existingExpense.getCategory());
                editTextDate.setText(existingExpense.getDate());
                getSupportActionBar().setTitle("Edit Expense");
                saveExpenseButton.setText("Update Expense");
            }
        } else {
            // Set default date for new expense
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            editTextDate.setText(sdf.format(new Date()));
        }

        // Save button listener
        saveExpenseButton.setOnClickListener(v -> saveExpense());
    }

    private void saveExpense() {
        String description = editTextDescription.getText().toString().trim();
        String amountStr = editTextAmount.getText().toString().trim();
        String category = editTextCategory.getText().toString().trim();
        String date = editTextDate.getText().toString().trim();

        // Validate inputs
        if (description.isEmpty()) {
            Toast.makeText(this, "Please enter a description", Toast.LENGTH_SHORT).show();
            return;
        }
        if (amountStr.isEmpty()) {
            Toast.makeText(this, "Please enter an amount", Toast.LENGTH_SHORT).show();
            return;
        }
        if (category.isEmpty()) {
            Toast.makeText(this, "Please enter a category", Toast.LENGTH_SHORT).show();
            return;
        }
        if (date.isEmpty()) {
            Toast.makeText(this, "Please enter a date", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                Toast.makeText(this, "Amount must be positive", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create or update expense
            MainActivity.Expense expense;
            int id = isEditing ? existingExpense.getId() : (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
            expense = new MainActivity.Expense(id, description, amount, date, category);

            // Return result
            Intent result = new Intent();
            if (isEditing) {
                result.putExtra("updatedExpense", expense);
            } else {
                result.putExtra("newExpense", expense);
            }
            setResult(RESULT_OK, result);
            finish();
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid amount format", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}