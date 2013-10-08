package com.storassa.android.scuolasci;

import java.net.MalformedURLException;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.Button;
import android.widget.TextView;

public class LoginFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View result = inflater.inflate(R.layout.login_fragment, container,
				false);

		TextView usernameView = (TextView)result.findViewById(R.id.username_text);
		final String username = String.valueOf(usernameView.getText());
		TextView passwordView = (TextView)result.findViewById(R.id.username_text);
		final String password = String.valueOf(passwordView.getText());
		
		final MainActivity parentActivity = (MainActivity)getActivity();
		Button loginBtn = (Button) result.findViewById(R.id.login_button);
		loginBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					HttpConnectionHelper helper = HttpConnectionHelper
							.getHelper();
					String result = helper.openConnection(username, password);
					System.out.println(result);
					if (result == "") {
						parentActivity.setLoginStatus(true);
						parentActivity.switchLoginFragment();
					}
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					CommonHelper.exitMessage(R.string.http_issue_dialog_title,
							R.string.http_issue, parentActivity);
				}
			}
		});

		return result;
	}

}
