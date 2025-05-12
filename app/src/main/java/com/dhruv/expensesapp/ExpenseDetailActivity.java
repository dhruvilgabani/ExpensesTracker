package com.dhruv.expensesapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ExpenseDetailActivity extends AppCompatActivity {
    private EditText etDate, etDescription, etAmount, etNotes, etReminderDate;
    private Spinner spinnerCategory, spinnerInterval;
    private CheckBox cbRecurring;
    private Button btnSave, btnDelete;
    private DatabaseHelper dbHelper;
    private Expense expense;
    private int expenseId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_detail);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Transaction Details");
        }

        etDate = findViewById(R.id.etDate);
        etDescription = findViewById(R.id.etDescription);
        etAmount = findViewById(R.id.etAmount);
        etNotes = findViewById(R.id.etNotes);
        etReminderDate = findViewById(R.id.etReminderDate);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        cbRecurring = findViewById(R.id.cbRecurring);
        spinnerInterval = findViewById(R.id.spinnerInterval);
        btnSave = findViewById(R.id.btnSave);
        btnDelete = findViewById(R.id.btnDelete);
        dbHelper = new DatabaseHelper(this);

        expenseId = getIntent().getIntExtra("expense_id", -1);
        if (expenseId == -1) {
            finish();
            return;
        }

        expense = dbHelper.getExpenseById(expenseId);
        if (expense == null) {
            finish();
            return;
        }

        etDate.setText(expense.getDate());
        etDescription.setText(expense.getDescription());
        etAmount.setText(String.format(Locale.getDefault(), "%.2f", expense.getAmount()));
        etNotes.setText(expense.getNotes());
        etReminderDate.setText(expense.getReminderDate());
        cbRecurring.setChecked(expense.isRecurring());

        List<String> categories = dbHelper.getAllCategories();
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);
        for (int i = 0; i < categories.size(); i++) {
            if (categories.get(i).equals(expense.getCategory())) {
                spinnerCategory.setSelection(i);
                break;
            }
        }

        String[] intervals = {"Daily", "Weekly", "Monthly"};
        ArrayAdapter<String> intervalAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, intervals);
        intervalAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerInterval.setAdapter(intervalAdapter);
        for (int i = 0; i < intervals.length; i++) {
            if (intervals[i].equals(expense.getRecurringInterval())) {
                spinnerInterval.setSelection(i);
                break;
            }
        }

        // Setup date pickers
        Calendar calendar = Calendar.getInstance();
        etDate.setOnClickListener(v -> {
            DatePickerDialog dialog = new DatePickerDialog(this, (view, year, month, day) -> {
                etDate.setText(String.format(Locale.getDefault(), "%d-%02d-%02d", year, month + 1, day));
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            dialog.show();
        });

        etReminderDate.setOnClickListener(v -> {
            DatePickerDialog dialog = new DatePickerDialog(this, (view, year, month, day) -> {
                etReminderDate.setText(String.format(Locale.getDefault(), "%d-%02d-%02d", year, month + 1, day));
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            dialog.show();
        });

        btnSave.setOnClickListener(v -> saveChanges());
        btnDelete.setOnClickListener(v -> deleteExpense());
    }

    private void saveChanges() {
        String date = etDate.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String amountStr = etAmount.getText().toString().trim();
        String notes = etNotes.getText().toString().trim();
        String category = spinnerCategory.getSelectedItem().toString();
        boolean isRecurring = cbRecurring.isChecked();
        String interval = isRecurring ? spinnerInterval.getSelectedItem().toString() : null;
        String reminderDate = etReminderDate.getText().toString().trim();

        if (description.isEmpty() || date.isEmpty() || amountStr.isEmpty()) {
            etDescription.setError(description.isEmpty() ? "Required" : null);
            etDate.setError(date.isEmpty() ? "Required" : null);
            etAmount.setError(amountStr.isEmpty() ? "Required" : null);
            return;
        }

        try {
            double amount = Double.parseDouble(amountStr);
            String type = expense.getType();

            Expense updatedExpense = new Expense(expenseId, description, amount, date, type, category, notes, isRecurring, interval, reminderDate);
            dbHelper.updateExpense(updatedExpense);

            if (!reminderDate.isEmpty()) {
                ReminderReceiver.scheduleReminder(this, updatedExpense);
            }

            setResult(RESULT_OK);
            finish();
        } catch (NumberFormatException e) {
            etAmount.setError("Invalid amount");
        }
    }

    private void deleteExpense() {
        dbHelper.deleteExpense(expenseId);
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}