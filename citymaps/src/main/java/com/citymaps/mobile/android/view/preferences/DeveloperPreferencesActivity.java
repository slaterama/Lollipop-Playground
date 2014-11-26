package com.citymaps.mobile.android.view.preferences;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.support.v4.app.Fragment;
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
