package com.fleetchat.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import android.content.Context;

public class FileIO {
	private static final String CHAT_DIR = "chat";
	
	Context _context;
	File _file;
	String _filename;

	public FileIO(Context context, String filename) {
		_context = context;
		_filename = filename;
		_file = new File(context.getFilesDir(), filename);
	}

	public void output(String string) {
		FileOutputStream outputStream;

		try {
		  outputStream = _context.openFileOutput(_filename, Context.MODE_PRIVATE);
		  outputStream.write(string.getBytes());
		  outputStream.close();
		} catch (Exception e) {
		  e.printStackTrace();
		}
	}
	public void write(String string){
		FileWriter fw;
		try {
			fw = new FileWriter(_file, true);
			fw.append(string);
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void input() {

//		String returnlist = null;
//		try {
//
//			FileInputStream fis = _context.openFileInput(_filename);
//			ObjectInputStream ois = new ObjectInputStream(fis);
//			returnlist = ois.readObject();
//			ois.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return returnlist;
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
	
	public String getStringFromFile (String filePath) throws Exception {
	    File fl = new File(filePath);
	    FileInputStream fin = new FileInputStream(_file);
	    String ret = convertStreamToString(fin);
	    //Make sure you close all streams.
	    fin.close();        
	    return ret;
	}
	public String convertStreamToString(InputStream is) throws Exception {
	    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
	    StringBuilder sb = new StringBuilder();
	    String line = null;
	    while ((line = reader.readLine()) != null) {
	      sb.append(line).append("\n");
	    }
	    reader.close();
	    return sb.toString();
	}
}
