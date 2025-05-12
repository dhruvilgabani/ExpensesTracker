package com.dhruv.expensesapp;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AddExpenseActivity extends AppCompatActivity {
    private EditText etDescription, etAmount, etNotes, etReminderDate, etDate;
    private RadioGroup rgType;
    private RadioButton rbIncome, rbExpense;
    private Spinner spinnerCategory, spinnerInterval;
    private CheckBox cbRecurring;
    private Button btnSave;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Add Transaction");
        }

        etDescription = findViewById(R.id.etDescription);
        etDate = findViewById(R.id.etDate);
        etAmount = findViewById(R.id.etAmount);
        etNotes = findViewById(R.id.etNotes);
        etReminderDate = findViewById(R.id.etReminderDate);
        rgType = findViewById(R.id.rgType);
        rbIncome = findViewById(R.id.rbIncome);
        rbExpense = findViewById(R.id.rbExpense);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        cbRecurring = findViewById(R.id.cbRecurring);
        spinnerInterval = findViewById(R.id.spinnerInterval);
        btnSave = findViewById(R.id.btnSave);
        dbHelper = new DatabaseHelper(this);

        // Setup category spinner
        List<String> categories = dbHelper.getAllCategories();
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);

        // Setup interval spinner
        String[] intervals = {"Daily", "Weekly", "Monthly"};
        ArrayAdapter<String> intervalAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, intervals);
        intervalAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerInterval.setAdapter(intervalAdapter);

        // Setup transaction date picker
        Calendar calendar = Calendar.getInstance();
        etDate.setText(String.format(Locale.getDefault(), "%d-%02d-%02d",
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH)));
        etDate.setOnClickListener(v -> {
            DatePickerDialog dialog = new DatePickerDialog(this, (view, year, month, day) -> {
                etDate.setText(String.format(Locale.getDefault(), "%d-%02d-%02d", year, month + 1, day));
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            dialog.show();
        });

        // Setup reminder date picker
        etReminderDate.setOnClickListener(v -> {
            DatePickerDialog dialog = new DatePickerDialog(this, (view, year, month, day) -> {
                etReminderDate.setText(String.format(Locale.getDefault(), "%d-%02d-%02d", year, month + 1, day));
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            dialog.show();
        });

        btnSave.setOnClickListener(v -> saveEntry());
    }

    private void saveEntry() {
        String description = etDescription.getText().toString().trim();
        String date = etDate.getText().toString().trim();
        String amountStr = etAmount.getText().toString().trim();
        String notes = etNotes.getText().toString().trim();
        String type = rbIncome.isChecked() ? "Income" : "Expense";
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
            Expense expense = new Expense(0, description, amount, date, type, category, notes, isRecurring, interval, reminderDate);
            dbHelper.addExpense(expense);

            // Schedule reminder if set
            if (!reminderDate.isEmpty()) {
                ReminderReceiver.scheduleReminder(this, expense);
            }

            finish();
        } catch (NumberFormatException e) {
            etAmount.setError("Invalid amount");
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}