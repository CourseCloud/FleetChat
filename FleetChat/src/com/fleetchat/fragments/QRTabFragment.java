package com.fleetchat.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.fleetchat.R;
import com.fleetchat.qr.QRGeneratorFragment;
import com.fleetchat.qr.QRScannerFragment;

public class QRTabFragment extends Fragment {
	View view;
	ImageView iv1, iv2;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.qr_tab_fragment, container, false);

		iv1 = (ImageView) view.findViewById(R.id.qr_tab_fragment_iv1);
		iv2 = (ImageView) view.findViewById(R.id.qr_tab_fragment_iv2);

		iv1.setOnClickListener(mOnClickListener);
		iv2.setOnClickListener(mOnClickListener);

		return view;
	}

	View.OnClickListener mOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {

			switch (v.getId()) {
			case R.id.qr_tab_fragment_iv1:
				changeFragment(new QRGeneratorFragment());

				break;
			case R.id.qr_tab_fragment_iv2:
				changeFragment(new QRScannerFragment());

				break;

			default:
				break;
			}
		}
	};

	private void changeFragment(Fragment fragment) {
		FragmentTransaction transaction = getFragmentManager()
				.beginTransaction();

		// Replace whatever is in the fragment_container view with
		// this fragment,
		// and add the transaction to the back stack
		transaction.replace(R.id.qr_tab_fragment_frameLayout, fragment);

		// Commit the transaction
		transaction.commit();
	}

}
