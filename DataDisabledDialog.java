package com.storassa.android.scuolasci;

import java.lang.reflect.Method;
import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

public class DataDisabledDialog extends DialogFragment {

   private static MainActivity parentActivity;
   boolean timerElapsed;

   static DataDisabledDialog newInstance(String title, MainActivity parent) {
      parentActivity = parent;
      DataDisabledDialog f = new DataDisabledDialog();
      Bundle args = new Bundle();
      args.putString("title", title);
      f.setArguments(args);
      return f;
   }

   @Override
   public Dialog onCreateDialog(Bundle savedInstanceState) {

      // Use the Builder class for convenient dialog construction
      AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
      builder.setMessage(R.string.connection_unavailable).setPositiveButton(
            R.string.open_connection, new DialogInterface.OnClickListener() {
               public void onClick(DialogInterface dialog, int id) {
                  try {
                     enableData();
                     parentActivity.getMeteoFragment();
                  } catch (Exception e) {
                     throw new RuntimeException(e.toString());
                  }
               }
            }).setNegativeButton(R.string.cancel,
            new DialogInterface.OnClickListener() {
               public void onClick(DialogInterface dialog, int id) {
                  getActivity().finish();
               }
            });
      // Create the AlertDialog object and return it
      return builder.create();
   }

   /*
    * ------------ PRIVATE METHODS --------------------------------------
    */

   private void enableData() {
      ConnectivityManager mgr = (ConnectivityManager) getActivity()
            .getSystemService(Context.CONNECTIVITY_SERVICE);
      Method dataMtd;
      try {
         dataMtd = ConnectivityManager.class.getDeclaredMethod(
               "setMobileDataEnabled", boolean.class);
         dataMtd.setAccessible(true);
         dataMtd.invoke(mgr, true);
      } catch (Exception e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      checkDataAvailable();

   }

   private void checkDataAvailable() {

      ConnectivityManager cm = (ConnectivityManager) getActivity()
            .getSystemService(Context.CONNECTIVITY_SERVICE);
      NetworkInfo netInfo = cm.getActiveNetworkInfo();

      // start the timer: after WAITING_TIME set timerElapsed to true
      Timer timer = getTimer(WAITING_TIME);

      // wait for the network connection or exit when timer elapses
      while ((netInfo == null || !netInfo.isConnected()) && !timerElapsed) {
         cm = (ConnectivityManager) getActivity().getSystemService(
               Context.CONNECTIVITY_SERVICE);
         netInfo = cm.getActiveNetworkInfo();
      }

      // delete the timer to freeze timer resources
      timer.cancel();

      // if the timer elapsed without connection, open a popup to close the
      // program
      if (timerElapsed) {
         AlertDialog dialog = getNotifyExitDialog(R.string.http_issue,
               R.string.http_issue_dialog_title);
         dialog.show();
      } else {
         parentActivity.setDataAvailable();
         timerElapsed = false;
      }
   }

   private Timer getTimer(int wait) {
      Timer timer = new Timer();
      timer.schedule(new TimerTask() {
         @Override
         public void run() {
            timerElapsed = true;
         }
      }, wait);

      return timer;
   }

   private AlertDialog getNotifyExitDialog(int titleId, int msgId) {
      AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
      builder.setMessage(R.string.http_issue).setTitle(
            R.string.http_issue_dialog_title);
      builder.setPositiveButton(R.string.ok,
            new DialogInterface.OnClickListener() {
               public void onClick(DialogInterface dialog, int id) {
                  getActivity().finish();
               }
            });

      AlertDialog dialog = builder.create();
      return dialog;
   }

   static final int WAITING_TIME = 10000;

}