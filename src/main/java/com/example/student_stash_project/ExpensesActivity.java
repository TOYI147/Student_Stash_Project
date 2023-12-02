package com.example.student_stash_project;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;import android.content.Intent;


import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import lecho.lib.hellocharts.view.PieChartView;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ExpensesActivity extends AppCompatActivity {
    private HashMap<String, Float> categoryTotals = new HashMap<>();
    private ArrayList<Integer> colors = new ArrayList<>();
    private TextView totalExpensesText;
    private RecyclerView expensesList;
    private ExpensesAdapter expensesAdapter;
    private List<Data> expenseDataList;
    private double totalExpenses = 0;
    private PieChartView pieChartView; // Declare here

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenses);
        colors.add(Color.MAGENTA);
        colors.add(Color.BLUE);
        colors.add(Color.RED);
        colors.add(Color.YELLOW);// Add more colors as needed

        FloatingActionButton fabBackButton = findViewById(R.id.fabBackButton);
        fabBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the current activity
                finish();
            }
        });

        TextView greetingText = findViewById(R.id.greetingText);
        totalExpensesText = findViewById(R.id.totalExpensesText);
        expensesList = findViewById(R.id.expensesList);
        pieChartView = findViewById(R.id.chart);


        greetingText.setText(String.format("Hello, %s", getUsername()));

        setupRecyclerView();
        fetchExpensesFromFirebase();
    }

    private String getUsername() {
        // Get the intent that started this activity
        Intent intent = getIntent();
        String username = intent.getStringExtra("USERNAME_KEY"); // Use the same key

        if (username != null) {
            // Return the username if it's not null
            return username;
        } else {
            // Return a default value or null if username is not found
            return "DefaultUsername"; // or return null;
        }
    }


    private void updatePieChart() {
        List<SliceValue> pieData = new ArrayList<>();
        int colorIndex = 0;

        for (Map.Entry<String, Float> entry : categoryTotals.entrySet()) {
            pieData.add(new SliceValue(entry.getValue(), colors.get(colorIndex % colors.size())).setLabel(entry.getKey()));
            colorIndex++;
        }

        PieChartData pieChartData = new PieChartData(pieData);
        pieChartData.setHasLabels(true).setValueLabelTextSize(14);
        pieChartData.setHasCenterCircle(true).setCenterText1("Expenses").setCenterText1FontSize(20).setCenterText1Color(Color.parseColor("#0097A7"));
        pieChartView.setPieChartData(pieChartData);
    }

    private void setupRecyclerView() {
        expenseDataList = new ArrayList<>();
        expensesAdapter = new ExpensesAdapter(expenseDataList);
        expensesList.setLayoutManager(new LinearLayoutManager(this));
        expensesList.setAdapter(expensesAdapter);
    }

    private void fetchExpensesFromFirebase() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("expenses");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                expenseDataList.clear();
                categoryTotals.clear();
                totalExpenses = 0;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Data expense = snapshot.getValue(Data.class);
                    if (expense != null) {
                        expenseDataList.add(expense);
                        totalExpenses += Double.parseDouble(expense.getAmount());

                        // Aggregate data for pie chart
                        float amount = Float.parseFloat(expense.getAmount());
                        categoryTotals.put(expense.getCategory(), categoryTotals.getOrDefault(expense.getCategory(), 0f) + amount);
                    }
                }

                expensesAdapter.notifyDataSetChanged();
                updateTotalExpenses();
                updatePieChart(); // Update Pie Chart here
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ExpensesActivity.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void updateTotalExpenses() {
        String totalExpensesStr = String.format("$%.2f", totalExpenses);
        totalExpensesText.setText(totalExpensesStr);
    }

    // Optionally add method for updating Pie Chart
}



