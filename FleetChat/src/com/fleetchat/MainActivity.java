package com.fleetchat;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.fleetchat.fragments.ChatListFragment;
import com.fleetchat.fragments.ContactFragment;
import com.fleetchat.fragments.NFCTabFragment;
import com.fleetchat.fragments.QRTabFragment;
import com.fleetchat.fragments.SettingsFragment;
import com.fleetchat.tools.AlertDialogManager;
import com.fleetchat.tools.GCMUtilities;

public class MainActivity extends FragmentActivity {

	protected static final String TAG = "MainActivity";
	public static final String PREF = "PREF";
	public static final String PREF_NAME = "PREF_NAME";
	public static final String PREF_EMAIL = "PREF_EMAIL";
	public static final String PREF_PORID = "PREF_PORID";
	public static GCMUtilities GCM;
	public static String USER_PORTRAIT_ID = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		GCM = new GCMUtilities(this);
		setContentView(R.layout.activity_main);
		setUserPortraitID();
		setTab();
	}

	private void setUserPortraitID() {
		USER_PORTRAIT_ID = getUserPorID(this);
		if (USER_PORTRAIT_ID.equalsIgnoreCase(""))
			USER_PORTRAIT_ID = "00";
	}

	private void setTab() {
		final ActionBar actionBar = getActionBar();

		// Specify that tabs should be displayed in the action bar.
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create a tab listener that is called when the user changes tabs.
		ActionBar.TabListener tabListener = new ActionBar.TabListener() {

			@Override
			public void onTabSelected(Tab tab,
					android.app.FragmentTransaction ft) {
				Fragment fragment = null;
				switch (tab.getPosition()) {
				case 0:
					fragment = new ContactFragment();
					tab.setIcon(R.drawable.friendclick);
					break;
				case 1:
					fragment = new ChatListFragment();
					tab.setIcon(R.drawable.chatclick);
					break;
				case 2:
					fragment = new SettingsFragment();
					tab.setIcon(R.drawable.settingsclick);
					break;
				case 3:
					fragment = new QRTabFragment();
					tab.setIcon(R.drawable.qrnewclick);
					break;
				case 4:
					fragment = new NFCTabFragment();
					tab.setIcon(R.drawable.nfcclick);
					break;
				}

				try {
					FragmentTransaction transaction = getSupportFragmentManager()
							.beginTransaction();

					// Replace whatever is in the fragment_container view with
					// this fragment,
					// and add the transaction to the back stack
					transaction.replace(R.id.main_activity_frameLayout,
							fragment);

					// Commit the transaction
					transaction.commit();
				} catch (Exception e) {
					Log.e(TAG, e.toString());
				}
			}

			@Override
			public void onTabUnselected(Tab tab,
					android.app.FragmentTransaction ft) {
				switch (tab.getPosition()) {
				case 0:
					tab.setIcon(R.drawable.friend);
					break;
				case 1:
					tab.setIcon(R.drawable.chat);
					break;
				case 2:
					tab.setIcon(R.drawable.settings);
					break;
				case 3:
					tab.setIcon(R.drawable.qrnew);
					break;
				case 4:
					tab.setIcon(R.drawable.nfc);
					break;
				}
			}

			@Override
			public void onTabReselected(Tab tab,
					android.app.FragmentTransaction ft) {

			}
		};
		Tab friendTab = actionBar.newTab().setTabListener(tabListener)
				.setIcon(R.drawable.friend);
		actionBar.addTab(friendTab);
		Tab chatTab = actionBar.newTab().setTabListener(tabListener)
				.setIcon(R.drawable.chat);
		actionBar.addTab(chatTab);
		Tab settingTab = actionBar.newTab().setTabListener(tabListener)
				.setIcon(R.drawable.settings);
		actionBar.addTab(settingTab);
		Tab qrTab = actionBar.newTab().setTabListener(tabListener)
				.setIcon(R.drawable.qrnew);
		actionBar.addTab(qrTab);
		Tab nfcTab = actionBar.newTab().setTabListener(tabListener)
				.setIcon(R.drawable.nfc);
		actionBar.addTab(nfcTab);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			adm.showEnterNameDialog(MainActivity.this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	AlertDialogManager adm = new AlertDialogManager();

	@Override
	public void onResume() {
		GCM.onResume();

		if (getSharedPreferences(PREF, Context.MODE_PRIVATE).getString(
				PREF_NAME, "").equals("")) {
			adm.showEnterNameDialog(MainActivity.this);
		}
		super.onResume();
	}

	@Override
	public void onDestroy() {
		GCM.onDestroy();
		super.onDestroy();
	}

	public static String getUserName(Context context) {
		return context.getSharedPreferences(MainActivity.PREF,
				Context.MODE_PRIVATE).getString(MainActivity.PREF_NAME, "");
	}

	public static String getUserEmail(Context context) {
		return context.getSharedPreferences(MainActivity.PREF,
				Context.MODE_PRIVATE).getString(MainActivity.PREF_EMAIL, "");
	}

	public static String getUserPorID(Context context) {
		return context.getSharedPreferences(MainActivity.PREF,
				Context.MODE_PRIVATE).getString(MainActivity.PREF_PORID, "");
	}
}
