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

import org.apache.http.client.HttpClient;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class LoginFragment extends Fragment {

	String responseString = "";
	int responseCode;
	int counter = 0;
	String username, password;

	String urlParameters;
	HttpURLConnection connection;

	// get storage info
	BasicStorageHelper storageHelper;

	// views and activities
	TextView usernameView;
	TextView passwordView;
	Button loginBtn;
	MainActivity parentActivity;
	View result;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		result = inflater.inflate(R.layout.login_fragment, container, false);

		// get the storage helper
		storageHelper = BasicStorageHelper.getStorageHelper();

		// get the usarname and password views
		usernameView = (TextView) result.findViewById(R.id.username_text);
		passwordView = (TextView) result.findViewById(R.id.password_text);

		// get the parent activity that fired this fragment
		parentActivity = (MainActivity) getActivity();

		// get the login button view and set its OnClickListener
		loginBtn = (Button) result.findViewById(R.id.login_button);
		loginBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				// set the URL params initial string
				urlParameters = "task=signin&accion=signin";

				// set the username
				username = getUsername();
				urlParameters += username;
				urlParameters += "&textBoxUsername=";

				// set the password
				password = getPassword();
				urlParameters += "&textBoxPassword=";
				urlParameters += password;

				// launch the thread to POST the server
				ExecutorService exec = Executors.newCachedThreadPool();
				exec.execute(new Runnable() {

					@Override
					public void run() {
						sendLoginPost();

					}
				});

				// Launch the timer that each REPETITION_TIME check the
				// response
				// code
				Timer timer = new Timer();
				timer.schedule(new TimerTask() {

					@Override
					public void run() {

						checkCode(responseCode);
						this.cancel();

					} // run in TimerTask (new Runnable) definition

				}, 0, REPETITION_TIME); // TimerTask

			} // onClick in setOnClickListener(new View.OnClickListener())
				// definition

		}); // setOnClickListener definition

		return result;
	}

	private String getUsername() {
		String result = "";

		if (storageHelper.isRememberMe()) {
			result = storageHelper.getUsername();
		} else {
			result = String.valueOf(usernameView.getText());
		}

		return result;
	}

	private String getPassword() {
		String result = "";

		if (storageHelper.isRememberMe()) {
			result = storageHelper.getPassword();
		} else {
			result = String.valueOf(passwordView.getText());
		}

		return result;
	}

	private void checkCode(int code) {
		// if the response code is 303, the password is correct
		if (responseCode == 303) {
			HttpConnectionHelper httpHelper = HttpConnectionHelper.getHelper();
			String location = connection.getHeaderField("Location");
			
			try {
				httpHelper.openGenericConnection(location);
			} catch (IOException e) {
				e.printStackTrace();
			}

			// wait WAITING_TICKS * REPETITION_TIME mseconds to receive
			// 303
		} else if (counter < WAITING_TICKS) {
			counter++;

			// if the response code is 200, the password is incorrect
		} else if (responseCode == 200) {
			parentActivity.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					AlertDialog.Builder builder = new AlertDialog.Builder(
							parentActivity);

					builder.setMessage("Wrong password").setTitle(
							String.valueOf("INFO"));
					builder.setPositiveButton(R.string.ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									parentActivity.finish();
								} // onClick
							}); // OnClickListener

					AlertDialog dialog = builder.create();
					dialog.show();
				} // run
			}); // runOnUiThread

		} else {
			parentActivity.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					CommonHelper.exitMessage(R.string.http_issue,
							R.string.http_issue_dialog_title, parentActivity);
				} // run
			}); // Runnable
		} // else

	}

	private void sendLoginPost() {

		// set the URL
		final String params = urlParameters;
		final URL url;

		try {
			url = new URL(SIGNING_URL);
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
			throw new RuntimeException(e1);
		}

		try {
			connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setInstanceFollowRedirects(false);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			connection.setRequestProperty("charset", "utf-8");
			connection.setRequestProperty("Content-Length",
					"" + Integer.toString(params.getBytes().length));
			connection.setUseCaches(false);

			DataOutputStream wr = new DataOutputStream(
					connection.getOutputStream());
			wr.writeBytes(params);
			wr.flush();
			wr.close();
			BufferedInputStream in = new BufferedInputStream(
					connection.getInputStream());
			responseCode = connection.getResponseCode();

			java.util.Scanner s = new java.util.Scanner(in).useDelimiter("\\A");
			responseString = s.hasNext() ? s.next() : "";
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	private final static int REPETITION_TIME = 1000;
	private final static int WAITING_TICKS = 10;
	private final static String SIGNING_URL = "http://www.scuolascilimone.com/it/area-riservata/access/signin";
	// private final static String SIGNING_URL = "155.132.54.41";
}
