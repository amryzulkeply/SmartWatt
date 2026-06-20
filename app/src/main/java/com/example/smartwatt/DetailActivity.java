package com.example.smartwatt;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

public class DetailActivity extends AppCompatActivity {

    TextView tvDetail;
    Button btnDelete, btnEdit;

    DBHelper db;
    String id;

    String month;
    int unit;
    double total, rebate, finalCost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        tvDetail = findViewById(R.id.tvDetail);
        btnDelete = findViewById(R.id.btnDelete);
        btnEdit = findViewById(R.id.btnEdit);

        db = new DBHelper(this);

        id = getIntent().getStringExtra("id");

        loadData();

        btnDelete.setOnClickListener(v -> {
            db.getWritableDatabase().delete("bills", "id=?", new String[]{id});
            Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
            finish();
        });

        btnEdit.setOnClickListener(v -> showEditDialog());
    }

    private void loadData() {

        Cursor cursor = db.getReadableDatabase()
                .rawQuery("SELECT * FROM bills WHERE id=?", new String[]{id});

        if (cursor.moveToFirst()) {

            month = cursor.getString(1);
            unit = Integer.parseInt(cursor.getString(2));
            total = Double.parseDouble(cursor.getString(3));
            rebate = Double.parseDouble(cursor.getString(4));
            finalCost = Double.parseDouble(cursor.getString(5));

            tvDetail.setText(
                    "Month: " + month +
                            "\nUnit: " + unit +
                            "\nTotal: RM " + total +
                            "\nRebate: " + rebate +
                            "\nFinal: RM " + finalCost
            );
        }

        cursor.close();
    }

    private void showEditDialog() {

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        EditText etMonth = new EditText(this);
        etMonth.setText(month);

        EditText etUnit = new EditText(this);
        etUnit.setText(String.valueOf(unit));

        EditText etRebate = new EditText(this);
        etRebate.setText(String.valueOf(rebate));

        layout.addView(etMonth);
        layout.addView(etUnit);
        layout.addView(etRebate);

        new android.app.AlertDialog.Builder(this)
                .setTitle("Edit Bill")
                .setView(layout)
                .setPositiveButton("Save", (dialog, which) -> {

                    String newMonth = etMonth.getText().toString();
                    int newUnit = Integer.parseInt(etUnit.getText().toString());
                    double newRebate = Double.parseDouble(etRebate.getText().toString());

                    double newTotal = calculateBill(newUnit);
                    double newFinal = newTotal - (newTotal * newRebate / 100);

                    db.updateBill(id, newMonth, newUnit, newTotal, newRebate, newFinal);

                    loadData();

                    Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    public double calculateBill(int unit) {

        double total = 0;
        int remain = unit;

        if (remain > 0) {
            int x = Math.min(remain, 200);
            total += x * 0.218;
            remain -= x;
        }

        if (remain > 0) {
            int x = Math.min(remain, 100);
            total += x * 0.334;
            remain -= x;
        }

        if (remain > 0) {
            int x = Math.min(remain, 300);
            total += x * 0.516;
            remain -= x;
        }

        if (remain > 0) {
            total += remain * 0.546;
        }

        return total;
    }
}