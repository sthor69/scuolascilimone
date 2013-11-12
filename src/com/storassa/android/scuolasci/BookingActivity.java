package com.storassa.android.scuolasci;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class BookingActivity extends Activity {

   TextView bookingDayTxt;
   TextView bookingHourTxt;
   TextView instructorTxt;
   TextView locationTxt;
   TextView sportTxt;
   TextView lessonTypeTxt;
   TextView customerNameTxt;
   
   Button cancelBtn;

   Instructor[] instructors;
   String[] instructorNames;
   String instructor;
   String[] skiAreas = { "sole", "maneggio", "limone1400" };
   String skiArea;
   String[] sports = { "sci", "snowboard" };
   String sport;
   String[] lessonTypes = { "lezioni individuali", "lezioni weekend" };
   String lessonType;
   ArrayList<String> customerNames;

   @Override
   protected void onCreate(Bundle savedInstanceState) {

      // get the instructors from instructors.txt in res/raw/
      getInstructors();

      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_booking);

      // initialize the views
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
      instructor = i;
      runOnUiThread(new Runnable() {
         @Override
         public void run() {
            instructorTxt.setText(i);
         }
      });
   }

   public void setLocation(final String _skiArea) {
      skiArea = _skiArea;
      runOnUiThread(new Runnable() {
         @Override
         public void run() {
            locationTxt.setText(_skiArea);
         }
      });
   }

   public void setSport(final String _sport) {
      sport = _sport;
      runOnUiThread(new Runnable() {
         @Override
         public void run() {
            sportTxt.setText(_sport);
         }
      });
   }

   public void setType(final String _lessonType) {
      lessonType = _lessonType;
      runOnUiThread(new Runnable() {
         @Override
         public void run() {
            lessonTypeTxt.setText(_lessonType);
         }
      });
   }

   public String getLocation() {
      return locationTxt.getText().toString();
   }

   public String getSport() {
      return sportTxt.getText().toString();
   }

   public String getInstructor() {
      return instructorTxt.getText().toString();
   }

   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
      if (CUSTOMER_REQUEST == requestCode) {
         if (resultCode == RESULT_OK) {
            customerNames.clear();
            customerNames.addAll(data.getStringArrayListExtra("customers"));
            runOnUiThread(new Runnable() {
               @Override
               public void run() {
                  String more = "";
                  if (customerNames.size() > 1)
                     more = ", ...";
                  customerNameTxt.setText(customerNames.get(0) + more);
               }
            });
         }
      }
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
            showLocationChooser();
         }
      });

      sportTxt = (TextView) findViewById(R.id.booking_sport);
      sport = sportTxt.getText().toString();
      sportTxt.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View arg0) {
            showSportChooser();
         }
      });

      lessonTypeTxt = (TextView) findViewById(R.id.lesson_type_txt);
      lessonType = lessonTypeTxt.getText().toString();
      lessonTypeTxt.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View arg0) {
            showTypeChooser();
         }
      });

      customerNameTxt = (TextView) findViewById(R.id.customer_txt);
      customerNames = new ArrayList<String>();
      customerNames.add(getIntent().getStringExtra("customer"));
      customerNameTxt.setText(customerNames.get(0));
      customerNameTxt.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View arg0) {
            showCustomers();
         }
      });
      
      cancelBtn = (Button)findViewById(R.id.booking_cancel_btn);
      cancelBtn.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            finish();
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
      // get the instructor list
      DialogFragment newFragment = GenericDialog.newInstance(
            DialogType.INSTRUCTORS, instructors, skiArea, sport);
      newFragment.show(getFragmentManager(), "instructorChooser");
   }

   private void showLocationChooser() {
      DialogFragment newFragment = GenericDialog.newInstance(
            DialogType.SKIAREA, skiAreas);
      newFragment.show(getFragmentManager(), "skiAreaChooser");
   }

   private void showSportChooser() {
      DialogFragment newFragment = GenericDialog.newInstance(DialogType.SPORTS,
            sports);
      newFragment.show(getFragmentManager(), "sportChooser");
   }

   private void showTypeChooser() {
      DialogFragment newFragment = GenericDialog.newInstance(DialogType.TYPES,
            lessonTypes);
      newFragment.show(getFragmentManager(), "lessonTypeChooser");
   }

   private void showCustomers() {
      Intent newIntent = new Intent(this, CustomerActivity.class);
      newIntent.putExtra("customers", customerNames);
      startActivityForResult(newIntent, CUSTOMER_REQUEST);
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

   private void getInstructors() {
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
         String[] data = null;
         while (scanner.hasNext()) {
            try {
               line = scanner.nextLine();
               data = line.split(",");
               String name = data[0];
               String surname = data[1];
               instructorNames[i] = name + " " + surname;
               String[] location = data[2].split(" ");
               String[] sport = data[3].split(" ");
               instructors[i++] = new Instructor(name, surname, location, sport);
            } catch (Exception e) {
               e.printStackTrace();
               System.out.println(data[0] + " " + data[1]);
            }
         }

         scanner.close();
      } catch (IOException e) {
         throw new RuntimeException(e);
      }

   }

   private final static int CUSTOMER_REQUEST = 100;
}
