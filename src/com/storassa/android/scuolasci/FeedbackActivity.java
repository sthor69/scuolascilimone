package com.storassa.android.scuolasci;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class FeedbackActivity extends Activity {

	Button okBtn, cancelBtn;
	EditText text;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feedback);

		text = (EditText)findViewById(R.id.feedback_txt);
		
		okBtn = (Button) findViewById(R.id.feedback_ok_btn);
		okBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// it's not ACTION_SEND
				Intent intent = new Intent(Intent.ACTION_SENDTO); 
				intent.setType("text/plain");
				intent.putExtra(Intent.EXTRA_SUBJECT,
						"Feedback");
				// TODO put the correct string in the booking email
				String body = getEmailBody();
				intent.putExtra(Intent.EXTRA_TEXT, body);
				intent.setData(Uri.parse("mailto:sertorassa@hotmail.com")); 
				// this will make such that when user returns to your app,
				// your app is displayed, instead of the email app.
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
				startActivity(intent);
				finish();

			}
		});

		cancelBtn = (Button) findViewById(R.id.feedback_cancel_btn);
		cancelBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
	}
	
	private String getEmailBody() {
		String result = "";
		
		result += "Commento ricevuto da: " + getIntent().getStringExtra("customer");
		result += "\n\n" + text.getText();
		
		return result;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.feedback, menu);
		return true;
	}

}
