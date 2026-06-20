package com.example.smartwatt;

import android.content.Intent;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;

public class ListActivity extends AppCompatActivity {

    ListView listView;
    DBHelper db;

    ArrayList<HashMap<String, String>> dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        listView = findViewById(R.id.listView);
        db = new DBHelper(this);

        // ambil data dari database
        dataList = db.getAll();

        ArrayList<String> displayList = new ArrayList<>();

        for (int i = 0; i < dataList.size(); i++) {
            HashMap<String, String> map = dataList.get(i);

            String month = map.get("month");
            String finalCost = map.get("final");

            displayList.add(month + " - RM " + String.format("%.2f", Double.parseDouble(finalCost)));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                displayList
        );

        listView.setAdapter(adapter);

        // click item (nanti detail page)
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, android.view.View view, int position, long id) {

                Intent intent = new Intent(ListActivity.this, DetailActivity.class);

                intent.putExtra("id", dataList.get(position).get("id"));

                startActivity(intent);
            }
        });
    }
}