package com.gatech.whereabouts.whereabouts;

import android.app.ListActivity;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutionException;


public class FourSquareActivity extends ListActivity {

    Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        try {
            location = intent.getParcelableExtra("LOCATION");
        } catch (NumberFormatException nfe) {
            System.out.println("Could not parse !");
        }

        String uri = buildStringURI(location);

        AsyncTask<String, Void, JSONObject> taskResponse = new RequestTask().execute(uri);

        JSONObject response = null;
        try {
            response = taskResponse.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }

        ListView listView = (ListView) findViewById(R.id.listView);
        FourSquareResponse locations = readStream(response);




    }

    private FourSquareResponse readStream(JSONObject in) {
        FourSquareJSONParser parser = new FourSquareJSONParser();
        return parser.parse(in);
    }

    private String buildStringURI(Location l) {
        return "https://api.foursquare.com/v2/venues/search?" +
                "ll=" + l.getLatitude() + "," + l.getLongitude() + "&" +
                "client_id=UHQDD5ZR4JJVTNQ5KVEZ2TDRICVMMX2BZ4IFL454EUZXSC4P&" +
                "client_secret=EJ2U4YPYAZUIBQWVWL2VSEI0QMK0ZJGVHUEWWA0YABMXLP4I&" +
                "v=20150214";
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_four_square, menu);
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

    class RequestTask extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... uri) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response;
            JSONObject o;
            try {
                response = httpclient.execute(new HttpGet(uri[0]));
                StatusLine statusLine = response.getStatusLine();
                if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                    BufferedReader rd = new BufferedReader(new
                            InputStreamReader(response.getEntity().getContent()));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = rd.readLine()) != null) {
                        result.append(line);
                    }
                    try {
                        o = new JSONObject(result.toString());
                    } catch (JSONException e) {
                        throw new RuntimeException(e.getMessage());
                    }
                    return o;
                } else {
                    //Closes the connection.
                    response.getEntity().getContent().close();
                    throw new IOException(statusLine.getReasonPhrase());
                }
            } catch (ClientProtocolException e) {
                //TODO Handle problems..
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            super.onPostExecute(result);
            //Do anything with response..
            Log.i("readStream", "i think it worked?! Place breakpoint here.");
        }
    }


}
