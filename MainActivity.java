package com.storassa.android.scuolasci;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
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
   private boolean timerElapsed = false;
   private AlertDialog dialog;
   boolean dataEnabled = false, dataAvailable = false;
   String result = "";

   @Override
   protected void onCreate(Bundle savedInstanceState) {

      dataEnabled = false;
      dataAvailable = false;
      double minSnow, maxSnow;
      String lastSnow;

      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);

      TextView minSnowText = (TextView) findViewById(R.id.min_snow_text);
      TextView maxSnowText = (TextView) findViewById(R.id.max_snow_text);
      TextView lastSnowText = (TextView) findViewById(R.id.last_snow_text);

      // if this is the first creation, the user is not logged
      // otherwise get the saved state
      if (savedInstanceState == null)
         logged = false;
      else
         logged = savedInstanceState.getBoolean("logged");

      // if the user is logged in show the logout button,
      // else show the username/password and login password
      switchLoginFragment();

      // get the meteo information, if data are available
      getMeteoFragment();

      // get the Weather2 web page
      ExecutorService exec = Executors.newCachedThreadPool();
      exec.execute(new Runnable() {

         @Override
         public void run() {
            try {
               HttpConnectionHelper helper = HttpConnectionHelper.getHelper();
               result = helper.openGenericConnection(WEATHER2_API);
            } catch (Exception e) {
               e.printStackTrace();
            }
         }
      });

      // wait for the http response or exit after WAITING_TIME
      Timer timer = new Timer();
      timer.schedule(new TimerTask() {

         @Override
         public void run() {
            timerElapsed = true;

         }
      }, WAITING_TIME);

      while (result == "" && !timerElapsed)
         ;
      timer.cancel();

      if (timerElapsed) {
         AlertDialog.Builder builder = new AlertDialog.Builder(this);
         builder.setMessage(R.string.http_issue).setTitle(
               R.string.http_issue_dialog_title);
         builder.setPositiveButton(R.string.ok,
               new DialogInterface.OnClickListener() {
                  public void onClick(DialogInterface dialog, int id) {
                     System.exit(0);
                  }
               });

         AlertDialog dialog = builder.create();
         dialog.show();

      } else
         timerElapsed = false;

      // parse the result to get snow information
      ParseWeatherHelper whetherHelper = new ParseWeatherHelper(result);
      minSnow = whetherHelper.getMinSnow();
      maxSnow = whetherHelper.getMaxSnow();
      lastSnow = whetherHelper.getLastSnow();

      // set the textviews for the snow info
      minSnowText.setText("Min snow: " + String.valueOf(minSnow));
      maxSnowText.setText("Max snow: " + String.valueOf(maxSnow));
      lastSnowText.setText("Last snow: " + lastSnow);

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

   protected void getMeteoFragment() {

      // check that data is enabled on the device
      checkDataAvailable();

      // if device is connected to Internet update the meteo
      if (dataAvailable) {

         // manage the meteo fragment
         fm = getFragmentManager();
         FragmentTransaction ft = fm.beginTransaction();
         ft.replace(R.id.meteo_list_placeholder, new MeteoFragment())
               .commitAllowingStateLoss();
      }
   }

   /*
    * ------------ PRIVATE METHODS ------------
    */

   private void checkDataAvailable() {

      // check whether data is enalbed and in case open DataDisabledDialog
      ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
      NetworkInfo netInfo = cm.getActiveNetworkInfo();
      if (netInfo == null || !netInfo.isConnectedOrConnecting()) {
         getNoDataDialog();
      }
      // else, if data is enabled but connection is not available, open an alert
      // dialog
      // TODO replace with a new defined DialogFragment DataUnavailableDialog
      else if (!netInfo.isConnected()) {
         AlertDialog.Builder builder = new AlertDialog.Builder(this);
         builder.setMessage(R.string.http_issue).setTitle(
               R.string.http_issue_dialog_title);
         builder.setPositiveButton(R.string.ok,
               new DialogInterface.OnClickListener() {
                  public void onClick(DialogInterface dialog, int id) {
                     finish();
                  }
               });

         AlertDialog dialog = builder.create();
         dialog.show();
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
   private static final int WAITING_TIME = 5000;

}