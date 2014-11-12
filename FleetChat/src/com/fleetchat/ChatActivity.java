package com.fleetchat;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.ActionBar;
import android.app.Activity;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import com.fleetchat.chatbubble.ChatArrayAdapter;
import com.fleetchat.chatbubble.ChatMessage;
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
	private ChatArrayAdapter chatArrayAdapter;
	private String _contactName;
	private boolean side = false;

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
		chatArrayAdapter = new ChatArrayAdapter(getApplicationContext(),
				R.layout.activity_chat_singlemessage);
		_lvContent = (ListView) findViewById(R.id.chat_activity_listView1);
		_lvContent.setAdapter(chatArrayAdapter);
		_lvContent.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
		_etMessage = (EditText) findViewById(R.id.chat_activity_editText_msg);
		_btnSend = (Button) findViewById(R.id.chat_activity_btn_send);

		_btnSend.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				fio.addChatDetail(_contact, _etMessage.getText().toString(),
						true);
				// TODO (Xu) post message
				MainActivity.GCM.postDataSendMessage(_contact, _contactName,
						_etMessage.getText().toString());
				sendMessage(false, _etMessage.getText().toString());
			}
		});
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
		_etMessage.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if ((event.getAction() == KeyEvent.ACTION_DOWN)
						&& (keyCode == KeyEvent.KEYCODE_ENTER)) {
					return sendChatMessage();
				}
				return false;
			}
		});
		chatArrayAdapter.registerDataSetObserver(new DataSetObserver() {
			@Override
			public void onChanged() {
				super.onChanged();
				_lvContent.setSelection(chatArrayAdapter.getCount() - 1);
			}
		});
	}

	private void setContent() {
		ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();

		list = fio.getChatDetail(_contact);
		// TODO "DEBUG"
		Log.d("DEBUG", "fio.getChatDetail(_contact) = " + list);

		String s = "";
		s = _etMessage.getText().toString();
	}

	private boolean sendMessage(Boolean side, String message) {
		chatArrayAdapter.add(new ChatMessage(side, message));
		_etMessage.setText("");
		return true;
	}

	private boolean sendChatMessage() {
		chatArrayAdapter.add(new ChatMessage(side, _etMessage.getText()
				.toString()));
		_etMessage.setText("");
		return true;
	}
}
