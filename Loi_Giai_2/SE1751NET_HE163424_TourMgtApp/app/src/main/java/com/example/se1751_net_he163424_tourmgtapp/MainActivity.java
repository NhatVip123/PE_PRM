package com.example.se1751_net_he163424_tourmgtapp;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.database.Cursor;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.se1751_net_he163424_tourmgtapp.dao.TourDatabaseHelper;
import com.example.se1751_net_he163424_tourmgtapp.entity.Tour;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private TourDatabaseHelper dbHelper;
    private ListView tourListView;
    private Button createTourButton;
    private ArrayList<Tour> tourList;
    private ArrayAdapter<Tour> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new TourDatabaseHelper(this);
        tourListView = findViewById(R.id.tourListView);
        createTourButton = findViewById(R.id.createTourButton);
        tourList = new ArrayList<>();

        // Kiểm tra lần đầu chạy ứng dụng
        SharedPreferences prefs = getSharedPreferences("TourAppPrefs", MODE_PRIVATE);
        boolean isFirstRun = prefs.getBoolean("isFirstRun", true);

        if (isFirstRun || dbHelper.isTableEmpty()) {
            dbHelper.loadFirst();

            // Đánh dấu rằng ứng dụng đã chạy lần đầu
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("isFirstRun", false);
            editor.apply();
        }

        loadTours();

        createTourButton.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, CreateTourActivity.class));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTours();
    }

    private void loadTours() {
        tourList.clear();
        Cursor cursor = dbHelper.getAllTours();
        int index = 1;
        if (cursor.moveToFirst()) {
            do {
                String code = cursor.getString(cursor.getColumnIndexOrThrow("Tour_Code"));
                String title = cursor.getString(cursor.getColumnIndexOrThrow("Tour_Title"));
                double price = cursor.getDouble(cursor.getColumnIndexOrThrow("Price"));
                int members = cursor.getInt(cursor.getColumnIndexOrThrow("Members"));
                tourList.add(new Tour(code, title, price, members));
                index++;
            } while (cursor.moveToNext());
        }
        cursor.close();

        adapter = new ArrayAdapter<Tour>(this, R.layout.tour_item, R.id.titleTextView, tourList) {
            @Override
            public View getView(int position, View convertView, android.view.ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                Tour tour = tourList.get(position);

                TextView indexTextView = view.findViewById(R.id.indexTextView);
                TextView codeTextView = view.findViewById(R.id.codeTextView);
                TextView titleTextView = view.findViewById(R.id.titleTextView);
                TextView membersTextView = view.findViewById(R.id.membersTextView);
                TextView priceTextView = view.findViewById(R.id.priceTextView);
                Button deleteButton = view.findViewById(R.id.deleteButton);

                indexTextView.setText(String.valueOf(position + 1));
                codeTextView.setText(tour.getCode());
                titleTextView.setText(tour.getTitle());
                membersTextView.setText(String.valueOf(tour.getMembers()));
                priceTextView.setText(String.format("$%.2f", tour.getPrice()));

                deleteButton.setOnClickListener(v -> {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Confirmation")
                            .setMessage("Are you sure you want to delete?")
                            .setPositiveButton("Yes", (dialog, which) -> {
                                if (dbHelper.deleteTour(tour.getCode())) {
                                    Toast.makeText(MainActivity.this, "Tour deleted", Toast.LENGTH_SHORT).show();
                                    loadTours();
                                } else {
                                    Toast.makeText(MainActivity.this, "Failed to delete", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setNegativeButton("No", null)
                            .show();
                });

                return view;
            }
        };
        tourListView.setAdapter(adapter);
    }
}