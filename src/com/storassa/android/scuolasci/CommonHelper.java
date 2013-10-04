package com.storassa.android.scuolasci;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

public class CommonHelper {
	public static void exitMessage (int titleId, int messageId, final Activity activity) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setMessage(R.string.http_issue).setTitle(
				R.string.http_issue_dialog_title);
		builder.setPositiveButton(R.string.ok,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						activity.finish();
					}
				});

		AlertDialog dialog = builder.create();
		dialog.show();
	}

      public static MeteoItem getMeteoItemFromDataPoint(FIODataPoint _dataPoint) {
      String icon = _dataPoint.icon().replace('\"', ' ').trim();
      MeteoItem result = new MeteoItem(getIconResourceIdFromString(icon),
            _dataPoint.temperatureMin(), _dataPoint.temperatureMax(),
            _dataPoint.humidity(), _dataPoint.precipProbability(), -1, -1, -1);

      return result;
   	}

      public static int getIconResourceIdFromString(String iconString) {
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


}
