package com.ewaywidget.config;

import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.ewaywidget.WidgetFactory;
import com.ewaywidget.R;
import com.ewaywidget.http.Response;
import com.widget.checkid.RequestCity;

public class CityActivity extends Activity {
	protected static final int NONE = 0;
	protected static final int LOAD = 1;
	protected static final int LOADED = 2;
	Context context;
	ListView lvCities;
	ProgressBar progressBar;
	ArrayList<City> entries;
	private LayoutInflater mInflater;
	private int[] entryValues;
	private int selectedPosition;
	SharedPreferences prefs;
	SharedPreferences.Editor editor;
	Handler hCity;
	City selectedCity;
	public String curentCity;
	int widgetID;

	@SuppressLint("HandlerLeak")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		context = this;
		Intent i = getIntent();
		curentCity = i.getStringExtra("currentCity");
		selectedPosition = -1;
		widgetID = i.getIntExtra("widgetID", 0);
		
		setContentView(R.layout.city_activity);
		getActionBar().setHomeButtonEnabled(true);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		mInflater = LayoutInflater.from(context);
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		editor = prefs.edit();
		entries = generateEntries();
		CustomListPreferenceAdapter adapter = new CustomListPreferenceAdapter();
		lvCities = (ListView) findViewById(R.id.cities_list);
		progressBar = (ProgressBar) findViewById(R.id.progressBar2);
		lvCities.setAdapter(adapter);

		hCity = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case NONE:
					lvCities.setVisibility(View.VISIBLE);
					progressBar.setVisibility(View.GONE);
					Toast.makeText(context, "Нових міст не знайдено!",
							Toast.LENGTH_SHORT).show();
					break;
				case LOAD:
					new Thread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							hCity.sendEmptyMessage(LOADED);
							RequestCity mRequest = new RequestCity();
							Response response = new Response(mRequest.execute());
							Element root = response.getData()
									.getDocumentElement();
							Log.i("CITIES",
									WidgetFactory.toStringXML(response.getData()));
							NodeList routes = root
									.getElementsByTagName("route");
							Log.i("LOG", "" + routes.getLength());

