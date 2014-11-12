package com.fleetchat.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.fleetchat.R;
import com.fleetchat.util.FileIOConstants;
import com.fleetchat.util.GCMConstants;
import com.fleetchat.util.TimeUtilities;

import android.content.Context;
import android.util.Log;

public class FileIO implements FileIOConstants, GCMConstants {

	private static final String TAG = "FileIO";

	Context _context;

	public FileIO(Context context) {
		_context = context;

	}

	/**************************************************************************/
	/** Dir **/
	/**************************************************************************/

	/**
	 * Get chat folder's direction.
	 * 
	 * @return /data/data/com.fleetchat/files/chat
	 */
	public String getChatDir() {
		File dir = new File(_context.getFilesDir() + File.separator + CHAT_DIR);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		return dir.getPath();
	}

	/**
	 * Get List of FileDir: /data/data/com.fleetchat/files/
	 */
	public void getFileDirList() {
		Log.i(TAG, "getFileDirList");
		File f = _context.getFilesDir();
		for (String s : f.list()) {
			Log.d(TAG, s);
		}
	}

	/**
	 * Get List of FileDir: /data/data/com.fleetchat/files/chat
	 */
	public String[] getChatDirList() {
		Log.i(TAG, "getChatDirList");
		File f = new File(getChatDir());
		for (String s : f.list()) {
			Log.d(TAG, s);
		}
		return f.list();
	}

	/**************************************************************************/
	/** Chat **/
	/**************************************************************************/

	public String getChatDetailModifiedTime(String filename) {
		File f = new File(getChatDir(), filename);
		Date d = new Date(f.lastModified());
		String time = TimeUtilities.getTimehhmm(d);
		return time;
	}

	public String getChatContent(String contact) {

		File f = new File(getChatDir(), contact);
		if (!f.exists()) {
			f.mkdirs();
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (getStringFromFile(f.getPath()).equals("")) {
			Log.d(TAG, f.getPath() + " : Content empty!");
			return " ";
		}
		return getStringFromFile(f.getPath());
	}

	public void writeFileToChat(String filename, String string) {

		writeFile(getChatDir(), filename, string);
	}

	public void writeFile(String dir, String filename, String string) {
		FileWriter fw;
		try {
			File f = new File(dir, filename);
			if (!f.exists()) {
				f.createNewFile();
			}
			fw = new FileWriter(f, true);
			fw.append(string);
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**************************************************************************/
	/** Contact **/
	/**************************************************************************/

	public void addContact(HashMap<String, Object> item) {
		File file = new File(_context.getFilesDir(), CONTACT);
		Log.i("path", _context.getFilesDir() + "");
		ArrayList<HashMap<String, Object>> list;

		if (getContact() == null) {
			list = new ArrayList<HashMap<String, Object>>();
		} else {
			list = getContact();
		}
		list.add(item);
		writeObject(file, list);
	}

	public Boolean checkContactExist(String gcmid) {

		ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();

		list = getContact();
		Log.w(TAG, "checkContactExist = " + list);

		// Read MESSAGE only
		if (list != null) {
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i).get(EXTRA_GCMID).equals(gcmid)) {
					Log.d(TAG, i + ". found exised : " + list.get(i));
					return true;
				}
			}
		}
		return false;
	}

	/*
	 * public Boolean addContact(String name, String date, String gcmid) {
	 * HashMap<String, Object> item = new HashMap<String, Object>();
	 * item.put("pic1", R.drawable.ic_launcher); item.put(EXTRA_NAME, name);
	 * item.put(EXTRA_GCMID, date); item.put(EXTRA_DATE, gcmid);
	 * 
	 * return addContact(item); }
	 */

	@SuppressWarnings("unchecked")
	public ArrayList<HashMap<String, Object>> getContact() {
		File file = new File(_context.getFilesDir(), CONTACT);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return (ArrayList<HashMap<String, Object>>) readObject(file);
	}

	/**************************************************************************/
	/** ChatDetail **/
	/**************************************************************************/

	public void addChatDetail(String filename, HashMap<String, Object> item) {
		File file = new File(getChatDir(), filename);
		ArrayList<HashMap<String, Object>> list;
		if (getChatDetail(filename) == null) {
			list = new ArrayList<HashMap<String, Object>>();
		} else {
			list = getChatDetail(filename);
		}
		if (!item.isEmpty()) {
			list.add(item);
		}
		writeObject(file, list);
	}

	public void addChatDetail(String filename, String message, boolean myPost) {
		HashMap<String, Object> item = new HashMap<String, Object>();
		item.put(MESSAGE, message);
		item.put(TIME, TimeUtilities.getTimeyyyyMMddhhmmss());
		item.put(WHO_POST, myPost);

		addChatDetail(filename, item);
	}

	@SuppressWarnings("unchecked")
	public ArrayList<HashMap<String, Object>> getChatDetail(String filename) {
		File file = new File(getChatDir(), filename);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return (ArrayList<HashMap<String, Object>>) readObject(file);
	}

	/**************************************************************************/
	/** Private **/
	/**************************************************************************/

	private boolean writeObject(File file, Object object) {

		try {
			FileOutputStream fos = new FileOutputStream(file);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(object);
			oos.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private Object readObject(File file) {
		Object returnlist = null;
		try {
			FileInputStream fis = new FileInputStream(file);
			ObjectInputStream ois = new ObjectInputStream(fis);
			returnlist = ois.readObject();
			ois.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnlist;
	}

	private String getStringFromFile(String filePath) {
		File f = new File(filePath);
		String ret = "";
		FileInputStream fin;
		try {
			fin = new FileInputStream(f);
			ret = convertStreamToString(fin);
			// Make sure you close all streams.
			fin.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ret;
	}

	/**
	 * getStringFromFile's sub-function.
	 */
	private String convertStreamToString(InputStream is) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line).append("\n");
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

}
