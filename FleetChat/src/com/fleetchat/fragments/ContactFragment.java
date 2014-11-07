package com.fleetchat.fragments;

import java.util.ArrayList;
import java.util.HashMap;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.fleetchat.R;

public class ContactFragment extends Fragment {
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

	private void setListView() {

		listview = (ListView) view
				.findViewById(R.id.contact_fragment_listView1);
		int temp = 10;
		// SET lst3
		for (int i = 0; i < temp; i++) {
			HashMap<String, Object> item = new HashMap<String, Object>();
			item.put("pic1", R.drawable.ic_launcher);
			item.put("item1", "test");
			item.put("item2", "test");
			list.add(item);
		}

		listview.setOnItemClickListener(new ListView.OnItemClickListener() {
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
				R.layout.simple_list_item_3, new String[] { "pic1", "item1",
						"item2" }, // key的名字
				new int[] { R.id.simple_list_item_3_imageView1,
						R.id.simple_list_item_3_textView1,
						R.id.simple_list_item_3_textView2 });
		listview.setAdapter(simpleAdapter);
	}

}
