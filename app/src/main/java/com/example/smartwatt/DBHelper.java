package com.example.smartwatt;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;

public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context) {
        super(context, "SmartWatt.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE bills(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "month TEXT," +
                "unit INTEGER," +
                "total REAL," +
                "rebate REAL," +
                "final REAL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS bills");
        onCreate(db);
    }

    // INSERT DATA
    public void insertBill(String month, int unit, double total, double rebate, double finalCost) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("month", month);
        cv.put("unit", unit);
        cv.put("total", total);
        cv.put("rebate", rebate);
        cv.put("final", finalCost);

        db.insert("bills", null, cv);
    }

    // GET ALL DATA
    public ArrayList<HashMap<String, String>> getAll() {

        ArrayList<HashMap<String, String>> list = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM bills", null);

        while (cursor.moveToNext()) {

            HashMap<String, String> map = new HashMap<>();

            map.put("id", cursor.getString(0));
            map.put("month", cursor.getString(1));
            map.put("unit", cursor.getString(2));
            map.put("total", cursor.getString(3));
            map.put("rebate", cursor.getString(4));
            map.put("final", cursor.getString(5));

            list.add(map);
        }

        cursor.close();
        return list;
    }

    public void updateBill(String id, String month, int unit, double total, double rebate, double finalCost) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("month", month);
        cv.put("unit", unit);
        cv.put("total", total);
        cv.put("rebate", rebate);
        cv.put("final", finalCost);

        db.update("bills", cv, "id = ?", new String[]{id});
    }
}