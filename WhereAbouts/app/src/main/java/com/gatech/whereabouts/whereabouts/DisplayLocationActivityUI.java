package com.gatech.whereabouts.whereabouts;

import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ExpandableListView;

import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;


/**
 * Created by liumomo610 on 4/22/15.
 */
public class DisplayLocationActivityUI extends ActionBarActivity implements
        ConnectionCallbacks, OnConnectionFailedListener{


    public GoogleApiClient client;
    public DatabaseHandler dbHandler;
    public Location location;
    public ArrayList<Venue> locations;
    public ArrayList<String> tripPurposes;
    public Map<String, String> keywordDictionary;
    public String savedTags;

    ExpandableListView locationListView;
    ExpandableListView tripPurposeListView;

    ExpandableSelectListAdapter locationExpandableAdapter;
    ExpandableSelectListAdapter tripPurposeExpandableAdapter;

    List<Venue> groupListLocation;
    HashMap<Venue, List<Venue>> childMapLocation;

    List<String> groupListPurpose;
    HashMap<String, List<String>> childMapPurpose;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        dbHandler = new DatabaseHandler(getApplicationContext());
        try {
            keywordDictionary = TripPurposes.loadFromJSONTripPurposes(
                    getResources().openRawResource(R.raw.keyword_dictionary));
            Log.i("keywordDic", String.valueOf(keywordDictionary.isEmpty()));
        } catch (IOException e) {

        } catch (JSONException e) {
            e.printStackTrace();
        }
        client = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }


//        init();

//        tripPurposeListView = (ExpandableListView) findViewById(R.id.purposelist);
//        tripPurposeExpandableAdapter = new ExpandableSelectListAdapter(
//                this,
//                groupListPurpose,
//                childMapPurpose,
//                R.layout.purposelist_parent,
//                R.layout.purposelist_child
//        );
//        tripPurposeListView.setAdapter(tripPurposeExpandableAdapter);


//    private void init() {
//        groupListLocation = new ArrayList<>();
//        childMapLocation = new HashMap<>();
//
//        List<String> groupList0 = new ArrayList<>();
//        groupList0.add("groupList0 - 1");
//        groupList0.add("groupList0 - 2");
//        groupList0.add("groupList0 - 3");
//        groupList0.add("groupList0 - 4");
//        groupList0.add("groupList0 - 5");
//        groupList0.add("groupList0 - 6");
//        groupList0.add("groupList0 - 7");
//        groupList0.add("groupList0 - 8");
//        groupList0.add("groupList0 - 9");
//        groupList0.add("Other");
//
////        groupListLocation.add("blah blah");
////        childMapLocation.put(groupListLocation.get(0), groupList0);
//
//        groupListPurpose = new ArrayList<>();
//        childMapPurpose = new HashMap<>();
//
//        List<String> groupList2 = new ArrayList<>();
//        groupList2.add("groupList0 - 1");
//        groupList2.add("groupList0 - 2");
//        groupList2.add("groupList0 - 3");
//        groupList2.add("groupList0 - 4");
//        groupList2.add("groupList0 - 5");
//        groupList2.add("groupList0 - 6");
//        groupList2.add("groupList0 - 7");
//        groupList2.add("groupList0 - 8");
//        groupList2.add("groupList0 - 9");
//        groupList2.add("Other");
//
//
//        groupListPurpose.add("blah blah purpose");
//        childMapPurpose.put(groupListPurpose.get(0), groupList2);
//
//    }





    @Override
    protected void onStart() {
        super.onStart();
        client.connect();
    }

    @Override
    protected void onStop() {
        client.disconnect();
        super.onStop();
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

    @Override
    public void onConnected(Bundle bundle) {
        location = LocationServices.FusedLocationApi.getLastLocation(client);

        if (location != null) {
            setContentView(R.layout.activity_display_location_ui);

//            TextView latitude = (TextView) findViewById(R.id.latitude);
//            TextView longitude = (TextView) findViewById(R.id.longitude);
//
//            latitude.setText(String.valueOf(location.getLatitude()));
//            longitude.setText(String.valueOf(location.getLongitude()));

            FourSquareAsycCaller client = new FourSquareAsycCaller(location);
            final FourSquareResponse fourSquareLocations = client.execute();

//            final Spinner locationSpinner = (Spinner) findViewById(R.id.locationreal);

            locations = prioritizeLocations(location, mostRecentVenues(), fourSquareLocations);

            prepareListData();

            locationListView = (ExpandableListView) findViewById(R.id.placelist);
            locationExpandableAdapter = new ExpandableSelectListAdapter(
                    this,
                    groupListLocation,
                    childMapLocation,
                    R.layout.placelist_parent,
                    R.layout.placelist_child
            );
            locationListView.setAdapter(locationExpandableAdapter);


//            ArrayAdapter<Venue> locationAdapter = new ArrayAdapter<>(
//                    this,
//                    android.R.layout.simple_spinner_item,
//                    locations);
//            locationSpinner.setAdapter(locationAdapter);
//            locationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                @Override
//                public void onItemSelected(AdapterView<?> parent, View view, int position,
//                                           long id) {
//                    Spinner tripPurposeSpinner = (Spinner) findViewById(R.id.trippurpose);
//
//                    savedTags = ((Venue) locationSpinner.getSelectedItem()).categories;
//
//                    tripPurposes = prioritizeTripPurposes((Venue) locationSpinner.getSelectedItem());
//
//                    ArrayAdapter<String> tripPurposeAdapter = new ArrayAdapter<>(
//                            parent.getContext(),
//                            android.R.layout.simple_spinner_item,
//                            tripPurposes);
//
//                    tripPurposeSpinner.setAdapter(tripPurposeAdapter);
//                    tripPurposeSpinner.setSelection(0);
//                }
//
//                @Override
//                public void onNothingSelected(AdapterView<?> parent) { }
//            });

        }
    }

    @Override
    public void onConnectionSuspended(int i) { }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) { }

    public void confirm(View view) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        Date date = new Date();
        String currTime = df.format(date.getTime());
