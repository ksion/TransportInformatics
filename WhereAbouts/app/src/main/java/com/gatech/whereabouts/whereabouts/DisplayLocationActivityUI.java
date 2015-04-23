package com.gatech.whereabouts.whereabouts;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;


/**
 * Created by liumomo610 on 4/22/15 at 9:48 PM.
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

    ExpandableSelectListAdapter<Venue> locationExpandableAdapter;
    ExpandableSelectListAdapter<String> tripPurposeExpandableAdapter;

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
        } catch (Exception e) {
            e.printStackTrace();
        }

        client = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

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

            FourSquareAsycCaller client = new FourSquareAsycCaller(location);
            final FourSquareResponse fourSquareLocations = client.execute();

            groupListLocation = new ArrayList<>();
            childMapLocation = new HashMap<>();

            groupListPurpose = new ArrayList<>();
            childMapPurpose = new HashMap<>();

            locations = prioritizeLocations(location, mostRecentVenues(), fourSquareLocations);
            tripPurposes = prioritizeTripPurposes(locations.get(0));

            locationListView = (ExpandableListView) findViewById(R.id.placelist);
            locationExpandableAdapter = new ExpandableSelectListAdapter<>(
                    this,
                    groupListLocation,
                    childMapLocation,
                    R.layout.placelist_parent,
                    R.layout.placelist_child
            );
            locationListView.setAdapter(locationExpandableAdapter);
            locationListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                    savedTags = ((Venue) parent.getSelectedItem()).categories;
                    tripPurposes = prioritizeTripPurposes((Venue) parent.getSelectedItem());

                    tripPurposeExpandableAdapter = new ExpandableSelectListAdapter<>(
                            parent.getContext(),
                            groupListPurpose,
                            childMapPurpose,
                            R.layout.purposelist_parent,
                            R.layout.purposelist_child
                    );
                    tripPurposeListView.setAdapter(tripPurposeExpandableAdapter);
                    tripPurposeListView.setSelection(0);
                    return false;
                }
            });

            tripPurposeListView = (ExpandableListView) findViewById(R.id.purposelist);
            tripPurposeExpandableAdapter = new ExpandableSelectListAdapter<>(
                    this,
                    groupListPurpose,
                    childMapPurpose,
                    R.layout.purposelist_parent,
                    R.layout.purposelist_child
            );
            tripPurposeListView.setAdapter(tripPurposeExpandableAdapter);
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

        UserDataStruct ud = new UserDataStruct();
        ud.endDateTime = Timestamp.valueOf(currTime);
        ud.endLocLat = location.getLatitude();
        ud.endLocLng = location.getLongitude();
        ud.confirmed = true;
        ud.placeName = ((Venue) locationListView.getSelectedItem()).name;
        ud.tripPurpose = (String) tripPurposeListView.getSelectedItem();
        ud.tags = savedTags == null || savedTags.isEmpty() ? savedTags : "";

        dbHandler.createData(ud);

        Toast.makeText(getApplicationContext(), "Location Confirmed", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void skip(View view) {
        Venue skip = new Venue("**Skipped**",
                new PlaceLocation(location.getLatitude(), location.getLongitude()));
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        Date date = new Date();
        String currTime = df.format(date.getTime());

        UserDataStruct ud = new UserDataStruct();
        ud.endDateTime = Timestamp.valueOf(currTime);
        ud.endLocLat = location.getLatitude();
        ud.endLocLng = location.getLongitude();
        ud.confirmed = false;
        ud.placeName = skip.name;
        ud.tripPurpose = "**Skipped**";
        ud.tags = savedTags == null || savedTags.isEmpty() ? savedTags : "";

        dbHandler.createData(ud);

        Toast.makeText(getApplicationContext(), "User Opted Skip Complete", Toast.LENGTH_SHORT).show();

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
                if (items.getInt(7) == 1) {
                    Venue v = new Venue(items.getString(8),
                            new PlaceLocation(items.getDouble(5), items.getDouble(6), true));
                    v.location.dateAdded = items.getString(2);
                    v.categories = items.getString(10);
                    ace.add(v);
                }
            } while (items.moveToNext());
        }

        items.close();
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

        groupListLocation.add(priority.get(0));
        childMapLocation.put(priority.get(0), priority);

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

            if (item.moveToFirst()) { priority.add(item.getString(0)); }

            if (!priority.isEmpty()) { tp.remove(priority.get(0)); }
            item.close();
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

        groupListPurpose.add(priority.get(0));
        childMapPurpose.put(priority.get(0), priority);


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
}
