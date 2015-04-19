package com.gatech.whereabouts.whereabouts;

/**
 * Created by ksion on 3/3/15.
 */
public class Venue implements Comparable<Venue> {

    public String name;
    public PlaceLocation location;
    public String[] categories;

    public Venue(String name, PlaceLocation location,
                 String[] categories) {
        this.name = name;
        this.location = location;
        this.categories = categories;
    }

    public Venue(String name, PlaceLocation location) {
        this.name = name;
        this.location = location;
    }

    public String[] getVenueCategories() {
        return categories;
    }

    @Override
    public int compareTo(Venue another) {

        double me  = this.location.latitude + this.location.longitude;
        double you = another.location.latitude + another.location.longitude;

        if(me == you) {
            return 0;
        } else {
            Double tmp = Math.floor(me - you);
            return tmp.intValue();
        }
    }

    @Override
    public String toString() {
        return name;
    }
}

