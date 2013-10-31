package com.storassa.android.scuolasci;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class BookingActivity extends Activity {

	TextView bookingDayTxt;
	TextView bookingHourTxt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_booking);

		setViewMember();

		String currentDayString = getCurrentDay();
		bookingDayTxt.setText(getResources().getString(R.string.booking_day)
				+ ": " + currentDayString);
		bookingHourTxt.setText(getResources().getString(
				R.string.booking_hour_from)
				+ ": -   "
				+ getResources().getString(R.string.booking_hour_to)
				+ ": -");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.booking, menu);
		return true;
	}

	public void setBookingHour(final int hour, final int minute) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				bookingHourTxt.setText(getResources().getString(
						R.string.booking_hour_from)
						+ ": " + hour + ":" + 
						new DecimalFormat("00").format(minute) + " "
						+ getResources().getString(R.string.booking_hour_to)
						+ ": " + (hour + 1) + ":" + 
						new DecimalFormat("00").format(minute));
			}
		});
	}
	public void setBookingDay(Calendar c) {
		final String day = getStringFromCalendar(c);

		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				bookingDayTxt.setText(getResources().getString(
						R.string.booking_day)
						+ ": " + day);
			}
		});
		bookingDayTxt.setText(getResources().getString(R.string.booking_day)
				+ ": " + day);
	}

	private void setViewMember() {
		bookingDayTxt = (TextView) findViewById(R.id.booking_day_txt);
		bookingDayTxt.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				showDatePickerDialog();
			}
		});
		
		bookingHourTxt = (TextView) findViewById(R.id.booking_hour_txt);
		bookingHourTxt.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				showTimePickerDialog();
				
			}
		});
	}

	private void showDatePickerDialog() {
		DialogFragment newFragment = new DatePickerFragment();
		newFragment.show(getFragmentManager(), "datePicker");
	}

	private void showTimePickerDialog() {
		DialogFragment newFragment = new TimePickerFragment();
		newFragment.show(getFragmentManager(), "timePicker");
	}
	
	private String getCurrentDay() {
		Date date = new Date();
		Calendar c = Calendar.getInstance();

		try {
			c.setTime(date);
		} catch (Exception e) {
			throw new RuntimeException(e.toString());
		}

		return getStringFromCalendar(c);
	}

	private String getStringFromCalendar(Calendar c) {
		Month month = Month.values()[c.get(Calendar.MONTH)];
		String monthString = month.getLabel(getBaseContext());
		int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
		int year = c.get(Calendar.YEAR);

		String dayString = String.valueOf(dayOfMonth);
		dayString += " " + monthString + " " + String.valueOf(year);

		return dayString;
	}
}
