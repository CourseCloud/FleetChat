package com.fleetchat.qr;

import java.util.Calendar;

import android.app.Dialog;
import android.app.Service;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
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

import com.fleetchat.MainActivity;
import com.fleetchat.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;

//TODO (Ho) Need to fix it. 拿掉limited好友限制( 自動刪除好友功能)，將選項改成 產生永久QR Code & 產生有時效性QR Code就好
public class QRGeneratorFragment extends Fragment {
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

	private String reg_id;
	// TODO From user's registration
	private String USER_NAME = "Username";

	public QRGeneratorFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.qrcode_fragment, container, false);
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
		ch1 = (CheckBox) rootView.findViewById(R.id.qrcode_fragment_checkBox1);
		ch1.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				chooseDate2 = "" + " " + year + "/" + (month + 1) + "/" + day;
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
		rb1 = (RadioButton) rootView.findViewById(R.id.qrcode_fragment_radio0);
		rb2 = (RadioButton) rootView.findViewById(R.id.qrcode_fragment_radio1);
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
			public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
				QRGeneratorFragment.this.hour = hourOfDay;
				QRGeneratorFragment.this.minute = minute;
				chooseTime2 = "" + " " + hour + ":" + minute;
			}
		});
		btn_generate = (Button) rootView
				.findViewById(R.id.qrcode_fragment_button1);
		btn_generate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				strToGen = "FleetChat" + "duration:" + chooseDate1
						+ "expiration:" + chooseDate2 + "-" + chooseTime2 + ""
						+ "regID:" + reg_id + "UserName:" + USER_NAME;

				int smallerDimension = width < height ? width : height;
				smallerDimension = smallerDimension * 1 / 2;

				// Encode with a QR Code image
				QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(strToGen, null,
						QRContents.Type.TEXT, BarcodeFormat.QR_CODE.toString(),
						smallerDimension);
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
				QRGeneratorFragment.this.year = year;
				QRGeneratorFragment.this.month = month;
				QRGeneratorFragment.this.day = day;
				chooseDate1 = "" + " " + year + "/" + (month + 1) + "/" + day;
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
				QRGeneratorFragment.this.year = year;
				QRGeneratorFragment.this.month = month;
				QRGeneratorFragment.this.day = day;
				chooseDate2 = "" + " " + year + "/" + (month + 1) + "/" + day;
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
		WindowManager manager = (WindowManager) getActivity().getSystemService(
				Service.WINDOW_SERVICE);

		Display display = manager.getDefaultDisplay();
		width = display.getWidth();
		height = display.getHeight();
	}
}
