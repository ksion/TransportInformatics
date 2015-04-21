package com.gatech.whereabouts.whereabouts;

public class PlaceLocation {
    public double latitude;
    public double longitude;
    public boolean inDatabase;
    public String dateAdded;

    public PlaceLocation() {}

    public PlaceLocation(double lat, double lng) {
        new PlaceLocation(lat, lng, false);
    }

    public PlaceLocation(double lat, double lng, boolean inDatabase) {
        latitude = lat;
        longitude = lng;
        this.inDatabase = inDatabase;
    }
}
