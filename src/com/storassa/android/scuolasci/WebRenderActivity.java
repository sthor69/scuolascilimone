package com.storassa.android.scuolasci;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.webkit.WebView;

public class WebRenderActivity extends Activity implements HttpResultCallable {

   WebView container;
   Intent intent;
   Request request;
   URL url;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_web_render);
      
      setViewMember();
      
      container.setBackgroundColor(0);
      intent = getIntent();
      request = (Request)intent.getSerializableExtra("request");
      
      ExecutorService exec = Executors.newCachedThreadPool();
      exec.execute(new Runnable() {

         @Override
         public void run() {
            try {
               HttpConnectionHelper helper = HttpConnectionHelper.getHelper();
               switch (request) {
               case SCUDERIA:
                  url = new URL(SCUDERIA_URI);
                  break;
               case RACINGTEAM:
                  url = new URL(RACINGTEAM_URI);
                  break;
               case INSTRUCTOR:
                  url = new URL(INSTRUCTOR_URI);
                  break;
               }
                  
               helper.openGenericConnection(Request.SCUDERIA,
                     WebRenderActivity.this, url);
            } catch (IOException e) {

            }

         }
      });

   }

   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      // Inflate the menu; this adds items to the action bar if it is present.
      getMenuInflater().inflate(R.menu.web_render, menu);
      return true;
   }

   @Override
   public void resultAvailable(Request request, String[] result,
         Feature[] features) {
      if ((result != null) && (result[0] != null))
         container.loadDataWithBaseURL(null, getAvailBtn(result[1]), "text/html", "utf-8", null);

   }

   //
   // ---------------- PRIVATE METHODS --------------------
   //

   private String getAvailBtn(String response) {
      String temp = response;
      // TODO set the correct maximum index
      String result = "";
      int start, end;
      
      start = temp.indexOf("lastnews");
      temp = temp.substring(start + 11);

      end = temp.indexOf("hidden");
      result = temp.substring(0, end - 13);
      
      if (result.indexOf("\"/pdf") != -1)
    	  result = result.replace("\"/pdf/", "\"http://www.scuolascilimone.com/pdf/");
      
//      switch(request) {
//      case SCUDERIA:
//         start = temp.indexOf("lastnews");
//         temp = temp.substring(start + 11);
//
//         end = temp.indexOf("hidden");
//         result = temp.substring(0, end - 13);
//         break;
//      case RACINGTEAM:
//         start = temp.indexOf("lastnews");
//         temp = temp.substring(start + 11);
//
//         end = temp.indexOf("hidden");
//         result = temp.substring(0, end - 13);
//         break;
//      case INSTRUCTOR:
//         start = temp.indexOf("lastnews");
//         temp = temp.substring(start + 11);
//
//         end = temp.indexOf("hidden");
//         result = temp.substring(0, end - 13);
//         break;
//      }

      return result;
      

   }

   private void setViewMember() {
      container = (WebView) findViewById(R.id.web_render);
   }

   private static final String SCUDERIA_URI = "http://www.scuolascilimone.com/it/area-riservata/news/latest/scuderia";
   private static final String RACINGTEAM_URI = "http://www.scuolascilimone.com/it/area-riservata/news/latest/racing-team";
   private static final String INSTRUCTOR_URI = "http://www.scuolascilimone.com/it/area-riservata/news/latest/maestri";

}
