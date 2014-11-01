package com.fleetchat.tools;

import static com.fleetchat.tools.CommonUtilities.EXTRA_MESSAGE;

import com.fleetchat.DemoActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.TextView;

/**
 * Receiving push messages
 */
public class MyBroadcastReceiver extends BroadcastReceiver {
	private TextView mTextView;

	public MyBroadcastReceiver() {
	}

	public MyBroadcastReceiver(TextView textView) {
		mTextView = textView;
	}

	@Override
	public void onReceive(Context context, Intent intent) {

		String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);
		DemoActivity.mDisplay.append(newMessage + "\n");

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
