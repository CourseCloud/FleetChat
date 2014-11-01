package com.fleetchat.tools;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.content.IntentFilter;
import android.util.Log;

import com.fleetchat.GCMIntentService;
import com.google.android.gcm.GCMRegistrar;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.MulticastResult;
import com.google.android.gcm.server.Sender;

@SuppressWarnings("deprecation")
public class GCMUtilities {
	private static final String TAG = "GCMUtilities";
	Context _context;
	MyBroadcastReceiver receiver;

	public GCMUtilities(Context context) {
		_context = context;

		// Make sure the device has the proper dependencies.
		GCMRegistrar.checkDevice(context);
		// Make sure the manifest was properly set - comment out this line
		// while developing the app, then uncomment it when it's ready.
		GCMRegistrar.checkManifest(context);

		receiver = new MyBroadcastReceiver();
	}

	public void onResume() {
		IntentFilter filter = new IntentFilter(CommonUtilities.DISPLAY_MESSAGE_ACTION);
		_context.registerReceiver(receiver, filter);
	}

	public void onDestroy() {
		try {
			_context.unregisterReceiver(receiver);
			GCMRegistrar.onDestroy(_context);
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}
	}

	public void postData(final List<String> regIds, final String title,
			final String message) {
		final Sender sender = new Sender(CommonUtilities.GOOGLE_API_KEY);

		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					if (regIds.size() > 0) {
						Message.Builder msg = new Message.Builder();
						msg.addData(GCMIntentService.EXTRA_TITLE, title);
						msg.addData(GCMIntentService.EXTRA_MESSAGE, message);

						MulticastResult MR = sender.sendNoRetry(msg.build(),
								regIds);
						Log.e(TAG, MR.toString());
					}
				} catch (IOException e) {
					Log.e(TAG, e.toString());
				}

			}
		}).start();
	}

	/**
	 * Get GCM registration id
	 */
	public String getRegistrationId() {
		if (GCMRegistrar.getRegistrationId(_context).equals("")) {
			Log.d(TAG, "GCMRegistrar.register");
			GCMRegistrar.register(_context, CommonUtilities.SENDER_ID);
		}
		return GCMRegistrar.getRegistrationId(_context);
	}

}
