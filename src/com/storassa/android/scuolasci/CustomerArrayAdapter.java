package com.storassa.android.scuolasci;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CustomerArrayAdapter extends ArrayAdapter<String> {
   
   int resource;

   public CustomerArrayAdapter(Context context, int _resource,
         List<String> items) {
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
           // Otherwise we'll update the existing View
           newView = (LinearLayout) convertView;
       }
       
       String item = getItem(position);
       
       TextView customerTxt = (TextView)newView.findViewById(R.id.customer_list_item);
       customerTxt.setText(item);
       
       return newView;
   }
}
