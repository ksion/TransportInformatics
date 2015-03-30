package com.gatech.whereabouts.whereabouts;

/**
 * Created by Moon Chang on 3/26/2015.
 */

//all good
public class UserData {
    private String _time, _location, _purpose;
    private  int _id;


    public UserData(int id, String time, String location, String purpose) {
        _id = id;
        _time = time;
        _location = location;
        _purpose = purpose;

    }
    public int getId() { return  _id;}
    public  String getTime() { return  _time;}
    public  String getLocation() { return  _location;}
    public  String getPurpose() { return  _purpose;}

}
