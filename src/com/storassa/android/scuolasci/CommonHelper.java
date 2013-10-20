package com.storassa.android.scuolasci;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import dme.forecastiolib.FIODataPoint;

public class CommonHelper {
	public static void exitMessage(int titleId, int messageId,
			final Activity activity) {
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

	public static void exitMessage(String title, int messageId,
			final Activity activity) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setMessage(R.string.http_issue).setTitle(
				title);
		builder.setPositiveButton(R.string.ok,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						activity.finish();
					}
				});

		AlertDialog dialog = builder.create();
		dialog.show();
	}

	/**
	 * This helper returns a MeteoItem object from a DataPoint object
	 * 
	 * @param dataPoint
	 *            the DataPoint which the data should be retrieved from
	 * @param daily
	 *            it states whether the DataPoint is a daily or not (hourly)
	 * @return the MeteoItem
	 */
	public static MeteoItem getMeteoItemFromDataPoint(FIODataPoint dataPoint,
			boolean daily) {
		String icon = dataPoint.icon().replace('\"', ' ').trim();
		MeteoItem result;
		// daily data points include max and min temp
		if (daily) {
			result = new MeteoItem(getIconResourceIdFromString(icon),
					dataPoint.temperatureMin(), dataPoint.temperatureMax(),
					dataPoint.humidity(), dataPoint.precipProbability(), -1,
					-1, -1);

			// hourly data points only include temp
		} else {
			result = new MeteoItem(getIconResourceIdFromString(icon),
					dataPoint.temperature(), dataPoint.temperature(),
					dataPoint.humidity(), dataPoint.precipProbability(), -1,
					-1, -1);
		}

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
