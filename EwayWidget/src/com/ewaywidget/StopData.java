package com.ewaywidget;
 
import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class StopData {
	private int transportCount = 0;
	private ArrayList<String> transportTypes = new ArrayList<String>();
	private ArrayList<String> transportRouteNums = new ArrayList<String>();
    private ArrayList<String> transportRouteIDs = new ArrayList<String>();
	private ArrayList<Boolean> hasGpss = new ArrayList<Boolean>();
	private ArrayList<String> directions = new ArrayList<String>();
	private ArrayList<String> nextTimes = new ArrayList<String>();
	private ArrayList<String> afterNextTimes = new ArrayList<String>();
    private ArrayList<String> timeSources = new ArrayList<String>();
 
	public int getTransportCount() {
		return transportCount;
	}
 
	public ArrayList<String> getTransportTypes() {
		return transportTypes;
	}
 
	public ArrayList<String> getTransportRouteNums() {
		return transportRouteNums;
	}
	public ArrayList<String> getTransportRouteIDs() {
		return transportRouteIDs;
	}
 
	public ArrayList<Boolean> getHasGps() {
		return hasGpss;
	}
 
	public ArrayList<String> getDirection() {
		return directions;
	}
 
	public ArrayList<String> getNextTime() {
		return nextTimes;
	}
 
	public ArrayList<String> getAfterNextTime() {
		return afterNextTimes;
	}

    public ArrayList<String> getTimeSources() {
        return timeSources;
    }

    public StopData(){
        SQLiteDatabase db = WidgetFactory.context.openOrCreateDatabase("data.db", Context.MODE_PRIVATE, null);
        Cursor cursor = db.rawQuery("SELECT * FROM routes;", null);
        int columns = cursor.getColumnCount();
        String names[] = cursor.getColumnNames();
        if (cursor.moveToFirst()) {
            while ( !cursor.isAfterLast() ) {
                for(int i = 0; i < columns; i++){
                    if ("_id".equals(names[i])){
                        transportRouteIDs.add(cursor.getString(i));
                    }
                    else if ("_route_num".equals(names[i])){
                        transportRouteNums.add(cursor.getString(i));
                    }
                    else if ("_type".equals(names[i])){
                        transportTypes.add(cursor.getString(i));
                    }
                    else if ("_direction".equals(names[i])){
                        directions.add(cursor.getString(i));
                    }
                    else if ("_next_time".equals(names[i])){
                        nextTimes.add(cursor.getString(i));
                    }
                    else if ("_after_next_time".equals(names[i])){
                        afterNextTimes.add(cursor.getString(i));
                    }
                    else if ("_time_source".equals(names[i])){
                        timeSources.add(cursor.getString(i));
                    }
                    else if ("_hasGps".equals(names[i])){
                        if (cursor.getInt(i) == 0){
                            hasGpss.add(false);
                        }
                        else{
                            hasGpss.add(true);
                        }
                    }
                }
                transportCount++;
                cursor.moveToNext();
            }
            cursor.close();
        }
    }

}