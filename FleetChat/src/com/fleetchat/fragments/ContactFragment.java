package com.fleetchat.fragments;

import java.io.File;
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
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.fleetchat.ChatActivity;
import com.fleetchat.R;
import com.fleetchat.tools.AlertDialogManager;
import com.fleetchat.tools.FileIO;
import com.fleetchat.util.GCMConstants;

public class ContactFragment extends Fragment implements GCMConstants {
	private static final String TAG = "ContactFragment";
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
			simpleAdapter = new SimpleAdapter(getActivity(), list,
					R.layout.simple_list_item_3, new String[] { "pic1",
							EXTRA_NAME, EXTRA_DATE }, new int[] {
							R.id.simple_list_item_3_imageView1,
							R.id.simple_list_item_3_textView1,
							R.id.simple_list_item_3_textView2 });
			listview.setAdapter(simpleAdapter);
		}
	}

}
