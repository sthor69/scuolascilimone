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
            if (counter != -1 && hourly != null) {
	        String[] meteoIconString = new String[MAX_HOURS];

            	for (int i = 0; i < MAX_HOURS; i++)
            	meteoIconString[i] = hourly.getHour(i).icon().replace('\"', ' ')
                  .trim();
         
            	for (int i = 0; i < MAX_HOURS; i++) {
            		dataPoint[i] = hourly.getHour(i);
            		meteoItems.add(CommonHelper.getMeteoItemFromDataPoint(dataPoint[i]));
		}
	    }
	    else if (counter < WAITING_TICKS) {
		counter++;
		}
	    else {
		CommonHelper.exitMessage(R.string.http_issue, R.string.http_issue_dialog_title, parentActivity);		
	        }
         }
      }, 0, REPETITION_TIME);

         int resId = R.layout.meteo_list;
         adapter = new MeteoArrayAdapter(this, resId, meteoItems);
         ListView meteoListView = (ListView) findViewById(R.id.hour_meteo_list);
         meteoListView.setAdapter(adapter);
      }

   }

   private final static int REPETITION_TIME = 1000;
   private final static int MAX_HOURS = 48;
   private final static int WAITING_TICKS = 10;
}
