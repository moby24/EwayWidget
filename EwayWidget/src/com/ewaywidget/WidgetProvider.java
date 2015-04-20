package com.ewaywidget;

import java.util.Arrays;
import com.ewaywidget.config.SettingsActivity;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

public class WidgetProvider extends AppWidgetProvider {

	private static final String WIDGET_REFRESH = "widget_refresh";
	private static final String URI_SCHEME = "uri_scheme";
	private final static String ACTION_ON_CLICK = "action_on_click";
	static final String WIDGET_ID = "widget_id";
	static final String ROUTE_ID = "route_id";
	static final String LIST_ID = "list_id";
	static final String CITY_ID = "city_id";

	@SuppressLint("HandlerLeak")
	@Override
	public void onUpdate(final Context context,
			AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);

		if (SettingsActivity.dataBox == null) {
			SettingsActivity.dataBox = new DataBox(context);
		}
		for (int i : appWidgetIds) {
//			downloadWidget(context, appWidgetManager, i);
		}

	}

	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);

		SharedPreferences sp = context.getSharedPreferences(
				SettingsActivity.WIDGET_PREF_CONFIG, Context.MODE_PRIVATE);

		ConnectivityManager conMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();

		if (ACTION_ON_CLICK.equalsIgnoreCase(intent.getAction())) {

			int appWidgetId = intent.getIntExtra(WIDGET_ID, -1);
			String routeId = intent.getStringExtra(ROUTE_ID);

			if (appWidgetId != -1) {
				if (DataBox.favSet.contains(routeId)) {
					DataBox.favSet.remove(routeId);
				} else {
					DataBox.favSet.add(routeId);
				}
				if (activeNetwork != null) {
					updateWidgetList(context,
							AppWidgetManager.getInstance(context), sp,
							appWidgetId);
				}
				if (activeNetwork == null) {

					downloadWidget(context,
							AppWidgetManager.getInstance(context), appWidgetId);
				}
			}

		}

		final String action = intent.getAction();

		if (AppWidgetManager.ACTION_APPWIDGET_DELETED.equals(action)) {
			final int appWidgetId = intent.getExtras().getInt(
					AppWidgetManager.EXTRA_APPWIDGET_ID,
					AppWidgetManager.INVALID_APPWIDGET_ID);

			if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
				this.onDeleted(context, new int[] { appWidgetId });

			}
		} else if (WIDGET_REFRESH.equals(action)) {
			Uri uri = intent.getData();
			int widget_id = 0;
			if (uri != null) {
				widget_id = Integer
						.parseInt(uri.getQueryParameter("widget_id"));
			}

			if (widget_id != AppWidgetManager.INVALID_APPWIDGET_ID) {
				if (activeNetwork != null) {
					updateWidget(context, AppWidgetManager.getInstance(context), sp, widget_id);
					if (WidgetFactory.h != null) {
						WidgetFactory.h
								.sendEmptyMessage(WidgetFactory.UPDATE_DATABASE);
					}
				}
				if (activeNetwork == null) {
					downloadWidget(context,
							AppWidgetManager.getInstance(context), widget_id);
				}

			}
		} else {

			super.onReceive(context, intent);

		}

	}

	public static void updateWidget(Context context,
			AppWidgetManager appWidgetManager, SharedPreferences sp,
			int appWidgetId) {
		RemoteViews rv = new RemoteViews(context.getPackageName(),
				R.layout.widget);

		String widgetId = sp.getString(SettingsActivity.WIDGET_TEXT_CONFIG
				+ appWidgetId, null);
		
		String cityId = sp.getString(SettingsActivity.WIDGET_CITY_CONFIG
				+ appWidgetId, null);

		setConfigureActivity(rv, context, appWidgetId);

		setUpdateTV(rv, context, sp, appWidgetId);

		setList(rv, context,widgetId,cityId, appWidgetId);

		setListClick(rv, context);

		setRefresh(rv, context, appWidgetId);

		appWidgetManager.updateAppWidget(appWidgetId, rv);
		appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId,
				R.id.lvList);

	}

	static void updateWidgetList(Context context,
			AppWidgetManager appWidgetManager, SharedPreferences sp,
			int appWidgetId) {
		RemoteViews rv = new RemoteViews(context.getPackageName(),
				R.layout.widget);
		String widgetId = sp.getString(SettingsActivity.WIDGET_TEXT_CONFIG
				+ appWidgetId, null);
		
		String cityId = sp.getString(SettingsActivity.WIDGET_CITY_CONFIG
				+ appWidgetId, null);
		setConfigureActivity(rv, context, appWidgetId);

		setList(rv, context, widgetId,cityId, appWidgetId);

		setListClick(rv, context);

		setRefresh(rv, context, appWidgetId);

		appWidgetManager.updateAppWidget(appWidgetId, rv);
		appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId,
				R.id.lvList);
	}

	public static void downloadWidget(Context context,
			AppWidgetManager appWidgetManager, int appWidgetId) {
		RemoteViews rv = new RemoteViews(context.getPackageName(),
				R.layout.progress);
		SharedPreferences sp = context.getSharedPreferences(SettingsActivity.WIDGET_PREF_CONFIG,
				Context.MODE_PRIVATE);
		String stopText = sp.getString(SettingsActivity.WIDGET_STOP_CONFIG + appWidgetId, null);
		rv.setTextViewText(R.id.tvUpdateNew, stopText);
		appWidgetManager.updateAppWidget(appWidgetId, rv);

	}

	private static void setConfigureActivity(RemoteViews rv, Context context,
			int appWidgetId) {
		Intent configIntent = new Intent(context, SettingsActivity.class);
		configIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);
		configIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
		PendingIntent pIntent = PendingIntent.getActivity(context, appWidgetId,
				configIntent, 0);
		rv.setOnClickPendingIntent(R.id.tvUpdate, pIntent);

	}

	@SuppressLint("NewApi")
	private static void setUpdateTV(RemoteViews rv, Context context,
			SharedPreferences sp, int appWidgetId) {

		String titleStopF = sp.getString(SettingsActivity.WIDGET_STOP_CONFIG
				+ appWidgetId, "Завантаження...");
		Log.i("STOP_FINISH", "" + titleStopF);

		rv.setTextViewText(R.id.tvUpdate, titleStopF);
		Intent updIntent = new Intent(context, WidgetProvider.class);
		updIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
		updIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,
				new int[] { appWidgetId });
		PendingIntent updPIntent = PendingIntent.getBroadcast(context, 0,
				updIntent, 0);
		rv.setOnClickPendingIntent(R.id.imageViewRefresh, updPIntent);
	}

	private static void setList(RemoteViews rv, Context context,
			String widgetTextId, String widgetCityId , int appWidgetId) {
		Intent adapter = new Intent(context, WidgetService.class);
		adapter.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
		adapter.putExtra(LIST_ID, widgetTextId);
		adapter.putExtra(CITY_ID, widgetCityId);
		Uri data = Uri.parse(adapter.toUri(Intent.URI_INTENT_SCHEME));
		adapter.setData(data);
		rv.setRemoteAdapter(R.id.lvList, adapter);
	}

	private static void setListClick(RemoteViews rv, Context context) {
		Intent listClickIntent = new Intent(context, WidgetProvider.class);
		listClickIntent.setAction(ACTION_ON_CLICK);
		PendingIntent listClickPIntent = PendingIntent.getBroadcast(context, 0,
				listClickIntent, 0);
		rv.setPendingIntentTemplate(R.id.lvList, listClickPIntent);
	}

	private static void setRefresh(RemoteViews rv, Context context,
			int appWidgetId) {

		Intent active = new Intent(context, WidgetProvider.class);
		active.setAction(WIDGET_REFRESH);
		Uri data = Uri.parse(URI_SCHEME + "://widget#");
		data = data.buildUpon()
				.appendQueryParameter("widget_id", String.valueOf(appWidgetId))
				.build();
		active.setData(data);
		PendingIntent pi = PendingIntent.getBroadcast(context, 0, active, 0);
		rv.setOnClickPendingIntent(R.id.imageViewRefresh, pi);
	}

	@Override
	public void onEnabled(Context context) {
		// TODO Auto-generated method stub
		super.onEnabled(context);
		Log.d("OPEN_E", "onEnabled");

	}

	@Override
	public void onDisabled(Context context) {
		// TODO Auto-generated method stub
		super.onDisabled(context);
		Log.d("OPEN_D", "onDisabled");
	}

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		// TODO Auto-generated method stub
		SharedPreferences sp = context.getSharedPreferences(
				SettingsActivity.WIDGET_PREF_CONFIG, Context.MODE_PRIVATE);
		sp.edit().clear();
		Log.d("OPEN_Del", "onDeleted" + Arrays.toString(appWidgetIds));
		super.onDeleted(context, appWidgetIds);
	}

}