package com.gatech.whereabouts.whereabouts;

public class FourSquareLocation {
    public double latitude;
    public double longitude;
    public boolean valid;
    public String dateAdded;

    public FourSquareLocation() {}

    public void setValid() {
        if (latitude >= 99.0 || longitude > 180.0) {
            valid = false;
        } else { valid = true; }
    }
}
