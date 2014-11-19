package com.citymaps.mobile.android.view.settings;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import com.citymaps.mobile.android.R;
import com.citymaps.mobile.android.preference.PreferenceFragment;

public class DeveloperPreferencesFragment extends PreferenceFragment {

	public static DeveloperPreferencesFragment newInstance() {
		return new DeveloperPreferencesFragment();
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		addPreferencesFromResource(R.xml.preferences_developer);
	}

}
