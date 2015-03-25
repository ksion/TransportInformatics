package com.gatech.whereabouts.whereabouts;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ksion on 3/3/15.
 */
public class FourSquareVenue {

    public String name;
    public FourSquareLocation location;
    public List<FourSquareCategory> categories;

    public FourSquareVenue(String name, FourSquareLocation location,
                           List<FourSquareCategory> categories) {
        this.name = name;
        this.location = location;
        this.categories = categories;
    }

    public ArrayList<String> getVenueCategories() {
        ArrayList<String> items = new ArrayList<>();
        for (FourSquareCategory c : categories) {
            items.add(c.name);
        }
        return items;
    }
}

