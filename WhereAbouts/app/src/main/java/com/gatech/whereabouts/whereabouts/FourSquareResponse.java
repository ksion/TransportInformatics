package com.gatech.whereabouts.whereabouts;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by ksion on 3/3/15.
 */
public class FourSquareResponse {
    private List<FourSquareVenue> venues;

    public FourSquareResponse() {
        venues = new LinkedList<>();
    }

    public void addVenue(FourSquareVenue fourSquareVenue) {
        venues.add(fourSquareVenue);
    }

    public List<FourSquareVenue> getVenues() {
        return venues;
    }
 }
