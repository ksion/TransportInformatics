package com.gatech.whereabouts.whereabouts;

import java.util.ArrayList;

/**
 * Created by ksion on 3/3/15.
 */
public class FourSquareResponse {
    private ArrayList<Venue> venues;

    public FourSquareResponse() {
        venues = new ArrayList<>();
    }

    public void addVenue(Venue venue) {
        venues.add(venue);
    }

    public ArrayList<Venue> getVenues() {
        return venues;
    }

    public ArrayList<String> getVenueNames() {
        ArrayList<String> venueNames = new ArrayList<>();
        for (Venue v : venues) {
            venueNames.add(v.name);
        }
        return venueNames;
    }


 }
