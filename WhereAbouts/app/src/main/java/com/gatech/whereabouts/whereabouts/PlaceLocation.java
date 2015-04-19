package com.gatech.whereabouts.whereabouts;

public class PlaceLocation {
    public double latitude;
    public double longitude;
    public boolean valid;
    public String dateAdded;

    public PlaceLocation() {}

    public PlaceLocation(double lat, double lng) {
        latitude = lat;
        longitude = lng;
        valid = false;
    }

    public void setValid() {
        if (latitude >= 99.0 || longitude > 180.0) {
            valid = false;
        } else { valid = true; }
    }
}