							hCity.sendEmptyMessage(NONE);
						}
					}).start();
					break;
				case LOADED:
					lvCities.setVisibility(View.GONE);
					progressBar.setVisibility(View.VISIBLE);
					break;
				}
			};
		};
	}

	private ArrayList<City> generateEntries() {
		ArrayList<City> cities = new ArrayList<City>();
		String countryKey = "country";
		String res_con = prefs.getString(countryKey, "Ukraine");
		Log.i("COUNTRY", res_con);
		if (res_con.equalsIgnoreCase("Україна")||res_con.equalsIgnoreCase("Ukraine")) {

			City city1 = new City();
			City city2 = new City();
			City city3 = new City();
			City city4 = new City();
			City city5 = new City();
			City city6 = new City();
			City city7 = new City();
			City city8 = new City();
			City city9 = new City();
			City city10 = new City();
			City city11 = new City();
			City city12 = new City();
			City city13 = new City();
			City city14 = new City();
			City city15 = new City();
			City city16 = new City();
			City city17 = new City();
			City city18 = new City();
			City city19 = new City();
			City city20 = new City();
			City city21 = new City();
			City city22 = new City();
			City city23 = new City();
			City city24 = new City();
			City city25 = new City();
			City city26 = new City();
			City city27 = new City();
			City city28 = new City();
			City city29 = new City();
			City city30 = new City();
			City city31 = new City();
			City city32 = new City();
			City city33 = new City();
			City city34 = new City();
			City city35 = new City();
			City city36 = new City();
			City city37 = new City();
			City city38 = new City();
			City city39 = new City();
			City city40 = new City();
			City city41 = new City();
			City city42 = new City();
			City city43 = new City();
			City city44 = new City();
			City city45 = new City();
			City city46 = new City();
			City city47 = new City();
			City city48 = new City();
			City city49 = new City();
			City city50 = new City();
			City city51 = new City();
			City city52 = new City();
			City city53 = new City();

			city1.setName("Алушта");
			city2.setName("Алчевськ");
			city3.setName("Бердянськ");
			city4.setName("Біла Церква");
			city5.setName("Бориспіль");
			city6.setName("Вінниця");
			city7.setName("Горлівка");
			city8.setName("Джанкой");
			city9.setName("Дніпродзержинськ");
			city10.setName("Дніпропетровськ");
			city11.setName("Донецьк");
			city12.setName("Дрогобич");
			city13.setName("Євпаторія");
			city14.setName("Житомир");
			city15.setName("Запоріжжя");
			city16.setName("Івано-Франківськ");
			city17.setName("Кам′янець-Подільський");
			city18.setName("Керч");
			city19.setName("Київ");
			city20.setName("Кіровоград");
			city21.setName("Конотоп");
			city22.setName("Краматорськ");
			city23.setName("Кременчук");
			city24.setName("Кривий Ріг");
			city25.setName("Луганськ");
			city26.setName("Луцьк");
			city27.setName("Львів");
			city28.setName("Макіївка");
			city29.setName("Маріуполь");
			city30.setName("Мелітополь");
			city31.setName("Миколаїв");
			city32.setName("Нікополь");
			city33.setName("Одеса");
			city34.setName("Олександрія");
			city35.setName("Полтава");
			city36.setName("Рівне");
			city37.setName("Севастополь");
			city38.setName("Сєвєродонецьк");
			city39.setName("Сімферополь");
			city40.setName("Слов′янськ");
			city41.setName("Судак");
			city42.setName("Суми");
			city43.setName("Тернопіль");
			city44.setName("Ужгород");
			city45.setName("Умань");
			city46.setName("Феодосія");
			city47.setName("Харків");
			city48.setName("Херсон");
			city49.setName("Хмельницький");
			city50.setName("Черкаси");
			city51.setName("Чернівці");
			city52.setName("Чернігів");
			city53.setName("Ялта");

			city1.setID(1);
			city2.setID(2);
			city3.setID(3);
			city4.setID(4);
			city5.setID(5);
			city6.setID(6);
			city7.setID(7);
			city8.setID(8);
			city9.setID(9);
			city10.setID(10);
			city11.setID(11);
			city12.setID(12);
			city13.setID(13);
			city14.setID(14);
			city15.setID(15);
			city16.setID(16);
			city17.setID(17);
			city18.setID(18);
			city19.setID(19);
			city20.setID(20);
			city21.setID(21);
			city22.setID(22);
			city23.setID(23);
			city24.setID(24);
			city25.setID(25);
			city26.setID(26);
			city27.setID(27);
			city28.setID(28);
			city29.setID(29);
			city30.setID(30);
			city31.setID(31);
			city32.setID(32);
			city33.setID(33);
			city34.setID(34);
			city35.setID(35);
			city36.setID(36);
			city37.setID(37);
			city38.setID(38);
			city39.setID(39);
			city40.setID(40);
			city41.setID(41);
			city42.setID(42);
			city43.setID(43);
			city44.setID(44);
			city45.setID(45);
			city46.setID(46);
			city47.setID(47);
			city48.setID(48);
			city49.setID(49);
			city50.setID(50);
			city51.setID(51);
			city52.setID(52);

			cities.add(city1);
			cities.add(city2);
			cities.add(city3);
			cities.add(city4);
			cities.add(city5);
			cities.add(city6);
			cities.add(city7);
			cities.add(city8);
			cities.add(city9);
			cities.add(city10);
			cities.add(city11);
			cities.add(city12);
			cities.add(city13);
			cities.add(city14);
			cities.add(city15);
			cities.add(city16);
			cities.add(city17);
			cities.add(city18);
			cities.add(city19);
			cities.add(city20);
			cities.add(city21);
			cities.add(city22);
			cities.add(city23);
			cities.add(city24);
			cities.add(city25);
			cities.add(city26);
			cities.add(city27);
			cities.add(city28);
			cities.add(city29);
			cities.add(city30);
			cities.add(city31);
			cities.add(city32);
			cities.add(city33);
			cities.add(city34);
			cities.add(city35);
			cities.add(city36);
			cities.add(city37);
			cities.add(city38);
			cities.add(city39);
			cities.add(city40);
			cities.add(city41);
			cities.add(city42);
			cities.add(city43);
			cities.add(city44);
			cities.add(city45);
			cities.add(city46);
			cities.add(city47);
			cities.add(city48);
			cities.add(city49);
			cities.add(city50);
			cities.add(city51);
			cities.add(city52);
			cities.add(city53);

		}

		else if (res_con.equalsIgnoreCase("Росія")) {
			City another8 = new City();
			another8.setName("Бєлорєченськ");
			another8.setID(0);
			cities.add(another8);
			City another9 = new City();
			another9.setName("Лабинськ");
			another9.setID(1);
			cities.add(another9);
		}

		else if (res_con.equalsIgnoreCase("Молдова")) {
			City another = new City();
			another.setName("Бельцы");
			another.setID(1);
			cities.add(another);
			City another2 = new City();
			another2.setName("Бендеры");
			another2.setID(2);
			cities.add(another2);
			City another3 = new City();
			another3.setName("Кишинев");
			another3.setID(3);
			cities.add(another3);
			City another4 = new City();
			another4.setName("Тирасполь");
			another4.setID(4);
			cities.add(another4);
		} else if (res_con.equalsIgnoreCase("Bulgaria")) {
			City another5 = new City();
			another5.setName("Sofia");
			another5.setID(1);
			cities.add(another5);
		}

		else if (res_con.equalsIgnoreCase("Hrvatska")) {
			City another7 = new City();
			another7.setName("Zagreb");
			another7.setID(1);
			cities.add(another7);
		}

		else if (res_con.equalsIgnoreCase("Србија")) {
			City another6 = new City();
			another6.setName("Београд");
			another6.setID(1);
			cities.add(another6);
		}

		else if (res_con.equalsIgnoreCase("Қазақстан")) {
			City another10 = new City();
			another10.setName("Астана");
			another10.setID(1);
			cities.add(another10);
		}

		entryValues = new int[cities.size()];
		for (int i = 0; i < cities.size(); i++) {
			entryValues[i] = i;
		}
		return cities;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == android.R.id.home) {
			onBackPressed();
			return true;
		} else if (id == R.id.refresh) {
			hCity.sendEmptyMessage(LOAD);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public class CustomListPreferenceAdapter extends BaseAdapter {
		public int getCount() {
			return entries.size();
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(final int position, View convertView,
				ViewGroup parent) {
			View row = convertView;
			CustomHolder holder;

			if (row == null) {
				row = mInflater.inflate(R.layout.cities_settings_row, parent,
						false);
				holder = new CustomHolder(row);
				row.setTag(holder);
			}

			else {
				holder = (CustomHolder) row.getTag();
			}

			holder.init(row, position);

			/*
			 * if (holder.cityName.equalsIgnoreCase(curentCity)) {
			 * selectCityView(holder); Log.i("TAG", "open method"); }
			 */
			row.setClickable(true);
			row.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					selectCity(v);
					notifyDataSetChanged();
				}
			});

			if (selectedCity != null
					&& selectedCity.getID() == entries.get(position).getID()
					|| holder.cityName.equalsIgnoreCase(curentCity)) {

				selectCityView(holder);
				try {
					curentCity = selectedCity.getName();
				} catch (NullPointerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				unselectCityView(holder);
			}

			return row;
		}

		private void unselectCityView(CustomHolder holder) {
			// Log.d(TAG,"Select City at position = " + holder.position);
			holder.rButton.setChecked(false);
			holder.textViewName.setTextColor(Color.parseColor("#FF000000"));
		}

		private void selectCityView(CustomHolder holder) {
			Log.d("TAG", "Select City at position = " + holder.position);
			holder.rButton.setChecked(true);
			holder.textViewName.setTextColor(Color.parseColor("#FF1FD5FF"));
		}

		protected void selectCity(View v) {
			try {
				CustomHolder holder1 = (CustomHolder) v.getTag();
				if (holder1 != null)
					selectedPosition = holder1.position;
				try {
					selectedCity = entries.get(holder1.position);

				} catch (NullPointerException npe) {
					Log.e("TAG",
							"Cities is  null. Please set cities before calling");
				}
			} catch (ClassCastException cce) {
				cce.printStackTrace();
				Log.e("TAG",
						"Cities is  null. Please set cities before calling");
			}
		}

		public class CustomHolder {
			public String cityName;
			public int id;
			public int position;
			TextView textViewName = null;
			RadioButton rButton = null;

			public CustomHolder(View v) {
				textViewName = (TextView) v.findViewById(R.id.city_name);
				rButton = (RadioButton) v.findViewById(R.id.radioCity);
			}

			public void init(View row, final int position) {

				cityName = entries.get(position).getName();

				id = entries.get(position).getID();

				this.position = position;
				// Log.i("Log City Name", cityName);
				textViewName.setText(cityName);
				rButton.setId(position);

				Log.i("Log idBut", "" + position + cityName + curentCity);

				rButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						selectedPosition = v.getId();
						selectedCity = entries.get(selectedPosition);
						notifyDataSetChanged();
					}
				});

			}
		}

	}

	@Override
	public void onBackPressed() {
	
		if (selectedPosition != -1) {
			Intent intent = new Intent();
			intent.putExtra("cityName", entries.get(selectedPosition).getName());
			SharedPreferences putCity = getSharedPreferences(SettingsActivity.WIDGET_PREF_CONFIG,
					MODE_PRIVATE);
			Editor editor = putCity.edit();
			editor.putString(SettingsActivity.WIDGET_CITY_CONFIG + widgetID,  SettingsActivity.CITIES.get(entries.get(selectedPosition).getName()));
			editor.apply();
			Log.i("ON_BACK_PRESSED",widgetID+ " " +SettingsActivity.CITIES.get(entries.get(selectedPosition).getName()));
			setResult(2, intent);
		}
		super.onBackPressed();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.sub_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}
}
