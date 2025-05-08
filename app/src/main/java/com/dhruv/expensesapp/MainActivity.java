package com.dhruv.expensesapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ListView expenseListView;
    private ArrayAdapter<Expense> expenseAdapter;
    private List<Expense> expenseList;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        expenseListView = findViewById(R.id.expenseListView);
        MaterialButton addExpenseButton = findViewById(R.id.addExpenseButton);
        MaterialButton viewStatsButton = findViewById(R.id.viewStatsButton);

        // Initialize database and expense list
        dbHelper = new DatabaseHelper(this);
        expenseList = new ArrayList<>();
        expenseAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                expenseList
        );
        expenseListView.setAdapter(expenseAdapter);

        // Load data from SQLite
        loadExpenseData();

        // Set up button listeners
        addExpenseButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddExpenseActivity.class);
            startActivityForResult(intent, 1);
        });

        viewStatsButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, StatsActivity.class);
            startActivity(intent);
        });

        // Set up ListView item click listener
        expenseListView.setOnItemClickListener((parent, view, position, id) -> {
            Expense expense = expenseList.get(position);
            Intent intent = new Intent(MainActivity.this, ExpenseDetailActivity.class);
            intent.putExtra("expense", expense);
            startActivityForResult(intent, 2);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == 1 || requestCode == 2) && resultCode == RESULT_OK) {
            loadExpenseData();
        }
    }

    private void loadExpenseData() {
        expenseList.clear();
        expenseList.addAll(dbHelper.getAllExpenses());
        expenseAdapter.notifyDataSetChanged();
    }

    // Expense model class
    public static class Expense implements Parcelable {
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

        protected Expense(Parcel in) {
            id = in.readInt();
            description = in.readString();
            amount = in.readDouble();
            date = in.readString();
            category = in.readString();
        }

        public static final Creator<Expense> CREATOR = new Creator<Expense>() {
            @Override
            public Expense createFromParcel(Parcel in) {
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
        public void writeToParcel(Parcel dest, int flags) {
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