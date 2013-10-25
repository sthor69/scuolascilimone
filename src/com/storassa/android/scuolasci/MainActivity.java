package com.storassa.android.scuolasci;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends Activity implements HttpResultCallable {

   FragmentManager fm;
   boolean logged = false;
   private int counter = 0;
   boolean dataEnabled = false, dataAvailable = false;
   String result = "";
   private BroadcastReceiver networkChangeReceiver;
   String username, password;

   // get storage info
   SharedPreferences settings;

   // snow parameters and views
   double minSnow, maxSnow;
   String lastSnow;
   TextView minSnowText, maxSnowText, lastSnowText;
   FrameLayout fl;
   Button racingBtn, scuderiaBtn, instructorBtn, loginBtn;
   LinearLayout adsView;

   // the enabled buttons
   Feature[] features;

   HttpConnectionHelper helper;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);

      setContentView(R.layout.activity_main);

      // get all the views
      setViewMember();

      // define a new default uncaught Exception handler, in order to point to
      // the one
      // connected to GitHub

      if (!(Thread.getDefaultUncaughtExceptionHandler() instanceof DefaultExceptionHandler)) {
         Thread.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler(
               Thread.getDefaultUncaughtExceptionHandler()));
      }

      // define the cookie manager to perform http requests
      CookieManager cookieManager = new CookieManager();
      CookieHandler.setDefault(cookieManager);

      // temporary debug: retrieve username and password
      settings = getPreferences(0);

      // TODO remove in production code
      SharedPreferences.Editor editor = settings.edit();
      editor.putBoolean("remembered", true).putString("username", "larapic")
            .putString("password", "gualano").commit();

      if (settings.getBoolean("remembered", false) == true) {
         username = settings.getString("username", "");
         password = settings.getString("password", "");
      }

      addButtons(helper);

      helper = HttpConnectionHelper.getHelper();
      helper.openConnection(this, username, password);


      // put the progress bar before loading the buttons
      // fl = (FrameLayout) findViewById(R.id.login_place);
      // fl.addView(new ProgressBar(this));

      // initialize variables (including views)
      dataEnabled = false;
      dataAvailable = false;

      // add the ads
      int[] res = {R.drawable.skier, R.drawable.ico_pioggia, R.drawable.icona_nuvola};
      ImageView[] imageView = new ImageView[3];
      for (int i = 0; i < 3; i++) {
         imageView[i] = new ImageView(this);
         imageView[i].setImageResource(res[i]);
         imageView[i].setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
               LayoutParams.WRAP_CONTENT));
         adsView.addView(imageView[i]);
      }

      // add the receiver for data availability
      addNetworkChangeReceiver();

      // if this is the first creation, the user is not logged
      // otherwise get the saved state
      // if (savedInstanceState == null)
      // logged = false;
      // else
      // logged = savedInstanceState.getBoolean("logged");

      checkDataAvailable();
      if (dataAvailable) {
         // get the meteo information, if data are available
         getMeteoFragment();

         // if data are available get the snow report
         getSnowReport();
      }

   }

   protected void setLogged(boolean _logged) {
      logged = _logged;
   }

   protected boolean isLogged() {
      return logged;
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

   // public void switchLoginFragment() {
   // fm = getFragmentManager();
   // FragmentTransaction ft = fm.beginTransaction();
   // ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
   // if (logged) {
   // try {
   // helper.openConnection(this, "larapic", "gualano");
   // } catch (Exception e) {
   // e.printStackTrace();
   // }
   // ft.replace(R.id.login_place, new LoggedFragment()).commit();
   // } else {
   // ft.replace(R.id.login_place, new MainButtonFragment()).commit();
   // }
   // logged = !logged;
   // }

   public void setDataAvailable() {
      dataAvailable = true;
   }

   public void setLoginStatus(boolean status) {
      logged = status;
   }

   /*
    * ------------ PRIVATE METHODS ------------
    */

   private void getMeteoFragment() {

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
                  ft.replace(R.id.meteo_list_placeholder, new MeteoFragment())
                        .commitAllowingStateLoss();
               } catch (Exception e) {
                  e.printStackTrace();
               }
            }
         });
      }
   }

   private void getSnowReport() {

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
                  runOnUiThread(new Runnable() {
                     public void run() {
                        minSnowText.setText("Min snow: "
                              + String.valueOf(minSnow));
                        maxSnowText.setText("Max snow: "
                              + String.valueOf(maxSnow));
                        lastSnowText.setText("Last snow: " + lastSnow);
                     }
                  });

               } else if (counter < WAITING_TICKS)
                  counter++;
               else {
                  CommonHelper.exitMessage(R.string.http_issue,
                        R.string.http_issue_dialog_title, MainActivity.this);
               }

            }
         }, 0, REPETITION_TIME);

         if (counter >= WAITING_TICKS)
            timer.cancel();
      }
   }

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

   private void addButtons(final HttpConnectionHelper helper) {

      ExecutorService exec = Executors.newCachedThreadPool();
      exec.execute(new Runnable() {

         @Override
         public void run() {
            try {
               while (features == null)
                  ;
               for (Feature f : features) {
                  if (f.equals(Feature.RACING_TEAM))
                     racingBtn.setEnabled(true);
                  else if (f.equals(Feature.SCUDERIA))
                     scuderiaBtn.setEnabled(true);
                  else if (f.equals(Feature.INSTRUCTOR))
                     instructorBtn.setEnabled(true);
               }

            } catch (Exception e) {
               e.printStackTrace();
            }
         }
      });

   }

   // when the user is connected remove the progress bar and show the buttons
   @Override
   public void resultAvailable(String[] result, Feature[] _features) {

      // if there are problems logging into the server, show the exit dialog
      if (result == null)
         runOnUiThread(new Runnable() {

            @Override
            public void run() {
               CommonHelper.exitMessage(R.string.http_issue_dialog_title,
                     R.string.http_issue, MainActivity.this);               
            }
         });
         
      setLogged(true);
      features = _features;
   }

   private void setViewMember() {
      minSnowText = (TextView) findViewById(R.id.min_snow_text);
      maxSnowText = (TextView) findViewById(R.id.max_snow_text);
      lastSnowText = (TextView) findViewById(R.id.last_snow_text);
      
      
      scuderiaBtn = (Button) findViewById(R.id.scuderia_btn);
      scuderiaBtn.setOnClickListener(new View.OnClickListener() {
         
         @Override
         public void onClick(View v) {
            // TODO Auto-generated method stub
            
         }
      });

      racingBtn = (Button) findViewById(R.id.racing_team_btn);
      racingBtn.setOnClickListener(new View.OnClickListener() {
         
         @Override
         public void onClick(View v) {
            // TODO Auto-generated method stub
            
         }
      });
      
      instructorBtn = (Button) findViewById(R.id.instructor_btn);
      instructorBtn.setOnClickListener(new View.OnClickListener() {
         
         @Override
         public void onClick(View v) {
            // TODO Auto-generated method stub
            
         }
      });
      
      loginBtn = (Button)findViewById(R.id.login_logout_btn);
      loginBtn.setOnClickListener(new View.OnClickListener() {
         
         @Override
         public void onClick(View v) {
            // TODO Auto-generated method stub
            
         }
      });
      
      adsView = (LinearLayout) findViewById(R.id.ads_container);
   }

   // private static final String WEATHER2_API =
   // "http://www.myweather2.com/Ski-Resorts/Italy/Limone-Piemonte/snow-report.aspx";
   private static final String WEATHER2_API = "http://www.myweather2.com/developer/weather.ashx?uac=Tax7vNwxqd&uref=bc13f25a-d9dc-4f89-9405-aa03b447a3c9";
   private static final int REPETITION_TIME = 1000;
   private static final int WAITING_TICKS = 10;

}