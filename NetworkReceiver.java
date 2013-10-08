package com.storassa.android.scuolasci;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NetworkReceiver extends BroadcastReceiver {

   @Override
   public void onReceive(Context context, Intent intent) {
      context.sendBroadcast(new Intent(
            "com.storassa.android.scuolasci.NETWORK_CHANGE"));

   }

}
