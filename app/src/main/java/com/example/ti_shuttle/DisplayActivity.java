package com.example.ti_shuttle;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.util.ArrayList;

public class DisplayActivity extends AppCompatActivity {


    DBHelper helper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        helper = new DBHelper(this);
        ArrayList<pass_data> data = helper.getAllEntries();
        ListView listView = findViewById(R.id.listView);

        String[] fromCols = new String[]{"AID", "date", "Location_in", "Time_in", "Location_out", "Time_out"};
        int[] toViews = {R.id.AID, R.id.Date, R.id.Location_in, R.id.Time_in, R.id.Location_out, R.id.Time_out};

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.mylist, helper.getCursor(),fromCols, toViews);
        listView.setAdapter(adapter);


    }
}
