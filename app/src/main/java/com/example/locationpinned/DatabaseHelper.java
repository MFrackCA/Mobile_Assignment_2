package com.example.locationpinned;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    public Context context;
    private static final String DATABASE_NAME = "locations.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "my_locations";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_ADDRESS = "street_address";
    public static final String COLUMN_LATITUDE = "street_latitude";
    public static final String COLUMN_LONGITUDE = "street_longitude";
    DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    // Create database
    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME +
                " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_ADDRESS + " TEXT, " +
                COLUMN_LATITUDE + " REAL, " +
                COLUMN_LONGITUDE + " REAL);";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void addAddress(String address, double latitude, double longitude){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        // setup content values to pass to table
        cv.put(COLUMN_ADDRESS, address);
        cv.put(COLUMN_LATITUDE, latitude);
        cv.put(COLUMN_LONGITUDE, longitude);

        long result = db.insert(TABLE_NAME, null, cv);
        if(result == -1){
            Toast.makeText(context, "Failed to add address to Database", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context, "Successfully added address to Database", Toast.LENGTH_SHORT).show();
        }

        db.close();
    }
    public void deleteAddress(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void updateAddress(int id, String address, double latitude, double longitude) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        // setup content values to pass to table
        cv.put(COLUMN_ADDRESS, address);
        cv.put(COLUMN_LATITUDE, latitude);
        cv.put(COLUMN_LONGITUDE, longitude);

        int result = db.update(TABLE_NAME, cv, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        if(result == 0) {
            Toast.makeText(context, "Failed to update address", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Address updated successfully", Toast.LENGTH_SHORT).show();
        }

        db.close();
    }
    public void loadFile(String address, double longitude, double latitude){
        SQLiteDatabase db =this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        // setup content values to pass to table
        cv.put(COLUMN_ADDRESS, address);
        cv.put(COLUMN_LATITUDE, latitude);
        cv.put(COLUMN_LONGITUDE, longitude);


        //insert note into table
        long result = db.insert(TABLE_NAME, null, cv);
        // Check if save was successful
        if(result ==-1){
            Toast.makeText(context, "Failed to add to Database", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context, "Successfully added location to Database", Toast.LENGTH_SHORT).show();
        }
        db.close();
    }

    public Cursor readAllData(String searchBarText) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor;

        if(searchBarText.isEmpty()) {
            cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        } else {
            // Here, you'd search for locations by address. Modify as per requirements.
            cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ADDRESS + " LIKE ?", new String[] {"%" + searchBarText + "%"});
        }

        return cursor;
    }

    public void deleteAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
        db.close();
    }
}
