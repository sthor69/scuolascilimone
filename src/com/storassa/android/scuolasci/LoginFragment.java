package com.storassa.android.scuolasci;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class LoginFragment extends Fragment {

	String response;
	int counter = 0;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View result = inflater.inflate(R.layout.login_fragment, container,
				false);

		TextView usernameView = (TextView) result
				.findViewById(R.id.username_text);
		final String username = String.valueOf(usernameView.getText());
		TextView passwordView = (TextView) result
				.findViewById(R.id.username_text);
		final String password = String.valueOf(passwordView.getText());

		final MainActivity parentActivity = (MainActivity) getActivity();
		Button loginBtn = (Button) result.findViewById(R.id.login_button);
		loginBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String urlParameters = "task=signin&accion=signin&textBoxUsername=";
				urlParameters += username;
				urlParameters += "&textBoxPassword=";
				urlParameters += password;
				final String params = urlParameters;
				final URL url;

				try {
					url = new URL(
							"http://www.scuolascilimone.com/it/area-riservata/access/signin");
				} catch (MalformedURLException e1) {
					e1.printStackTrace();
					throw new RuntimeException(e1);
				}

				ExecutorService exec = Executors.newCachedThreadPool();
				exec.execute(new Runnable() {

					@Override
					public void run() {
						try {
							HttpURLConnection connection = (HttpURLConnection) url
									.openConnection();
							connection.setDoOutput(true);
							connection.setDoInput(true);
							connection.setInstanceFollowRedirects(false);
							connection.setRequestMethod("POST");
							connection.setRequestProperty("Content-Type",
									"application/x-www-form-urlencoded");
							connection.setRequestProperty("charset", "utf-8");
							connection.setRequestProperty(
									"Content-Length",
									""
											+ Integer.toString(params
													.getBytes().length));
							connection.setUseCaches(false);

							DataOutputStream wr = new DataOutputStream(
									connection.getOutputStream());
							wr.writeBytes(params);
							wr.flush();
							wr.close();
							BufferedInputStream in = new BufferedInputStream(
									connection.getInputStream());

							java.util.Scanner s = new java.util.Scanner(in)
									.useDelimiter("\\A");
							response = s.hasNext() ? s.next() : "";
						} catch (IOException e) {
							e.printStackTrace();
							throw new RuntimeException(e);
						}

					}
				});
			}
		});

		Timer timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				if (response != null){
					System.out.println(response);

					//TODO 
					this.cancel();
				} else if (counter < WAITING_TICKS) {
					counter++;
					
				} else {
					CommonHelper.exitMessage(R.string.http_issue,
							R.string.http_issue_dialog_title, parentActivity);
				} 
			}
		}, 0, REPETITION_TIME);

		return result;
	}

	private final static int REPETITION_TIME = 1000;
	private final static int WAITING_TICKS = 10;

}
