package com.example.student_stash_project;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private ProgressDialog progressDialog; // For showing progress while saving data

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        CardView cardViewExpenses = findViewById(R.id.cardViewExpenses);
        CardView cardViewIncome = findViewById(R.id.cardViewIncome);
        CardView cardViewUserProfile = findViewById(R.id.cardViewUserProfile);

        cardViewExpenses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ExpensesActivity.class);
                startActivity(intent);
            }
        });

        cardViewIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, IncomeActivity.class);
                startActivity(intent);
            }
        });


        cardViewUserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start User Profile Activity
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        FloatingActionButton fabAddExpense = findViewById(R.id.fabAddExpense);
        fabAddExpense.setOnClickListener(view -> showAddExpenseDialog());

        FloatingActionButton fabAddIncome = findViewById(R.id.fabAddIncome);
        fabAddIncome.setOnClickListener(view -> showAddIncomeDialog());
    }



    private void createAddDialog(int layout, String type) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(layout, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        setupDialogUI(dialogView, dialog, type);
        dialog.show();
    }

    private void showAddExpenseDialog() {
        showProgressDialog("Saving expense...");
        createAddDialog(R.layout.dialog_add_data, "expense");
    }

    private void showAddIncomeDialog() {
        showProgressDialog("Saving income...");
        createAddDialog(R.layout.dialog_add_data, "income");
    }


    private void setupDialogUI(View dialogView, AlertDialog dialog, String type) {
        EditText editTextType = dialogView.findViewById(R.id.editTextType);
        EditText editTextAmount = dialogView.findViewById(R.id.editTextAmount);
        EditText editTextDate = dialogView.findViewById(R.id.editTextDate);
        setupDatePicker(editTextDate);

        Button btnSave = dialogView.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(v -> {
            String categoryType = editTextType.getText().toString().trim();
            String amount = editTextAmount.getText().toString().trim();
            String date = editTextDate.getText().toString().trim();

            if (validateInputs(categoryType, amount, date)) {
                showProgressDialog("Saving " + type + "...");
                if (type.equals("expense")) {
                    saveToFirebase(categoryType, amount, date, "expenses");
                } else {
                    saveToFirebase(categoryType, amount, date, "incomes");
                }
                dialog.dismiss();
            }
        });

        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(v -> dialog.dismiss());
    }

    private void setupDatePicker(EditText editTextDate) {
        editTextDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    MainActivity.this, (view, selectedYear, selectedMonth, selectedDayOfMonth) -> {
                selectedMonth = selectedMonth + 1;
                String selectedDate = selectedDayOfMonth + "/" + selectedMonth + "/" + selectedYear;
                editTextDate.setText(selectedDate);
            }, year, month, day);
            datePickerDialog.show();
        });
    }

    private boolean validateInputs(String category, String amount, String date) {
        if (category.isEmpty() || amount.isEmpty() || date.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return false;
        }
        try {
            Double.parseDouble(amount);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid amount", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void saveToFirebase(String category, String amount, String date, String ref) {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference(ref);
        String id = myRef.push().getKey();
        Data data = new Data(category, amount, date);
        myRef.child(id).setValue(data)
                .addOnSuccessListener(aVoid -> {
                    progressDialog.dismiss();
                    Toast.makeText(MainActivity.this, "Data saved successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(MainActivity.this, "Failed to save data", Toast.LENGTH_SHORT).show();
                });
    }

    private void showProgressDialog(String message) {
        progressDialog.setMessage(message);
        progressDialog.show();
    }
    public void onLogoutClicked(View view) {
        // Sign out from Firebase
        FirebaseAuth.getInstance().signOut();

        // If using Google Sign-In, also sign out from Google
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, gso);
        googleSignInClient.signOut().addOnCompleteListener(this,
                task -> {
                    // Google Sign Out successful, now navigate back to SignIn/MainActivity
                    Intent intent = new Intent(MainActivity.this, Login_Activity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                });
    }


}
