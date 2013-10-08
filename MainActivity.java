package com.storassa.android.scuolasci;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity {

	FragmentManager fm;
	boolean logged = false;
	TextView introText;
	String cachedIntroText = "";
	ImageView meteoImage;
	TextView meteoDateView;
	String meteoDate;
	Button btnMeteoPrevDate, btnMeteoSuccDate;
	MeteoItem[] meteoItems;
	int currentMeteoIconResource = 0;
	private int counter = 0;
	boolean dataEnabled = false, dataAvailable = false;
	String result = "";
	TextView minSnowText, maxSnowText, lastSnowText;
	private BroadcastReceiver networkChangeReceiver;
	double minSnow, maxSnow;
	String lastSnow;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		dataEnabled = false;
		dataAvailable = false;

		addNetworkChangeReceiver();

		setContentView(R.layout.activity_main);

		minSnowText = (TextView) findViewById(R.id.min_snow_text);
		maxSnowText = (TextView) findViewById(R.id.max_snow_text);
		lastSnowText = (TextView) findViewById(R.id.last_snow_text);

		// if this is the first creation, the user is not logged
		// otherwise get the saved state
		if (savedInstanceState == null)
			logged = false;
		else
			logged = savedInstanceState.getBoolean("logged");

		// if the user is logged in show the logout button,
		// else show the username/password and login password
		switchLoginFragment();

		checkDataAvailable();
		if (dataAvailable) {
			// get the meteo information, if data are available
			getMeteoFragment();

			// if data are available get the snow report
			getSnowReport();
		}

	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);

		savedInstanceState.putBoolean("logged", logged);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	protected void onPause() {
		super.onPause();
		try {
			// unregisterReceiver(networkChangeReceiver);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			unregisterReceiver(networkChangeReceiver);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		try {
			// registerReceiver(networkChangeReceiver, filter);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
	}

	public void switchLoginFragment() {
		fm = getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		if (logged) {
			ft.replace(R.id.login_place, new LoggedFragment()).commit();
		} else {
			ft.replace(R.id.login_place, new LoginFragment()).commit();
		}
		logged = !logged;
	}

	public void setDataAvailable() {
		dataAvailable = true;
	}
	
	public void setLoginStatus(boolean status) {
		logged = status;
	}

	protected void getMeteoFragment() {

		// check that data is enabled on the device
		// checkDataAvailable();

		// if device is connected to Internet update the meteo
		if (dataAvailable) {

			ExecutorService exec = Executors.newCachedThreadPool();
			exec.execute(new Runnable() {

				@Override
				public void run() {
					try {
						fm = getFragmentManager();
						FragmentTransaction ft = fm.beginTransaction();
						ft.replace(R.id.meteo_list_placeholder,
								new MeteoFragment()).commitAllowingStateLoss();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
	}

	protected void getSnowReport() {

		// reset the counter for the waiting period of http request
		counter = 0;

		// check that data is enabled on the device
		// checkDataAvailable();

		// if device is connected to Internet update the meteo
		if (dataAvailable) {

			// get the Weather2 web page
			ExecutorService exec = Executors.newCachedThreadPool();
			exec.execute(new Runnable() {

				@Override
				public void run() {
					try {
						HttpConnectionHelper helper = HttpConnectionHelper
								.getHelper();
						result = helper.openGenericConnection(WEATHER2_API);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			// wait for the http response or exit after WAITING_TICKS
			Timer timer = new Timer();
			timer.schedule(new TimerTask() {

				@Override
				public void run() {
					if (result != null) {
						// parse the result to get snow information
						ParseWeatherHelper whetherHelper = new ParseWeatherHelper(
								result);
						minSnow = whetherHelper.getMinSnow();
						maxSnow = whetherHelper.getMaxSnow();
						lastSnow = whetherHelper.getLastSnow();

						// set the textviews for the snow info
						minSnowText.setText("Min snow: "
								+ String.valueOf(minSnow));
						maxSnowText.setText("Max snow: "
								+ String.valueOf(maxSnow));
						lastSnowText.setText("Last snow: " + lastSnow);
					} else if (counter < WAITING_TICKS)
						counter++;
					else {
						CommonHelper.exitMessage(R.string.http_issue,
								R.string.http_issue_dialog_title,
								MainActivity.this);
					}

				}
			}, 0, REPETITION_TIME);

			if (counter >= WAITING_TICKS)
				timer.cancel();
		}
	}

	/*
	 * ------------ PRIVATE METHODS ------------
	 */

	private void addNetworkChangeReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.storassa.android.scuolasci.NETWORK_CHANGE");

		networkChangeReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {

				// check data connection
				ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo netInfo = cm.getActiveNetworkInfo();

				// if data connection is now available, get info from Internet
				if (netInfo != null)
					if (netInfo.isConnected()) {

						dataAvailable = true;

						// get the meteo information, if data are available
						getMeteoFragment();

						// if data are available get the snow report
						getSnowReport();
					}
			}
		};

		registerReceiver(networkChangeReceiver, filter);

	}

	private void checkDataAvailable() {

		// check whether data is enalbed and in case open DataDisabledDialog
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo == null || !netInfo.isConnectedOrConnecting()) {
			getNoDataDialog();
		}
		// else, if data is enabled but connection is not available, open an
		// alert dialog
		else if (!netInfo.isConnected()) {
			// warn the user that Internet is not available
			CommonHelper.exitMessage(R.string.http_issue,
					R.string.http_issue_dialog_title, this);
		} else
			setDataAvailable();
	}

	private void getNoDataDialog() {
		DataDisabledDialog dialog = DataDisabledDialog.newInstance(
				"R.string.connection_unavailable", this);

		dialog.show(getFragmentManager(), "");

	}

	// private static final String WEATHER2_API =
	// "http://www.myweather2.com/Ski-Resorts/Italy/Limone-Piemonte/snow-report.aspx";
	private static final String WEATHER2_API = "http://www.myweather2.com/developer/weather.ashx?uac=Tax7vNwxqd&uref=bc13f25a-d9dc-4f89-9405-aa03b447a3c9";
	private static final int REPETITION_TIME = 1000;
	private static final int WAITING_TICKS = 10;

}