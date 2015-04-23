package com.gatech.whereabouts.whereabouts;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    DatabaseHandler dbHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHandler = new DatabaseHandler(getApplicationContext());


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void sendMessage(View view) {
        Intent intent = new Intent(this, DisplayLocationActivityUI.class);
        startActivity(intent);
    }

    public void exportDB(MenuItem item) {
        File exportDir = new File(Environment.getExternalStorageDirectory(), "");
        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }

        File file = new File(exportDir, "whereabouts.csv");
        try {
            file.createNewFile();
            CSVWriter writer = new CSVWriter(new FileWriter(file), ',', '"', "\n");
            Cursor curCSV = dbHandler.getDatabaseCursor();
            writer.writeNext(curCSV.getColumnNames());
            while(curCSV.moveToNext()) {
                String arrStr[] = {
                    curCSV.getString(0),
                    curCSV.getString(1),
                    curCSV.getString(2),
                    curCSV.getString(3),
                    curCSV.getString(4),
                    curCSV.getString(5),
                    curCSV.getString(6),
                    curCSV.getString(7),
                    curCSV.getString(8),
                    curCSV.getString(9),
                    curCSV.getString(10)
                };
                writer.writeNext(arrStr);
            }
            writer.close();
            curCSV.close();

            Toast.makeText(getApplicationContext(), "Export to CSV Complete", Toast.LENGTH_SHORT).show();

        }
        catch(Exception sqlEx) {
            Log.e("MainActivity", sqlEx.getMessage(), sqlEx);
        }
    }


}
