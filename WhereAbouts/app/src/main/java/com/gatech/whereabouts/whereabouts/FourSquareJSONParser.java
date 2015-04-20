package com.gatech.whereabouts.whereabouts;

import android.util.Log;

import com.google.common.base.Joiner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;

import static java.util.Calendar.getInstance;

public class FourSquareJSONParser {

    public FourSquareJSONParser() { }

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
            String c = extractFourSquareCategories(o.getJSONArray("categories"));
            response.addVenue(new Venue(n, l, c));
        }
        return response;
    }

    private String extractFourSquareCategories(JSONArray categories) throws JSONException {
        ArrayList<String> ace = new ArrayList<>();

        for (int i = 0; i < categories.length(); i++) {
            JSONObject o = (JSONObject) categories.get(i);
            String name = o.getString("name");
            String[] cats = name.split(" ");
            ace.addAll(Arrays.asList(cats));
            ace.add(o.getString("shortName"));
        }

        return Joiner.on(',').join(ace.iterator());
    }

    private PlaceLocation extractFourSquareLocation(JSONObject o) throws JSONException {
        JSONObject location = o.getJSONObject("location");
        PlaceLocation l = new PlaceLocation();

        l.latitude = location.getDouble("lat");
        l.longitude = location.getDouble("lng");

        DateFormat df = DateFormat.getDateTimeInstance();
        l.dateAdded = df.format(getInstance().getTime());
        l.inDatabase = false;

        return l;
    }


}
