package com.storassa.android.scuolasci;

import java.util.ArrayList;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

public class CustomerActivity extends Activity {

   ListView customerListView;
   Button addBtn, okBtn;
   ArrayList<String> customerData;

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
      customerData = getIntent().getStringArrayListExtra(
            "customers");
      CustomerArrayAdapter adapter = new CustomerArrayAdapter (this,
            R.layout.customer_list, customerData);
      customerListView.setAdapter(adapter);
      customerListView
            .setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

               @Override
               public boolean onItemLongClick(AdapterView<?> parentView, View view,
                     int position, long id) {
                  remove(position);
                  return false;
               }
            });

      addBtn = (Button) findViewById(R.id.add_customer_btn);
      addBtn.setOnClickListener(new View.OnClickListener() {
		@Override
		public void onClick(View arg0) {
			add();
		}
	});

      okBtn = (Button) findViewById(R.id.ok_customer_btn);
      okBtn.setOnClickListener(new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent newIntent = new Intent();
			newIntent.putExtra("customers", customerData);
			setResult(RESULT_OK, newIntent);
			CustomerActivity.this.finish();
		}
	});
   }
   
   public void addCustomer(String name) {
      customerData.add(name);
      runOnUiThread(new Runnable() {
         @Override
         public void run() {
            CustomerArrayAdapter adapter = new CustomerArrayAdapter (CustomerActivity.this,
                  R.layout.customer_list, customerData);
            customerListView.setAdapter(adapter);  
         }
      });
   }
   
   private void remove(int id) {
      customerData.remove(id);
      runOnUiThread(new Runnable() {
         @Override
         public void run() {
            CustomerArrayAdapter adapter = new CustomerArrayAdapter (CustomerActivity.this,
                  R.layout.customer_list, customerData);
            customerListView.setAdapter(adapter);         
         }
      });
   }
   
   private void add() {
      DialogFragment dialog = new AddCustomerDialog();
      dialog.show(getFragmentManager(), "addCustomerDialog");
   }
   
   private final static int CUSTOMER_REQUEST = 100;

}
