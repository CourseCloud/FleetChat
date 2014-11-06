package com.fleetchat.tools;

import com.fleetchat.DemoFragment;
import com.fleetchat.GCMIntentService;
import com.fleetchat.util.GCMConstants;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

/**
 * Receiving push messages
 */
public class MyBroadcastReceiver extends BroadcastReceiver implements
		GCMConstants {
	private static final String TAG = "MyBroadcastReceiver";
	private TextView mTextView;

	public MyBroadcastReceiver() {
	}

	public MyBroadcastReceiver(TextView textView) {
		mTextView = textView;
	}

	@Override
	public void onReceive(Context context, Intent intent) {

		// String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);
		// DemoActivity.mDisplay.append(newMessage + "\n");

		String action = intent.getStringExtra(EXTRA_ACTION);

		// ACTION_SEND_MESSAGE
		if (action.equals(ACTION_SEND_MESSAGE)) {

			// TODO 收到訊息後，fileIO到txt檔
			Log.w(TAG, "get ACTION_SEND_MESSAGE !!");

			Log.d(TAG, intent.getStringExtra(EXTRA_TITLE));
			Log.d(TAG, intent.getStringExtra(EXTRA_MESSAGE));
			Log.d(TAG, intent.getStringExtra(EXTRA_DATE));
		}
		// ACTION_ADD_FRIEND
		else if (action.equals(ACTION_ADD_FRIEND)) {
			Log.w(TAG, "get ACTION_ADD_FRIEND !!");

			Log.d(TAG, intent.getStringExtra(EXTRA_DATE));
			Log.d(TAG, intent.getStringExtra(EXTRA_GCMID));

		}

	}

	/**
	 * Notifies UI to display a message.
	 * <p>
	 * This method is defined in the common helper because it's used both by the
	 * UI and the background service.
	 *
	 * @param context
	 *            application's context.
	 * @param message
	 *            message to be displayed.
	 */
	public static void displayMessage(Context context, String message) {
		Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
		intent.putExtra(EXTRA_MESSAGE, message);
		context.sendBroadcast(intent);
	}

	public static void sendMessage(Context context, Bundle bundle) {
		String action = bundle.getString(EXTRA_ACTION);

		Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
		if (action != null) {
			if (action.equals(ACTION_ADD_FRIEND)) {

				intent.putExtra(EXTRA_ACTION, ACTION_ADD_FRIEND);
				intent.putExtra(EXTRA_DATE, bundle.getString(EXTRA_DATE));
				intent.putExtra(EXTRA_GCMID, bundle.getString(EXTRA_GCMID));
			} else if (action.equals(ACTION_SEND_MESSAGE)) {

				intent.putExtra(EXTRA_ACTION, ACTION_SEND_MESSAGE);
				intent.putExtra(EXTRA_TITLE, bundle.getString(EXTRA_TITLE));
				intent.putExtra(EXTRA_MESSAGE, bundle.getString(EXTRA_MESSAGE));
				intent.putExtra(EXTRA_DATE, bundle.getString(EXTRA_DATE));
			}
		}
		context.sendBroadcast(intent);

	}

}
