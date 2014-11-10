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

import com.fleetchat.tools.FileIO;
import com.fleetchat.tools.StringUtilities;
import com.fleetchat.util.FileIOConstants;
import com.fleetchat.util.GCMConstants;

public class ChatActivity extends Activity implements GCMConstants,
		FileIOConstants {
	// UI
	private EditText _etContent;
	private EditText _etMessage;
	private Button _btnSend;

	// Class
	private FileIO fio;
	private String _contact = "temp";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		fio = new FileIO(getApplicationContext());

		setContentView(R.layout.chat_activity);
		Initialize();
		setView();

	}

	private void Initialize() {
		// Initialize ActionBar
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowHomeEnabled(false);

		// Get contact's GCM id.
		_contact = getIntent().getStringExtra(EXTRA_GCMID);

		// actionBar.setTitle()
		ArrayList<HashMap<String, Object>> list = fio.getContact();
		for (HashMap<String, Object> item : list) {
			if (item.get(EXTRA_GCMID).equals(_contact)) {
				actionBar.setTitle((CharSequence) item.get(EXTRA_NAME));
				break;
			}
		}

		// Create File
		fio.addChatDetail(_contact, new HashMap<String, Object>());

	}

	private void setView() {
		_etContent = (EditText) findViewById(R.id.chat_activity_editText_content);
		_etMessage = (EditText) findViewById(R.id.chat_activity_editText_message);
		_btnSend = (Button) findViewById(R.id.chat_activity_button_send);

		// If _etMessage is empty, set _btnSend enabled.
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

				_etMessage.setText("");

				setContent();
			}
		});

		setContent();

	}

	ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();

	private void setContent() {

		list = fio.getChatDetail(_contact);
		Log.d("DEBUG", "fio.getChatDetail(_contact) = " + list);

		String s = "";
		// Read MESSAGE only
		if (list != null) {
			for (int i = 0; i < list.size(); i++) {
				s = s + list.get(i).get(MESSAGE) + "\n";
			}
		}

		_etContent.setText(s);
	}

}
