package com.fleetchat.util;

import java.util.Date;

import android.text.format.DateFormat;

public class TimeUtilities {
	private static final String TAG = "TimeUtilities";

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
	
	/**
	 * get yyyyMMddhhmmss
	 * 
	 * @return ex. 20141105181533
	 */
	public static String getTimeyyyyMMddhhmmss(Date d) {
		return (String) DateFormat.format("yyyyMMddhhmmss", d);
	}

	/**
	 * get yyyyMMddhhmmss
	 * 
	 * @return ex. 20141105181533
	 */
	public static String getTimeyyyyMMddhhmmss() {
		return (String) DateFormat.format("yyyyMMddhhmmss", new Date());
	}

}
