package com.fleetchat.tools;

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

		// String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);
		// DemoActivity.mDisplay.append(newMessage + "\n");

		if (intent.getAction().equalsIgnoreCase(
				CommonUtilities.DISPLAY_MESSAGE_ACTION)) {
			Log.d(TAG,
					"get!!"
							+ intent.getExtras().getString(
									GCMIntentService.EXTRA_ACTION));
			Log.d(TAG,
					"get2!!"
							+ intent.getExtras().getString(
									GCMIntentService.EXTRA_DATE));
		}
		
		
		// ACTION_ADD_FRIEND
		if(intent.getStringExtra(GCMIntentService.ACTION_ADD_FRIEND) !=null){

			Log.d(TAG, "get ACTION_ADD_FRIEND !!");
		}
		// ACTION_SEND_MESSAGE
		if(intent.getAction().equals(GCMIntentService.ACTION_SEND_MESSAGE)){

			// TODO 收到訊息後，fileIO到txt檔 
			Log.d(TAG, "get ACTION_SEND_MESSAGE !!");
			

			Log.d(TAG, intent.getStringExtra(GCMIntentService.EXTRA_TITLE));
			Log.d(TAG, intent.getStringExtra(GCMIntentService.EXTRA_MESSAGE));
			Log.d(TAG, intent.getStringExtra(GCMIntentService.EXTRA_DATE));
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
		Intent intent = new Intent(CommonUtilities.DISPLAY_MESSAGE_ACTION);
		intent.putExtra(GCMIntentService.EXTRA_MESSAGE, message);
		context.sendBroadcast(intent);
	}

	public static void addFriend(Context context, String date) {
		Intent intent = new Intent(GCMIntentService.ACTION_ADD_FRIEND);
		intent.putExtra(GCMIntentService.EXTRA_DATE, date);
		intent.putExtra(GCMIntentService.EXTRA_DATE, date);
		context.sendBroadcast(intent);
	}

	public static void sendMessage(Context context, String title,
			String message, String date) {
		Intent intent = new Intent(GCMIntentService.ACTION_SEND_MESSAGE);
		intent.putExtra(GCMIntentService.EXTRA_TITLE, title);
		intent.putExtra(GCMIntentService.EXTRA_MESSAGE, message);
		intent.putExtra(GCMIntentService.EXTRA_DATE, date);
		context.sendBroadcast(intent);
	}
}
