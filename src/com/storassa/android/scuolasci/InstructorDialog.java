package com.storassa.android.scuolasci;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class InstructorDialog extends DialogFragment {
	
	String[] instructors;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		
	    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    builder.setTitle(R.string.instructor_dialog_title)
	           .setItems(instructors, new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int which) {
	               // The 'which' argument contains the index position
	               // of the selected item
	           }
	    });
	    return builder.create();
	}
}
