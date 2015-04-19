package com.gatech.whereabouts.whereabouts;

import android.location.Location;
import android.os.AsyncTask;

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

/**
 * Created by ksion on 3/25/15.
 */
public class FourSquareAsycCaller {

    String uri;
    Location location;
    public FourSquareAsycCaller(Location l) {
        location = l;
        uri = buildStringURI(location);
    }

    private String buildStringURI(Location l) {
        return "https://api.foursquare.com/v2/venues/search?" +
                "ll=" + l.getLatitude() + "," + l.getLongitude() + "&" +
                "client_id=UHQDD5ZR4JJVTNQ5KVEZ2TDRICVMMX2BZ4IFL454EUZXSC4P&" +
                "client_secret=EJ2U4YPYAZUIBQWVWL2VSEI0QMK0ZJGVHUEWWA0YABMXLP4I&" +
                "v=20150214&" +
                "limit=5";
    }

    public FourSquareResponse execute() {
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

        return readStream(response);
    }

    private FourSquareResponse readStream(JSONObject in) {
        FourSquareJSONParser parser = new FourSquareJSONParser();
        return parser.parse(in);
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
        }
    }
}
