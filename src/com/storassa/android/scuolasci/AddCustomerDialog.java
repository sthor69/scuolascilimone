package com.storassa.android.scuolasci;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class AddCustomerDialog extends DialogFragment {
   Dialog dialog;
   String name;

   @Override
   public Dialog onCreateDialog(Bundle savedInstanceState) {
      AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
      // Get the layout inflater
      LayoutInflater inflater = getActivity().getLayoutInflater();
      final View view = inflater.inflate(R.layout.add_customer_dialog, null);

      // Inflate and set the layout for the dialog
      // Pass null as the parent view because its going in the dialog layout
      builder.setView(view)
      // Add action buttons
            .setPositiveButton(R.string.ok,
                  new DialogInterface.OnClickListener() {
                     @Override
                     public void onClick(DialogInterface dialog, int id) {

                        EditText userTxt = ((EditText) view
                              .findViewById(R.id.add_customer_txt));
                        
                        // hide the keyboard
                        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(
                              Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(userTxt.getWindowToken(), 0);
                        
                        String usr = userTxt.getText().toString();
                        CustomerActivity parent = (CustomerActivity)getActivity();
                        if (!usr.equals(""))
                        	parent.addCustomer(usr);
                     }
                  }).setNegativeButton(R.string.cancel,
                  new DialogInterface.OnClickListener() {
                     public void onClick(DialogInterface dialog, int id) {
                        AddCustomerDialog.this.getDialog().cancel();
                     }
                  });
      return builder.create();
   }
}
