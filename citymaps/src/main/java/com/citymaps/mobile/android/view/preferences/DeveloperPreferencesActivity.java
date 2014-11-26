package com.citymaps.mobile.android.view.preferences;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.citymaps.mobile.android.R;
import com.citymaps.mobile.android.app.TrackedActionBarActivity;
import com.citymaps.mobile.android.preference.PreferenceFragment;
import com.citymaps.mobile.android.util.IntentUtils;

public class DeveloperPreferencesActivity extends TrackedActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState == null) {
			boolean showWelcomeMessage = false;
			Intent data = getIntent();
			if (data != null) {
				showWelcomeMessage = data.getBooleanExtra(IntentUtils.EXTRA_DEVELOPER_PASSWORD_JUST_ENTERED, false);
			}

			Fragment fragment = PreferencesFragment.newInstance(showWelcomeMessage);
			getSupportFragmentManager()
					.beginTransaction()
					.add(android.R.id.content, fragment, null)
					.commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.developer_preferences, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	public static class PreferencesFragment extends PreferenceFragment
			implements Preference.OnPreferenceClickListener {

		private static final String ARG_SHOW_WELCOME_MESSAGE = "showWelcomeMessage";

		public static PreferencesFragment newInstance(boolean showWelcomeMessage) {
			PreferencesFragment fragment = new PreferencesFragment();
			Bundle args = new Bundle();
			args.putBoolean(ARG_SHOW_WELCOME_MESSAGE, showWelcomeMessage);
			fragment.setArguments(args);
			return fragment;
		}

		private boolean mShowWelcomeMessage;

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			if (savedInstanceState == null) {
				Bundle args = getArguments();
				if (args != null) {
					mShowWelcomeMessage = args.getBoolean(ARG_SHOW_WELCOME_MESSAGE);
				}
			}
		}

		@Override
		public void onViewCreated(View view, Bundle savedInstanceState) {
			super.onViewCreated(view, savedInstanceState);
			addPreferencesFromResource(R.xml.preferences_developer);
		}

		@Override
		public void onResume() {
			super.onResume();

			if (mShowWelcomeMessage) {
				mShowWelcomeMessage = false;
				Toast.makeText(getActivity(), R.string.pref_developer_mode_welcome_developer,
						Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		public boolean onPreferenceClick(Preference preference) {
			//Pref pref = Pref.fromKey(preference.getKey());
			return false;
		}
	}
}
