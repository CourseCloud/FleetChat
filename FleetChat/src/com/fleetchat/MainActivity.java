package com.fleetchat;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.fleetchat.fragments.ContactFragment;
import com.fleetchat.fragments.ChatListFragment;
import com.fleetchat.fragments.DemoFragment;
import com.fleetchat.fragments.NFCTabFragment;
import com.fleetchat.fragments.QRTabFragment;
import com.fleetchat.tools.GCMUtilities;

public class MainActivity extends FragmentActivity {

	protected static final String TAG = "MainActivity";
	public static GCMUtilities GCM;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		GCM = new GCMUtilities(this);
		setContentView(R.layout.activity_main);
		setTab();
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
					break;
				case 1:
					fragment = new ChatListFragment();
					break; 
				case 2:
					fragment = new DemoFragment();
					break;
				case 3:
					fragment = new QRTabFragment();
					break;
				case 4:
					fragment = new NFCTabFragment();
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

			}

			@Override
			public void onTabReselected(Tab tab,
					android.app.FragmentTransaction ft) {

			}
		};

		// Add 3 tabs, specifying the tab's text and TabListener
		for (int i = 0; i < 5; i++) {
			Tab tab = actionBar.newTab().setText("Tab " + (i + 1))
					.setTabListener(tabListener)
					.setIcon(R.drawable.ic_launcher);
			actionBar.addTab(tab);
		}
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
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onResume() {
		GCM.onResume();
		super.onResume();
	}

	@Override
	public void onDestroy() {
		GCM.onDestroy();
		super.onDestroy();
	}

}
