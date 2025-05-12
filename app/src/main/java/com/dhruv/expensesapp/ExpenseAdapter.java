package com.dhruv.expensesapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> implements Filterable {
    private List<Expense> expenses;
    private List<Expense> expensesFiltered;
    private Context context;
    private static final int REQUEST_CODE_DETAIL = 1;

    public ExpenseAdapter(List<Expense> expenses, Context context) {
        this.expenses = expenses != null ? expenses : new ArrayList<>();
        this.expensesFiltered = new ArrayList<>(this.expenses);
        this.context = context;
    }

    @Override
    public ExpenseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_expense, parent, false);
        return new ExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ExpenseViewHolder holder, int position) {
        Expense expense = expensesFiltered.get(position);
        holder.tvDescription.setText(expense.getDescription());
        holder.tvAmount.setText(String.format("$%.2f", expense.getAmount()));
        holder.tvDate.setText(expense.getDate());
        holder.tvCategory.setText(expense.getCategory());
        holder.tvNotes.setText(expense.getNotes().isEmpty() ? "No notes" : expense.getNotes());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ExpenseDetailActivity.class);
            intent.putExtra("expense_id", expense.getId());
            if (context instanceof Activity) {
                ((Activity) context).startActivityForResult(intent, REQUEST_CODE_DETAIL);
            }
        });
    }

    @Override
    public int getItemCount() {
        return expensesFiltered.size();
    }

    public void updateData(List<Expense> newExpenses) {
        this.expenses = newExpenses != null ? newExpenses : new ArrayList<>();
        this.expensesFiltered = new ArrayList<>(this.expenses);
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<Expense> filteredList = new ArrayList<>();
                if (constraint == null || constraint.length() == 0) {
                    filteredList.addAll(expenses);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    for (Expense expense : expenses) {
                        if (expense.getDescription().toLowerCase().contains(filterPattern) ||
                                expense.getCategory().toLowerCase().contains(filterPattern) ||
                                expense.getNotes().toLowerCase().contains(filterPattern)) {
                            filteredList.add(expense);
                        }
                    }
                }
                FilterResults results = new FilterResults();
                results.values = filteredList;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                expensesFiltered = (List<Expense>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    static class ExpenseViewHolder extends RecyclerView.ViewHolder {
        TextView tvDescription, tvAmount, tvDate, tvCategory, tvNotes;

        ExpenseViewHolder(View itemView) {
            super(itemView);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvNotes = itemView.findViewById(R.id.tvNotes);
        }
    }
}