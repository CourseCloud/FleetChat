package com.fleetchat.fragments;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;

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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
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
		// TODO Auto-generated method stub
		mNfcAdapter = NfcAdapter.getDefaultAdapter(getActivity());

		// Handle all of our received NFC intents in this activity.
		mNfcPendingIntent = PendingIntent.getActivity(getActivity(), 0,
				new Intent(getActivity(), getClass())
						.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

		// Reading tag
		IntentFilter ndefDetected = new IntentFilter(
				NfcAdapter.ACTION_NDEF_DISCOVERED);
		try {
			ndefDetected.addDataType("text/plain");
		} catch (MalformedMimeTypeException e) {
		}
		mNdefExchangeFilters = new IntentFilter[] { ndefDetected };

		// Writing tag
		IntentFilter tagDetected = new IntentFilter(
				NfcAdapter.ACTION_TAG_DISCOVERED);
		mWriteTagFilters = new IntentFilter[] { tagDetected };
	}

	private void init() {
		reg_id = MainActivity.GCM.getRegistrationId();
		initCalendar();
		chooseDate1 = "Permenant Permission";
		rg = (RadioGroup) rootView
				.findViewById(R.id.nfcpush_fragment_radioGroup1);
		rb1 = (RadioButton) rootView.findViewById(R.id.nfcpush_fragment_radio0);
		rb2 = (RadioButton) rootView.findViewById(R.id.nfcpush_fragment_radio1);
		rg.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				chooseDate1 = "" + " " + year + "/" + (month + 1) + "/" + day;
				if (rb1.isChecked()) {
					chooseDate1 = "permenant permission";

					dp1.setVisibility(View.GONE);
				} else {
					dp1.setVisibility(View.VISIBLE);
				}
			}
		});
		dp1 = (DatePicker) rootView
				.findViewById(R.id.nfcpush_fragment_datePicker1);
		dp1.setVisibility(View.GONE);
		setDatePicker1(dp1);
		btnStart = (Button) rootView
				.findViewById(R.id.nfcpush_fragment_button1);
		btnStart.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				strToGen = "FleetChat" + "duration:" + chooseDate1
						+ "expiration:" + "" + "regID:" + reg_id + "UserName:"
						+ USER_NAME;
				enableNdefExchangeMode();
				disableTagWriteMode();

				new AlertDialog.Builder(getActivity())
						.setTitle("Waiting...")
						.setMessage("Wait for exchange device")
						.setPositiveButton("exchange finished",
								new DialogInterface.OnClickListener() {

									public void onClick(DialogInterface dialog,
											int id) {
										// TODO Auto-generated method stub
										if (id == DialogInterface.BUTTON_POSITIVE) {
											disableNdefExchangeMode();
											enableTagWriteMode();
										}
									}
								}).show();
			}
		});
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

	private void promptForContent(final NdefMessage msg) {
		strFromDevice = new String(msg.getRecords()[0].getPayload());
		Log.i("strToSend", strFromDevice);
		if (strFromDevice.split("duration:")[0].equalsIgnoreCase("FleetChat")) {
			HashMap<String, Object> item = new HashMap<String, Object>();
			item.put(EXTRA_NAME,
					strFromDevice.split("regID:")[1].split("UserName:")[1]);
			item.put(EXTRA_GCMID,
					strFromDevice.split("regID:")[1].split("UserName:")[0]);
			item.put(EXTRA_DATE,
					strFromDevice.split("duration:")[1].split("expiration")[0]);
			fio = new FileIO(getActivity());
			if (fio.addContact(item)) {
				Toast t = Toast.makeText(getActivity(),
						"Friend has been added successfully",
						Toast.LENGTH_SHORT);
				t.show();
			} else {
				Toast t = Toast.makeText(getActivity(),
						"You two are friends already!", Toast.LENGTH_SHORT);
				t.show();
			}

		} else {

			strFromDevice = null;
			AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
			dialog.setTitle("Warning").setMessage("No string is detected!")
					.setPositiveButton("OK", null).show();
		}
	}

	private NdefMessage getNoteAsNdef() {
		byte[] textBytes = strToGen.getBytes();
		NdefRecord textRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA,
				"text/plain".getBytes(), new byte[] {}, textBytes);
		return new NdefMessage(new NdefRecord[] { textRecord });
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mResumed = true;
		if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getActivity().getIntent()
				.getAction())) {
			NdefMessage[] messages = getNdefMessages(getActivity().getIntent());
			byte[] payload = messages[0].getRecords()[0].getPayload();
			getActivity().setIntent(new Intent()); // Consume this intent.
		}
		enableNdefExchangeMode();

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
			Log.d("TAG", "Unknown intent.");
		}
		return msgs;
	}

	@SuppressWarnings("deprecation")
	private void enableNdefExchangeMode() {
		mNfcAdapter.enableForegroundNdefPush(getActivity(), getNoteAsNdef());
		mNfcAdapter.enableForegroundDispatch(getActivity(), mNfcPendingIntent,
				mNdefExchangeFilters, null);
	}

	@SuppressWarnings("deprecation")
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

	private void setDatePicker1(DatePicker dp) {
		initCalendar();
		dp.init(year, month, day, new OnDateChangedListener() {

			@Override
			public void onDateChanged(DatePicker dp, int year, int month,
					int day) {
				NFCTabFragment.this.year = year;
				NFCTabFragment.this.month = month;
				NFCTabFragment.this.day = day;
				chooseDate1 = "" + " " + year + "/" + (month + 1) + "/" + day;
				Log.i("Date1", chooseDate1);
			}
		});
	}

	private void initCalendar() {
		cal = Calendar.getInstance();
		year = cal.get(Calendar.YEAR);
		month = cal.get(Calendar.MONTH);
		day = cal.get(Calendar.DAY_OF_MONTH);
		hour = cal.get(Calendar.HOUR);
		minute = cal.get(Calendar.MINUTE);
	}

	private void toast(String text) {
		Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
	}

}
