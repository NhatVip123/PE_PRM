package com.example.se1751_net_he163424_tourmgtapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.se1751_net_he163424_tourmgtapp.adapter.TourAdapter;
import com.example.se1751_net_he163424_tourmgtapp.dao.TourDatabaseHelper;
import com.example.se1751_net_he163424_tourmgtapp.entity.Tour;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements TourAdapter.OnTourDeletedListener {

    private TourDatabaseHelper dbHelper;
    private RecyclerView tourRecyclerView;
    private ArrayList<Tour> tourList;
    private TourAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Thiết lập tiêu đề trên ActionBar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Tour List");
        }

        dbHelper = new TourDatabaseHelper(this);
        tourRecyclerView = findViewById(R.id.tourRecyclerView);
        tourList = new ArrayList<>();

        // Thiết lập RecyclerView
        tourRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TourAdapter(this, tourList, dbHelper, this);
        tourRecyclerView.setAdapter(adapter);

        // Tạo FrameLayout để chứa RecyclerView và FAB
        FrameLayout frameLayout = new FrameLayout(this);
        frameLayout.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        ));

        // Xóa RecyclerView khỏi parent hiện tại và thêm vào FrameLayout
        View recyclerView = findViewById(R.id.tourRecyclerView);
        ((ViewGroup) recyclerView.getParent()).removeView(recyclerView);
        frameLayout.addView(recyclerView);

        // Thêm Floating Action Button
        FloatingActionButton fab = new FloatingActionButton(this);
        fab.setId(View.generateViewId());
        fab.setImageResource(android.R.drawable.ic_input_add);
        fab.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, CreateTourActivity.class)));

        // Định vị FAB bằng FrameLayout.LayoutParams
        FrameLayout.LayoutParams fabParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        fabParams.gravity = android.view.Gravity.END | android.view.Gravity.BOTTOM;
        fabParams.setMargins(0, 0, 16, 16);
        fab.setLayoutParams(fabParams);

        frameLayout.addView(fab);

        // Đặt FrameLayout làm nội dung của Activity
        setContentView(frameLayout);

        // Kiểm tra lần đầu chạy ứng dụng
        SharedPreferences prefs = getSharedPreferences("TourAppPrefs", MODE_PRIVATE);
        boolean isFirstRun = prefs.getBoolean("isFirstRun", true);

        if (isFirstRun || dbHelper.isTableEmpty()) {
            dbHelper.loadFirst();
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("isFirstRun", false);
            editor.apply();
        }

        loadTours();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTours();
    }

    private void loadTours() {
        tourList.clear();
        Cursor cursor = dbHelper.getAllTours();
        if (cursor.moveToFirst()) {
            do {
                String code = cursor.getString(cursor.getColumnIndexOrThrow("Tour_Code"));
                String title = cursor.getString(cursor.getColumnIndexOrThrow("Tour_Title"));
                double price = cursor.getDouble(cursor.getColumnIndexOrThrow("Price"));
                int members = cursor.getInt(cursor.getColumnIndexOrThrow("Members"));
                tourList.add(new Tour(code, title, price, members));
            } while (cursor.moveToNext());
        }
        cursor.close();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onTourDeleted() {
        if (dbHelper.isTableEmpty()) {
            dbHelper.loadFirst();
            loadTours();
        }
        Toast.makeText(this, "Tour deleted", Toast.LENGTH_SHORT).show();
    }
}