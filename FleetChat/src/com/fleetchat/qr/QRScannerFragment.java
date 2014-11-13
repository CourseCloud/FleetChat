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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fleetchat.MainActivity;
import com.fleetchat.R;
import com.fleetchat.tools.AlertDialogManager;
import com.fleetchat.tools.FileIO;
import com.fleetchat.util.GCMConstants;

public class QRScannerFragment extends Fragment implements GCMConstants {
	// download package
	private static final String PACKAGE = "com.google.zxing.client.android";

	// UIs
	private TextView tvMessage;
	private ImageView btScan;
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

	private void init() {
		tvMessage = (TextView) rootView.findViewById(R.id.tvMessage);
		btScan = (ImageView) rootView
				.findViewById(R.id.qrcode_fragment2_Scanner);
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
					String name = contents.split("regID:")[1]
							.split("UserName:")[1];
					String gcmidFromOther = contents.split("regID:")[1]
							.split("UserName:")[0];
					String deadline = contents.split("expiration:")[1]
							.split("regID:")[0];
					item.put(EXTRA_NAME, name);
					item.put(EXTRA_DATE, deadline);
					item.put(EXTRA_GCMID, gcmidFromOther);

					fio = new FileIO(getActivity());
					if (!fio.checkContactExist(gcmidFromOther)) {
						fio.addContact(item);

						String myName = MainActivity.getUserName(getActivity());
						MainActivity.GCM.postDataAddFriend(gcmidFromOther,
								deadline, myName);
						message = "Content: " + contents + "\nFormat: "
								+ format;

						new AlertDialogManager().showMessageDialog(
								getActivity(), "Succes",
								"Friend has been added/updated!");
					} else {

						new AlertDialogManager().showMessageDialog(
								getActivity(), "Fail",
								"You two are friends already!");
					}

				} else {
					message = null;
					new AlertDialogManager().showMessageDialog(getActivity(),
							"Warning",
							"This format is wrong, please try to scan QRcode!");
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