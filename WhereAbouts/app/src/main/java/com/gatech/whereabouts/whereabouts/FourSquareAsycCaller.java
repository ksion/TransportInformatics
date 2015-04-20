package com.gatech.whereabouts.whereabouts;

import android.location.Location;
import android.os.AsyncTask;

import org.json.JSONObject;

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

}
