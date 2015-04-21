package com.gatech.whereabouts.whereabouts;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ksion on 4/8/15.
 */

public class TripPurposes {
    String[] purposes = {
            "Home / Residential",
            "Work",
            "School",
            "Religious",
            "Medical",
            "Dental",
            "Shopping / Errands",
            "Social / Recreational",
            "Family",
            "Personal Business",
            "Transporting Someone",
            "Meals",
            "Other"
    };


    public static Map<String, String> loadFromJSONTripPurposes(InputStream in) throws IOException, JSONException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        StringBuilder responseStrBuilder = new StringBuilder();

        String inputStr;
        while ((inputStr = reader.readLine()) != null)
            responseStrBuilder.append(inputStr);

        JsonParser p = new JsonParser();
        JsonElement el =  p.parse(responseStrBuilder.toString());
        JsonElement dictionary = el.getAsJsonObject().get("dictionary");

        return new Gson().fromJson(dictionary,
                new TypeToken<HashMap<String, String>>() {}.getType());

    }
}
