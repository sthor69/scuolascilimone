package com.storassa.android.scuolasci;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import dme.forecastiolib.FIODaily;
import dme.forecastiolib.FIODataPoint;
import dme.forecastiolib.ForecastIO;

public class MeteoFragment extends Fragment {

	private FIODataPoint[] dataPoint;
	private ArrayList<MeteoItem> meteoItems;
	private ArrayAdapter<MeteoItem> adapter;
	private Activity parentActivity;
	FIODaily daily;
	private int counter = 0;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		parentActivity = activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View result = inflater.inflate(R.layout.meteo_fragment, container,
				false);
		final ListView meteoListView = (ListView) result
				.findViewById(R.id.meteo_list);
		meteoListView
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// only the first two days can be expanded in hourly
						// forecast
						if (id < 2) {
							Intent myIntent = new Intent(getActivity(),
									MainActivity.class);
							myIntent.putExtra("day", id);
							getActivity().startActivity(myIntent);
						} else {
							AlertDialog.Builder builder = new AlertDialog.Builder(
									getActivity());
							builder.setMessage(R.string.meteo_list_restriction)
									.setTitle(R.string.warning);

							builder.setPositiveButton(R.string.ok,
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {
											;
										}
									});

							AlertDialog dialog = builder.create();
							dialog.show();
						}
					}
				});

		// retrieve the saved meteo items, if available
		if (savedInstanceState != null)
			meteoItems = (ArrayList<MeteoItem>) Arrays
					.asList((MeteoItem[]) savedInstanceState
							.getParcelableArray("meteo_items"));
		else
			meteoItems = new ArrayList<MeteoItem>();

		// get the meteo information in a different thread
		dataPoint = new FIODataPoint[MAX_FORECAST_DAYS];
		ExecutorService exec = Executors.newCachedThreadPool();
		exec.execute(new Runnable() {

			@Override
			public void run() {
				try {
					String forecastIoKey = getResources().getString(
							R.string.forecastio_api_key);
					String limoneLatitude = getResources().getString(
							R.string.limone_latitude);
					String limoneLongitude = getResources().getString(
							R.string.limone_longitude);

					ForecastIO fio = new ForecastIO(forecastIoKey);
					fio.setUnits(ForecastIO.UNITS_SI);
					fio.setExcludeURL("hourly,minutely");
					fio.getForecast(limoneLatitude, limoneLongitude);
					daily = new FIODaily(fio);
				} catch (Exception e) {
					// if there are problems print the stack and warn the user
					e.printStackTrace();
					CommonHelper.exitMessage(R.string.http_issue, R.string.http_issue_dialog_title, parentActivity);
				}
			}
		});

		// wait for the http response or exit after 10s
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				if (daily != null) {
					String[] meteoIconString = new String[MAX_FORECAST_DAYS];
					for (int i = 0; i < MAX_FORECAST_DAYS; i++)
						meteoIconString[i] = daily.getDay(i).icon().replace('\"', ' ')
								.trim();

					for (int i = 0; i < MAX_FORECAST_DAYS; i++) {
						dataPoint[i] = daily.getDay(i);
						meteoItems.add(getMeteoItemFromDataPoint(dataPoint[i]));
					}

					int resId = R.layout.meteo_list;
					adapter = new MeteoArrayAdapter(parentActivity, resId, meteoItems);
					meteoListView.setAdapter(adapter);
				}
				else if (counter < WAITING_TICKS){
					counter++;
				}
				else {
					CommonHelper.exitMessage(R.string.http_issue, R.string.http_issue_dialog_title, parentActivity);
				}

			}
		}, 0, REPETITION_TIME);

		// if the counter expired delete the timer
		if (counter >= WAITING_TICKS)
			timer.cancel();

		return result;
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);

		// savedInstanceState.putIntArray("meteo_icon", meteoIconResource);
		// savedInstanceState.putParcelableArray("meteo_items",
		// (Parcelable[]) (meteoItems.toArray()));
	}

	private MeteoItem getMeteoItemFromDataPoint(FIODataPoint _dataPoint) {
		String icon = _dataPoint.icon().replace('\"', ' ').trim();
		MeteoItem result = new MeteoItem(getIconResourceIdFromString(icon),
				_dataPoint.temperatureMin(), _dataPoint.temperatureMax(),
				_dataPoint.humidity(), _dataPoint.precipProbability(), -1, -1,
				-1);

		return result;
	}

	private int getIconResourceIdFromString(String iconString) {
		if (iconString.equals("rain"))
			return R.drawable.rain_icon;
		else if (iconString.equals("clear-day"))
			return R.drawable.sun_icon;
		else if (iconString.equals("cloudy"))
			return R.drawable.cloud_icon;
		else if (iconString.equals("snow"))
			return R.drawable.snow_icon;
		else if (iconString.equals("partly-cloudy-day"))
			return R.drawable.sun_cloud_mix_icon;

		return R.drawable.sun_icon;

	}

	// private static final String LIMONE_LATITUDE = "44.2013202";
	// private static final String LIMONE_LONGITUDE = "7.576090300000033";
	// private static final String METEO_API_FIO_KEY =
	// "66d2edf03dbf0185e0cb48f1a23a29ed";
	// TODO put the website for snow reports

	private static final int MAX_FORECAST_DAYS = 7;
	private static final int REPETITION_TIME = 1000;
	private static final int WAITING_TICKS = 10;

}