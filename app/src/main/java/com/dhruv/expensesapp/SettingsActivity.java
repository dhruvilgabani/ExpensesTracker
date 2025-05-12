package com.dhruv.expensesapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {
    private EditText etNewPin, etNewCategory;
    private Button btnChangePin, btnAddCategory;
    private RecyclerView rvCategories;
    private CategoryAdapter categoryAdapter;
    private SharedPreferences prefs;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Settings");
        }

        etNewPin = findViewById(R.id.etNewPin);
        btnChangePin = findViewById(R.id.btnChangePin);
        etNewCategory = findViewById(R.id.etNewCategory);
        btnAddCategory = findViewById(R.id.btnAddCategory);
        rvCategories = findViewById(R.id.rvCategories);
        prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        dbHelper = new DatabaseHelper(this);

        rvCategories.setLayoutManager(new LinearLayoutManager(this));
        List<String> categories = dbHelper.getAllCategories();
        categoryAdapter = new CategoryAdapter(categories, category -> {
            dbHelper.deleteCategory(category);
            categories.remove(category);
            categoryAdapter.notifyDataSetChanged();
        });
        rvCategories.setAdapter(categoryAdapter);

        btnChangePin.setOnClickListener(v -> changePin());
        btnAddCategory.setOnClickListener(v -> addCategory());
    }

    private void changePin() {
        String newPin = etNewPin.getText().toString().trim();
        if (newPin.length() != 4 || !newPin.matches("\\d+")) {
            etNewPin.setError("Enter a 4-digit PIN");
            return;
        }

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("pin", newPin);
        editor.apply();
        etNewPin.setText("");
        etNewPin.setError("PIN updated");
    }

    private void addCategory() {
        String category = etNewCategory.getText().toString().trim();
        if (category.isEmpty()) {
            etNewCategory.setError("Enter a category name");
            return;
        }

        List<String> categories = dbHelper.getAllCategories();
        if (categories.contains(category)) {
            etNewCategory.setError("Category already exists");
            return;
        }

        dbHelper.addCategory(category);
        categories.add(category);
        categoryAdapter.notifyDataSetChanged();
        etNewCategory.setText("");
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}