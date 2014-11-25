package com.citymaps.mobile.android.view.settings;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import com.citymaps.mobile.android.app.TrackedActionBarActivity;
import com.citymaps.mobile.android.util.LogEx;

public class PreferencesActivity extends TrackedActionBarActivity {

	public static final int DEFAULT_PREFERENCES = 0;
	public static final int DEFAULT_PREFERENCES_AUTHENTICATED = 1;
	public static final int DEVELOPER_PREFERENCES = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState == null) {
			Fragment fragment = MainPreferencesFragment.newInstance();
			getSupportFragmentManager()
					.beginTransaction()
					.add(android.R.id.content, fragment, null)
					.commit();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		LogEx.d("!!!!!!");
		super.onActivityResult(requestCode, resultCode, data);
	}
}
