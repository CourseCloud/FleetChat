package com.fleetchat.fragments;

import java.io.IOException;
import java.util.Calendar;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.fleetchat.MainActivity;
import com.fleetchat.R;
import com.fleetchat.tools.FileIO;
import com.fleetchat.util.GCMConstants;

public class NFCTabFragment extends Fragment implements GCMConstants {
	private View rootView;
	// nfc params
	private Button btnStart;
	private NfcAdapter mNfcAdapter;
	private boolean mResumed = false;
	private boolean mWriteMode = false;
	private ProgressDialog pd;
	private Toast toast;
	private EditText mNote;
	// basic UIs
	private RadioGroup rg;
	private RadioButton rb1, rb2;
	private DatePicker dp1;
	// Strings to deliver
	private String chooseDate1;
	private String strToGen = "";
	// datePicker params
	private Calendar cal;
	private int year;
	private int month;
	private int day;
	private int hour;
	private int minute;

	PendingIntent mNfcPendingIntent;
	IntentFilter[] mWriteTagFilters;
	IntentFilter[] mNdefExchangeFilters;

	private String reg_id;
	// TODO From user's registration
	private String USER_NAME = "Username";
	private String strFromDevice;
	private FileIO fio;

	public NFCTabFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.nfc_fragment, container, false);
		init();
		initNFC();
		return rootView;
	}

	private void initNFC() {
		mNfcAdapter = NfcAdapter.getDefaultAdapter(getActivity());

		// findViewById(R.id.write_tag).setOnClickListener(mTagWriter);
		// mNote = ((EditText) findViewById(R.id.note));
		mNote.addTextChangedListener(mTextWatcher);

		// Handle all of our received NFC intents in this activity.
		mNfcPendingIntent = PendingIntent.getActivity(getActivity(), 0,
				new Intent(getActivity(), getClass())
						.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

		// Intent filters for reading a note from a tag or exchanging over p2p.
		IntentFilter ndefDetected = new IntentFilter(
				NfcAdapter.ACTION_NDEF_DISCOVERED);
		try {
			ndefDetected.addDataType("text/plain");
		} catch (MalformedMimeTypeException e) {
		}
		mNdefExchangeFilters = new IntentFilter[] { ndefDetected };

		// Intent filters for writing to a tag
		IntentFilter tagDetected = new IntentFilter(
				NfcAdapter.ACTION_TAG_DISCOVERED);
		mWriteTagFilters = new IntentFilter[] { tagDetected };
	}

	private void init() {
		reg_id = MainActivity.GCM.getRegistrationId();
		btnStart = (Button) rootView
				.findViewById(R.id.nfcpush_fragment_button1);
		btnStart.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				enableNdefExchangeMode();
				disableTagWriteMode();
				AlertDialog.Builder dialog = new AlertDialog.Builder(
						getActivity());
				dialog.setTitle("Waiting...")
						.setMessage("Waiting for the other device")
						.setPositiveButton("Finish",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int id) {
										// TODO Auto-generated method stub
										if (id == DialogInterface.BUTTON_POSITIVE) {
											dialog.dismiss();
											disableNdefExchangeMode();
											enableTagWriteMode();
										}
									}
								});
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();
		mResumed = true;
		// Sticky notes received from Android
		if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getActivity().getIntent()
				.getAction())) {
			NdefMessage[] messages = getNdefMessages(getActivity().getIntent());
			byte[] payload = messages[0].getRecords()[0].getPayload();
			setNoteBody(new String(payload));
			getActivity().setIntent(new Intent()); // Consume this intent.
		}
		enableNdefExchangeMode();
	}

	@Override
	public void onPause() {
		super.onPause();
		mResumed = false;
		mNfcAdapter.disableForegroundNdefPush(getActivity());
	}

	protected void onNewIntent(Intent intent) {
		// NDEF exchange mode
		if (!mWriteMode
				&& NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
			NdefMessage[] msgs = getNdefMessages(intent);
			promptForContent(msgs[0]);
		}

		// Tag writing mode
		if (mWriteMode
				&& NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
			Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
			writeTag(getNoteAsNdef(), detectedTag);
		}
	}

	private TextWatcher mTextWatcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {

		}

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {

		}

		@Override
		public void afterTextChanged(Editable arg0) {
			if (mResumed) {
				mNfcAdapter.enableForegroundNdefPush(getActivity(),
						getNoteAsNdef());
			}
		}
	};

	private View.OnClickListener mTagWriter = new View.OnClickListener() {
		@Override
		public void onClick(View arg0) {
			// Write to a tag for as long as the dialog is shown.
			disableNdefExchangeMode();
			enableTagWriteMode();

			new AlertDialog.Builder(getActivity())
					.setTitle("Touch tag to write")
					.setOnCancelListener(
							new DialogInterface.OnCancelListener() {
								@Override
								public void onCancel(DialogInterface dialog) {
									disableTagWriteMode();
									enableNdefExchangeMode();
								}
							}).create().show();
		}
	};

	private void promptForContent(final NdefMessage msg) {
		new AlertDialog.Builder(getActivity())
				.setTitle("Replace current content?")
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								String body = new String(msg.getRecords()[0]
										.getPayload());
								setNoteBody(body);
							}
						})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {

					}
				}).show();
	}

	private void setNoteBody(String body) {
		Editable text = mNote.getText();
		text.clear();
		text.append(body);
	}

	private NdefMessage getNoteAsNdef() {
		byte[] textBytes = mNote.getText().toString().getBytes();
		NdefRecord textRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA,
				"text/plain".getBytes(), new byte[] {}, textBytes);
		return new NdefMessage(new NdefRecord[] { textRecord });
	}

	NdefMessage[] getNdefMessages(Intent intent) {
		// Parse the intent
		NdefMessage[] msgs = null;
		String action = intent.getAction();
		if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
				|| NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
			Parcelable[] rawMsgs = intent
					.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
			if (rawMsgs != null) {
				msgs = new NdefMessage[rawMsgs.length];
				for (int i = 0; i < rawMsgs.length; i++) {
					msgs[i] = (NdefMessage) rawMsgs[i];
				}
			} else {
				// Unknown tag type
				byte[] empty = new byte[] {};
				NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN,
						empty, empty, empty);
				NdefMessage msg = new NdefMessage(new NdefRecord[] { record });
				msgs = new NdefMessage[] { msg };
			}
		} else {
			getActivity().finish();
		}
		return msgs;
	}

	private void enableNdefExchangeMode() {
		mNfcAdapter.enableForegroundNdefPush(getActivity(), getNoteAsNdef());
		mNfcAdapter.enableForegroundDispatch(getActivity(), mNfcPendingIntent,
				mNdefExchangeFilters, null);
	}

	private void disableNdefExchangeMode() {
		mNfcAdapter.disableForegroundNdefPush(getActivity());
		mNfcAdapter.disableForegroundDispatch(getActivity());
	}

	private void enableTagWriteMode() {
		mWriteMode = true;
		IntentFilter tagDetected = new IntentFilter(
				NfcAdapter.ACTION_TAG_DISCOVERED);
		mWriteTagFilters = new IntentFilter[] { tagDetected };
		mNfcAdapter.enableForegroundDispatch(getActivity(), mNfcPendingIntent,
				mWriteTagFilters, null);
	}

	private void disableTagWriteMode() {
		mWriteMode = false;
		mNfcAdapter.disableForegroundDispatch(getActivity());
	}

	boolean writeTag(NdefMessage message, Tag tag) {
		int size = message.toByteArray().length;

		try {
			Ndef ndef = Ndef.get(tag);
			if (ndef != null) {
				ndef.connect();

				if (!ndef.isWritable()) {
					toast("Tag is read-only.");
					return false;
				}
				if (ndef.getMaxSize() < size) {
					toast("Tag capacity is " + ndef.getMaxSize()
							+ " bytes, message is " + size + " bytes.");
					return false;
				}

				ndef.writeNdefMessage(message);
				toast("Wrote message to pre-formatted tag.");
				return true;
			} else {
				NdefFormatable format = NdefFormatable.get(tag);
				if (format != null) {
					try {
						format.connect();
						format.format(message);
						toast("Formatted tag and wrote message");
						return true;
					} catch (IOException e) {
						toast("Failed to format tag.");
						return false;
					}
				} else {
					toast("Tag doesn't support NDEF.");
					return false;
				}
			}
		} catch (Exception e) {
			toast("Failed to write tag");
		}

		return false;
	}

	private void toast(String text) {
		Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
	}
}
