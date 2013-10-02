package com.storassa.android.scuolasci;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

public class CommonHelper {
	public static void exitMessage (int titleId, int messageId, final Activity activity) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setMessage(R.string.http_issue).setTitle(
				R.string.http_issue_dialog_title);
		builder.setPositiveButton(R.string.ok,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						activity.finish();
					}
				});

		AlertDialog dialog = builder.create();
		dialog.show();
	}
}
