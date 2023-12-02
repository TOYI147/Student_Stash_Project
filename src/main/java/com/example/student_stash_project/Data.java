package com.example.student_stash_project;

public class Data {

    private String category; // Represents expense category or income type
    private String amount;   // The amount of the expense or income
    private String date;     // Date of the transaction

    // Default constructor is required for calls to DataSnapshot.getValue(Data.class)
    public Data() {
    }

    public Data(String category, String amount, String date) {
        this.category = category;
        this.amount = amount;
        this.date = date;
    }

    // Getters and setters for all fields
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}

