package com.storassa.android.scuolasci;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class MainButtonFragment extends Fragment {
   
   View result;
   
   MainActivity parentActivity;
   Button loginBtn;
   

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
         Bundle savedInstanceState) {
      
   // Inflate the layout for this fragment
      result = inflater.inflate(R.layout.main_button_layout, container, false);
      
      parentActivity = (MainActivity)getActivity();
      loginBtn = (Button) result.findViewById(R.id.login_logout_btn);
      
      if (parentActivity.isLogged())
         loginBtn.setText(R.string.logout);
      else
         loginBtn.setText(R.string.login);
      
      HttpConnectionHelper helper = HttpConnectionHelper.getHelper();
      final Feature[] features = helper.getFeature();
      
      parentActivity.runOnUiThread(new Runnable() {

		@Override
		public void run() {
		      for (Feature f : features) {
		    	  if (f.equals(Feature.SCUDERIA)) {
		    		  parentActivity.scuderiaBtn.setEnabled(true);
		    	  } else if (f.equals(Feature.RACING_TEAM)) {
		    		  parentActivity.racingBtn.setEnabled(true);
		    	  } else if (f.equals(Feature.INSTRUCTOR)) {
		    		  parentActivity.instructorBtn.setEnabled(true);
		    	  }
		    		  
		      }
		}
    	  
      });
      
      return result;
   }
}
