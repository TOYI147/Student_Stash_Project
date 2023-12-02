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

public class IncomeActivity extends AppCompatActivity {
    private HashMap<String, Float> categoryTotals = new HashMap<>();
    private ArrayList<Integer> colors = new ArrayList<>();
    private TextView totalIncomeText;
    private RecyclerView incomeList;
    private IncomeAdapter incomeAdapter; // Use an IncomeAdapter instead of ExpensesAdapter
    private List<Data> incomeDataList;
    private double totalIncome = 0;
    private PieChartView pieChartView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income);
        colors.add(Color.MAGENTA);
        colors.add(Color.BLUE);
        colors.add(Color.RED);
        colors.add(Color.YELLOW); // Add more colors as needed

        FloatingActionButton fabBackButton = findViewById(R.id.fabBackButton1);
        fabBackButton.setOnClickListener(v -> finish()); // Simplified with lambda

        TextView greetingText = findViewById(R.id.greetingText);
        totalIncomeText = findViewById(R.id.totalIncomeText);
        incomeList = findViewById(R.id.incomeList);
        pieChartView = findViewById(R.id.chart1);

        greetingText.setText(String.format("Hello, %s", getUsername()));

        setupRecyclerView();
        fetchIncomeFromFirebase(); // Renamed to reflect fetching income data
    }

    private String getUsername() {
        Intent intent = getIntent();
        return intent.getStringExtra("USERNAME_KEY");
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
        pieChartData.setHasCenterCircle(true).setCenterText1("Incomes").setCenterText1FontSize(20).setCenterText1Color(Color.parseColor("#0097A7"));
        pieChartView.setPieChartData(pieChartData);
    }

    private void setupRecyclerView() {
        incomeDataList = new ArrayList<>();
        incomeAdapter = new IncomeAdapter(incomeDataList); // Make sure to create IncomeAdapter
        incomeList.setLayoutManager(new LinearLayoutManager(this));
        incomeList.setAdapter(incomeAdapter);
    }

    private void fetchIncomeFromFirebase() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("incomes"); // Changed to incomes
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                incomeDataList.clear();
                categoryTotals.clear();
                totalIncome = 0;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Data income = snapshot.getValue(Data.class);
                    if (income != null) {
                        incomeDataList.add(income);
                        totalIncome += Double.parseDouble(income.getAmount());

                        float amount = Float.parseFloat(income.getAmount());
                        categoryTotals.put(income.getCategory(), categoryTotals.getOrDefault(income.getCategory(), 0f) + amount);
                    }
                }

                incomeAdapter.notifyDataSetChanged();
                updateTotalIncome();
                updatePieChart();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(IncomeActivity.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateTotalIncome() {
        String totalIncomeStr = String.format("$%.2f", totalIncome);
        totalIncomeText.setText(totalIncomeStr);
    }

    // Create an IncomeAdapter class similar to ExpensesAdapter
}









