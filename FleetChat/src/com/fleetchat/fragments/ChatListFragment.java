package com.fleetchat.fragments;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Intent;
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

import com.fleetchat.ChatActivity;
import com.fleetchat.R;
import com.fleetchat.tools.FileIO;
import com.fleetchat.tools.TimeUtilities;

public class ChatListFragment extends Fragment {
	protected static final String TAG = "Fragment2";
	public static final String CHAT_TITLE = "CHAT_TITLE";

	// Adapter Constants
	private static final String IMAGE = "IMAGE";
	private static final String NAME = "NAME";
	private static final String CONTENT = "CONTENT";
	private static final String TIME = "TIME";

	// UI
	private View _view;
	private TextView _Display;
	private ListView _listview;

	// Class
	private FileIO fio;

	// Variables
	private String[] chatList;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		_view = inflater.inflate(R.layout.chat_list_fragment, container, false);
		fio = new FileIO(getActivity());
		setView();
		return _view;
	}

	private void setView() {
		_Display = (TextView) _view
				.findViewById(R.id.chat_list_fragment_display);
		_listview = (ListView) _view
				.findViewById(R.id.chat_list_fragment_listView1);
		Button _btn = (Button) _view
				.findViewById(R.id.chat_list_fragment_button1);

		// Button
		_btn.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				fio.writeFileToChat(TimeUtilities.getTimehhmm(), "");

				setAdatper();
			}
		});

		// ListView
		setAdatper();
		_listview.setOnItemClickListener(new ListView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// lst1.get(position) = David, Rick or Jay，可搭配AlertDialog顯示之
				// 關於 Toast 可參考p.89
				Toast.makeText(getActivity(), "" + list.get(position),
						Toast.LENGTH_SHORT).show();

				Log.d("DEBUG", "" + list.get(position));

				Intent intent = new Intent(getActivity(), ChatActivity.class);
				Bundle b = new Bundle();
				b.putString(CHAT_TITLE, chatList[position]);
				intent.putExtras(b);
				startActivity(intent);
			}
		});
	}

	private ArrayList<HashMap<String, Object>> list;
	private SimpleAdapter simpleAdapter;

	private void setAdatper() {
		list = new ArrayList<HashMap<String, Object>>();
		chatList = fio.getChatDirList();
		for (int i = 0; i < chatList.length; i++) {
			HashMap<String, Object> item = new HashMap<String, Object>();
			item.put(IMAGE, R.drawable.head);
			item.put(NAME, chatList[i]);
			item.put(CONTENT, "CONTENTddddddddddddd");
			item.put(TIME, fio.getChatDetails(chatList[i]));
			list.add(item);
		}

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
