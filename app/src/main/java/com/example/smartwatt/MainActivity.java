package com.example.smartwatt;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Spinner spMonth;
    EditText etUnit, etRebate;
    Button btnCalc, btnView, btnSave, btnAbout;
    TextView tvResult;

    DBHelper db;

    double lastTotal = 0;
    double lastFinal = 0;
    String lastMonth = "";
    int lastUnit = 0;
    double lastRebate = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // connect UI
        spMonth = findViewById(R.id.spMonth);
        etUnit = findViewById(R.id.etUnit);
        etRebate = findViewById(R.id.etRebate);
        btnCalc = findViewById(R.id.btnCalc);
        btnView = findViewById(R.id.btnView);
        btnSave = findViewById(R.id.btnSave);
        btnAbout = findViewById(R.id.btnAbout);
        tvResult = findViewById(R.id.tvResult);

        db = new DBHelper(this);

        // hide save button awal
        btnSave.setVisibility(View.GONE);

        // month spinner
        String[] months = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                months
        );
        spMonth.setAdapter(adapter);

        // CALCULATE BUTTON
        btnCalc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String unitStr = etUnit.getText().toString();
                String rebateStr = etRebate.getText().toString();

                if (unitStr.isEmpty()) {
                    etUnit.setError("Enter kWh");
                    return;
                }

                int unit = Integer.parseInt(unitStr);
                double rebate = rebateStr.isEmpty() ? 0 : Double.parseDouble(rebateStr);

                if (unit < 1 || unit > 1000) {
                    etUnit.setError("Must be 1 - 1000");
                    return;
                }

                lastMonth = spMonth.getSelectedItem().toString();
                lastUnit = unit;
                lastRebate = rebate;

                lastTotal = calculateBill(unit);
                lastFinal = lastTotal - (lastTotal * rebate / 100);

                tvResult.setText(
                        "Total: RM " + String.format("%.2f", lastTotal) +
                                "\nFinal: RM " + String.format("%.2f", lastFinal)
                );

                btnSave.setVisibility(View.VISIBLE);
            }
        });

        // SAVE BUTTON
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                db.insertBill(
                        lastMonth,
                        lastUnit,
                        lastTotal,
                        lastRebate,
                        lastFinal
                );

                Toast.makeText(MainActivity.this,
                        "Saved to Database",
                        Toast.LENGTH_SHORT).show();

                btnSave.setVisibility(View.GONE);
            }
        });

        // VIEW HISTORY
        btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ListActivity.class));
            }
        });

        // ABOUT PAGE
        btnAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
            }
        });
    }

    // CALCULATION FUNCTION
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