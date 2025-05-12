package com.dhruv.expensesapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "ExpensesDB";
    private static final int DATABASE_VERSION = 2; // Incremented due to schema changes
    private static final String TABLE_EXPENSES = "expenses";
    private static final String TABLE_CATEGORIES = "categories";
    private static final String KEY_ID = "id";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_AMOUNT = "amount";
    private static final String KEY_DATE = "date";
    private static final String KEY_TYPE = "type";
    private static final String KEY_CATEGORY = "category";
    private static final String KEY_NOTES = "notes";
    private static final String KEY_RECURRING = "recurring";
    private static final String KEY_RECURRING_INTERVAL = "recurring_interval";
    private static final String KEY_REMINDER_DATE = "reminder_date";
    private static final String KEY_NAME = "name";
    private Context context;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_EXPENSES_TABLE = "CREATE TABLE " + TABLE_EXPENSES + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_DESCRIPTION + " TEXT,"
                + KEY_AMOUNT + " REAL,"
                + KEY_DATE + " TEXT,"
                + KEY_TYPE + " TEXT,"
                + KEY_CATEGORY + " TEXT,"
                + KEY_NOTES + " TEXT,"
                + KEY_RECURRING + " INTEGER," // 0 for false, 1 for true
                + KEY_RECURRING_INTERVAL + " TEXT,"
                + KEY_REMINDER_DATE + " TEXT" + ")";
        db.execSQL(CREATE_EXPENSES_TABLE);

        String CREATE_CATEGORIES_TABLE = "CREATE TABLE " + TABLE_CATEGORIES + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_NAME + " TEXT UNIQUE" + ")";
        db.execSQL(CREATE_CATEGORIES_TABLE);

        // Seed default categories
        String[] defaultCategories = {"Food", "Travel", "Salary", "Bills", "Other"};
        for (String category : defaultCategories) {
            ContentValues values = new ContentValues();
            values.put(KEY_NAME, category);
            db.insert(TABLE_CATEGORIES, null, values);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXPENSES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
        onCreate(db);
    }

    public void addExpense(Expense expense) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_DESCRIPTION, expense.getDescription());
        values.put(KEY_AMOUNT, expense.getAmount());
        values.put(KEY_DATE, expense.getDate());
        values.put(KEY_TYPE, expense.getType());
        values.put(KEY_CATEGORY, expense.getCategory());
        values.put(KEY_NOTES, expense.getNotes());
        values.put(KEY_RECURRING, expense.isRecurring() ? 1 : 0);
        values.put(KEY_RECURRING_INTERVAL, expense.getRecurringInterval());
        values.put(KEY_REMINDER_DATE, expense.getReminderDate());
        db.insert(TABLE_EXPENSES, null, values);
        db.close();
    }

    public Expense getExpenseById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_EXPENSES, null, KEY_ID + " = ?", new String[]{String.valueOf(id)}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            Expense expense = new Expense(
                    cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(KEY_DESCRIPTION)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_AMOUNT)),
                    cursor.getString(cursor.getColumnIndexOrThrow(KEY_DATE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(KEY_TYPE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(KEY_CATEGORY)),
                    cursor.getString(cursor.getColumnIndexOrThrow(KEY_NOTES)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(KEY_RECURRING)) == 1,
                    cursor.getString(cursor.getColumnIndexOrThrow(KEY_RECURRING_INTERVAL)),
                    cursor.getString(cursor.getColumnIndexOrThrow(KEY_REMINDER_DATE))
            );
            cursor.close();
            db.close();
            return expense;
        }
        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return null;
    }

    public void updateExpense(Expense expense) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_DESCRIPTION, expense.getDescription());
        values.put(KEY_AMOUNT, expense.getAmount());
        values.put(KEY_DATE, expense.getDate());
        values.put(KEY_TYPE, expense.getType());
        values.put(KEY_CATEGORY, expense.getCategory());
        values.put(KEY_NOTES, expense.getNotes());
        values.put(KEY_RECURRING, expense.isRecurring() ? 1 : 0);
        values.put(KEY_RECURRING_INTERVAL, expense.getRecurringInterval());
        values.put(KEY_REMINDER_DATE, expense.getReminderDate());
        db.update(TABLE_EXPENSES, values, KEY_ID + " = ?", new String[]{String.valueOf(expense.getId())});
        db.close();
    }

    public void deleteExpense(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_EXPENSES, KEY_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public List<Expense> getAllExpenses() {
        List<Expense> expenseList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_EXPENSES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Expense expense = new Expense(
                        cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_DESCRIPTION)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_AMOUNT)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_TYPE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_CATEGORY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_NOTES)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(KEY_RECURRING)) == 1,
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_RECURRING_INTERVAL)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_REMINDER_DATE))
                );
                expenseList.add(expense);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return expenseList;
    }

    public List<Expense> getFilteredExpenses(String type, String startDate, String endDate, String category) {
        List<Expense> expenseList = new ArrayList<>();
        StringBuilder query = new StringBuilder("SELECT * FROM " + TABLE_EXPENSES);
        List<String> args = new ArrayList<>();
        boolean hasCondition = false;

        if (type != null && !type.equals("All")) {
            query.append(hasCondition ? " AND " : " WHERE ").append(KEY_TYPE).append(" = ?");
            args.add(type);
            hasCondition = true;
        }
        if (category != null && !category.equals("All")) {
            query.append(hasCondition ? " AND " : " WHERE ").append(KEY_CATEGORY).append(" = ?");
            args.add(category);
            hasCondition = true;
        }
        if (startDate != null && endDate != null) {
            query.append(hasCondition ? " AND " : " WHERE ").append(KEY_DATE).append(" BETWEEN ? AND ?");
            args.add(startDate);
            args.add(endDate);
        }

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query.toString(), args.toArray(new String[0]));

        if (cursor.moveToFirst()) {
            do {
                Expense expense = new Expense(
                        cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_DESCRIPTION)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_AMOUNT)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_TYPE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_CATEGORY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_NOTES)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(KEY_RECURRING)) == 1,
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_RECURRING_INTERVAL)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_REMINDER_DATE))
                );
                expenseList.add(expense);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return expenseList;
    }

    public double getTotalIncome() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(" + KEY_AMOUNT + ") FROM " + TABLE_EXPENSES + " WHERE " + KEY_TYPE + " = ?", new String[]{"Income"});
        double total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0);
        }
        cursor.close();
        db.close();
        return total;
    }

    public double getTotalExpenses() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(" + KEY_AMOUNT + ") FROM " + TABLE_EXPENSES + " WHERE " + KEY_TYPE + " = ?", new String[]{"Expense"});
        double total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0);
        }
        cursor.close();
        db.close();
        return total;
    }

    public void addCategory(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);
        db.insert(TABLE_CATEGORIES, null, values);
        db.close();
    }

    public List<String> getAllCategories() {
        List<String> categories = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + KEY_NAME + " FROM " + TABLE_CATEGORIES, null);
        if (cursor.moveToFirst()) {
            do {
                categories.add(cursor.getString(cursor.getColumnIndexOrThrow(KEY_NAME)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return categories;
    }

    public void deleteCategory(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CATEGORIES, KEY_NAME + " = ?", new String[]{name});
        db.close();
    }

    public void exportToCSV(Context context) {
        try {
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "expenses.csv");
            FileWriter writer = new FileWriter(file);
            writer.append("ID,Description,Amount,Date,Type,Category,Notes,Recurring,RecurringInterval,ReminderDate\n");

            List<Expense> expenses = getAllExpenses();
            for (Expense expense : expenses) {
                writer.append(String.format("%d,%s,%.2f,%s,%s,%s,%s,%b,%s,%s\n",
                        expense.getId(), expense.getDescription(), expense.getAmount(), expense.getDate(),
                        expense.getType(), expense.getCategory(), expense.getNotes(),
                        expense.isRecurring(), expense.getRecurringInterval(), expense.getReminderDate()));
            }
            writer.flush();
            writer.close();
            Toast.makeText(context, "Exported to " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(context, "Export failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void importFromCSV(Context context) {
        try {
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "expenses.csv");
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            reader.readLine(); // Skip header
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 10) {
                    ContentValues values = new ContentValues();
                    values.put(KEY_DESCRIPTION, data[1]);
                    values.put(KEY_AMOUNT, Double.parseDouble(data[2]));
                    values.put(KEY_DATE, data[3]);
                    values.put(KEY_TYPE, data[4]);
                    values.put(KEY_CATEGORY, data[5]);
                    values.put(KEY_NOTES, data[6]);
                    values.put(KEY_RECURRING, Boolean.parseBoolean(data[7]) ? 1 : 0);
                    values.put(KEY_RECURRING_INTERVAL, data[8]);
                    values.put(KEY_REMINDER_DATE, data[9]);
                    db.insert(TABLE_EXPENSES, null, values);
                }
            }
            db.setTransactionSuccessful();
            db.endTransaction();
            db.close();
            reader.close();
            Toast.makeText(context, "Imported successfully", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(context, "Import failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}