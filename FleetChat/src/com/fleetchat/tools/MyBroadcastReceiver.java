package com.fleetchat.tools;

import static com.fleetchat.tools.CommonUtilities.EXTRA_MESSAGE;

import com.fleetchat.DemoActivity;
import com.fleetchat.GCMIntentService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

/**
 * Receiving push messages
 */
public class MyBroadcastReceiver extends BroadcastReceiver {
	private static final String TAG = "MyBroadcastReceiver";
	private TextView mTextView;

	public MyBroadcastReceiver() {
	}

	public MyBroadcastReceiver(TextView textView) {
		mTextView = textView;
	}

	@Override
	public void onReceive(Context context, Intent intent) {

		// TODO 收到訊息後，fileIO到txt檔
		String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);
		DemoActivity.mDisplay.append(newMessage + "\n");
		Bundle b = intent.getExtras();
		if (b != null) {
			// ACTION_ADD_FRIEND
			if (b.getString(GCMIntentService.EXTRA_ACTION).equalsIgnoreCase(
					GCMIntentService.ACTION_ADD_FRIEND)) {

				Log.d(TAG, "get ACTION_ADD_FRIEND !!");

				// ACTION_SEND_MESSAGE
			} else if (b.getString(GCMIntentService.EXTRA_ACTION)
					.equalsIgnoreCase(GCMIntentService.ACTION_SEND_MESSAGE)) {

				Log.d(TAG, "get ACTION_SEND_MESSAGE !!");

			}
		}

		// if
		// (intent.getAction().equalsIgnoreCase(GCMIntentService.DAVID_GCM_RECEIVE_MESSAGE))
		// {
		// if (intent.getStringExtra(GCMIntentService.EXTRA_KEY_GCM_TOKEN) !=
		// null) {
		// if (mTextView != null) {
		// mTextView.append("KEY = " +
		// intent.getStringExtra(GCMIntentService.EXTRA_KEY_GCM_TOKEN) + "\n");
		// } else {
		// Log.d("DEBUG",
		// intent.getStringExtra(GCMIntentService.EXTRA_KEY_GCM_TOKEN));
		// }
		// }
		//
		// if (intent.getStringExtra(GCMIntentService.EXTRA_MESSAGE) != null) {
		// if (mTextView != null) {
		// mTextView.append(intent.getStringExtra(GCMIntentService.EXTRA_MESSAGE)
		// + "\n");
		// } else {
		// Log.d("DEBUG",
		// intent.getStringExtra(GCMIntentService.EXTRA_MESSAGE));
		// }
		// }
		// }
	}
}
