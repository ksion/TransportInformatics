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
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class MainActivity extends ActionBarActivity {

    DatabaseHandler dbHandler;
    TextView textView;
    ListView dataListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHandler = new DatabaseHandler(getApplicationContext());




        init();
        expandableListView = (ExpandableListView) findViewById(R.id.placelist);
        myExpandableListAdapter = new MyExpandableListAdapter(this, groupList, childMap);
        expandableListView.setAdapter(myExpandableListAdapter);

        expandableListViewP = (ExpandableListView) findViewById(R.id.purposelist);
        myExpandableListAdapterP = new MyExpandableListAdapter(this, groupListP, childMapP);
        expandableListViewP.setAdapter(myExpandableListAdapterP);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
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
        }
        catch(Exception sqlEx) {
            Log.e("MainActivity", sqlEx.getMessage(), sqlEx);
        }
    }

    public void dbTest(View view) {
        UserDataStruct ud = new UserDataStruct();
        Date d = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        String currTime = df.format(d.getTime());
        ud.endDateTime         = Timestamp.valueOf(currTime); //10min
        ud.endLocLat           = 33.777614;
        ud.endLocLng           = -84.404826; // 6th street apartments
        ud.confirmed           = true;
        ud.placeName           = "Sixth Street Apartments";
        ud.tripPurpose         = "Home";
        ud.tags                = "home, residential, dorm";
        dbHandler.createData(ud);
        Toast.makeText(getApplicationContext(), "Data has been added", Toast.LENGTH_SHORT).show();
        textView.setText("#Datapoints: "+ dbHandler.getDataCount());
    }

    public void clear(View view) {
        dbHandler.deleteAll();
        textView.setText("#Datapoints: " + dbHandler.getDataCount());
    }

    public void listing(View view) {
        List<UserDataStruct> uds = dbHandler.getAll();
        if (uds.size() > 0) {
            ListAdapter adapt = new ArrayAdapterUserDataStruct(this, R.layout.list_userdata,
                    uds);
            dataListView.setAdapter(adapt);
        }
    }
    ExpandableListView expandableListView;
    MyExpandableListAdapter myExpandableListAdapter;

    ExpandableListView expandableListViewP;
    MyExpandableListAdapter myExpandableListAdapterP;

    List<String> groupList;
    HashMap<String, List<String>> childMap;

    List<String> groupListP;
    HashMap<String, List<String>> childMapP;


    private void init() {
        groupList = new ArrayList<String>();
        childMap = new HashMap<String, List<String>>();

        List<String> groupList0 = new ArrayList<String>();
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



        groupList.add("blah blah");


        childMap.put(groupList.get(0), groupList0);

        groupListP = new ArrayList<String>();
        childMapP = new HashMap<String, List<String>>();

        List<String> groupList2 = new ArrayList<String>();
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



        groupListP.add("blah blah purpose");


        childMapP.put(groupListP.get(0), groupList2);

    }





}
