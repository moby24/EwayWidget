package com.ewaywidget;

public class ListItem {

	String transportType;
	String transportRouteNum;
	String transportRouteID;
	String transportDirection;
	String nextTime;
	String afterNextTime;
    String timeSource;
    int transportTypeDrawable;
    boolean hasDrawable = true;
	boolean gps;
	boolean isFavourite;

	public ListItem(int i, StopData obj) {
		transportType = obj.getTransportTypes().get(i);
		transportRouteNum = obj.getTransportRouteNums().get(i);
		transportDirection = obj.getDirection().get(i);
		nextTime = obj.getNextTime().get(i);
		afterNextTime = obj.getAfterNextTime().get(i);
        timeSource = obj.getTimeSources().get(i);
		gps = obj.getHasGps().get(i);
		transportRouteID = obj.getTransportRouteIDs().get(i);

        if (DataBox.favSet != null && DataBox.favSet.contains(transportRouteID)){
            isFavourite = true;
        }

        if ("Автобус".equals(transportType)){
            if (isFavourite)
                transportTypeDrawable = R.drawable.bus_y;
            else
                transportTypeDrawable = R.drawable.bus_w;
        }
        else if ("Тролейбус".equals(transportType)){
            if (isFavourite)
                transportTypeDrawable = R.drawable.troll_y;
            else
                transportTypeDrawable = R.drawable.troll_w;
        }
        else if ("Маршрутка".equals(transportType) || "Приміський".equals(transportType)){
            if (isFavourite)
                transportTypeDrawable = R.drawable.mbus_y;
            else
                transportTypeDrawable = R.drawable.mbus_w;
        }
        else if ("Трамвай".equals(transportType)){
            if (isFavourite)
                transportTypeDrawable = R.drawable.tram_y;
            else
                transportTypeDrawable = R.drawable.tram_w;
        }
        else{
            hasDrawable = false;
        }
	}
}
