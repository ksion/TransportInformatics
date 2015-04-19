package com.gatech.whereabouts.whereabouts;

import android.content.Intent;
import android.content.IntentSender;
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

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;


public class DisplayLocationActivity extends ActionBarActivity
        implements ConnectionCallbacks, OnConnectionFailedListener {

    public GoogleApiClient client;
    DatabaseHandler dbHandler;
    public Location location;
    public boolean mResolvingError = false;
    public ArrayList<Venue> locations;

    private static final int REQUEST_RESOLVE_ERROR = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHandler = new DatabaseHandler(getApplicationContext());
        client = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private ArrayList<Venue> mostRecentVenues() {
        SQLiteDatabase db = dbHandler.getWritableDatabase();

        ArrayList<Venue> ace = new ArrayList<>();
        Cursor items = db.rawQuery("select distinct * from user_data where endDateTime > DATE('now', '-3 days') limit 3", null);
        int count = 0;
        if (items.moveToFirst()) {
            do {
                Venue v = new Venue(items.getString(8),
                        new PlaceLocation(items.getDouble(5), items.getDouble(6)));
                ace.add(v);
                count++;
            } while (items.moveToNext());
        }//populate list with three most recent places
        Log.i("mao", "count: " + count);
        return ace;
    }

    public void confirm(View view) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        Date date = new Date();
        String currTime = df.format(date.getTime());
        Spinner tripPurpose = (Spinner) findViewById(R.id.trippurpose);
        Spinner tripCategories = (Spinner) findViewById(R.id.tpcategoryselect);
        Spinner placeName = (Spinner) findViewById(R.id.locationreal);

        UserDataStruct ud = new UserDataStruct();
        ud.endDateTime = Timestamp.valueOf(currTime);
        ud.endLocLat = location.getLatitude();
        ud.endLocLng = location.getLongitude();
        ud.confirmed = true;
        ud.placeName = (String) placeName.getSelectedItem();
        ud.tripPurpose = (String) tripPurpose.getSelectedItem();
        ud.tags = (String) tripCategories.getSelectedItem();

        dbHandler.createData(ud);

        Toast.makeText(getApplicationContext(), "Location Confirmed", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
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
            setContentView(R.layout.activity_display_location);

            TextView latitude = (TextView) findViewById(R.id.latitude);
            TextView longitude = (TextView) findViewById(R.id.longitude);

            latitude.setText(String.valueOf(location.getLatitude()));
            longitude.setText(String.valueOf(location.getLongitude()));

            FourSquareAsycCaller client = new FourSquareAsycCaller(location);
            final FourSquareResponse fourSquareLocations = client.execute();

            Spinner locationSpinner = (Spinner) findViewById(R.id.locationreal);
            locations = prioritizeLocations(location, mostRecentVenues(), fourSquareLocations);

            ArrayAdapter<Venue> locationAdapter = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_spinner_item,
                    locations);
            locationSpinner.setAdapter(locationAdapter);
            locationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
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

    @Override
    protected void onStart() {
        super.onStart();
        if (!mResolvingError) {  // more about this later
            client.connect();
        }
    }

    @Override
    protected void onStop() {
        client.disconnect();
        super.onStop();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (!mResolvingError && result.hasResolution()) {
            try {
                mResolvingError = true;
                result.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                client.connect();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_RESOLVE_ERROR) {
            mResolvingError = false;
            if (resultCode == RESULT_OK) {
                // Make sure the app is not already connected or attempting to connect
                if (!client.isConnecting() &&
                        !client.isConnected()) {
                    client.connect();
                }
            }
        }
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
