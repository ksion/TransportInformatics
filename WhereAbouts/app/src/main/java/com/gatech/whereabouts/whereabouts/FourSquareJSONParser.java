package com.gatech.whereabouts.whereabouts;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;

import static java.util.Calendar.getInstance;

public class FourSquareJSONParser {

    public FourSquareJSONParser() {
    }

    public FourSquareResponse parse(JSONObject in) {
        try {
            return readJSONStream(in.getJSONObject("response"));
        } catch (JSONException e) {
            Log.e("Error", "Something bad happened!", e);
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private FourSquareResponse readJSONStream(JSONObject in) throws JSONException {
        FourSquareResponse response = new FourSquareResponse();
        JSONArray venues = in.getJSONArray("venues");

        for (int i = 0; i < venues.length(); i++) {
            JSONObject o = venues.getJSONObject(i);
            String n = o.getString("name");
            PlaceLocation l = extractFourSquareLocation(o);
            response.addVenue(new Venue(n, l));
        }
        return response;
    }

    private PlaceLocation extractFourSquareLocation(JSONObject o) throws JSONException {
        JSONObject location = o.getJSONObject("location");
        PlaceLocation l = new PlaceLocation();

        l.latitude = location.getDouble("lat");
        l.longitude = location.getDouble("lng");

        DateFormat df = DateFormat.getDateTimeInstance();
        l.dateAdded = df.format(getInstance().getTime());
        l.setValid();

        return l;
    }


}
