package com.storassa.android.scuolasci;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class LoginFragment extends Fragment {
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, 
        Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View result = inflater.inflate(R.layout.login_fragment, container, false);
        
        final Activity parentActivity = getActivity();
        Button loginBtn = (Button)result.findViewById(R.id.login_button);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                ((MainActivity)parentActivity).switchLoginFragment();
            }
        });
        
        return result;
    }


}
