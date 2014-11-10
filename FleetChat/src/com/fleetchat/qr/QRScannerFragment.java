package com.fleetchat.qr;

import java.util.Calendar;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.fleetchat.MainActivity;
import com.fleetchat.R;
import com.fleetchat.tools.FileIO;
import com.fleetchat.tools.TimeUtilities;
import com.fleetchat.util.GCMConstants;

// TODO (Ho) Need to fix it.
public class QRScannerFragment extends Fragment implements GCMConstants {
	// download package
	private static final String PACKAGE = "com.google.zxing.client.android";

	// UIs
	private RadioGroup rg;
	private RadioButton rb1, rb2;
	private CheckBox ch1;
	private DatePicker dp1, dp2;
	private TimePicker tp;
	private Button btn_generate;
	private View rootView;

	// datePicker params
	private Calendar cal;
	private int year;
	private int month;
	private int day;
	private int hour;
	private int minute;

	// String used to generate qrcode
	private String strToGen;
	private String chooseDate1, chooseDate2;
	private String chooseTime2;

	// Contact details
	private String reg_id;
	private FileIO fio;

	public QRScannerFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater
				.inflate(R.layout.qrcode_fragment2, container, false);
		init();
		return rootView;
	}

	private TextView tvMessage;
	private Button btScan;

	private void init() {
		tvMessage = (TextView) rootView.findViewById(R.id.tvMessage);
		btScan = (Button) rootView.findViewById(R.id.qrcode_fragment2_Scan);
		btScan.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent intent = new Intent(
						"com.google.zxing.client.android.SCAN");
				intent.setPackage("com.google.zxing.client.android");
				intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
				try {
					startActivityForResult(intent, 0);
				} catch (ActivityNotFoundException ex) {
					showDownloadDialog();
				}
			}

		});
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == 0) {
			String message = "";
			if (resultCode == Activity.RESULT_OK) {
				String contents = intent.getStringExtra("SCAN_RESULT");
				String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
				if (format.equalsIgnoreCase("QR_CODE")
						&& contents.split("duration:")[0]
								.equalsIgnoreCase("FleetChat")) {
					HashMap<String, Object> item = new HashMap<String, Object>();
					item.put(EXTRA_NAME,
							contents.split("regID:")[1].split("UserName:")[1]);
					item.put(EXTRA_GCMID,
							contents.split("regID:")[1].split("UserName:")[0]);
					item.put(
							EXTRA_DATE,
							contents.split("duration:")[1].split("expiration")[0]);

					// TODO (Ho) Need Add GCM function.
					// getRegistrationId change to someone's id.
					// getTimeyyyyMMddhhmmss change to qrDeadlineTime 
					MainActivity.GCM.postDataAddFriend(
							MainActivity.GCM.getRegistrationId(),
							TimeUtilities.getTimeyyyyMMddhhmmss(), "Annoymous");

					fio = new FileIO(getActivity());
					if (fio.addContact(item)) {
						Toast t = Toast.makeText(getActivity(),
								"Friend has been added successfully",
								Toast.LENGTH_SHORT);
						t.show();
						message = "Content: " + contents + "\nFormat: "
								+ format;
					} else {
						Toast t = Toast.makeText(getActivity(),
								"You two are friends already!",
								Toast.LENGTH_SHORT);
						t.show();
					}

				} else {

					message = null;
					AlertDialog.Builder dialog = new AlertDialog.Builder(
							getActivity());
					dialog.setTitle("Warning")
							.setMessage(
									"This format is wrong, please try to scan QRcode!")
							.setPositiveButton("OK", null).show();
				}
			} else if (resultCode == Activity.RESULT_CANCELED) {
				message = "Scan was Cancelled!";
			}
			tvMessage.setText(message);
		}
	}

	private void showDownloadDialog() {
		AlertDialog.Builder downloadDialog = new AlertDialog.Builder(
				getActivity());
		downloadDialog.setTitle("No Barcode Scanner Found");
		downloadDialog
				.setMessage("Please download and install Barcode Scanner!");
		downloadDialog.setPositiveButton("Yes",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialogInterface, int i) {
						Uri uri = Uri.parse("market://search?q=pname:"
								+ PACKAGE);
						Intent intent = new Intent(Intent.ACTION_VIEW, uri);
						try {
							startActivity(intent);
						} catch (ActivityNotFoundException ex) {
							Log.e(ex.toString(),
									"Play Store is not installed; cannot install Barcode Scanner");
						}
					}
				});
		downloadDialog.setNegativeButton("No",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialogInterface, int i) {
						dialogInterface.cancel();
					}
				});
		downloadDialog.show();
	}

}