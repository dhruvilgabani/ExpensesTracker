package com.dhruv.expensesapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class ExpenseDetailActivity extends AppCompatActivity {
    private ActivityResultLauncher<Intent> editExpenseLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_detail);

        // Enable ActionBar back button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Expense Details");
        }

        // Initialize ActivityResultLauncher for editing
        editExpenseLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        MainActivity.Expense updatedExpense = result.getData().getParcelableExtra("updatedExpense");
                        int position = getIntent().getIntExtra("position", -1);
                        if (updatedExpense != null && position != -1) {
                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("action", "edit");
                            resultIntent.putExtra("updatedExpense", updatedExpense);
                            resultIntent.putExtra("position", position);
                            setResult(RESULT_OK, resultIntent);
                            finish();
                        }
                    }
                });

        // Get expense and position from intent
        MainActivity.Expense expense = getIntent().getParcelableExtra("expense");
        int position = getIntent().getIntExtra("position", -1);

        // Initialize views
        TextView descriptionText = findViewById(R.id.detailDescriptionText);
        TextView amountText = findViewById(R.id.detailAmountText);
        TextView categoryText = findViewById(R.id.detailCategoryText);
        TextView dateText = findViewById(R.id.detailDateText);
        Button editButton = findViewById(R.id.editButton);
        Button deleteButton = findViewById(R.id.deleteButton);

        // Display expense details
        if (expense != null) {
            descriptionText.setText(expense.getDescription());
            amountText.setText(String.format("$%.2f", expense.getAmount()));
            categoryText.setText(expense.getCategory());
            dateText.setText(expense.getDate());
        }

        // Edit button listener
        editButton.setOnClickListener(v -> {
            Intent intent = new Intent(ExpenseDetailActivity.this, AddExpenseActivity.class);
            intent.putExtra("expense", expense);
            editExpenseLauncher.launch(intent);
        });

        // Delete button listener
        deleteButton.setOnClickListener(v -> {
            Intent result = new Intent();
            result.putExtra("action", "delete");
            result.putExtra("position", position);
            setResult(RESULT_OK, result);
            finish();
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}