package com.citymaps.mobile.android.view.settings;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import com.citymaps.mobile.android.R;
import com.citymaps.mobile.android.app.SessionManager;
import com.citymaps.mobile.android.model.User;

public class MainPreferencesFragment extends PreferencesFragment {

	private static final String EMAIL_INTENT_TYPE = "message/rfc822";

	public static MainPreferencesFragment newInstance() {
		return new MainPreferencesFragment();
	}

	protected User mCurrentUser;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mCurrentUser = SessionManager.getInstance(activity).getCurrentUser();
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		addPreferencesFromResource(R.xml.preferences_general);

		addPreferencesFromResource(mCurrentUser == null
				? R.xml.preferences_unauthenticated
				: R.xml.preferences_authenticated);
	}
}
