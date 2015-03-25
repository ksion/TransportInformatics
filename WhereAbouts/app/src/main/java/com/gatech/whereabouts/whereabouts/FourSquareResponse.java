package com.gatech.whereabouts.whereabouts;

import java.util.ArrayList;

/**
 * Created by ksion on 3/3/15.
 */
public class FourSquareResponse {
    private ArrayList<FourSquareVenue> venues;

    public FourSquareResponse() {
        venues = new ArrayList<>();
    }

    public void addVenue(FourSquareVenue fourSquareVenue) {
        venues.add(fourSquareVenue);
    }

    public ArrayList<FourSquareVenue> getVenues() {
        return venues;
    }

    public ArrayList<String> getVenueNames() {
        ArrayList<String> venueNames = new ArrayList<>();
        for (FourSquareVenue v : venues) {
            venueNames.add(v.name);
        }
        return venueNames;
    }


 }
