package com.storassa.android.scuolasci;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import dme.forecastiolib.FIODataPoint;
import dme.forecastiolib.FIOHourly;
import dme.forecastiolib.ForecastIO;

public class MeteoActivity extends Activity {

   private FIODataPoint[] dataPoint;
   private ArrayList<MeteoItem> meteoItems;
   private ArrayAdapter<MeteoItem> adapter;
   private int[] meteoIconResource = null;
   FIOHourly hourly = null;
   boolean timerElapsed = false;

   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
   // Inflate the menu; this adds items to the action bar if it is present.
      getMenuInflater().inflate(R.menu.activity_meteo, menu);
      
      return true;

   }

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_meteo);

      if (savedInstanceState != null)
         meteoItems = (ArrayList<MeteoItem>) Arrays
               .asList((MeteoItem[]) savedInstanceState
                     .getParcelableArray("meteo_items"));
      else
         meteoItems = new ArrayList<MeteoItem>();
      
      getIntent().getExtras().getInt("Day");
      
      dataPoint = new FIODataPoint[MAX_HOURS];
      meteoIconResource = new int[MAX_HOURS];

      // get the hourly meteo in a new thread
      ExecutorService exec = Executors.newCachedThreadPool();
      exec.execute(new Runnable() {

         @Override
         public void run() {

            String forecastIoKey = getResources().getString(
                  R.string.forecastio_api_key);
            String limoneLatitude = getResources().getString(
                  R.string.limone_latitude);
            String limoneLongitude = getResources().getString(
                  R.string.limone_longitude);

            try {
               ForecastIO fio = new ForecastIO(forecastIoKey);
               fio.setUnits(ForecastIO.UNITS_SI);
               fio.setExcludeURL("currently,minutely,daily");
               fio.getForecast(limoneLatitude, limoneLongitude);
               hourly = new FIOHourly(fio);
            } catch (Exception e) {
               e.printStackTrace();
            }
         }
      });

      // wait for the http response or exit after 5s
      Timer timer = new Timer();
      timer.schedule(new TimerTask() {

         @Override
         public void run() {
            timerElapsed = true;

         }
      }, WAITING_TIME);

      while (hourly == null && !timerElapsed)
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
         
         timerElapsed = false;

      } else {

         String[] meteoIconString = new String[MAX_HOURS];
         for (int i = 0; i < MAX_HOURS; i++)
            meteoIconString[i] = hourly.getHour(i).icon().replace('\"', ' ')
                  .trim();
         
         for (int i = 0; i < MAX_HOURS; i++) {
            dataPoint[i] = hourly.getHour(i);
            meteoItems.add(getMeteoItemFromDataPoint(dataPoint[i]));
         }

         // TODO update using a new hour_meteo_list layout
         int resId = R.layout.meteo_list;
         adapter = new MeteoArrayAdapter(this, resId, meteoItems);
         ListView meteoListView = (ListView) findViewById(R.id.hour_meteo_list);
         meteoListView.setAdapter(adapter);
      }

   }

   // TODO refactor using a helper class

   private MeteoItem getMeteoItemFromDataPoint(FIODataPoint _dataPoint) {
      String icon = _dataPoint.icon().replace('\"', ' ').trim();
      MeteoItem result = new MeteoItem(getIconResourceIdFromString(icon),
            _dataPoint.temperatureMin(), _dataPoint.temperatureMax(),
            _dataPoint.humidity(), _dataPoint.precipProbability(), -1, -1, -1);

      return result;
   }

   // TODO move to a static helper
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

   private static final int WAITING_TIME = 10000;
   private final static int MAX_HOURS = 48;
}
