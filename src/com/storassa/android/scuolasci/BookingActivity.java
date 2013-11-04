package com.storassa.android.scuolasci;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class BookingActivity extends Activity {

   TextView bookingDayTxt;
   TextView bookingHourTxt;
   TextView instructorTxt;
   TextView locationTxt;
   TextView sportTxt;

   Instructor[] instructors;
   String[] instructorNames;
   String skiArea;
   String sport;

   @Override
   protected void onCreate(Bundle savedInstanceState) {

      try {
         Resources res = getResources();
         InputStream in_s = res.openRawResource(R.raw.instructors);

         byte[] b = new byte[in_s.available()];
         in_s.read(b);
         String text = new String(b);

         Scanner scanner = new Scanner(text);
         
         int instructorNr = Integer.parseInt(scanner.nextLine());
         instructors = new Instructor[instructorNr];
         instructorNames = new String[instructorNr];

         String line;
         int i = 0;
         while (scanner.hasNext()) {
            line = scanner.nextLine();
            String name = line.split(",")[0];
            String surname = line.split(",")[1];
            instructorNames[i] = name + " " + surname;
            String[] location = line.split(",")[2].split(" ");
            String[] sport = line.split(",")[3].split(" ");
            instructors[i++] = new Instructor(name, surname, location, sport);
         }

         scanner.close();
      } catch (IOException e) {
         throw new RuntimeException(e);
      }

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
                  + ": "
                  + hour
                  + ":"
                  + new DecimalFormat("00").format(minute)
                  + " "
                  + getResources().getString(R.string.booking_hour_to)
                  + ": "
                  + (hour + 1)
                  + ":"
                  + new DecimalFormat("00").format(minute));
         }
      });
   }

   public void setBookingDay(Calendar c) {
      final String day = getStringFromCalendar(c);

      runOnUiThread(new Runnable() {

         @Override
         public void run() {
            bookingDayTxt.setText(getResources()
                  .getString(R.string.booking_day)
                  + ": " + day);
         }
      });
      bookingDayTxt.setText(getResources().getString(R.string.booking_day)
            + ": " + day);
   }

   public void setInstructor(final String i) {
      runOnUiThread(new Runnable() {

         @Override
         public void run() {
            instructorTxt.setText(i);
         }
      });
   }

   public String getLocation() {
      return locationTxt.getText().toString();
   }

   public String getSport() {
      return sportTxt.getText().toString();
   }

   public String[] getInstructors() {
      // TODO insert right code here
      return null;
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

      instructorTxt = (TextView) findViewById(R.id.instructor_txt);
      instructorTxt.setOnClickListener(new View.OnClickListener() {

         @Override
         public void onClick(View arg0) {
            showInstructorChooser();

         }
      });

      locationTxt = (TextView) findViewById(R.id.booking_location);
      skiArea = locationTxt.getText().toString();
      locationTxt.setOnClickListener(new View.OnClickListener() {

         @Override
         public void onClick(View arg0) {
            // TODO Auto-generated method stub

         }
      });

      sportTxt = (TextView) findViewById(R.id.booking_sport);
      sport = sportTxt.getText().toString();
      sportTxt.setOnClickListener(new View.OnClickListener() {

         @Override
         public void onClick(View arg0) {
            // TODO Auto-generated method stub

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

   private void showInstructorChooser() {
      DialogFragment newFragment = InstructorDialog
            .newInstance(instructors, skiArea, sport);
      newFragment.show(getFragmentManager(), "instructorChooser");
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

   // TODO set the real number
   private final static int INSTRUCTOR_NR = 100;
}
