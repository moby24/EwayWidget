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
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.ewaywidget.R;
import com.ewaywidget.http.Response;
import com.widget.checkid.RequestCity;

public class CountryActivity extends Activity implements
		SharedPreferences.OnSharedPreferenceChangeListener {
	protected static final int NONE = 0;
	protected static final int LOAD = 1;
	protected static final int LOADED = 2;
	Context context;
	ListView lvCountres;
	ProgressBar progressBar;
	ArrayList<Country> entries;
	private LayoutInflater mInflater;
	ArrayList<RadioButton> rButtonList;
	private ArrayList<TextView> textViewCountryNameList;
	private int[] entryValues;
	private int selectedPosition;
	private String currentCountry;
	SharedPreferences prefs;
	SharedPreferences.Editor editor;
	Handler hCountry;
	int widgetID;
	

	
	@SuppressLint("HandlerLeak")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		context = this;
		Intent i = getIntent();
		currentCountry = i.getStringExtra("currentCountry");
		widgetID = i.getIntExtra("widgetID", 0);

		setContentView(R.layout.country_activity);
		getActionBar().setHomeButtonEnabled(true);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		mInflater = LayoutInflater.from(context);
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		editor = prefs.edit();
		rButtonList = new ArrayList<RadioButton>();
		entries = generateEntries();
		CustomListPreferenceAdapter adapter = new CustomListPreferenceAdapter(
				context);
		lvCountres = (ListView) findViewById(R.id.country_list);
		progressBar = (ProgressBar) findViewById(R.id.progressBar1);
		lvCountres.setAdapter(adapter);
		textViewCountryNameList = new ArrayList<TextView>();

		hCountry = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case NONE:
					lvCountres.setVisibility(View.VISIBLE);
					progressBar.setVisibility(View.GONE);
					Toast.makeText(context, "Нових країн не знайдено!",
							Toast.LENGTH_SHORT).show();
					break;
				case LOAD:
					new Thread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							hCountry.sendEmptyMessage(LOADED);
							RequestCity mRequest = new RequestCity();
							Response response = new Response(mRequest.execute());
							Element root = response.getData()
									.getDocumentElement();
							NodeList routes = root
									.getElementsByTagName("route");
							Log.i("LOG", "" + routes.getLength());

							hCountry.sendEmptyMessage(NONE);

						}
					}).start();
					break;
				case LOADED:
					lvCountres.setVisibility(View.GONE);
					progressBar.setVisibility(View.VISIBLE);
					break;
				}
			};
		};

	}

	private ArrayList<Country> generateEntries() {
		ArrayList<Country> countries = new ArrayList<Country>();
		Country ukraine = new Country();
		Country moldova = new Country();
		Country bulgaria = new Country();
		Country croatia = new Country();
		Country serbia = new Country();
		Country russia = new Country();
		Country kazakhstan = new Country();

		ukraine.setName("Україна");
		ukraine.setDefaultCity("Київ");
		russia.setName("Росія");
		russia.setDefaultCity("Бєлорєченськ");
		moldova.setName("Молдова");
		moldova.setDefaultCity("Кишинев");
		bulgaria.setName("Bulgaria");
		bulgaria.setDefaultCity("Sofia");
		croatia.setName("Hrvatska");
		croatia.setDefaultCity("Zagreb");
		serbia.setName("Србија");
		serbia.setDefaultCity("Београд");
		kazakhstan.setName("Қазақстан");
		kazakhstan.setDefaultCity("Астана");

		countries.add(ukraine);
		countries.add(russia);
		countries.add(moldova);
		countries.add(bulgaria);
		countries.add(croatia);
		countries.add(serbia);
		countries.add(kazakhstan);

		entryValues = new int[countries.size()];
		for (int i = 0; i < countries.size(); i++) {
			entryValues[i] = i;
		}
		return countries;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == android.R.id.home) {
			onBackPressed();
			return true;
		} else if (id == R.id.refresh) {
			hCountry.sendEmptyMessage(LOAD);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private class CustomListPreferenceAdapter extends BaseAdapter {
		public CustomListPreferenceAdapter(Context context) {

		}

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
			CustomHolder holder = null;

			if (row == null) {
				row = mInflater.inflate(R.layout.country_settings_row, parent,
						false);
				holder = new CustomHolder(row, position);
				row.setTag(holder);
				row.setClickable(true);
				row.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						for (int i = 0; i < rButtonList.size(); i++) {
							if (i != position) {
								rButtonList.get(i).setChecked(false);
							} else {
								rButtonList.get(i).setChecked(true);
							}
						}
						int index = position;
						int value = entryValues[index];
						editor.putInt("CountryValue", value);
						editor.commit();
					}
				});
				if (holder.countryName.equals(currentCountry))
					rButtonList.get(position).setChecked(true);
			}
			return row;
		}

		class CustomHolder {
			private TextView textViewName = null;
			private RadioButton rButton = null;
			public String countryName;
			private ImageView iv = null;

			CustomHolder(View row, final int position) {

				textViewName = (TextView) row.findViewById(R.id.country_name);
				textViewName.setText(entries.get(position).getName());

				rButton = (RadioButton) row.findViewById(R.id.radioCountry);
				rButton.setId(position);

				iv = (ImageView) row.findViewById(R.id.icon_list);
				switch (position) {
				case 0:
					iv.setImageResource(R.drawable.flag_ua);
					break;
				case 1:
					iv.setImageResource(R.drawable.flag_ru);
					break;
				case 2:
					iv.setImageResource(R.drawable.flag_md);
					break;
				case 3:
					iv.setImageResource(R.drawable.flag_bg);
					break;
				case 4:
					iv.setImageResource(R.drawable.flag_hr);
					break;
				case 5:
					iv.setImageResource(R.drawable.flag_rs);
					break;
				case 6:
					iv.setImageResource(R.drawable.flag_kz);
					break;
				}

				rButtonList.add(rButton);

				textViewCountryNameList.add(textViewName);

				countryName = entries.get(position).getName();

				rButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							for (int i = 0; i < rButtonList.size(); i++) {
								RadioButton rb = rButtonList.get(i);
								if (rb != buttonView) {
									rb.setChecked(false);
								}
								if (rb.isChecked()) {
									textViewCountryNameList
											.get(i)
											.setTextColor(
													Color.parseColor("#FF1FD5FF"));
								} else {
									textViewCountryNameList
											.get(i)
											.setTextColor(
													Color.parseColor("#FF000000"));
								}
							}

							int index = buttonView.getId();
							int value = entryValues[index];
							selectedPosition = value;
							editor.putInt("CountryValue", value);
							editor.commit();
						}
					}
				});
			}
		}
	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent();
		intent.putExtra("countryName", entries.get(selectedPosition).getName());
		intent.putExtra("defaultCity", entries.get(selectedPosition)
				.getDefaultCity());
		Log.i("getDefaultCity","DEFAULT "+ entries.get(selectedPosition).getDefaultCity());
		
		SharedPreferences intevalSpnew = getSharedPreferences(SettingsActivity.WIDGET_PREF_CONFIG,
				MODE_PRIVATE);
		Editor editor = intevalSpnew.edit();
		editor.putString(SettingsActivity.WIDGET_CITY_CONFIG + widgetID,  SettingsActivity.CITIES.get(entries.get(selectedPosition).getDefaultCity()));
		editor.apply();
		
		setResult(1, intent);
		
		
		
		super.onBackPressed();
	}

	@SuppressLint("InflateParams")
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.sub_menu, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
	}

}
