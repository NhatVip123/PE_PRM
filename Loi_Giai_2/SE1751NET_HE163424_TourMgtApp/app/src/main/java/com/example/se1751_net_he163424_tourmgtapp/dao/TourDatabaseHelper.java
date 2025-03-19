package com.example.se1751_net_he163424_tourmgtapp.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TourDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "BookDatabase";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "Tour";
    private static final String COLUMN_CODE = "Tour_Code";
    private static final String COLUMN_TITLE = "Tour_Title";
    private static final String COLUMN_PRICE = "Price";
    private static final String COLUMN_MEMBERS = "Members";

    public TourDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_CODE + " TEXT PRIMARY KEY," +
                COLUMN_TITLE + " TEXT NOT NULL," +
                COLUMN_PRICE + " REAL NOT NULL," +
                COLUMN_MEMBERS + " INTEGER NOT NULL)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertTour(String code, String title, double price, int members) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CODE, code);
        values.put(COLUMN_TITLE, title);
        values.put(COLUMN_PRICE, price);
        values.put(COLUMN_MEMBERS, members);
        long result = db.insert(TABLE_NAME, null, values);
        return result != -1;
    }

    public Cursor getAllTours() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
    }

    public boolean deleteTour(String code) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, COLUMN_CODE + "=?", new String[]{code}) > 0;
    }

    public void loadFirst() {
        insertTour("TSAPA01", "Sapa 4 ngày 3 đêm", 123.0, 50);
        insertTour("TAM0203", "Tam Đảo 2 ngày 1 đêm", 300.0, 50);
        insertTour("TNHAT01", "Nhật Bản 5 ngày 4 đêm", 800.0, 30);
    }

    public boolean isTableEmpty() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_NAME, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        return count == 0;
    }
}