package com.example.student_stash_project;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class IncomeAdapter extends RecyclerView.Adapter<IncomeAdapter.IncomeViewHolder> {
    private List<Data> incomes;

    public IncomeAdapter(List<Data> incomes) {
        this.incomes = incomes;
    }

    @Override
    public IncomeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.income_item, parent, false);
        return new IncomeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(IncomeViewHolder holder, int position) {
        Data income = incomes.get(position);
        holder.bind(income);
    }

    @Override
    public int getItemCount() {
        return incomes.size();
    }

    public static class IncomeViewHolder extends RecyclerView.ViewHolder {
        private TextView sourceTextView; // Assuming you have a source field
        private TextView amountTextView;
        private TextView dateTextView;

        public IncomeViewHolder(View itemView) {
            super(itemView);
            sourceTextView = itemView.findViewById(R.id.categoryTextView1); // Replace with your actual ID
            amountTextView = itemView.findViewById(R.id.amountTextView1);   // Replace with your actual ID
            dateTextView = itemView.findViewById(R.id.dateTextView1);       // Replace with your actual ID
        }

        public void bind(Data income) {
            sourceTextView.setText(income.getCategory()); // Assuming category is the income source
            amountTextView.setText(income.getAmount());
            dateTextView.setText(income.getDate());
        }
    }
}

