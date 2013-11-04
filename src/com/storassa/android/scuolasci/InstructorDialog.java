package com.storassa.android.scuolasci;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class InstructorDialog extends DialogFragment {

   String[] instructors;

   public static InstructorDialog newInstance(Instructor[] _instructors,
         String _skiArea, String _sport) {
      InstructorDialog dialog = new InstructorDialog();

      String[] instructorNames = getNames(_instructors, _skiArea, _sport);
      Bundle args = new Bundle();
      args.putStringArray("instructors", instructorNames);
      dialog.setArguments(args);
      return dialog;
   }

   @Override
   public Dialog onCreateDialog(Bundle savedInstanceState) {

      instructors = getArguments().getStringArray("instructors");

      AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
      builder.setTitle(R.string.instructor_dialog_title).setItems(instructors,
            new DialogInterface.OnClickListener() {
               public void onClick(DialogInterface dialog, int which) {
                  ((BookingActivity) getActivity())
                        .setInstructor(instructors[which]);
               }
            });
      return builder.create();
   }

   private static String[] getNames(Instructor[] _instructors, String _skiArea,
         String _sport) {
      String skiArea = _skiArea.toLowerCase();
      String sport = _sport.toLowerCase();
      String[] temp = new String[_instructors.length];

      int count = 0;
      for (Instructor ins : _instructors)
         if (ins.getSkiArea().contains(skiArea)
               && ins.getDiscipline().contains(sport))
            temp[count++] = ins.getName() + " " + ins.getSurname();

      String[] result = null;
      if (count > 0) {
         result = new String[count];
         for (int i = 0; i < count; i++)
            result[i] = temp[i];
      }

      return result;
   }
}
