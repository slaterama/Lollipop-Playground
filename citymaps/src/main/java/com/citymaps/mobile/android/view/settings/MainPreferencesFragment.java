package com.citymaps.mobile.android.view.settings;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.SwitchPreference;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.citymaps.mobile.android.BuildConfig;
import com.citymaps.mobile.android.R;
import com.citymaps.mobile.android.app.SessionManager;
import com.citymaps.mobile.android.app.VolleyManager;
import com.citymaps.mobile.android.model.User;
import com.citymaps.mobile.android.model.UserSettings;
import com.citymaps.mobile.android.model.volley.UserSettingsRequest;
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

	protected MainPreferencesListener mListener;

	protected SessionManager mSessionManager;

	protected ConnectivityManager mConnectivityManager;

	protected User mCurrentUser;

	protected boolean mUserLoggedIn = false;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		try {
			mListener = (MainPreferencesListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement MainPreferencesListener");
		}

		mSessionManager = SessionManager.getInstance(activity);
		mConnectivityManager = (ConnectivityManager) activity.getSystemService(Activity.CONNECTIVITY_SERVICE);

		mCurrentUser = mSessionManager.getCurrentUser();
		mUserLoggedIn = (mCurrentUser != null);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		addPreferencesFromResource(R.xml.preferences_general);

		addPreferencesFromResource(mUserLoggedIn
				? R.xml.preferences_authenticated
				: R.xml.preferences_unauthenticated);

		mShareAppPreference = findPreference(PreferenceType.SHARE_APP.toString());
		mFeedbackPreference = findPreference(PreferenceType.FEEDBACK.toString());

		mShareAppPreference.setOnPreferenceClickListener(this);
		mFeedbackPreference.setOnPreferenceClickListener(this);

		if (mUserLoggedIn) {
			mEmailNotificationsPreference = (SwitchPreference) findPreference(PreferenceType.EMAIL_NOTIFICATIONS.toString());
			mSignoutPreference = findPreference(PreferenceType.SIGNOUT.toString());

			mEmailNotificationsPreference.setEnabled(false);

			mEmailNotificationsPreference.setOnPreferenceChangeListener(this);
			mSignoutPreference.setOnPreferenceClickListener(this);
		} else {
			mSigninPreference = findPreference(PreferenceType.SIGNIN.toString());
			mSigninPreference.setOnPreferenceClickListener(this);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (mUserLoggedIn) {
			getActivity().registerReceiver(mConnectivityReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		if (mUserLoggedIn) {
			getActivity().unregisterReceiver(mConnectivityReceiver);
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
				mListener.onSignoutClick();
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

	// Api calls

	private void getUserSettings(String userId) {
		Request<UserSettings> request = UserSettingsRequest.newGetRequest(getActivity(), userId,
				new Response.Listener<UserSettings>() {
					@Override
					public void onResponse(UserSettings response) {
						mSessionManager.setCurrentUserSettings(response);
						mEmailNotificationsPreference.setSummary(null);
						mEmailNotificationsPreference.setChecked(response.isEmailNotifications());
					}
				},
				new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						if (mConnectivityManager.getActiveNetworkInfo() == null) {
							mEmailNotificationsPreference.setSummary(R.string.error_summary_no_connection);
						} else {
							mEmailNotificationsPreference.setSummary(error.getLocalizedMessage());
						}
					}
				});
		VolleyManager.getInstance(getActivity()).getRequestQueue().add(request);
	}

	private BroadcastReceiver mConnectivityReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			boolean connected = (mConnectivityManager.getActiveNetworkInfo() != null);
			if (connected) {
				mEmailNotificationsPreference.setEnabled(true);
				UserSettings settings = mSessionManager.getCurrentUserSettings();
				if (settings == null) {
					getUserSettings(mCurrentUser.getId());
				} else {
					mEmailNotificationsPreference.setSummary(null);
					mEmailNotificationsPreference.setChecked(settings.isEmailNotifications());
				}
			} else {
				mEmailNotificationsPreference.setEnabled(false);
				mEmailNotificationsPreference.setSummary(R.string.error_summary_no_connection);
			}
		}
	};

	/**
	 * This interface must be implemented by activities that contain this
	 * fragment to allow an interaction in this fragment to be communicated
	 * to the activity and potentially other fragments contained in that
	 * activity.
	 * <p/>
	 * See the Android Training lesson <a href=
	 * "http://developer.android.com/training/basics/fragments/communicating.html"
	 * >Communicating with Other Fragments</a> for more information.
	 */
	public interface MainPreferencesListener {
		public void onReceiveEmailNotificationsChange(User user, boolean notifications);
		public void onSigninClick();
		public void onSignoutClick();
	}
}
