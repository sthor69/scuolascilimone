package com.storassa.android.scuolasci;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MeteoArrayAdapter extends ArrayAdapter<MeteoItem> {
    int resource;

    public MeteoArrayAdapter(Context context, int _resource,
            List<MeteoItem> items) {
        super(context, _resource, items);
        resource = _resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Create and inflate the View to display
        LinearLayout newView;
        if (convertView == null) {
            // Inflate a new view if this is not an update.
            newView = new LinearLayout(getContext());
            String inflater = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater li;
            li = (LayoutInflater) getContext().getSystemService(inflater);
            li.inflate(resource, newView, true);
        } else {
            // Otherwise we’ll update the existing View
            newView = (LinearLayout) convertView;
        }
        MeteoItem item = getItem(position);

        int iconResource = item.getIconResourceId();
        int minTemp = (int)item.getMinTemp();
        int maxTemp = (int) item.getMaxTemp();
        int humidity = (int) (item.getHumidity() * 100);
        int precipit = (int) (item.getPrecipit() * 100);
        
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(
                "dd-MM-yyyy", Locale.getDefault());
        
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(date);
        } catch (Exception e) {
            throw new RuntimeException(e.toString());
        }
        c.add(Calendar.DATE, position - 1); // number of days to add
        
        DayOfWeek dayOfWeek = DayOfWeek.values()[c.get(Calendar.DAY_OF_WEEK) - 1];
        String dayOfWeekString = dayOfWeek.getLabel(parent.getContext());
        
        Month month = Month.values()[c.get(Calendar.MONTH)];
        String monthString = month.getLabel(parent.getContext());
        int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
        int year = c.get(Calendar.YEAR);
        
        String dayString = String.valueOf(dayOfMonth);
        dayString += " " + monthString + " " + String.valueOf(year);
       
        ImageView meteoImageView = (ImageView) newView
                .findViewById(R.id.meteo_list_image);
        TextView minTempView = (TextView) newView
                .findViewById(R.id.meteo_list_min_temp_text);
        TextView maxTempView = (TextView) newView
                .findViewById(R.id.meteo_list_max_temp_text);
        TextView humidityView = (TextView) newView
                .findViewById(R.id.meteo_list_humidity_text);
        TextView precipitView = (TextView) newView
                .findViewById(R.id.meteo_list_precipit_text);
        TextView meteoDayOfWeek = (TextView) newView
                .findViewById(R.id.meteo_day_of_week);
        TextView meteoDay = (TextView) newView
                .findViewById(R.id.meteo_day);
        
        meteoImageView.setImageResource(iconResource);
        minTempView.setText(" " + String.valueOf(minTemp));
        maxTempView.setText(" " + String.valueOf(maxTemp));
        humidityView.setText(" " + String.valueOf(humidity) + "%");
        precipitView.setText(" " + String.valueOf(precipit) + "%");
        meteoDayOfWeek.setText(dayOfWeekString);
        meteoDay.setText(dayString);
        return newView;
    }
}
