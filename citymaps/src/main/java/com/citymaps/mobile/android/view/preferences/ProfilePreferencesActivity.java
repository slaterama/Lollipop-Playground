package com.citymaps.mobile.android.view.preferences;

import android.os.Bundle;
import android.preference.Preference;
import android.support.v4.app.Fragment;
import android.view.View;
import com.citymaps.mobile.android.R;
import com.citymaps.mobile.android.app.TrackedActionBarActivity;
import com.citymaps.mobile.android.preference.PreferenceFragment;

public class ProfilePreferencesActivity extends TrackedActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState == null) {
			Fragment fragment = PreferencesFragment.newInstance();
			getSupportFragmentManager()
					.beginTransaction()
					.add(android.R.id.content, fragment, null)
					.commit();
		}
	}

	public static class PreferencesFragment extends PreferenceFragment
			implements Preference.OnPreferenceClickListener {

		public static PreferencesFragment newInstance() {
			return new PreferencesFragment();
		}

		@Override
		public void onViewCreated(View view, Bundle savedInstanceState) {
			super.onViewCreated(view, savedInstanceState);
			addPreferencesFromResource(R.xml.preferences_profile);
		}

		@Override
		public boolean onPreferenceClick(Preference preference) {
			//Pref pref = Pref.fromKey(preference.getKey());
			return false;
		}
	}
}
