package com.storassa.android.scuolasci;

import java.util.Locale;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class GenericDialog extends DialogFragment {

	String[] items;
	static Bundle args;
	DialogType type;
	AlertDialog.Builder builder;

	public static GenericDialog newInstance(DialogType _type, Object[] _arg,
			String _skiArea, String _sport) {
		GenericDialog dialog = new GenericDialog();

		switch (_type) {
		case INSTRUCTORS:
			dialog.type = _type;
			String[] instructorNames = getNames((Instructor[]) _arg, _skiArea,
					_sport);
			args = new Bundle();
			args.putStringArray("instructors", instructorNames);
			dialog.setArguments(args);
			break;
		case SPORTS:
			dialog.type = _type;
			args = new Bundle();
			args.putStringArray("sports", (String[]) _arg);
			dialog.setArguments(args);
			break;
		case SKIAREA:
			dialog.type = _type;
			args = new Bundle();
			args.putStringArray("skiareas", (String[]) _arg);
			dialog.setArguments(args);
			break;
		case TYPES:
			dialog.type = _type;
			args = new Bundle();
			args.putStringArray("lessontypes", (String[]) _arg);
			dialog.setArguments(args);
			break;
		}
		return dialog;

	}

	public static GenericDialog newInstance(DialogType type, String[] args) {
		return newInstance(type, args, null, null);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		switch (type) {
		case INSTRUCTORS:
			items = getArguments().getStringArray("instructors");
			builder = new AlertDialog.Builder(getActivity());
			builder.setTitle(R.string.instructor_dialog_title).setItems(items,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							((BookingActivity) getActivity())
									.setInstructor(items[which]);
						}
					});
			break;
		case SKIAREA:
			items = getArguments().getStringArray("skiareas");
			builder = new AlertDialog.Builder(getActivity());
			builder.setTitle(R.string.location_dialog_title).setItems(items,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							((BookingActivity) getActivity())
									.setLocation(items[which]);
						}
					});
			break;
		case SPORTS:
			items = getArguments().getStringArray("sports");
			builder = new AlertDialog.Builder(getActivity());
			builder.setTitle(R.string.sport_dialog_title).setItems(items,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							((BookingActivity) getActivity())
									.setSport(items[which]);
						}
					});
			break;		
		case TYPES:
			items = getArguments().getStringArray("lessontypes");
			builder = new AlertDialog.Builder(getActivity());
			builder.setTitle(R.string.type_dialog_title).setItems(items,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							((BookingActivity) getActivity())
									.setType(items[which]);
						}
					});
			break;
		}
		return builder.create();
	}

	private static String[] getNames(Instructor[] _instructors,
			String _skiArea, String _sport) {
		String skiArea = _skiArea.toLowerCase(Locale.getDefault());
		String sport = _sport.toLowerCase(Locale.getDefault());
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
