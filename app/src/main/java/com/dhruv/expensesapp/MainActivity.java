package com.dhruv.expensesapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ListView expenseListView;
    private ExpenseAdapter expenseAdapter;
    private List<Expense> expenseList;
    private static final String EXPENSES_FILE = "expenses.json";
    private ActivityResultLauncher<Intent> addExpenseLauncher;
    private ActivityResultLauncher<Intent> editExpenseLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_main);
        } catch (Exception e) {
            Log.e(TAG, "Error setting content view", e);
            Toast.makeText(this, "Failed to load UI", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Initialize ActivityResultLaunchers
        addExpenseLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Expense newExpense = result.getData().getParcelableExtra("newExpense");
                        if (newExpense != null) {
                            expenseList.add(newExpense);
                            if (expenseAdapter != null) {
                                expenseAdapter.notifyDataSetChanged();
                            }
                            saveExpenseData();
                        }
                    }
                });

        editExpenseLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        String action = result.getData().getStringExtra("action");
                        int position = result.getData().getIntExtra("position", -1);
                        if (position != -1 && position < expenseList.size()) {
                            if ("edit".equals(action)) {
                                Expense updatedExpense = result.getData().getParcelableExtra("updatedExpense");
                                if (updatedExpense != null) {
                                    expenseList.set(position, updatedExpense);
                                    if (expenseAdapter != null) {
                                        expenseAdapter.notifyDataSetChanged();
                                    }
                                    saveExpenseData();
                                }
                            } else if ("delete".equals(action)) {
                                expenseList.remove(position);
                                if (expenseAdapter != null) {
                                    expenseAdapter.notifyDataSetChanged();
                                }
                                saveExpenseData();
                            }
                        }
                    }
                });

        // Initialize views with null checks
        expenseListView = findViewById(R.id.expenseListView);
        MaterialButton addExpenseButton = findViewById(R.id.addExpenseButton);
        MaterialButton viewStatsButton = findViewById(R.id.viewStatsButton);

        if (expenseListView == null || addExpenseButton == null || viewStatsButton == null) {
            Log.e(TAG, "One or more views not found in activity_main.xml");
            Toast.makeText(this, "UI initialization failed", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Initialize expense list and adapter
        expenseList = new ArrayList<>();
        expenseAdapter = new ExpenseAdapter();
        expenseListView.setAdapter(expenseAdapter);

        // Load data
        loadExpenseData();

        // Set up button listeners
        addExpenseButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddExpenseActivity.class);
            addExpenseLauncher.launch(intent);
        });

        viewStatsButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, StatsActivity.class);
            intent.putParcelableArrayListExtra("expenseList", new ArrayList<>(expenseList));
            startActivity(intent);
        });

        // Set up ListView item click listener
        expenseListView.setOnItemClickListener((parent, view, position, id) -> {
            if (position >= 0 && position < expenseList.size()) {
                Expense expense = expenseList.get(position);
                Intent intent = new Intent(MainActivity.this, ExpenseDetailActivity.class);
                intent.putExtra("expense", expense);
                intent.putExtra("position", position);
                editExpenseLauncher.launch(intent);
            }
        });
    }

    private void loadExpenseData() {
        // Try to load from internal storage first
        File file = new File(getFilesDir(), EXPENSES_FILE);
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(openFileInput(EXPENSES_FILE)))) {
                StringBuilder jsonData = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    jsonData.append(line);
                }
                parseJsonData(jsonData.toString());
            } catch (IOException e) {
                Log.e(TAG, "Error reading expenses from internal storage", e);
                Toast.makeText(this, "Failed to load expenses", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Fallback to assets
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(getAssets().open("expenses.json")))) {
                StringBuilder jsonData = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    jsonData.append(line);
                }
                parseJsonData(jsonData.toString());
            } catch (IOException e) {
                Log.e(TAG, "Error reading expenses from assets", e);
                Toast.makeText(this, "Failed to load default expenses", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void parseJsonData(String jsonData) {
        try {
            Gson gson = new Gson();
            Type expenseListType = new TypeToken<List<Expense>>(){}.getType();
            List<Expense> expenses = gson.fromJson(jsonData, expenseListType);
            expenseList.clear();
            if (expenses != null) {
                expenseList.addAll(expenses);
            }
            if (expenseAdapter != null) {
                expenseAdapter.notifyDataSetChanged();
            }
        } catch (JsonSyntaxException e) {
            Log.e(TAG, "Error parsing JSON data", e);
            Toast.makeText(this, "Invalid expense data format", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveExpenseData() {
        Gson gson = new Gson();
        String jsonData = gson.toJson(expenseList);
        try (FileOutputStream fos = openFileOutput(EXPENSES_FILE, MODE_PRIVATE)) {
            fos.write(jsonData.getBytes());
        } catch (IOException e) {
            Log.e(TAG, "Error saving expenses", e);
            Toast.makeText(this, "Failed to save expenses", Toast.LENGTH_SHORT).show();
        }
    }

    // Custom adapter for ListView
    private class ExpenseAdapter extends ArrayAdapter<Expense> {
        ExpenseAdapter() {
            super(MainActivity.this, R.layout.item_expense, expenseList);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                try {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_expense, parent, false);
                } catch (Exception e) {
                    Log.e(TAG, "Error inflating item_expense layout", e);
                    return new View(getContext());
                }
            }
            Expense expense = getItem(position);
            TextView descriptionText = convertView.findViewById(R.id.descriptionText);
            TextView amountText = convertView.findViewById(R.id.amountText);
            TextView categoryText = convertView.findViewById(R.id.categoryText);
            TextView dateText = convertView.findViewById(R.id.dateText);

            if (expense != null && descriptionText != null && amountText != null && categoryText != null && dateText != null) {
                descriptionText.setText(expense.getDescription());
                amountText.setText(String.format("$%.2f", expense.getAmount()));
                categoryText.setText(expense.getCategory());
                dateText.setText(expense.getDate());
            } else {
                if (descriptionText != null) descriptionText.setText("");
                if (amountText != null) amountText.setText("");
                if (categoryText != null) categoryText.setText("");
                if (dateText != null) dateText.setText("");
            }

            return convertView;
        }
    }

    // Expense model class
    public static class Expense implements android.os.Parcelable {
        private int id;
        private String description;
        private double amount;
        private String date;
        private String category;

        public Expense(int id, String description, double amount, String date, String category) {
            this.id = id;
            this.description = description;
            this.amount = amount;
            this.date = date;
            this.category = category;
        }

        protected Expense(android.os.Parcel in) {
            id = in.readInt();
            description = in.readString();
            amount = in.readDouble();
            date = in.readString();
            category = in.readString();
        }

        public static final Creator<Expense> CREATOR = new Creator<Expense>() {
            @Override
            public Expense createFromParcel(android.os.Parcel in) {
                return new Expense(in);
            }

            @Override
            public Expense[] newArray(int size) {
                return new Expense[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(android.os.Parcel dest, int flags) {
            dest.writeInt(id);
            dest.writeString(description);
            dest.writeDouble(amount);
            dest.writeString(date);
            dest.writeString(category);
        }

        @Override
        public String toString() {
            return description + " - $" + amount + " (" + category + ")";
        }

        public int getId() { return id; }
        public String getDescription() { return description; }
        public double getAmount() { return amount; }
        public String getDate() { return date; }
        public String getCategory() { return category; }
    }
}