package com.tahanot.utils;

import android.app.AlertDialog;
import android.content.*;
import com.tahanot.R;

public class Alerts {
	private Context context;

	public Alerts(Context ctx)
	{
		context = ctx;
	}
	
	public void alertWithOkButton(int titleResourceId) {
		Logging.i(context, "Showing dialog with text: " + context.getString(titleResourceId));
		AlertDialog alertDialog = new AlertDialog.Builder(context).create();
		alertDialog.setTitle(titleResourceId);
		alertDialog.setButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		alertDialog.show();
	}

	
}
