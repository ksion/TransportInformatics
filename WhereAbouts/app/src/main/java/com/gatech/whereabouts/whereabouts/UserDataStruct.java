package com.gatech.whereabouts.whereabouts;

import java.sql.Timestamp;

/**
 * Created by ksion on 4/3/15.
 */
public class UserDataStruct {
    int id;
    Timestamp startDateTime;
    Timestamp endDateTime;
    double startLocLat;
    double startLocLng;
    double endLocLat;
    double endLocLng;
    boolean confirmed;
    String placeName;
    String tripPurpose;
    String tags;
}
