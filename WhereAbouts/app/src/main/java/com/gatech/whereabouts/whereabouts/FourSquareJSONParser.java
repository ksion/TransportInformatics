package com.gatech.whereabouts.whereabouts;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.LinkedList;
import java.util.List;

import static java.util.Calendar.getInstance;

public class FourSquareJSONParser {

    public FourSquareJSONParser() {
    }

    public FourSquareResponse parse(JSONObject in) {
        try {
            Log.i("Final", "hope this works");
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
            FourSquareLocation l = extractFourSquareLocation(o);
            List<FourSquareCategory> c = extractFourSquareCategories(o);
            response.addVenue(new FourSquareVenue(n, l, c));
        }
        return response;
    }

    private FourSquareLocation extractFourSquareLocation(JSONObject o) throws JSONException {
        JSONObject location = o.getJSONObject("location");
        FourSquareLocation l = new FourSquareLocation();

        l.latitude = location.getDouble("lat");
        l.longitude = location.getDouble("lng");

        DateFormat df = DateFormat.getDateTimeInstance();
        l.dateAdded = df.format(getInstance().getTime());
        l.setValid();

        return l;
    }

    private List<FourSquareCategory> extractFourSquareCategories(JSONObject o) throws JSONException {
        JSONArray catArray = o.getJSONArray("categories");
        List<FourSquareCategory> categories = new LinkedList<>();

        for (int i = 0; i < catArray.length(); i++) {
            JSONObject c = catArray.getJSONObject(i);
            FourSquareCategory cat = new FourSquareCategory();
            cat.name = c.getString("name");
            cat.pluralName = c.getString("pluralName");
            cat.shortName = c.getString("shortName");
            cat.primary = c.getBoolean("primary");
            categories.add(cat);
        }

        return categories;
    }
}
