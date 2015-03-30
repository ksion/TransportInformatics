package com.gatech.whereabouts.whereabouts;

/**
 * Created by Moon Chang on 3/29/2015.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Moon Chang on 3/29/2015.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "dataManager",
            TABLE_DATA = "data",
            KEY_ID = "id",
            KEY_TIME = "time",
            KEY_LOCATION = "location",
            KEY_PURPOSE = "purpose";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_DATA + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_TIME + " TEXT," + KEY_LOCATION + " TEXT," + KEY_PURPOSE + " TEXT)");
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

    public void createData(UserData ud) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_TIME, ud.getTime());
        values.put(KEY_LOCATION, ud.getLocation());
        values.put(KEY_PURPOSE, ud.getPurpose());


        db.insert(TABLE_DATA, null, values);
        db.close();
    }

    public UserData getData(int id) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(TABLE_DATA, new String[] { KEY_ID, KEY_TIME, KEY_LOCATION, KEY_PURPOSE}, KEY_ID + "=?", new String[] { String.valueOf(id) }, null, null, null, null );

        if (cursor != null)
            cursor.moveToFirst();

        UserData ud = new UserData(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2), cursor.getString(3));
        db.close();
        cursor.close();
        return ud;
    }

    public void deleteData(UserData ud) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_DATA, KEY_ID + "=?", new String[] { String.valueOf(ud.getId()) });
        db.close();
    }

    public int getDataCount() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_DATA, null);
        int count = cursor.getCount();
        db.close();
        cursor.close();

        return count;
    }

    public int updateData(UserData ud) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_TIME, ud.getTime());
        values.put(KEY_LOCATION, ud.getLocation());
        values.put(KEY_PURPOSE, ud.getPurpose());

        int rowsAffected = db.update(TABLE_DATA, values, KEY_ID + "=?", new String[] { String.valueOf(ud.getId()) });
        db.close();

        return rowsAffected;
    }

    public List<UserData> getAllDatas() {
        List<UserData> uds = new ArrayList<UserData>();

        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_DATA, null);

        if (cursor.moveToFirst()) {
            do {
                uds.add(new UserData(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2), cursor.getString(3)));
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return uds;
    }
}

