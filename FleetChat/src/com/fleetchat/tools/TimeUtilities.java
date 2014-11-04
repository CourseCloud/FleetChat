package com.fleetchat.tools;

import java.util.Date;

import android.text.format.DateFormat;
import android.text.format.Time;
import android.util.Log;

public class TimeUtilities {
	private static final String TAG = "TimeUtilities";

	/**
	 * Get current time.
	 * 
	 * @return
	 */
	public static String getTime() {
		Log.d(TAG, getNowTime().toString());
		return getNowTime().toString();
	}

	/**
	 * get hour:minute
	 * 
	 * @return ex. 00:00, 12:32
	 */
	public static String getTimehhmm() {
		return (String) DateFormat.format("hh:mm", new Date());
	}

	/**
	 * get hour:minute
	 * 
	 * @return ex. 00:00, 12:32
	 */
	public static String getTimehhmm(Date d) {
		return (String) DateFormat.format("hh:mm", d);
	}

	private static Time getNowTime() {
		Time now = new Time();
		now.setToNow();
		return now;
	}
}
