package com.dhruv.expensesapp;

public class Expense {
    private int id;
    private String description;
    private double amount;
    private String date;
    private String type;
    private String category;
    private String notes;
    private boolean recurring;
    private String recurringInterval;
    private String reminderDate;

    public Expense(int id, String description, double amount, String date, String type, String category,
                   String notes, boolean recurring, String recurringInterval, String reminderDate) {
        this.id = id;
        this.description = description;
        this.amount = amount;
        this.date = date;
        this.type = type;
        this.category = category;
        this.notes = notes;
        this.recurring = recurring;
        this.recurringInterval = recurringInterval;
        this.reminderDate = reminderDate;
    }

    public int getId() { return id; }
    public String getDescription() { return description; }
    public double getAmount() { return amount; }
    public String getDate() { return date; }
    public String getType() { return type; }
    public String getCategory() { return category; }
    public String getNotes() { return notes; }
    public boolean isRecurring() { return recurring; }
    public String getRecurringInterval() { return recurringInterval; }
    public String getReminderDate() { return reminderDate; }
}