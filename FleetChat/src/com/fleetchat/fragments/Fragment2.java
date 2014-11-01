package com.fleetchat.fragments;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.fleetchat.R;
import com.fleetchat.tools.FileIO;

public class Fragment2 extends Fragment {
	protected static final String TAG = "Fragment2";
	View _view;
	TextView mDisplay;
	FileIO fio;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		_view = inflater.inflate(R.layout.fragment2, container, false);
		setView();
		set();
		fio = new FileIO(getActivity(), "test");
		return _view;
	}

	private void setView() {
		mDisplay = (TextView) _view.findViewById(R.id.fragment2_display);
		Button btn = (Button) _view.findViewById(R.id.fragment2_button1);
		btn.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				Time now = new Time();
				now.setToNow();
				fio.writeFileToChat("" + now.hour + now.minute, "test\n");
				fio.getChatDir();
				fio.getChatDirList();

				try {
					mDisplay.setText((String) fio.getStringFromFile("test"));

					fio.getFileDirList();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});
	}

	private ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
	private SimpleAdapter simpleAdapter;
	private ListView _listview;

	private static final String IMAGE = "IMAGE";
	private static final String NAME = "NAME";
	private static final String CONTENT = "CONTENT";
	private static final String TIME = "TIME";

	private void set() {
		_listview = (ListView) _view.findViewById(R.id.fragment2_listView1);
		// SET lst3
		for (int i = 0; i < 3; i++) {
			HashMap<String, Object> item = new HashMap<String, Object>();
			item.put(IMAGE, R.drawable.head);
			item.put(NAME, "NAMEdddddddddddddddddddd");
			item.put(CONTENT, "CONTENTddddddddddddd");
			item.put(TIME, "testddddddddddddddddddddddd");
			list.add(item);
		}

		_listview.setOnItemClickListener(new ListView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// lst1.get(position) = David, Rick or Jay，可搭配AlertDialog顯示之
				// 關於 Toast 可參考p.89
				Toast.makeText(getActivity(), "" + list.get(position),
						Toast.LENGTH_SHORT).show();
			}
		});

		simpleAdapter = new SimpleAdapter(getActivity(), list,
				R.layout.chat_list_item, new String[] { IMAGE, CONTENT, NAME,
						TIME }, // key的名字
				new int[] { R.id.chat_list_item_imageView1,
						R.id.chat_list_item_tv_content,
						R.id.chat_list_item_tv_name,
						R.id.chat_list_item_tv_time });
		_listview.setAdapter(simpleAdapter);
	}
}
