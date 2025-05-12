package com.dhruv.expensesapp;

import android.content.Context;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class RecurringWorker extends Worker {
    private DatabaseHelper dbHelper;

    public RecurringWorker(Context context, WorkerParameters params) {
        super(context, params);
        dbHelper = new DatabaseHelper(context);
    }

    @Override
    public Result doWork() {
        List<Expense> expenses = dbHelper.getAllExpenses();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String today = sdf.format(Calendar.getInstance().getTime());

        for (Expense expense : expenses) {
            if (expense.isRecurring() && expense.getRecurringInterval() != null) {
                try {
                    Calendar lastDate = Calendar.getInstance();
                    lastDate.setTime(sdf.parse(expense.getDate()));
                    Calendar currentDate = Calendar.getInstance();

                    boolean shouldAdd = false;
                    switch (expense.getRecurringInterval()) {
                        case "Daily":
                            lastDate.add(Calendar.DAY_OF_MONTH, 1);
                            if (!lastDate.after(currentDate)) shouldAdd = true;
                            break;
                        case "Weekly":
                            lastDate.add(Calendar.WEEK_OF_YEAR, 1);
                            if (!lastDate.after(currentDate)) shouldAdd = true;
                            break;
                        case "Monthly":
                            lastDate.add(Calendar.MONTH, 1);
                            if (!lastDate.after(currentDate)) shouldAdd = true;
                            break;
                    }

                    if (shouldAdd) {
                        Expense newExpense = new Expense(
                                0, expense.getDescription(), expense.getAmount(), today,
                                expense.getType(), expense.getCategory(), expense.getNotes(),
                                true, expense.getRecurringInterval(), expense.getReminderDate()
                        );
                        dbHelper.addExpense(newExpense);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return Result.success();
    }

    public static void scheduleWork(Context context) {
        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(
                RecurringWorker.class, 1, TimeUnit.DAYS)
                .build();
        WorkManager.getInstance(context).enqueue(workRequest);
    }
}