package com.fleetchat.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

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

		fio = new FileIO(getActivity(), "test");
		return _view;
	}

	private void setView() {
		mDisplay = (TextView) _view.findViewById(R.id.display);
		Button btn = (Button) _view.findViewById(R.id.button1);
		btn.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				fio.write("twsss");
				fio.getChatDir();
				
				try {
					mDisplay.setText((String)fio.getStringFromFile("test"));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
