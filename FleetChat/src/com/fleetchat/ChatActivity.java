package com.fleetchat;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.fleet.chatbubbleexample.DiscussArrayAdapter;
import com.fleet.chatbubbleexample.OneComment;
import com.fleetchat.tools.FileIO;
import com.fleetchat.util.FileIOConstants;
import com.fleetchat.util.GCMConstants;
import com.fleetchat.util.StringUtilities;

public class ChatActivity extends Activity implements GCMConstants,
		FileIOConstants {
	private static final String TAG = "ChatActivity";
	// UI
	private EditText _etContent;
	private EditText _etMessage;
	private Button _btnSend;
	private ListView _lvContent;

	// Class
	private FileIO fio;
	private String _contact = "temp";

	// bubble params
	private com.fleet.chatbubbleexample.DiscussArrayAdapter bubbleAdapter;
	private String _contactName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		fio = new FileIO(getApplicationContext());

		setContentView(R.layout.chat_activity2);
		Initialize();
		setView();

	}

	private void Initialize() {
		// Initialize ActionBar
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowHomeEnabled(false);

		try {
			// Get contact's GCM id.
			_contact = getIntent().getStringExtra(EXTRA_GCMID);

			// actionBar.setTitle()
			ArrayList<HashMap<String, Object>> list = fio.getContact();
			for (HashMap<String, Object> item : list) {
				if (item.get(EXTRA_GCMID).equals(_contact)) {
					_contactName = (String) item.get(EXTRA_NAME);
					actionBar.setTitle(_contactName);
					break;
				}
			}

			// Create File
			fio.addChatDetail(_contact, new HashMap<String, Object>());

		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}
	}

	private void setView() {
		bubbleAdapter = new DiscussArrayAdapter(getApplicationContext(),
				R.layout.chat_activity_item_me);
		_lvContent = (ListView) findViewById(R.id.chat_activity_listView1);
		_lvContent.setAdapter(bubbleAdapter);
		// _etContent = (EditText)
		// findViewById(R.id.chat_activity_editText_content);
		_etMessage = (EditText) findViewById(R.id.chat_activity_editText_message);
		_btnSend = (Button) findViewById(R.id.chat_activity_button_send);

		// If _etMessage is empty, set _btnSend disabled.
		_etMessage.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				_btnSend.setEnabled(!StringUtilities.isEmpty(_etMessage
						.getText().toString()));
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

		// Button
		_btnSend.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				fio.addChatDetail(_contact, _etMessage.getText().toString(),
						true);
				// TODO (Xu) post message
				MainActivity.GCM.postDataSendMessage(_contact, _contactName,
						_etMessage.getText().toString());
				setContent();
				_etMessage.setText("");

			}
		});

		setContent();

	}

	private void setContent() {
		ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();

		list = fio.getChatDetail(_contact);
		// TODO "DEBUG"
		Log.d("DEBUG", "fio.getChatDetail(_contact) = " + list);

		String s = "";
		s = _etMessage.getText().toString();
		// Read MESSAGE only
		addBubble(s, true);

		// TODO (ho) 幫你寫好了，改好addBubble 應該就可以用了
		// if (list != null) {
		// for (int i = 0; i < list.size(); i++) {
		// addBubble((String) list.get(i).get(MESSAGE), (Boolean) list
		// .get(i).get(WHO_POST));
		// }
		// }

		// refresh
		_lvContent.setAdapter(bubbleAdapter);
	}

	// b stands for that the bubble comes out from "right"(OtherFriend) or
	// "left"(User)
	private void addBubble(String s, boolean b) {
		bubbleAdapter.add(new OneComment(b, s));
	}
}
