package com.fleetchat.fragments;

import com.fleetchat.MainActivity;
import com.fleetchat.R;
import com.fleetchat.tools.FileIO;
import com.fleetchat.util.GCMConstants;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class SettingsFragment extends Fragment implements GCMConstants {

	private View rootView;

	// settings fragment UI
	private EditText edName;
	private EditText edMail;
	public static ImageView personalPor;
	public ImageView ivNow;
	public String porBuffer;
	private TextView tvSelected;
	// reg_id is the local user id
	private String reg_id;
	// TODO From user's registration
	private String USER_NAME;
	private FileIO fio;

	public SettingsFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.settings_fragment, container,
				false);
		init();
		return rootView;
	}

	private void init() {
		reg_id = MainActivity.GCM.getRegistrationId();
		USER_NAME = MainActivity.getUserName(getActivity());
		edName = (EditText) rootView.findViewById(R.id.settings_fragment_ed1);
		edMail = (EditText) rootView.findViewById(R.id.settings_fragment_ed2);
		personalPor = (ImageView) rootView
				.findViewById(R.id.settings_personal_portrait);
		setSettingPortrait();
		edName.setEnabled(false);
		edMail.setEnabled(false);
		edName.setText(MainActivity.getUserName(getActivity()));
		edMail.setText(MainActivity.getUserEmail(getActivity()));
		personalPor.setOnClickListener(new ImageView.OnClickListener() {
			@Override
			public void onClick(View v) {
				showSelectDialog();
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	private void showSelectDialog() {
		LayoutInflater li = LayoutInflater.from(getActivity());
		View view = li.inflate(R.layout.settings_frament_dialog, null);
		final Dialog dialog = new Dialog(getActivity());
		dialog.setTitle("Select Your Portrait");
		dialog.setContentView(view);
		dialog.show();
		OnClickListener OnClickListener = new OnClickListener();
		tvSelected = (TextView) view.findViewById(R.id.settings_selected);
		tvSelected.setText("");
		Button btnSelect = (Button) view.findViewById(R.id.settings_btn);
		btnSelect.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				SharedPreferences sp = getActivity().getSharedPreferences(
						MainActivity.PREF, Context.MODE_PRIVATE);
				SharedPreferences.Editor se = sp.edit();
				personalPor.setImageDrawable(ivNow.getDrawable());
				Log.i("click", "yes");
				MainActivity.USER_PORTRAIT_ID = porBuffer;
				se.putString(MainActivity.PREF_PORID,
						MainActivity.USER_PORTRAIT_ID);
				se.commit();
				dialog.dismiss();
			}
		});
		ImageView iv11 = (ImageView) view.findViewById(R.id.portrait011);
		ImageView iv12 = (ImageView) view.findViewById(R.id.portrait012);
		ImageView iv13 = (ImageView) view.findViewById(R.id.portrait013);
		ImageView iv14 = (ImageView) view.findViewById(R.id.portrait014);
		ImageView iv21 = (ImageView) view.findViewById(R.id.portrait021);
		ImageView iv22 = (ImageView) view.findViewById(R.id.portrait022);
		ImageView iv23 = (ImageView) view.findViewById(R.id.portrait023);
		ImageView iv24 = (ImageView) view.findViewById(R.id.portrait024);
		ImageView iv31 = (ImageView) view.findViewById(R.id.portrait031);
		ImageView iv32 = (ImageView) view.findViewById(R.id.portrait032);
		ImageView iv33 = (ImageView) view.findViewById(R.id.portrait033);
		ImageView iv34 = (ImageView) view.findViewById(R.id.portrait034);
		ivNow = (ImageView) view.findViewById(R.id.settings_portraitnow);
		iv11.setOnClickListener(OnClickListener);
		iv12.setOnClickListener(OnClickListener);
		iv13.setOnClickListener(OnClickListener);
		iv14.setOnClickListener(OnClickListener);
		iv21.setOnClickListener(OnClickListener);
		iv22.setOnClickListener(OnClickListener);
		iv23.setOnClickListener(OnClickListener);
		iv24.setOnClickListener(OnClickListener);
		iv31.setOnClickListener(OnClickListener);
		iv32.setOnClickListener(OnClickListener);
		iv33.setOnClickListener(OnClickListener);
		iv34.setOnClickListener(OnClickListener);

	}

	private void setSettingPortrait() {
		int userPorID = Integer.valueOf(MainActivity.USER_PORTRAIT_ID);
		switch (userPorID) {
		case 00:
			personalPor.setImageDrawable(getActivity().getResources()
					.getDrawable(R.drawable.porknown));
			break;
		case 11:
			personalPor.setImageDrawable(getActivity().getResources()
					.getDrawable(R.drawable.por011));
			break;
		case 12:
			personalPor.setImageDrawable(getActivity().getResources()
					.getDrawable(R.drawable.por012));
			break;
		case 13:
			personalPor.setImageDrawable(getActivity().getResources()
					.getDrawable(R.drawable.por013));
			break;
		case 14:
			personalPor.setImageDrawable(getActivity().getResources()
					.getDrawable(R.drawable.por014));
			break;
		case 21:
			personalPor.setImageDrawable(getActivity().getResources()
					.getDrawable(R.drawable.por021));
			break;
		case 22:
			personalPor.setImageDrawable(getActivity().getResources()
					.getDrawable(R.drawable.por022));
			break;
		case 23:
			personalPor.setImageDrawable(getActivity().getResources()
					.getDrawable(R.drawable.por023));
			break;
		case 24:
			personalPor.setImageDrawable(getActivity().getResources()
					.getDrawable(R.drawable.por024));
			break;
		case 31:
			personalPor.setImageDrawable(getActivity().getResources()
					.getDrawable(R.drawable.por031));
			break;
		case 32:
			personalPor.setImageDrawable(getActivity().getResources()
					.getDrawable(R.drawable.por032));
			break;
		case 33:
			personalPor.setImageDrawable(getActivity().getResources()
					.getDrawable(R.drawable.por033));
			break;
		case 34:
			personalPor.setImageDrawable(getActivity().getResources()
					.getDrawable(R.drawable.por034));
			break;
		}
	}

	public class OnClickListener implements android.view.View.OnClickListener {

		@Override
		public void onClick(View v) {
			porBuffer = "";
			switch (v.getId()) {
			case R.id.portrait011:
				ivNow.setImageDrawable(getActivity().getResources()
						.getDrawable(R.drawable.por011));
				porBuffer = "11";
				break;
			case R.id.portrait012:
				ivNow.setImageDrawable(getActivity().getResources()
						.getDrawable(R.drawable.por012));
				porBuffer = "12";
				break;
			case R.id.portrait013:
				ivNow.setImageDrawable(getActivity().getResources()
						.getDrawable(R.drawable.por013));
				porBuffer = "13";
				break;
			case R.id.portrait014:
				ivNow.setImageDrawable(getActivity().getResources()
						.getDrawable(R.drawable.por014));
				porBuffer = "14";
				break;
			case R.id.portrait021:
				ivNow.setImageDrawable(getActivity().getResources()
						.getDrawable(R.drawable.por021));
				porBuffer = "21";
				break;
			case R.id.portrait022:
				ivNow.setImageDrawable(getActivity().getResources()
						.getDrawable(R.drawable.por022));
				porBuffer = "22";
				break;
			case R.id.portrait023:
				ivNow.setImageDrawable(getActivity().getResources()
						.getDrawable(R.drawable.por023));
				porBuffer = "23";
				break;
			case R.id.portrait024:
				ivNow.setImageDrawable(getActivity().getResources()
						.getDrawable(R.drawable.por024));
				porBuffer = "24";
				break;
			case R.id.portrait031:
				ivNow.setImageDrawable(getActivity().getResources()
						.getDrawable(R.drawable.por031));
				porBuffer = "31";
				break;
			case R.id.portrait032:
				ivNow.setImageDrawable(getActivity().getResources()
						.getDrawable(R.drawable.por032));
				porBuffer = "32";
				break;
			case R.id.portrait033:
				ivNow.setImageDrawable(getActivity().getResources()
						.getDrawable(R.drawable.por033));
				porBuffer = "33";
				break;
			case R.id.portrait034:
				ivNow.setImageDrawable(getActivity().getResources()
						.getDrawable(R.drawable.por034));
				porBuffer = "34";
				break;
			}
			tvSelected.setText("Selected");
			tvSelected.setTextColor(Color.RED);
		}

	}
}
