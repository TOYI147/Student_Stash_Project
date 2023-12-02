package com.example.student_stash_project;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        TextView userNameText = findViewById(R.id.user_name_text);
        TextView userLocationText = findViewById(R.id.user_location_text);

        // Set user name and location
        // TODO: Replace with actual user data
        userNameText.setText("Michael Angelo");
        userLocationText.setText("Naperville, IL");

        // Savings and Goals click listeners
        findViewById(R.id.savings_section).setOnClickListener(v -> {
            // TODO: Navigate to Savings Activity or Fragment
        });

        findViewById(R.id.goals_section).setOnClickListener(v -> {
            // TODO: Navigate to Goals Activity or Fragment
        });
    }

    // TODO: Add methods for fetching and displaying user data and financials
}

