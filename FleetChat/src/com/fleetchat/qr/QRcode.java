package com.fleetchat.qr;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Service;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;
import android.widget.Toast;

import com.fleetchat.MainActivity;
import com.fleetchat.R;
import com.fleetchat.tools.FileIO;
import com.fleetchat.util.GCMConstants;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;

public class QRcode extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_qrcode);
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.container, new QRcodeFragment()).commit();
		}
	}

	public static class QRcodeFragment extends Fragment {
		// tab UI
		private ImageView ivQRgen, ivQRscan;
		// UIs
		private RadioGroup rg;
		private RadioButton rb1, rb2;
		private CheckBox ch1;
		private DatePicker dp1, dp2;
		private TimePicker tp;
		private Button btn_generate;
		private View rootView;

		// dialog params
		public static Dialog qrDialog;
		public static TextView tv1, tv2;
		private Button btn1;
		private static ImageView iv1;
		private static int height, width;
		private String expireDate;
		private String durDate;
		private String verif = "Verification";
		private View view;

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

		// reg_id of the local device
		private String reg_id;
		// TODO From user's registration
		private String USER_NAME = "Username";

		public QRcodeFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			rootView = inflater.inflate(R.layout.qrcode_fragment, container,
					false);
			init();
			initDialog();
			getWindowSize();
			return rootView;
		}

		private void init() {
			reg_id = MainActivity.GCM.getRegistrationId();
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
					.findViewById(R.id.qrcode_fragment_radioGroup1);
			rb1 = (RadioButton) rootView
					.findViewById(R.id.qrcode_fragment_radio0);
			rb2 = (RadioButton) rootView
					.findViewById(R.id.qrcode_fragment_radio1);
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
					.findViewById(R.id.qrcode_fragment_datePicker1);
			dp1.setVisibility(View.GONE);
			dp2 = (DatePicker) rootView
					.findViewById(R.id.qrcode_fragment_datePicker2);
			dp2.setVisibility(View.GONE);
			setDatePicker1(dp1);
			setDatePicker2(dp2);
			tp = (TimePicker) rootView
					.findViewById(R.id.qrcode_fragment_timePicker1);
			tp.setVisibility(View.GONE);
			tp.setOnTimeChangedListener(new OnTimeChangedListener() {

				@Override
				public void onTimeChanged(TimePicker view, int hourOfDay,
						int minute) {
					QRcodeFragment.this.hour = hourOfDay;
					QRcodeFragment.this.minute = minute;
					chooseTime2 = "" + " " + hour + ":" + minute;
				}
			});
			btn_generate = (Button) rootView
					.findViewById(R.id.qrcode_fragment_button1);
			btn_generate.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					strToGen = "FleetChat" + "duration:" + chooseDate1
							+ "expiration:" + chooseDate2 + "-" + chooseTime2
							+ "" + "regID:" + reg_id + "UserName:" + USER_NAME;

					int smallerDimension = width < height ? width : height;
					smallerDimension = smallerDimension * 1 / 2;

					// Encode with a QR Code image
					QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(strToGen,
							null, Contents.Type.TEXT, BarcodeFormat.QR_CODE
									.toString(), smallerDimension);
					try {
						Bitmap bitmap = qrCodeEncoder.encodeAsBitmap();
						iv1.setImageBitmap(bitmap);

					} catch (WriterException e) {
						e.printStackTrace();
					}
					tv1.setText(chooseDate1);
					tv2.setText(chooseDate2 + "  " + chooseTime2);
					qrDialog.show();
				}
			});
		}

		private void setDatePicker1(DatePicker dp) {
			initCalendar();
			dp.init(year, month, day, new OnDateChangedListener() {

				@Override
				public void onDateChanged(DatePicker dp, int year, int month,
						int day) {
					QRcodeFragment.this.year = year;
					QRcodeFragment.this.month = month;
					QRcodeFragment.this.day = day;
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
					QRcodeFragment.this.year = year;
					QRcodeFragment.this.month = month;
					QRcodeFragment.this.day = day;
					chooseDate2 = "" + " " + year + "/" + (month + 1) + "/"
							+ day;
				}
			});
		}

		private void initDialog() {
			LayoutInflater li = LayoutInflater.from(getActivity());
			View view = li.inflate(R.layout.qrcode_fragment_dialog1, null);
			qrDialog = new Dialog(getActivity());
			qrDialog.setContentView(view);
			qrDialog.setTitle(verif);
			tv1 = (TextView) view.findViewById(R.id.qrcode_dialog_textView1);
			tv2 = (TextView) view.findViewById(R.id.qrcode_dialog_textView2);
			btn1 = (Button) view.findViewById(R.id.qrcode_dialog_button1);
			btn1.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					qrDialog.dismiss();
				}
			});
			iv1 = (ImageView) view.findViewById(R.id.qrcode_dialog_imageView1);
		}

		private void initCalendar() {
			cal = Calendar.getInstance();
			year = cal.get(Calendar.YEAR);
			month = cal.get(Calendar.MONTH);
			day = cal.get(Calendar.DAY_OF_MONTH);
			hour = cal.get(Calendar.HOUR);
			minute = cal.get(Calendar.MINUTE);
		}

		private void getWindowSize() {
			WindowManager manager = (WindowManager) getActivity()
					.getSystemService(Service.WINDOW_SERVICE);

			Display display = manager.getDefaultDisplay();
			width = display.getWidth();
			height = display.getHeight();
		}
	}

	public static class QRcodeFragment2 extends Fragment implements
			GCMConstants {
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

		public QRcodeFragment2() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			rootView = inflater.inflate(R.layout.qrcode_fragment2, container,
					false);
			init();
			return rootView;
		}

		private TextView tvMessage;
		private Button btScan;

		private void init() {
			reg_id = MainActivity.GCM.getRegistrationId();
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

		public void onActivityResult(int requestCode, int resultCode,
				Intent intent) {
			if (requestCode == 0) {
				String message = "";
				if (resultCode == RESULT_OK) {
					String contents = intent.getStringExtra("SCAN_RESULT");
					String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
					if (format.equalsIgnoreCase("QR_CODE")
							&& contents.split("duration:")[0]
									.equalsIgnoreCase("FleetChat")) {
						HashMap<String, Object> item = new HashMap<String, Object>();
						ArrayList<String> regIds = new ArrayList<String>();
						String name = contents.split("regID:")[1]
								.split("UserName:")[1];
						String gcmidFromOther = contents.split("regID:")[1]
								.split("UserName:")[0];
						String during = contents.split("duration:")[1]
								.split("expiration")[0];
						regIds.add(gcmidFromOther);
						item.put(EXTRA_NAME, name);
						item.put(EXTRA_GCMID, gcmidFromOther);
						item.put(EXTRA_DATE, during);
						
						fio = new FileIO(getActivity());
						if (fio.addContact(item)) {
							fio.addContact(item);
							MainActivity.GCM.postDataAddFriend(regIds, during,
									reg_id);
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
				} else if (resultCode == RESULT_CANCELED) {
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
						public void onClick(DialogInterface dialogInterface,
								int i) {
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
						public void onClick(DialogInterface dialogInterface,
								int i) {
							dialogInterface.cancel();
						}
					});
			downloadDialog.show();
		}

	}
}
