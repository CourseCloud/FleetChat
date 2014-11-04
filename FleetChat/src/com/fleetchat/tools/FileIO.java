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
import java.util.Date;

import android.content.Context;
import android.util.Log;

public class FileIO {
	private static final String CHAT_DIR = "chat";

	private static final String TAG = "FileIO";

	Context _context;

	public FileIO(Context context) {
		_context = context;
	}

	/**
	 * Get chat folder's diraction.
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
	 * 
	 * @return
	 */
	public String[] getChatDirList() {
		Log.i(TAG, "getChatDirList");
		File f = new File(getChatDir());
		for (String s : f.list()) {
			Log.d(TAG, s);
		}
		return f.list();
	}

	@SuppressWarnings("deprecation")
	public String getChatDetails(String filename) {
		String[] details;
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

	public void output(String filename, String string) {
		FileOutputStream outputStream;

		try {
			outputStream = _context.openFileOutput(filename,
					Context.MODE_PRIVATE);
			outputStream.write(string.getBytes());
			outputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void writeFileToChat(String filename, String string) {
		writeFile(getChatDir(), filename, string);
	}

	public void writeFile(String dir, String filename, String string) {
		FileWriter fw;
		try {
			File f = new File(dir, filename);
			fw = new FileWriter(f, true);
			fw.append(string);
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void input() {

		// String returnlist = null;
		// try {
		//
		// FileInputStream fis = _context.openFileInput(_filename);
		// ObjectInputStream ois = new ObjectInputStream(fis);
		// returnlist = ois.readObject();
		// ois.close();
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// return returnlist;
	}

	public void saveObject(String filename, Object object) {

		try {
			FileOutputStream fos = _context.openFileOutput(filename,
					Context.MODE_PRIVATE);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(object);
			oos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Object loadObject(String filename) {
		Object returnlist = null;
		try {
			FileInputStream fis = _context.openFileInput(filename);
			ObjectInputStream ois = new ObjectInputStream(fis);
			returnlist = ois.readObject();
			ois.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnlist;
	}

	public String getStringFromFile(String filePath) {
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

	public String convertStreamToString(InputStream is) {
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
