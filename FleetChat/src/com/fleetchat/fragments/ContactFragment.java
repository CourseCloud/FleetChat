package com.fleetchat.fragments;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.ClipData.Item;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.fleetchat.ChatActivity;
import com.fleetchat.R;
import com.fleetchat.tools.AlertDialogManager;
import com.fleetchat.tools.FileIO;
import com.fleetchat.util.GCMConstants;

public class ContactFragment extends Fragment implements GCMConstants {
	private static final String TAG = "ContactFragment";
	private static final String IMAGE = "IMAGE";
	View view;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.contact_fragment, container, false);
		setListView();
		return view;
	}

	private ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
	private ListView listview;
	private SimpleAdapter simpleAdapter;
	private FileIO fio;

	private void setListView() {

		listview = (ListView) view
				.findViewById(R.id.contact_fragment_listView1);

		fio = new FileIO(getActivity());
		list = fio.getContact();
		Log.d(TAG, "list = " + list);

		listview.setOnItemClickListener(new ListView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				// Create Chat File, if it is not existed.
				File f = new File(fio.getChatDir(), (String) list.get(position)
						.get(EXTRA_GCMID));
				if (!f.exists()) {
					fio.writeFileToChat(
							(String) list.get(position).get(EXTRA_GCMID), "");
				}

				// Change to Chat Activity.
				Intent intent = new Intent(getActivity(), ChatActivity.class);
				Bundle b = new Bundle();
				b.putString(EXTRA_GCMID,
						(String) list.get(position).get(EXTRA_GCMID));
				intent.putExtras(b);
				startActivity(intent);
			}
		});

		// TODO "DEBUG" use
		listview.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				new AlertDialogManager()
						.showMessageDialog(getActivity(), "position = "
								+ position, list.get(position).toString());
				return false;
			}
		});

		if (list != null) {
			// TODO [Check] Is it need to fix?
			Log.d("getPOR", fio.getContactPorID(EXTRA_GCMID));
			simpleAdapter = new SimpleAdapter(getActivity(), list,
					R.layout.simple_list_item_3, new String[] { "",
							EXTRA_NAME,EXTRA_DATE }, new int[] {
							R.id.simple_list_item_3_imageView1,
							R.id.simple_list_item_3_textView1,
							R.id.simple_list_item_3_textView2 });
			listview.setAdapter(simpleAdapter);
		}
		
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
