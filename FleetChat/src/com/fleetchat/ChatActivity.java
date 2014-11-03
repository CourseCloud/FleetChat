package com.fleetchat;

import com.fleetchat.fragments.ChatListFragment;
import com.fleetchat.tools.FileIO;
import com.fleetchat.tools.StringUtilities;
import com.fleetchat.tools.TimeUtilities;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Shader.TileMode;
import android.os.Bundle;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.text.format.Time;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class ChatActivity extends Activity {
	// UI
	private EditText _etContent;
	private EditText _etMessage;
	private Button _btnSend;

	// Class
	private FileIO fio;
	private String _contact;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		fio = new FileIO(getApplicationContext());

		setContentView(R.layout.chat_activity);
		InitializeActionBar();
		setView();

	}

	// Initialize ActionBar
	private void InitializeActionBar() {
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowHomeEnabled(false);

		_contact = getIntent().getExtras().getString(
				ChatListFragment.CHAT_TITLE, "");
		actionBar.setTitle(_contact);
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
				fio.writeFileToChat(_contact, _etMessage.getText().toString()
						+ "\n");

				_etMessage.setText("");
				_etContent.setText(fio.getChatContent(_contact));
//				_etContent.setSelection(_etContent.getSelectionEnd());
				_etContent.setSelection(_etContent.getSelectionStart());
			}
		});

		try {
			_etContent.setText(fio.getChatContent(_contact));
		} catch (Exception e) {
			Log.d("DEBUG", e.toString());
		}

	}
	
	private void set(){
		
	}

}
