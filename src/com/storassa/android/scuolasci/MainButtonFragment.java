package com.storassa.android.scuolasci;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MainButtonFragment extends Fragment {
   
   View result;

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
         Bundle savedInstanceState) {
      
   // Inflate the layout for this fragment
      result = inflater.inflate(R.layout.main_button_layout, container, false);
      
      return result;
   }
}
