package com.storassa.android.scuolasci;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class CustomerActivity extends Activity {

	ListView customerListView;
	Button addBtn, okBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_customer);

		setMemberView();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.customer, menu);
		return true;
	}

	private void setMemberView() {
		customerListView = (ListView) findViewById(R.id.customer_listview);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				R.id.customer_list_item, getIntent().getStringArrayExtra(
						"customers"));
		customerListView.setAdapter(adapter);
		customerListView
				.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

					@Override
					public boolean onItemLongClick(AdapterView<?> arg0,
							View arg1, int arg2, long arg3) {
						// TODO Auto-generated method stub
						return false;
					}
				});

		addBtn = (Button) findViewById(R.id.add_customer_btn);

		okBtn = (Button) findViewById(R.id.ok_customer_btn);
	}

}
