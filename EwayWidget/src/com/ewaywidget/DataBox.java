package com.ewaywidget;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.HashSet;
import java.util.Set;

public class DataBox {
    SQLiteDatabase db;
    public static Set<String> favSet;

    public DataBox(Context context){
        db = context.openOrCreateDatabase("data.db", Context.MODE_PRIVATE, null);
        if (favSet == null){
            favSet = new HashSet<String>();
        }
        String tableCreateTimestamp = "CREATE TABLE IF NOT EXISTS timestamp(_time TEXT);";
        String tableCreateRoutes = "CREATE TABLE IF NOT EXISTS routes(_id TEXT, " +
                "_route_num TEXT, " +
                "_type TEXT, " +
                "_direction TEXT, " +
                "_next_time TEXT, " +
                "_after_next_time TEXT, " +
                "_hasGps INTEGER, " +
                "_time_source TEXT);";
        String tableCreateFavs = "CREATE TABLE IF NOT EXISTS favourites(_stop_id TEXT);";
        db.execSQL(tableCreateTimestamp);
        db.execSQL(tableCreateRoutes);
        db.execSQL(tableCreateFavs);
        db.delete("timestamp", "1", null);
        db.delete("routes", "1", null);
        retrieveFavs();
    }

    private void retrieveFavs() {
        favSet.clear();
        Cursor cursor = db.rawQuery("SELECT * FROM favourites;", null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                favSet.add(cursor.getString(0));
                cursor.moveToNext();
            }
            cursor.close();
        }
    }
}
