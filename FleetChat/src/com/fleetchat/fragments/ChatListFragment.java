package com.fleetchat.fragments;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.fleetchat.ChatActivity;
import com.fleetchat.MainActivity;
import com.fleetchat.R;
import com.fleetchat.R.id;
import com.fleetchat.tools.AlertDialogManager;
import com.fleetchat.tools.FileIO;
import com.fleetchat.util.GCMConstants;
import com.fleetchat.util.TimeUtilities;

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
		_btn.setVisibility(View.GONE);

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
				Intent intent = new Intent(getActivity(), ChatActivity.class);
				Bundle b = new Bundle();
				b.putString(GCMConstants.EXTRA_GCMID, chatList[position]);
				intent.putExtras(b);
				startActivity(intent);
			}
		});

		// TODO "DEBUG" use
		_listview.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				new AlertDialogManager()
						.showMessageDialog(getActivity(), "position = "
								+ position, list.get(position).toString());
				return false;
			}
		});
	}

	private ArrayList<HashMap<String, Object>> list;
	private SimpleAdapter simpleAdapter;

	private void setAdatper() {
		list = new ArrayList<HashMap<String, Object>>();
		chatList = fio.getChatDirList();
		if (chatList.length > 0) {
			if (fio.getContactName(chatList[0]) != null) {
				_Display.setText("");
				for (int i = 0; i < chatList.length; i++) {
					HashMap<String, Object> item = new HashMap<String, Object>();
					// TODO [Check] Is it need to fix?
					Log.i("contactList", fio.getContactPorID(chatList[i]));
					item.put(IMAGE,
							getPortrait(fio.getContactPorID(chatList[i])));
					item.put(NAME, fio.getContactName(chatList[i]));
					item.put(CONTENT, fio.getLastChatDetail(chatList[i]));
					item.put(TIME, fio.getChatDetailModifiedTime(chatList[i]));
					list.add(item);
					simpleAdapter = new SimpleAdapter(getActivity(), list,
							R.layout.chat_list_item, new String[] { IMAGE,
									CONTENT, NAME, TIME }, // keyªº¦W¦r
							new int[] { R.id.chat_list_item_imageView1,
									R.id.chat_list_item_tv_content,
									R.id.chat_list_item_tv_name,
									R.id.chat_list_item_tv_time });

					_listview.setAdapter(simpleAdapter);
				}
			}
		} else {
			_Display.setText("No friend ids added");
		}
	}

	@Override
	public void onResume() {
		setAdatper();
		super.onResume();
	}

	private int getPortrait(String porID) {
		int POR_ID = Integer.valueOf(porID);
		int portraitID = 0;
		switch (POR_ID) {
		case 00:
			portraitID = R.drawable.porknown;
			break;
		case 11:
			portraitID = R.drawable.por011;
			break;
		case 12:
			portraitID = R.drawable.por012;
			break;
		case 13:
			portraitID = R.drawable.por013;
			break;
		case 14:
			portraitID = R.drawable.por014;
			break;
		case 21:
			portraitID = R.drawable.por021;
			break;
		case 22:
			portraitID = R.drawable.por022;
			break;
		case 23:
			portraitID = R.drawable.por023;
			break;
		case 24:
			portraitID = R.drawable.por024;
			break;
		case 31:
			portraitID = R.drawable.por031;
			break;
		case 32:
			portraitID = R.drawable.por032;
			break;
		case 33:
			portraitID = R.drawable.por033;
			break;
		case 34:
			portraitID = R.drawable.por033;
			break;
		}
		return portraitID;
	}

}
