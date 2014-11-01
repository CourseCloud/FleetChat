/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.fleetchat;

import static com.fleetchat.tools.CommonUtilities.DISPLAY_MESSAGE_ACTION;
import static com.fleetchat.tools.CommonUtilities.EXTRA_MESSAGE;
import static com.fleetchat.tools.CommonUtilities.SENDER_ID;
import static com.fleetchat.tools.CommonUtilities.SERVER_URL;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fleetchat.tools.AlertDialogManager;
import com.fleetchat.tools.CommonUtilities;
import com.fleetchat.tools.ConnectionDetector;
import com.google.android.gcm.GCMRegistrar;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.MulticastResult;
import com.google.android.gcm.server.Sender;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Main UI for the demo app.
 */
@SuppressWarnings("deprecation")
public class DemoActivity extends Activity {

	protected static final String TAG = "DemoActivity";
	TextView mDisplay;
	AsyncTask<Void, Void, Void> mRegisterTask;

	// class
	AlertDialogManager alert = new AlertDialogManager();
	ConnectionDetector cd;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		checkNotNull(SERVER_URL, "SERVER_URL");
		checkNotNull(SENDER_ID, "SENDER_ID");
		// Make sure the device has the proper dependencies.
		GCMRegistrar.checkDevice(this);
		// Make sure the manifest was properly set - comment out this line
		// while developing the app, then uncomment it when it's ready.
		GCMRegistrar.checkManifest(this);
		setContentView(R.layout.main2);
		setView();

		// Initailize
		cd = new ConnectionDetector(getApplicationContext());

		// Check if Internet present
		if (!cd.isConnectingToInternet()) {
			// Internet Connection is not present
			alert.showAlertDialog(DemoActivity.this, "Internet Connection Error",
					"Please connect to working Internet connection", false);
			// stop executing code by return
			return;
		}

		final String regId = GCMRegistrar.getRegistrationId(this);
		if (regId.equals("")) {
			// Automatically registers application on startup.
			GCMRegistrar.register(this, SENDER_ID);
		} else {
			// Device is already registered on GCM, check server.
			if (GCMRegistrar.isRegisteredOnServer(this)) {
				// Skips registration.
				mDisplay.append(getString(R.string.already_registered) + "\n");
			} else {

				// // Try to register again, but not in the UI thread.
				// // It's also necessary to cancel the thread onDestroy(),
				// // hence the use of AsyncTask instead of a raw thread.
				// final Context context = this;
				// mRegisterTask = new AsyncTask<Void, Void, Void>() {
				//
				// @Override
				// protected Void doInBackground(Void... params) {
				// ServerUtilities.register(context, regId);
				// return null;
				// }
				//
				// @Override
				// protected void onPostExecute(Void result) {
				// mRegisterTask = null;
				// }
				//
				// };
				// mRegisterTask.execute(null, null, null);
			}
		}

	}
	
	private void setView(){
		mDisplay = (TextView) findViewById(R.id.display);
		Button btn = (Button) findViewById(R.id.button1);
		btn.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				List<String> regIds = new ArrayList<String>();

				Log.w(TAG, "onClick");
				Log.w(TAG, GCMRegistrar.getRegistrationId(getApplicationContext()));
				regIds.add(GCMRegistrar.getRegistrationId(getApplicationContext()));
				// regIds.add(CommonUtilities.tempID2);
				postData(regIds, "Welcome", "Hello GCM");

			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		/*
		 * Typically, an application registers automatically, so options below
		 * are disabled. Uncomment them if you want to manually register or
		 * unregister the device (you will also need to uncomment the equivalent
		 * options on options_menu.xml).
		 */
		/*
		 * case R.id.options_register: GCMRegistrar.register(this, SENDER_ID);
		 * return true; case R.id.options_unregister:
		 * GCMRegistrar.unregister(this); return true;
		 */
		case R.id.options_clear:
			mDisplay.setText(null);
			return true;
		case R.id.options_exit:
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onResume() {
		registerReceiver(mHandleMessageReceiver, new IntentFilter(DISPLAY_MESSAGE_ACTION));
	super.onResume();
	}

	@Override
	protected void onDestroy() {
		if (mRegisterTask != null) {
			mRegisterTask.cancel(true);
		}
		unregisterReceiver(mHandleMessageReceiver);
		GCMRegistrar.onDestroy(this);
		super.onDestroy();
	}

	private void checkNotNull(Object reference, String name) {
		if (reference == null) {
			throw new NullPointerException(getString(R.string.error_config, name));
		}
	}

	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);
			mDisplay.append(newMessage + "\n");
		}
	};

	public void postData(final List<String> regIds, final String title, final String message) {
		final Sender sender = new Sender(CommonUtilities.GOOGLE_API_KEY);

		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					if (regIds.size() > 0) {
						Message.Builder msg = new Message.Builder();
						msg.addData(GCMIntentService.EXTRA_TITLE, title);
						msg.addData(GCMIntentService.EXTRA_MESSAGE, message);

						MulticastResult MR = sender.sendNoRetry(msg.build(), regIds);
						Log.e(TAG, MR.toString());
					}
				} catch (IOException e) {
					Log.e(TAG, e.toString());
				}

			}
		}).start();
	}

}