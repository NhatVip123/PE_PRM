package com.example.se1751_net_he163424_tourmgtapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.se1751_net_he163424_tourmgtapp.dao.TourDatabaseHelper;

public class CreateTourActivity extends AppCompatActivity {

    private EditText tourCodeEditText, tourTitleEditText, priceEditText, membersEditText;
    private Button backButton, saveButton;
    private TourDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_tour);

        tourCodeEditText = findViewById(R.id.tourCodeEditText);
        tourTitleEditText = findViewById(R.id.tourTitleEditText);
        priceEditText = findViewById(R.id.priceEditText);
        membersEditText = findViewById(R.id.membersEditText);
        backButton = findViewById(R.id.backButton);
        saveButton = findViewById(R.id.saveButton);
        dbHelper = new TourDatabaseHelper(this);

        backButton.setOnClickListener(v -> finish());
        saveButton.setOnClickListener(v -> saveTour());
    }

    private void saveTour() {
        String code = tourCodeEditText.getText().toString().trim();
        String title = tourTitleEditText.getText().toString().trim();
        String priceStr = priceEditText.getText().toString().trim();
        String membersStr = membersEditText.getText().toString().trim();

        if (code.isEmpty() || title.isEmpty() || priceStr.isEmpty() || membersStr.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isValidTourCode(code)) {
            Toast.makeText(this, "Tour Code must be 10 characters, start with 'T', and contain only A-Z and numbers", Toast.LENGTH_SHORT).show();
            return;
        }

        if (title.length() > 50) {
            Toast.makeText(this, "Tour Title must not exceed 50 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        double price = Double.parseDouble(priceStr);
        if (price <= 0 || price > 1000) {
            Toast.makeText(this, "Price must be between 0 and 1000", Toast.LENGTH_SHORT).show();
            return;
        }

        int members = Integer.parseInt(membersStr);
        if (members < 0 || members > 100) {
            Toast.makeText(this, "Members must be between 0 and 100", Toast.LENGTH_SHORT).show();
            return;
        }

        if (dbHelper.insertTour(code, title, price, members)) {
            Toast.makeText(this, "Tour saved", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to save tour", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isValidTourCode(String code) {
        if (code.length() != 10) return false;
        if (!code.startsWith("T")) return false;
        return code.matches("T[A-Z0-9]{9}");
    }
}