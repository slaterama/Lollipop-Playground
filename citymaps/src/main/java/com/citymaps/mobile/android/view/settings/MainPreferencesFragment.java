package com.citymaps.mobile.android.view.settings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.SwitchPreference;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import com.citymaps.mobile.android.BuildConfig;
import com.citymaps.mobile.android.R;
import com.citymaps.mobile.android.app.SessionManager;
import com.citymaps.mobile.android.model.User;
import com.citymaps.mobile.android.util.ShareUtils;

public class MainPreferencesFragment extends PreferencesFragment
		implements Preference.OnPreferenceClickListener,
		Preference.OnPreferenceChangeListener {

	private static final String EMAIL_INTENT_TYPE = "message/rfc822";

	private Preference mShareAppPreference;
	private Preference mFeedbackPreference;
	private SwitchPreference mEmailNotificationsPreference;
	private Preference mSigninPreference;
	private Preference mSignoutPreference;

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

		mShareAppPreference = findPreference(PreferenceType.SHARE_APP.toString());
		mFeedbackPreference = findPreference(PreferenceType.FEEDBACK.toString());
		mEmailNotificationsPreference = (SwitchPreference) findPreference(PreferenceType.EMAIL_NOTIFICATIONS.toString());
		mSigninPreference = findPreference(PreferenceType.SIGNIN.toString());
		mSignoutPreference = findPreference(PreferenceType.SIGNOUT.toString());

		mShareAppPreference.setOnPreferenceClickListener(this);
		mFeedbackPreference.setOnPreferenceClickListener(this);
		mEmailNotificationsPreference.setOnPreferenceChangeListener(this);
		if (mSigninPreference != null) {
			mSigninPreference.setOnPreferenceClickListener(this);
		}
		if (mSignoutPreference != null) {
			mSignoutPreference.setOnPreferenceClickListener(this);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			default:
				super.onActivityResult(requestCode, resultCode, data);
		}
	}

	@Override
	public boolean onPreferenceClick(Preference preference) {
		PreferenceType type = PreferenceType.fromKey(preference.getKey());
		switch (type) {
			case SHARE_APP: {
				ShareUtils.shareApp(getActivity());
				break;
			}
			case FEEDBACK: {
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
				startActivity(intent);
				break;
			}
			case SIGNIN: {

				break;
			}
			case SIGNOUT: {

				break;
			}
		}
		return false;
	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		PreferenceType type = PreferenceType.fromKey(preference.getKey());
		switch (type) {
			case EMAIL_NOTIFICATIONS:
				return false;
			default: {
				return false;
			}
		}
	}
}
