package com.ewaywidget;

import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ewaywidget.config.SettingsActivity;
import com.ewaywidget.http.Response;
import com.widget.checkid.RequestCheck;

import android.annotation.SuppressLint;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.RemoteViewsService.RemoteViewsFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class WidgetFactory implements RemoteViewsFactory {
	final static int STATUS_CONNECTING = 0;
	final static int STATUS_CONNECTED = 1;
	public static final int UPDATE_DATABASE = 2;
	public static Context context;
	private SQLiteDatabase db;
	public static ArrayList<ListItem> arrayList;
	private final int widgetID;
	private final String listIdUpdate;
	private final String cityUpdate;
	public static Handler h;

	WidgetFactory(Context ctx, Intent intent) {
		context = ctx;
		widgetID = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
				AppWidgetManager.INVALID_APPWIDGET_ID);
		listIdUpdate = intent.getStringExtra(WidgetProvider.LIST_ID);
		cityUpdate = intent.getStringExtra(WidgetProvider.CITY_ID);
		
		Log.d("LIST_ID", listIdUpdate);
	}

	@SuppressLint("HandlerLeak")
	@Override
	public void onCreate() {

		arrayList = new ArrayList<ListItem>();
		db = context
				.openOrCreateDatabase("data.db", Context.MODE_PRIVATE, null);

		h = new Handler() {
			@SuppressLint("ShowToast")
			public void handleMessage(Message msg) {
				RemoteViews rv = new RemoteViews(context.getPackageName(),
						R.layout.widget);
				switch (msg.what) {
				case STATUS_CONNECTING:
					rv.setInt(R.id.imageViewRefresh, "setVisibility", View.GONE);
					rv.setInt(R.id.progressBarRefresh, "setVisibility",
							View.VISIBLE);
					AppWidgetManager.getInstance(context).updateAppWidget(
							widgetID, rv);
					break;
				case STATUS_CONNECTED:
					rv.setInt(R.id.imageViewRefresh, "setVisibility",
							View.VISIBLE);
					rv.setInt(R.id.progressBarRefresh, "setVisibility",
							View.GONE);
					rv.setTextViewText(R.id.tvLastUpdateTime, retrieveTime());
					AppWidgetManager.getInstance(context)
							.notifyAppWidgetViewDataChanged(widgetID,
									R.id.lvList);
					AppWidgetManager.getInstance(context).updateAppWidget(
							widgetID, rv);
					break;
				case UPDATE_DATABASE:
					Thread thread = new Thread(new Runnable() {
						@Override
						public void run() {
							h.sendEmptyMessage(WidgetFactory.STATUS_CONNECTING);
							RequestCheck mRequest = new RequestCheck();
							Response response = new Response(mRequest.execute(
									cityUpdate, listIdUpdate));
							updateDB(response.getData());
							h.sendEmptyMessage(WidgetFactory.STATUS_CONNECTED);
						}
					});
					thread.start();
					break;

				}
			}
		};
		h.sendEmptyMessage(UPDATE_DATABASE);
	}

	private CharSequence retrieveTime() {
		Cursor cursor = db.rawQuery("SELECT * FROM timestamp;", null);
		String output = "";
		if (cursor.moveToFirst()) {
			while (!cursor.isAfterLast()) {
				output = cursor.getString(0);
				cursor.moveToNext();
			}
		}
		return output;
	}

	public static String toStringXML(Document doc) {
		try {
			StringWriter sw = new StringWriter();
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer
					.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

			transformer.transform(new DOMSource(doc), new StreamResult(sw));
			return sw.toString();
		} catch (Exception ex) {
			throw new RuntimeException("Error converting to String", ex);
		}
	}

	public void updateDB(Document data) {
		Element root = data.getDocumentElement();
		Log.i("REQUEST", toStringXML(data));
		SharedPreferences sp2 = context.getSharedPreferences(
				SettingsActivity.WIDGET_PREF_CONFIG, Context.MODE_PRIVATE);

		NodeList routes = root.getElementsByTagName("route");
		NodeList title = root.getElementsByTagName("title");
		String titleStopS = title.item(0).getTextContent();
		Editor editor = sp2.edit();
		editor.putString(SettingsActivity.WIDGET_STOP_CONFIG + widgetID,
				titleStopS);
		editor.apply();

		ContentValues timestampMap = new ContentValues();
		ContentValues routesCv = new ContentValues();
		int transportCount = routes.getLength();
		String timestamp = getTimeString();
		db.delete("timestamp", "1", null);
		db.delete("routes", "1", null);
		timestampMap.put("_time", timestamp);
		db.insert("timestamp", null, timestampMap);
		for (int i = 0; i < transportCount; i++) {
			NodeList routeAttrs = routes.item(i).getChildNodes();

			String type = routeAttrs.item(3).getTextContent();
			String routeNum = routeAttrs.item(1).getTextContent();
			String routeID = routeAttrs.item(0).getTextContent();
			String direction = routeAttrs.item(2).getTextContent();
			String nextTime = routeAttrs.item(10).getTextContent();
			String afterNextTime = routeAttrs.item(12).getTextContent();
			String timeSource = routeAttrs.item(14).getTextContent();
			Boolean hasGps = "true".equals(routeAttrs.item(6).getTextContent());

			int hasGpsInt = 0;
			if (hasGps)
				hasGpsInt = 1;
			routesCv.put("_id", routeID);
			routesCv.put("_route_num", routeNum);
			routesCv.put("_type", type);
			routesCv.put("_direction", direction);
			routesCv.put("_next_time", nextTime);
			routesCv.put("_after_next_time", afterNextTime);
			routesCv.put("_time_source", timeSource);
			routesCv.put("_hasGps", hasGpsInt);

			db.insert("routes", null, routesCv);
		}
		if (DataBox.favSet != null) {
			saveFavs();
		}
	}

	@SuppressLint("SimpleDateFormat")
	private String getTimeString() {
		Calendar c = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		return sdf.format(c.getTime());
	}

	private void saveFavs() {
		db.delete("favourites", "1", null);
		ContentValues favs = new ContentValues();
		Enumeration e = Collections.enumeration(DataBox.favSet);
		while (e.hasMoreElements()) {
			favs.put("_stop_id", e.nextElement().toString());
			db.insert("favourites", null, favs);
		}
	}

	@Override
	public int getCount() {
		return arrayList.size();
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public RemoteViews getLoadingView() {
		// TODO Auto-generated method stub
		return new RemoteViews(context.getPackageName(), R.layout.load);
	}

	@Override
	public RemoteViews getViewAt(int position) {
		RemoteViews rView = new RemoteViews(context.getPackageName(),
				R.layout.item);
		rView.setTextViewText(R.id.textViewTransportType,
				" " + arrayList.get(position).transportType);
		rView.setTextColor(R.id.textViewTransportType, Color.WHITE);
		rView.setTextViewText(R.id.textViewRouteNum,
				arrayList.get(position).transportRouteNum);
		rView.setTextColor(R.id.textViewRouteNum, Color.WHITE);

		if (arrayList.get(position).hasDrawable) {
			rView.setViewVisibility(R.id.textViewTransportType, View.GONE);
			rView.setViewVisibility(R.id.ivTransportType, View.VISIBLE);
			rView.setImageViewResource(R.id.ivTransportType,
					arrayList.get(position).transportTypeDrawable);
		}

		rView.setViewVisibility(R.id.textMin1, TextView.VISIBLE);
		rView.setViewVisibility(R.id.textMin2, TextView.VISIBLE);
		rView.setViewVisibility(R.id.textViewAfterNext, TextView.VISIBLE);

		String nextTime = arrayList.get(position).nextTime;
		String afterNextTime = arrayList.get(position).afterNextTime;
		String timeSource = arrayList.get(position).timeSource;
		if (nextTime.equals("0")) {
			nextTime = "<1";
		} else if (nextTime.equals("*") || nextTime.equals("?")) {
			nextTime = "-- ";
			rView.setViewVisibility(R.id.textMin1, TextView.GONE);
		}
		if (afterNextTime.equals("0")) {
			afterNextTime = "<1";
		} else if (afterNextTime.equals("*") || afterNextTime.equals("?")) {
			afterNextTime = "-- ";
			rView.setViewVisibility(R.id.textMin2, TextView.GONE);
		}

		if (timeSource.equals("gps")) {
			rView.setTextViewText(R.id.textViewGps, "•");
			rView.setTextViewTextSize(R.id.textViewGps,TypedValue.COMPLEX_UNIT_SP , 20);
			rView.setTextColor(R.id.textViewGps, Color.parseColor("#77cc00"));
			rView.setTextColor(R.id.textMin1, Color.parseColor("#77cc00"));
			rView.setTextColor(R.id.textMin2, Color.parseColor("#77cc00"));
			rView.setTextColor(R.id.textViewNext, Color.parseColor("#77cc00"));
			rView.setTextColor(R.id.textViewAfterNext,
					Color.parseColor("#77cc00"));
		} else if (timeSource.equals("schedule")) {
			rView.setTextViewText(R.id.textViewGps, "•");
			rView.setTextViewTextSize(R.id.textViewGps,TypedValue.COMPLEX_UNIT_SP , 20);
			rView.setTextColor(R.id.textViewGps, Color.parseColor("#1897f2"));
			rView.setTextColor(R.id.textMin1, Color.parseColor("#1897f2"));
			rView.setTextColor(R.id.textMin2, Color.parseColor("#1897f2"));
			rView.setTextColor(R.id.textViewNext, Color.parseColor("#1897f2"));
			rView.setTextColor(R.id.textViewAfterNext,
					Color.parseColor("#1897f2"));

			if (!nextTime.equals("<1")) {
				nextTime = " ~" + nextTime;
			}
			if (!afterNextTime.equals("<1")) {
				afterNextTime = " ~" + afterNextTime;
			}

		} else if (timeSource.equals("interval")) {
			rView.setTextViewText(R.id.textViewGps, "•");
			rView.setTextViewTextSize(R.id.textViewGps,TypedValue.COMPLEX_UNIT_SP , 20);
			rView.setViewVisibility(R.id.textViewAfterNext, TextView.GONE);
			rView.setViewVisibility(R.id.textMin2, TextView.GONE);
			rView.setTextColor(R.id.textViewGps, Color.parseColor("#f4b907"));
			rView.setTextColor(R.id.textViewNext, Color.parseColor("#f4b907"));
			rView.setTextColor(R.id.textMin1, Color.parseColor("#f4b907"));

		} else if (timeSource.equals("none")) {
			rView.setTextViewText(R.id.textViewGps, "НЕМАЄ ДАННИХ");
			rView.setTextViewTextSize(R.id.textViewGps,TypedValue.COMPLEX_UNIT_SP , 10);
			rView.setTextColor(R.id.textViewGps, Color.parseColor("#50ffffff"));
			rView.setTextColor(R.id.textViewNext, Color.parseColor("#50ffffff"));
			rView.setTextColor(R.id.textMin1, Color.parseColor("#50ffffff"));
			rView.setViewVisibility(R.id.textViewAfterNext,
					Color.parseColor("#50ffffff"));
			rView.setViewVisibility(R.id.textMin2,
					Color.parseColor("#50ffffff"));
			nextTime = "-- ";
			afterNextTime = "-- ";
			rView.setViewVisibility(R.id.textMin1, TextView.GONE);
		} else {
			rView.setTextViewText(R.id.textViewGps, timeSource);
			rView.setTextColor(R.id.textViewGps, Color.parseColor("#50ffffff"));
			rView.setViewVisibility(R.id.textMin1, TextView.GONE);
		}

		rView.setTextViewText(R.id.textViewNext, nextTime);
		rView.setTextViewText(R.id.textViewAfterNext, afterNextTime);

		rView.setTextViewText(R.id.textViewDirection,
				" › " + arrayList.get(position).transportDirection);

		rView.setTextColor(R.id.textViewDirection, Color.WHITE);
		rView.setImageViewResource(R.id.imageViewStar, R.drawable.icn_fav_off);

		if (arrayList.get(position).isFavourite) {

			rView.setTextColor(R.id.textViewTransportType,
					Color.parseColor("#ffc619"));
			rView.setTextColor(R.id.textViewRouteNum,
					Color.parseColor("#ffc619"));
			rView.setTextColor(R.id.textViewDirection,
					Color.parseColor("#ffc619"));
			if (arrayList.get(position).gps)
				rView.setTextColor(R.id.textViewGps, Color.GREEN);
			rView.setTextColor(R.id.textViewNext, Color.parseColor("#ffc619"));
			rView.setTextColor(R.id.textMin1, Color.parseColor("#ffc619"));
			rView.setTextColor(R.id.textViewAfterNext,
					Color.parseColor("#ffc619"));
			rView.setTextColor(R.id.textMin2, Color.parseColor("#ffc619"));
			rView.setImageViewResource(R.id.imageViewStar,
					R.drawable.icn_fav_on);

		}

		Intent clickIntent = new Intent();
		clickIntent.putExtra(WidgetProvider.WIDGET_ID, widgetID);
		clickIntent.putExtra(WidgetProvider.ROUTE_ID,
				arrayList.get(position).transportRouteID);
		rView.setOnClickFillInIntent(R.id.imageViewStar, clickIntent);

		return rView;
	}

	@Override
	public int getViewTypeCount() {
		return 1;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public void onDataSetChanged() {
		arrayList.clear();
		StopData data = new StopData();
		for (int i = 0; i < data.getTransportCount(); i++) {
			ListItem listItem = new ListItem(i, data);
			if (listItem.isFavourite) {
				arrayList.add(0, listItem);
			} else {
				arrayList.add(listItem);
			}
		}
	}

	@Override
	public void onDestroy() {
	}
}