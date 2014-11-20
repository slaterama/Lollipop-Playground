package com.citymaps.mobile.android.view.settings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import com.citymaps.mobile.android.BuildConfig;
import com.citymaps.mobile.android.R;
import com.citymaps.mobile.android.app.SessionManager;
import com.citymaps.mobile.android.model.User;
import com.citymaps.mobile.android.util.LogEx;

public class MainPreferencesFragment extends PreferencesFragment
		implements Preference.OnPreferenceClickListener {

	private static final String EMAIL_INTENT_TYPE = "message/rfc822";

	private Preference mPreferenceSendFeedback;

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

		mPreferenceSendFeedback = findPreference("pref_feedback");
		mPreferenceSendFeedback.setOnPreferenceClickListener(this);
	}

	@Override
	public boolean onPreferenceClick(Preference preference) {
		if (preference == mPreferenceSendFeedback) {
			String subject;
			if (mCurrentUser == null) {
				subject = BuildConfig.FEEDBACK_SUBJECT_VISITOR;
			} else {
				String fullName = mCurrentUser.getFullName();
				if (TextUtils.isEmpty(fullName)) {
					subject = BuildConfig.FEEDBACK_SUBJECT_USER;
				} else {
					subject = String.format(BuildConfig.FEEDBACK_SUBJECT, fullName);
				}
			}
			Intent intent = new Intent(Intent.ACTION_SEND);
			intent.setType(EMAIL_INTENT_TYPE);
			intent.putExtra(Intent.EXTRA_EMAIL, BuildConfig.FEEDBACK_EMAILS);
			intent.putExtra(Intent.EXTRA_SUBJECT, subject);
			intent.putExtra(Intent.EXTRA_TEXT, "");
			getActivity().startActivity(intent);
		}
		return false;
	}
}
