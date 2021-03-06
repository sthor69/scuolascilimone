package com.storassa.android.scuolasci;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;

public class LoginFragment extends DialogFragment {
   Dialog dialog;
   String username, password;

   EditText userTxt, pwdTxt;
   CheckBox hidePwdCheckBox;

   @Override
   public Dialog onCreateDialog(Bundle savedInstanceState) {
      AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
      // Get the layout inflater
      LayoutInflater inflater = getActivity().getLayoutInflater();
      final View view = inflater.inflate(R.layout.login_fragment, null);

      userTxt = ((EditText) view.findViewById(R.id.username));
      pwdTxt = ((EditText) view.findViewById(R.id.password));

      hidePwdCheckBox = (CheckBox) view.findViewById(R.id.show_password);
      // add onCheckedListener on checkbox
      // when user clicks on this checkbox, this is the handler.
      hidePwdCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
         public void onCheckedChanged(CompoundButton buttonView,
               boolean isChecked) {
            // checkbox status is changed from uncheck to checked.
            if (!isChecked) {
               // show password
               pwdTxt.setTransformationMethod(PasswordTransformationMethod
                     .getInstance());
            } else {
               // hide password
               pwdTxt.setTransformationMethod(HideReturnsTransformationMethod
                     .getInstance());
            }
         }
      });

      // Inflate and set the layout for the dialog
      // Pass null as the parent view because its going in the dialog layout
      builder.setView(view)
      // Add action buttons
            .setPositiveButton(R.string.ok,
                  new DialogInterface.OnClickListener() {
                     @Override
                     public void onClick(DialogInterface dialog, int id) {

                        // hide the keyboard
                        InputMethodManager imm = (InputMethodManager) getActivity()
                              .getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(pwdTxt.getWindowToken(), 0);

                        String usr = userTxt.getText().toString();
                        String pwd = pwdTxt.getText().toString();
                        boolean rememberMe = ((CheckBox) view
                              .findViewById(R.id.remember_me_btn)).isChecked();
                        ((StartingActivity) getActivity()).loginUser(usr, pwd,
                              rememberMe);
                     }
                  }).setNegativeButton(R.string.cancel,
                  new DialogInterface.OnClickListener() {
                     public void onClick(DialogInterface dialog, int id) {
                        LoginFragment.this.getDialog().cancel();
                     }
                  });
      return builder.create();
   }
}