//        Spinner placeName = (Spinner) findViewById(R.id.locationreal);
//        Spinner tripPurpose = (Spinner) findViewById(R.id.trippurpose);

        UserDataStruct ud = new UserDataStruct();
        ud.endDateTime = Timestamp.valueOf(currTime);
        ud.endLocLat = location.getLatitude();
        ud.endLocLng = location.getLongitude();
        ud.confirmed = true;
//        ud.placeName = ((Venue) placeName.getSelectedItem()).name;
//        ud.tripPurpose = (String) tripPurpose.getSelectedItem();
        ud.tags = savedTags == null || savedTags.isEmpty() ? savedTags : "";

        dbHandler.createData(ud);

        Toast.makeText(getApplicationContext(), "Location Confirmed", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private ArrayList<Venue> mostRecentVenues() {
        SQLiteDatabase db = dbHandler.getWritableDatabase();
        ArrayList<Venue> ace = new ArrayList<>();
        Cursor items = db.rawQuery("select distinct * from user_data where endDateTime > " +
                "DATE('now', '-3 days') limit 3", null);
        if (items.moveToFirst()) {
            do {
                Venue v = new Venue(items.getString(8),
                        new PlaceLocation(items.getDouble(5), items.getDouble(6), true));
                v.location.dateAdded = items.getString(2);
                v.categories = items.getString(10);
                ace.add(v);
            } while (items.moveToNext());
        }//populate list with three most recent places
        return ace;
    }

    private ArrayList<Venue> prioritizeLocations(Location location, ArrayList<Venue> pastVenues,
                                                 FourSquareResponse fourSquareLocations) {
        ArrayList<Venue> fourSquareVenues = fourSquareLocations.getVenues();
        ArrayList<Venue> priority = new ArrayList<>();

        ArrayList<Venue> allVenues = new ArrayList<>();
        allVenues.addAll(pastVenues);
        allVenues.addAll(fourSquareVenues);

        Map<Double, DataStore> queue = new TreeMap<>(); //handles sorting
        for (int i = 0; i < allVenues.size(); i++) {
            double euclideanDist = Math.sqrt(
                    Math.pow(location.getLatitude() - allVenues.get(i).location.latitude, 2) +
                            Math.pow(location.getLongitude() - allVenues.get(i).location.longitude, 2));

            if (euclideanDist < 3) {
                queue.put(euclideanDist, new DataStore(i, allVenues.get(i)));
            }
        }

        ArrayList<String> trackDupes = new ArrayList<>();
        for (DataStore d : queue.values()) {
            if (!trackDupes.contains(d.venue.name)) {
                priority.add(d.venue);
                trackDupes.add(d.venue.name);
            }
        }

        return priority;
    }

    private ArrayList<String> prioritizeTripPurposes(Venue curr) {
        ArrayList<String> priority = new ArrayList<>();
        List<String> tp = new ArrayList<>(Arrays.asList(new TripPurposes().purposes));
        if (curr.location.inDatabase) {
            SQLiteDatabase db = dbHandler.getWritableDatabase();
            String escapedName = curr.name.replaceAll("'", "\'");
            String sqlQuery = "select tripPurpose from user_data where endDateTime='" +
                    String.valueOf(curr.location.dateAdded.toCharArray()) + "' and placeName='" + escapedName + "'";
            Cursor item = db.rawQuery(sqlQuery, null);


            if (item.moveToFirst()) {
                priority.add(item.getString(0));
            }

            if (!priority.isEmpty()) {
                tp.remove(priority.get(0));
            }
        }

        if (curr.categories != null) {
            String[] fourSqTags = curr.categories.split(",");
            ArrayList<String> foundTripPurpose = findTagInKeywordDictionary(fourSqTags);

            if (foundTripPurpose.isEmpty() && priority.isEmpty()) {
                tp.add(0, "Select your trip purpose");
            } else {
                priority.addAll(foundTripPurpose);
                for (String x : foundTripPurpose) {
                    tp.remove(x);
                }
            }
        }

        priority.addAll(tp);

        return priority; //return most recent trip purpose if you have it
    }

    private ArrayList<String> findTagInKeywordDictionary(String[] fourSqTags) {
        ArrayList<String> purposes = new ArrayList<>();
        for (String fourSqTag : fourSqTags) {
            for (Map.Entry<String,String> k : keywordDictionary.entrySet()) {
                for (String keyword : k.getValue().split(",")) {
                    if (fourSqTag.equalsIgnoreCase(keyword) && !purposes.contains(k.getKey())) {
                        purposes.add(k.getKey());
                    }
                }
            }
        }
        return purposes;
    }

    private class DataStore {
        public int index;
        public Venue venue;

        public DataStore(int i, Venue v) {
            index = i;
            venue = v;
        }
    }






//    ***********************
//ExpandableListView locationListView;
//    ExpandableListView tripPurposeListView;
//
//    ExpandableSelectListAdapter locationExpandableAdapter;
//    ExpandableSelectListAdapter tripPurposeExpandableAdapter;
//
//    List<String> groupListLocation;
//    HashMap<String, List<String>> childMapLocation;
//
//    List<String> groupListPurpose;
//    HashMap<String, List<String>> childMapPurpose;
private void prepareListData() {
    groupListLocation = new ArrayList<>();
    childMapLocation = new HashMap<>();


    groupListLocation.add(locations.get(0));
    childMapLocation.put(locations.get(0), locations); // Header, Child data

}




}
