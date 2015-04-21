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
    ExpandableListView locationListView;
    ExpandableListView tripPurposeListView;

    ExpandableSelectListAdapter locationExpandableAdapter;
    ExpandableSelectListAdapter tripPurposeExpandableAdapter;

    List<String> groupListLocation;
    HashMap<String, List<String>> childMapLocation;

    List<String> groupListPurpose;
    HashMap<String, List<String>> childMapPurpose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHandler = new DatabaseHandler(getApplicationContext());

        init();
        locationListView = (ExpandableListView) findViewById(R.id.placelist);
        locationExpandableAdapter = new ExpandableSelectListAdapter(
                this,
                groupListLocation,
                childMapLocation,
                R.layout.placelist_parent,
                R.layout.placelist_child
        );
        locationListView.setAdapter(locationExpandableAdapter);

        tripPurposeListView = (ExpandableListView) findViewById(R.id.purposelist);
        tripPurposeExpandableAdapter = new ExpandableSelectListAdapter(
                this,
                groupListPurpose,
                childMapPurpose,
                R.layout.purposelist_parent,
                R.layout.purposelist_child
        );
        tripPurposeListView.setAdapter(tripPurposeExpandableAdapter);
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
        Intent intent = new Intent(this, DisplayLocationActivity.class);
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

    private void init() {
        groupListLocation = new ArrayList<>();
        childMapLocation = new HashMap<>();

        List<String> groupList0 = new ArrayList<>();
        groupList0.add("groupList0 - 1");
        groupList0.add("groupList0 - 2");
        groupList0.add("groupList0 - 3");
        groupList0.add("groupList0 - 4");
        groupList0.add("groupList0 - 5");
        groupList0.add("groupList0 - 6");
        groupList0.add("groupList0 - 7");
        groupList0.add("groupList0 - 8");
        groupList0.add("groupList0 - 9");
        groupList0.add("Other");

        groupListLocation.add("blah blah");
        childMapLocation.put(groupListLocation.get(0), groupList0);

        groupListPurpose = new ArrayList<>();
        childMapPurpose = new HashMap<>();

        List<String> groupList2 = new ArrayList<>();
        groupList2.add("groupList0 - 1");
        groupList2.add("groupList0 - 2");
        groupList2.add("groupList0 - 3");
        groupList2.add("groupList0 - 4");
        groupList2.add("groupList0 - 5");
        groupList2.add("groupList0 - 6");
        groupList2.add("groupList0 - 7");
        groupList2.add("groupList0 - 8");
        groupList2.add("groupList0 - 9");
        groupList2.add("Other");


        groupListPurpose.add("blah blah purpose");
        childMapPurpose.put(groupListPurpose.get(0), groupList2);

    }
}
