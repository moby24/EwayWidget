package com.ewaywidget.config;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.crashlytics.android.Crashlytics;
import com.ewaywidget.DataBox;
import com.ewaywidget.WidgetFactory;
import com.ewaywidget.WidgetProvider;
import com.ewaywidget.R;
import com.ewaywidget.http.Response;
import com.widget.checkid.RequestCheck;

import android.annotation.SuppressLint;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;
import android.util.Log;
import android.widget.Toast;

public class SettingsActivity extends PreferenceActivity implements
		SharedPreferences.OnSharedPreferenceChangeListener {
	SharedPreferences prefs;
	SharedPreferences.Editor editor;
	String inString;
	private Preference country;
	private Preference city;
	private Preference stop;
	private String cityKey;
	private String countryKey;
	private String stopId;

	SeekBarDialogRadius sbdr;
	SeekBarDialog sbd;
	EditTextPreference editTextPreference;
	MyListPreference listPreferenceView;

	static HashCity CITIES = new HashCity();
	private int widgetID = AppWidgetManager.INVALID_APPWIDGET_ID;
	private Intent resultValue;
	private final String LOG_TAG = "myLogs";
	public final static String WIDGET_PREF_CONFIG = "widget_pref_config";
	public final static String WIDGET_TEXT_CONFIG = "widget_text_config";
	public final static String WIDGET_CITY_CONFIG = "widget_city_config";
	public final static String WIDGET_STOP_CONFIG = "widget_stop_config";

	public final static String WIDGET_GPS_INTERVAL = "widget_gps_interval";
	public final static String WIDGET_GPS_INTERVAL_SEC = "widget_gps_interval_text_sec";
	public final static String WIDGET_GPS_UPDATE_TEXT = "widget_gps_update_text";
	protected static final int TOAST = 0;
	protected static final int WIDGET = 1;

	public static DataBox dataBox;
	public static Context context;
	static Handler h;
	private String checkCity;
	private String responseCity;
	public String titleStop;
	public NodeList title;

	@SuppressLint("HandlerLeak")
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		context = this;
		addPreferencesFromResource(R.xml.settings);
		
		listPreferenceView = (MyListPreference) findPreference("list");
		String[] array = { "Ручне", "Автооновлення" };
		listPreferenceView.setEntries(array);
		listPreferenceView.setDefaultValue("Ручне");
		listPreferenceView.setEntryValues(array);

		sbdr = (SeekBarDialogRadius) findPreference("dialogSeekBarPreferenceRadius");
		sbdr.setEnabled(false);
		sbd = (SeekBarDialog) findPreference("prefGpsInterval");
		sbd.setEnabled(false);

		editTextPreference = (EditTextPreference) findPreference("idstop");

		editTextPreference
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						// TODO Auto-generated method stub

						editTextPreference.setSummary(editTextPreference
								.getText());

						Thread mThread = new Thread(new Runnable() {
							@Override
							public void run() {
								String textId = editTextPreference.getText();
								RequestCheck mRequest = new RequestCheck();
								Response response = new Response(mRequest
										.execute(responseCity, textId));
								Element root = response.getData()
										.getDocumentElement();
								NodeList routes = root
										.getElementsByTagName("route");
								title = root.getElementsByTagName("title");

								int transportCount = routes.getLength();
								if (transportCount == 0) {
									Log.i("LOG", "" + routes.getLength());
									h.sendEmptyMessage(TOAST);
								} else {
									Log.i("LOG", "" + routes.getLength());
									h.sendEmptyMessage(WIDGET);
								}
							}
						});
						mThread.start();

						return true;
					}
				});

		countryKey = "country";
		cityKey = "city";
		stopId = "idstop";

		prefs = PreferenceManager.getDefaultSharedPreferences(this);

		country = findPreference(countryKey);
		country.setSummary(prefs.getString(countryKey, "Україна"));
		country.setIcon(getIcon(country.getSummary().toString()));

		city = findPreference(cityKey);
		city.setSummary(prefs.getString(cityKey, "Київ"));

		stop = findPreference(stopId);

		stop.setSummary(prefs.getString(stopId, "5385"));

		dataBox = new DataBox(context);

		h = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case TOAST:
					Toast.makeText(context, "Не існує данної зупинки!",
							Toast.LENGTH_SHORT).show();
					break;
				case WIDGET:
					correctId();
					break;
				}
			};
		};
		
		Crashlytics.start(this);
		
		Log.d(LOG_TAG, "onCreate config");
		ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
		if (activeNetwork == null) {
			Intent intent = new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK);
			startActivity(intent);
			Toast.makeText(this, getString(R.string.turn_on),
					Toast.LENGTH_SHORT).show();
			finish();
		}
		// insert ID configuration of widget
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		if (extras != null) {
			widgetID = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
					AppWidgetManager.INVALID_APPWIDGET_ID);
		}
		// check correct or not
		if (widgetID == AppWidgetManager.INVALID_APPWIDGET_ID) {
			finish();
		}

		// answer's intent
		resultValue = new Intent();
		resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID);

		// negative answer
		setResult(RESULT_CANCELED, resultValue);
	}

	private void correctId() {

		String textId = editTextPreference.getText();
		
		
		SharedPreferences sp = getSharedPreferences(WIDGET_PREF_CONFIG,
				MODE_PRIVATE);
		
		
		if(textId.equalsIgnoreCase(sp.getString(WIDGET_TEXT_CONFIG + widgetID, "0"))){
			extracted(sp);
			finish();
			
		}
		Editor editor = sp.edit();
		String titleStopS = title.item(0).getTextContent();

		editor.putString(WIDGET_TEXT_CONFIG + widgetID, textId);
		editor.putString(WIDGET_CITY_CONFIG + widgetID, responseCity);
		editor.putString(WIDGET_STOP_CONFIG + widgetID, titleStopS);
		editor.apply();

		Log.i("STOP_START", "" + titleStopS);
		/*if (WidgetFactory.h != null) {
			WidgetFactory.h.sendEmptyMessage(WidgetFactory.UPDATE_DATABASE);
		}*/
		/*if (WidgetFactory.arrayList != null) {
			WidgetFactory.arrayList.clear();
		}*/

		extracted(sp);
		finish();
	}

	private void extracted(SharedPreferences sp) {
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
		WidgetProvider.updateWidget(this, appWidgetManager, sp, widgetID);
		// positive answer
		setResult(RESULT_OK, resultValue);
		Log.d(LOG_TAG, "finish config " + widgetID);
	}

	@Override
	@Deprecated
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
			Preference preference) {
		// TODO Auto-generated method stub
		if (preference.getKey().equalsIgnoreCase("country")) {
			Intent i = new Intent(this, CountryActivity.class);
			i.putExtra("currentCountry", prefs.getString(countryKey, "Україна"));
			i.putExtra("widgetID", widgetID);
			startActivityForResult(i, 1);
		}
		if (preference.getKey().equalsIgnoreCase("city")) {
			Intent i = new Intent(this, CityActivity.class);
			i.putExtra("currentCity", inString);
			i.putExtra("widgetID", widgetID);
			startActivityForResult(i, 2);
		}
		return true;
	}

	@SuppressLint("Recycle")
	private int getIcon(String countryString) {
		int countryIcon = -1;
		Resources res = context.getResources();
		String countries[] = res.getStringArray(R.array.countriesEntries);
		TypedArray icons = res.obtainTypedArray(R.array.countriesIcons);
		for (int i = 0; i < countries.length; i++) {
			if (countryString.equals(countries[i])) {
				countryIcon = icons.getResourceId(i, -1);
				break;
			}
		}
		return countryIcon;
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		onResume();

		if (data == null) {
			return;
		}
		if (resultCode == 1) {
			inString = data.getStringExtra("defaultCity");
			String countryName = data.getStringExtra("countryName");
			editor = prefs.edit();
			editor.putString(countryKey, countryName);
			editor.apply();

			city.setDefaultValue(inString);
			city.setSummary(inString);
		}
		if (resultCode == 2) {
			inString = data.getStringExtra("cityName");
			String cityName = data.getStringExtra("cityName");
			editor = prefs.edit();
			editor.putString(cityKey, cityName);
			editor.apply();

		}

	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		if (key.equals(countryKey)) {
			String countryString = sharedPreferences.getString(countryKey,
					"Україна");
			country.setSummary(countryString);
			country.setIcon(getIcon(countryString));
		} else if (key.equals(cityKey)) {
			city.setSummary(sharedPreferences.getString(cityKey, "Київ"));
		} else if (key.equals(stopId)) {
			stop.setSummary(sharedPreferences.getString(
					editTextPreference.getText(), "5385"));
		}

		String update = (String) listPreferenceView.getSummary();
		Log.d(LOG_TAG, "onSharedPreferenceChanged_UPDATE " + update);

		checkCity = sharedPreferences.getString("city", "Київ");
		responseCity = CITIES.get(checkCity);

		Log.d(LOG_TAG, "onSharedPreferenceChanged " + responseCity);
		
	}

	@Override
	protected void onResume() {

		listPreferenceView
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						// TODO Auto-generated method stub
						Log.d("LIST_UPDATE", newValue.toString());
						return false;
					}
				});

		SharedPreferences sp = context.getSharedPreferences(
				WIDGET_PREF_CONFIG, Context.MODE_PRIVATE);
		responseCity = sp.getString(WIDGET_CITY_CONFIG + widgetID, "0");
		if (responseCity.equalsIgnoreCase("0")) {
			responseCity = CITIES.get(prefs.getString(cityKey, "Київ"));
		}
		Log.d("INSTING", "" + inString + responseCity + "    "
				+ WIDGET_CITY_CONFIG + widgetID);
		prefs.registerOnSharedPreferenceChangeListener(this);
		super.onResume();
	}

}
