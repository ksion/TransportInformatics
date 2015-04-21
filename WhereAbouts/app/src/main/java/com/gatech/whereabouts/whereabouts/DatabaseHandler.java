package com.gatech.whereabouts.whereabouts;

/**
 * Created by MChang on 3/29/2015.
 * Edited by KSion on 4/03/2015.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final Timestamp BIRTH = new Timestamp(2015, 1, 1, 0, 0, 0, 0);
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "WhereAbouts",
            TABLE_DATA = "user_data",
            KEY_ID = "id",
            KEY_START_DATETIME = "startDateTime",
            KEY_END_DATETIME = "endDateTime",
            KEY_START_LAT = "startLat",
            KEY_START_LNG = "startLng",
            KEY_END_LAT = "endLat",
            KEY_END_LNG = "endLng",
            KEY_CONFIRMED = "confirmed",
            KEY_PLACE_NAME = "placeName",
            KEY_TRIP_PURPOSE = "tripPurpose",
            KEY_TAGS = "tags";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        StringBuilder constructor = new StringBuilder();
        constructor.append("CREATE TABLE "      + TABLE_DATA + "(");
        constructor.append(KEY_ID               + " INTEGER PRIMARY KEY,"); //0
        constructor.append(KEY_START_DATETIME   + " NUMERIC,");             //1
        constructor.append(KEY_END_DATETIME     + " NUMERIC,");             //2
        constructor.append(KEY_START_LAT        + " REAL,");                //3
        constructor.append(KEY_START_LNG        + " REAL,");                //4
        constructor.append(KEY_END_LAT          + " REAL,");                //5
        constructor.append(KEY_END_LNG          + " REAL,");                //6
        constructor.append(KEY_CONFIRMED        + " NUMERIC,");             //7
        constructor.append(KEY_PLACE_NAME       + " TEXT,");                //8
        constructor.append(KEY_TRIP_PURPOSE     + " TEXT,");                //9
        constructor.append(KEY_TAGS             + " TEXT)");                //10

        db.execSQL(constructor.toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DATA);
        onCreate(db);
    }

    public void deleteAll() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("delete from " + TABLE_DATA);
    }

    public void createData(UserDataStruct ud) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_DATA, null);
        int size = cursor.getCount();

        if (size > 0) {
            cursor.moveToLast();
            values.put(KEY_START_DATETIME,  cursor.getString(2));  //endDateTime
            values.put(KEY_START_LAT,       cursor.getDouble(5));  //endLat
            values.put(KEY_START_LNG,       cursor.getDouble(6));  //endLng
        } else {
            values.put(KEY_START_DATETIME,  BIRTH.toString());
            values.put(KEY_START_LAT,       0.0);
            values.put(KEY_START_LNG,       0.0);
        }

        values.put(KEY_END_DATETIME,        ud.endDateTime.toString());
        values.put(KEY_END_LAT,             ud.endLocLat);
        values.put(KEY_END_LNG,             ud.endLocLng);
        values.put(KEY_CONFIRMED,           ud.confirmed);
        values.put(KEY_PLACE_NAME,          ud.placeName);
        values.put(KEY_TRIP_PURPOSE,        ud.tripPurpose);
        values.put(KEY_TAGS,                ud.tags);

        db.insert(TABLE_DATA, null, values);
        db.close();
    }

    public List<UserDataStruct> getAll() {
        List<UserDataStruct> uds = new ArrayList<>();

        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_DATA, null);

        if (cursor.moveToFirst()) {
            do {
                UserDataStruct ud = new UserDataStruct();
                ud.id               = cursor.getInt(0);
                ud.startDateTime    = new Timestamp(cursor.getLong(1));
                ud.endDateTime      = new Timestamp(cursor.getLong(2));
                ud.startLocLat      = cursor.getDouble(3);
                ud.startLocLng      = cursor.getDouble(4);
                ud.endLocLat        = cursor.getDouble(5);
                ud.endLocLng        = cursor.getDouble(6);
                ud.confirmed        = cursor.getInt(7) == 1;
                ud.placeName        = cursor.getString(8);
                ud.tripPurpose      = cursor.getString(9);
                ud.tags             = cursor.getString(10);

                uds.add(ud);
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return uds;
    }

    public Cursor getDatabaseCursor() {
        SQLiteDatabase db = getWritableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_DATA, null);
    }

    public int getDataCount() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_DATA, null);
        int count = cursor.getCount();
        db.close();
        cursor.close();
        return count;
    }

}

