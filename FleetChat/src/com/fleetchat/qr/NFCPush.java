package com.fleetchat.qr;

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
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;
import android.widget.Toast;

import com.fleetchat.R;

public class NFCPush extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// This would be added to mainActivity, so I didn't change the layout
		// name
		setContentView(R.layout.activity_qrcode);
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.container, new NFCFragment()).commit();
		}
	}

	public static class NFCFragment extends Fragment {

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
		private CheckBox ch1;
		private DatePicker dp1, dp2;
		private TimePicker tp;
		// Strings to deliver
		private String chooseDate1, chooseDate2, chooseTime2;
		private String strToGen;
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

		// User's reg_id
		// TODO from other Activity
		private String reg_id;

		public NFCFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			rootView = inflater
					.inflate(R.layout.nfc_fragment, container, false);
			init();
			initNFC();
			return rootView;
		}

		private void initNFC() {
			// TODO Auto-generated method stub
			mNfcAdapter = NfcAdapter.getDefaultAdapter(getActivity());

			mNote.addTextChangedListener(mTextWatcher);

			// Handle all of our received NFC intents in this activity.
			mNfcPendingIntent = PendingIntent.getActivity(getActivity(), 0,
					new Intent(getActivity(), getClass())
							.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

			// Intent filters for reading a note from a tag or exchanging over
			// p2p.
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
			initCalendar();
			chooseDate1 = "Permenant Permission";
			chooseDate2 = "No Limitation";
			chooseTime2 = "";
			ch1 = (CheckBox) rootView
					.findViewById(R.id.qrcode_fragment_checkBox1);
			ch1.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					chooseDate2 = "" + " " + year + "/" + (month + 1) + "/"
							+ day;
					chooseTime2 = "" + " " + hour + ":" + minute;
					if (ch1.isChecked()) {
						dp2.setVisibility(View.VISIBLE);
						tp.setVisibility(View.VISIBLE);
					} else {
						chooseDate2 = "No limitaion";
						chooseTime2 = "";
						dp2.setVisibility(View.GONE);
						tp.setVisibility(View.GONE);
					}
				}
			});
			rg = (RadioGroup) rootView
					.findViewById(R.id.nfcpush_fragment_radioGroup1);
			rb1 = (RadioButton) rootView
					.findViewById(R.id.nfcpush_fragment_radio0);
			rb2 = (RadioButton) rootView
					.findViewById(R.id.nfcpush_fragment_radio1);
			rg.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(RadioGroup group, int checkedId) {
					chooseDate1 = "" + " " + year + "/" + (month + 1) + "/"
							+ day;
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
			dp2 = (DatePicker) rootView
					.findViewById(R.id.nfcpush_fragment_datePicker2);
			dp2.setVisibility(View.GONE);
			setDatePicker1(dp1);
			setDatePicker2(dp2);
			tp = (TimePicker) rootView
					.findViewById(R.id.nfcpush_fragment_timePicker1);
			tp.setVisibility(View.GONE);
			tp.setOnTimeChangedListener(new OnTimeChangedListener() {

				@Override
				public void onTimeChanged(TimePicker view, int hourOfDay,
						int minute) {
					NFCFragment.this.hour = hourOfDay;
					NFCFragment.this.minute = minute;
					chooseTime2 = "" + " " + hour + ":" + minute;
				}
			});
			btnStart = (Button) rootView
					.findViewById(R.id.nfcpush_fragment_button1);
			btnStart.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					disableNdefExchangeMode();
					enableTagWriteMode();

					new AlertDialog.Builder(getActivity())
							.setTitle("Touch tag to write")
							.setOnCancelListener(
									new DialogInterface.OnCancelListener() {
										@Override
										public void onCancel(
												DialogInterface dialog) {
											disableTagWriteMode();
											enableNdefExchangeMode();
										}
									}).create().show();
				}
			});
		}

		protected void onNewIntent(Intent intent) {
			// NDEF exchange mode
			if (!mWriteMode
					&& NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent
							.getAction())) {
				NdefMessage[] msgs = getNdefMessages(intent);
				promptForContent(msgs[0]);
			}

			// Tag writing mode
			if (mWriteMode
					&& NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent
							.getAction())) {
				Tag detectedTag = intent
						.getParcelableExtra(NfcAdapter.EXTRA_TAG);
				writeTag(getNoteAsNdef(), detectedTag);
			}
		}

		private TextWatcher mTextWatcher = new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {

			}

			@SuppressWarnings("deprecation")
			@Override
			public void afterTextChanged(Editable arg0) {
				if (mResumed) {
					mNfcAdapter.enableForegroundNdefPush(getActivity(),
							getNoteAsNdef());
				}
			}
		};

		private void promptForContent(final NdefMessage msg) {
			new AlertDialog.Builder(getActivity())
					.setTitle("Replace current content?")
					.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {
									String body = new String(
											msg.getRecords()[0].getPayload());
									setNoteBody(body);
								}
							})
					.setNegativeButton("No",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {

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

		@Override
		public void onResume() {
			// TODO Auto-generated method stub
			super.onResume();
			if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getActivity()
					.getIntent().getAction())) {
				NdefMessage[] messages = getNdefMessages(getActivity()
						.getIntent());
				byte[] payload = messages[0].getRecords()[0].getPayload();
				setNoteBody(new String(payload));
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
					NdefMessage msg = new NdefMessage(
							new NdefRecord[] { record });
					msgs = new NdefMessage[] { msg };
				}
			} else {
				Log.d("TAG", "Unknown intent.");
			}
			return msgs;
		}

		@SuppressWarnings("deprecation")
		private void enableNdefExchangeMode() {
			mNfcAdapter
					.enableForegroundNdefPush(getActivity(), getNoteAsNdef());
			mNfcAdapter.enableForegroundDispatch(getActivity(),
					mNfcPendingIntent, mNdefExchangeFilters, null);
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
			mNfcAdapter.enableForegroundDispatch(getActivity(),
					mNfcPendingIntent, mWriteTagFilters, null);
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
					NFCFragment.this.year = year;
					NFCFragment.this.month = month;
					NFCFragment.this.day = day;
					chooseDate1 = "" + " " + year + "/" + (month + 1) + "/"
							+ day;
					Log.i("Date1", chooseDate1);
				}
			});

		}

		private void setDatePicker2(DatePicker dp) {
			initCalendar();
			dp.init(year, month, day, new OnDateChangedListener() {

				@Override
				public void onDateChanged(DatePicker dp, int year, int month,
						int day) {
					NFCFragment.this.year = year;
					NFCFragment.this.month = month;
					NFCFragment.this.day = day;
					chooseDate2 = "" + " " + year + "/" + (month + 1) + "/"
							+ day;
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
}
