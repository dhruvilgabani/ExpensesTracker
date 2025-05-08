package com.dhruv.expensesapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class ExpenseDetailActivity extends AppCompatActivity {
    private EditText editTextAmount, editTextCategory, editTextDescription;
    private MainActivity.Expense expense;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_detail);

        // Initialize views
        editTextDescription = findViewById(R.id.editTextDescription);
        editTextAmount = findViewById(R.id.editTextAmount);
        editTextCategory = findViewById(R.id.editTextCategory);
        Button saveButton = findViewById(R.id.saveButton);
        Button deleteButton = findViewById(R.id.deleteButton);

        // Initialize database
        dbHelper = new DatabaseHelper(this);

        // Get expense from intent
        expense = getIntent().getParcelableExtra("expense");
        if (expense != null) {
            editTextDescription.setText(expense.getDescription());
            editTextAmount.setText(String.valueOf(expense.getAmount()));
            editTextCategory.setText(expense.getCategory());
        }

        // Save button listener
        saveButton.setOnClickListener(v -> saveExpense());

        // Delete button listener
        deleteButton.setOnClickListener(v -> deleteExpense());
    }

    private void saveExpense() {
        String description = editTextDescription.getText().toString();
        String amountStr = editTextAmount.getText().toString();
        String category = editTextCategory.getText().toString();

        if (!description.isEmpty() && !amountStr.isEmpty() && !category.isEmpty()) {
            double amount = Double.parseDouble(amountStr);
            MainActivity.Expense updatedExpense = new MainActivity.Expense(
                    expense.getId(),
                    description,
                    amount,
                    expense.getDate(),
                    category
            );

            // Update database
            dbHelper.updateExpense(updatedExpense);

            // Return updated expense
            Intent resultIntent = new Intent();
            resultIntent.putExtra("updatedExpense", updatedExpense);
            setResult(RESULT_OK, resultIntent);
            finish();
        }
    }

    private void deleteExpense() {
        if (expense != null) {
            // Delete from database
            dbHelper.deleteExpense(expense.getId());

            // Signal deletion
            Intent resultIntent = new Intent();
            resultIntent.putExtra("deletedExpenseId", expense.getId());
            setResult(RESULT_OK, resultIntent);
            finish();
        }
    }
}