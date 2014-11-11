package com.fleetchat.tools;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.fleetchat.MainActivity;
import com.fleetchat.R;

@SuppressWarnings("deprecation")
public class AlertDialogManager {
	/**
	 * Function to display simple Alert Dialog
	 * 
	 * @param context
	 *            - application context
	 * @param title
	 *            - alert dialog title
	 * @param message
	 *            - alert message
	 * @param status
	 *            - success/failure (used to set icon) - pass null if you don't
	 *            want icon
	 * */
	public void showAlertDialog(Context context, String title, String message,
			Boolean status) {
		AlertDialog alertDialog = new AlertDialog.Builder(context).create();

		alertDialog.setTitle(title);
		alertDialog.setMessage(message);

		if (status != null)
			// Setting alert dialog icon
			alertDialog
					.setIcon((status) ? R.drawable.success : R.drawable.fail);

		// Setting OK Button
		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			}
		});

		// Showing Alert Message
		alertDialog.show();
	}

	public void showEnterNameDialog(final Context context) {
		final AlertDialog alertDialog = new AlertDialog.Builder(context)
				.create();
		alertDialog.setCancelable(false);
		alertDialog.setTitle("Enter Your Name");

		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);

		final EditText input = new EditText(context);
		input.setLayoutParams(lp);
		input.setHint("Type here");
		input.setText(context.getSharedPreferences(MainActivity.PREF,
				Context.MODE_PRIVATE).getString(MainActivity.PREF_NAME, ""));

		final Button btn = new Button(context);
		btn.setLayoutParams(lp);
		btn.setText("Confirm");
		btn.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!input.getText().toString().trim().isEmpty()) {
					SharedPreferences sp = context.getSharedPreferences(
							MainActivity.PREF, Context.MODE_PRIVATE);
					SharedPreferences.Editor se = sp.edit();
					se.putString(MainActivity.PREF_NAME, input.getText()
							.toString().trim());
					se.commit();
					alertDialog.dismiss();
				}
			}
		});

		final LinearLayout ll = new LinearLayout(context);
		ll.setLayoutParams(lp);
		ll.setOrientation(LinearLayout.VERTICAL);
		ll.addView(input);
		ll.addView(btn);
		alertDialog.setView(ll); // uncomment this line

		// Showing Alert Message
		alertDialog.show();
	}

	public void showMessageDialog(Context context, String title, String message) {
		final AlertDialog alertDialog = new AlertDialog.Builder(context).create();

		alertDialog.setTitle(title);

		alertDialog.setMessage(message);

		// Setting OK Button
		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				alertDialog.dismiss();
			}
		});

		// Showing Alert Message
		alertDialog.show();
	}
}
