package com.storassa.android.scuolasci;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class LoginFragment extends Fragment implements HttpResultCallable {

   String responseString = "";
   int responseCode;
   int counter = 0;
   String username, password;

   String urlParameters;
   HttpURLConnection connection;
   CookieManager cookieManager;

   // get storage info
   SharedPreferences settings;

   // login info already get
   boolean loginInfoAvailable = false;

   // views and activities
   TextView usernameView;
   TextView passwordView;
   CheckBox rememberMe;
   CheckBox showPassword;
   Button loginBtn;
   MainActivity parentActivity;
   View result;

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
         Bundle savedInstanceState) {

      // Inflate the layout for this fragment
      result = inflater.inflate(R.layout.login_fragment, container, false);

      settings = getActivity().getPreferences(0);
      cookieManager = new CookieManager();
      CookieHandler.setDefault(cookieManager);

      // get the usarname and password views
      usernameView = (TextView) result.findViewById(R.id.username_text);
      passwordView = (TextView) result.findViewById(R.id.password_text);
      rememberMe = (CheckBox) result.findViewById(R.id.rememberMe);
      showPassword = (CheckBox) result.findViewById(R.id.show_password);

      // get the parent activity that fired this fragment
      parentActivity = (MainActivity) getActivity();

      showPassword.setOnCheckedChangeListener(new OnCheckedChangeListener() {
         
         public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
             // checkbox status is changed from uncheck to checked.
             if (!isChecked) {
                     // show password
                 passwordView.setTransformationMethod(PasswordTransformationMethod.getInstance());
             } else {
                     // hide password
                passwordView.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
             }
         }
     });

      // get the login button view and set its OnClickListener
      loginBtn = (Button) result.findViewById(R.id.login_button);
      loginBtn.setOnClickListener(new View.OnClickListener() {

         @Override
         public void onClick(View v) {

            // set the URL params initial string
            urlParameters = "task=signin&accion=signin";

            // set the username
            username = getUsername();
            urlParameters += "&textBoxUsername=" + username;

            // set the password
            password = getPassword();
            urlParameters += "&textBoxPassword=" + password;

            urlParameters += "&buttonSubmit=";

            // launch the thread to POST the server
            ExecutorService exec = Executors.newCachedThreadPool();
            exec.execute(new Runnable() {

               @Override
               public void run() {
                  sendLoginPost();

               }
            });

            // Launch the timer that each REPETITION_TIME check the
            // response
            // code
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {

               @Override
               public void run() {

                  checkCode(responseCode);
                  if (loginInfoAvailable)
                     this.cancel();

               } // run in TimerTask (new Runnable) definition

            }, 0, REPETITION_TIME); // TimerTask

         } // onClick in setOnClickListener(new View.OnClickListener())
           // definition

      }); // setOnClickListener definition

      return result;
   }

   public static void main(String[] args) {

   }

   private String getUsername() {
      String result = "";

      if (settings.getBoolean("remembered", true)) {
         result = settings.getString("username", "");
      } else {
         result = String.valueOf(usernameView.getText());
         SharedPreferences.Editor editor = settings.edit();
         editor.putBoolean("remembered", true).putString("username", result)
               .commit();
      }

      return result;
   }

   private String getPassword() {
      String result = "";

      if (settings.getBoolean("remembered", true)) {
         result = settings.getString("password", "");
      } else {
         result = String.valueOf(passwordView.getText());
         SharedPreferences.Editor editor = settings.edit();
         editor.putBoolean("remembered", true).putString("password", result)
               .commit();
      }

      return result;
   }

   private void checkCode(int code) {
      // if the response code is 200, and there is no message
      // that the password is incorrect
      if (code == 200
            && responseString.indexOf("User or password incorrect") == -1) {
         String[] availableButtons = getAvailBtn(responseString);

         // set availability to true to cancel the timer
         loginInfoAvailable = true;

         // register username and password, if requested
         settings = parentActivity.getPreferences(0);
         if (rememberMe.isChecked()) {
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("remembered", true)
               .putString("username", usernameView.getText().toString())
               .putString("password", passwordView.getText().toString())
               .commit();
         }

         // wait WAITING_TICKS * REPETITION_TIME mseconds to receive
         // 303

      } else if (counter < WAITING_TICKS) {
         counter++;

         // if the response code is 200, the password is incorrect
      } else {

         // set availability to true to cancel the timer
         loginInfoAvailable = true;

         parentActivity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
               CommonHelper.exitMessage("Login",
                     R.string.http_issue_dialog_title, parentActivity);
            } // run
         }); // Runnable
      } // else

   }

   @Override
   public void resultAvailable(String[] result) {

      responseCode = Integer.parseInt(result[0]);
      responseString = result[1];
      System.out.print(responseCode);
      System.out.println(":" + responseString);
   }

   private void sendLoginPost() {

      HttpConnectionHelper helper = HttpConnectionHelper.getHelper();
      helper.openConnection(this, username, password);

   }

   private String[] getAvailBtn(String response) {
      String temp = response;
      // TODO set the correct maximum index
      String[] result = new String[10];
      int index = 0, start, end;
      boolean endOfString = false;

      while (!endOfString) {
         start = temp.indexOf("txt18");
         temp = temp.substring(start + 1);

         end = temp.indexOf("<");
         result[index++] = temp.substring(6, end);

         if (temp.indexOf("p>") < temp.indexOf("txt18")
               || (temp.indexOf("txt18") == -1)) {
            endOfString = true;
         }
      }

      return result;
   }

   private final static String CHARSET = "ISO-8859-1";
   private final static int REPETITION_TIME = 1000;
   private final static int WAITING_TICKS = 40;
   private final static String SIGNING_URL = "http://www.scuolascilimone.com/it/area-riservata/access/signin";
   // private final static String SIGNING_URL = "155.132.54.41";

}
