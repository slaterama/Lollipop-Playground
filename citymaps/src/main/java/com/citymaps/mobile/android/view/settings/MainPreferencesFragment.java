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
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.citymaps.mobile.android.BuildConfig;
import com.citymaps.mobile.android.R;
import com.citymaps.mobile.android.app.SessionManager;
import com.citymaps.mobile.android.app.VolleyManager;
import com.citymaps.mobile.android.model.User;
import com.citymaps.mobile.android.model.UserSettings;
import com.citymaps.mobile.android.model.request.UserRequest;
import com.citymaps.mobile.android.model.request.UserSettingsRequest;
import com.citymaps.mobile.android.model.request.VolleyCallbacks;
import com.citymaps.mobile.android.util.CommonUtils;
import com.citymaps.mobile.android.util.IntentUtils;
import com.citymaps.mobile.android.util.ShareUtils;

import java.util.HashMap;
import java.util.Map;

public class MainPreferencesFragment extends PreferencesFragment
		implements Preference.OnPreferenceClickListener,
		Preference.OnPreferenceChangeListener {

	private static final String STATE_KEY_HELPER_FRAGMENT = "helperFragment";

	private static final int REQUEST_CODE_USER_SETTINGS = 0;

	private static final int RESULT_ERROR = Activity.RESULT_FIRST_USER;

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

	protected HelperFragment mHelperFragment;

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
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState == null) {
			mHelperFragment = new HelperFragment();
			getFragmentManager()
					.beginTransaction()
					.add(mHelperFragment, HelperFragment.FRAGMENT_TAG)
					.commit();
		} else {
			mHelperFragment = (HelperFragment) getFragmentManager().getFragment(savedInstanceState,
					STATE_KEY_HELPER_FRAGMENT);
		}
		mHelperFragment.setTargetFragment(this, REQUEST_CODE_USER_SETTINGS);
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
			getActivity().registerReceiver(mConnectivityReceiver,
					new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		getFragmentManager().putFragment(outState, STATE_KEY_HELPER_FRAGMENT, mHelperFragment);
	}

	@Override
	public void onPause() {
		super.onPause();
		if (mUserLoggedIn) {
			getActivity().unregisterReceiver(mConnectivityReceiver);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case REQUEST_CODE_USER_SETTINGS:
				switch (resultCode) {
					case Activity.RESULT_OK:
						UserSettings settings = mSessionManager.getCurrentUserSettings();
						if (mEmailNotificationsPreference != null) {
							mEmailNotificationsPreference.setChecked(settings.isEmailNotifications());
						}
						break;
					case RESULT_ERROR:
						if (mConnectivityManager.getActiveNetworkInfo() != null) {
							String errorMessage = IntentUtils.getErrorMessage(data);
							// TODO Show error dialog
						}
						break;
				}

				break;
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
				mListener.onSigninClick();
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
				if (!CommonUtils.notifyIfNoNetwork(getActivity())) {
					boolean enabled = (Boolean) newValue;
					mHelperFragment.setEmailNotifications(enabled);
				}
				return false;
			default: {
				return false;
			}
		}
	}

	private BroadcastReceiver mConnectivityReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			boolean connected = (mConnectivityManager.getActiveNetworkInfo() != null);
			if (connected) {
				UserSettings settings = mSessionManager.getCurrentUserSettings();
				if (settings == null) {
					mHelperFragment.requestUserSettings();
				} else {
					mEmailNotificationsPreference.setChecked(settings.isEmailNotifications());
				}
			} else {
				// No action
			}
		}
	};

	public static class HelperFragment extends Fragment {

		public static final String FRAGMENT_TAG = HelperFragment.class.getName();

		private SessionManager mSessionManager;

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			mSessionManager = SessionManager.getInstance(activity);
		}

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setRetainInstance(true);
		}

		public void requestUserSettings() {
			User currentUser = mSessionManager.getCurrentUser();
			if (currentUser != null) {
				Request<UserSettings> request = UserSettingsRequest.newGetRequest(getActivity(), currentUser.getId(),
						mUserSettingsCallbacks, mUserSettingsCallbacks);
				VolleyManager.getInstance(getActivity()).getRequestQueue().add(request);
			}
		}

		public void setEmailNotifications(boolean enabled) {
			final UserSettings settings = mSessionManager.getCurrentUserSettings();
			if (settings != null) {
				Map<String, String> params = new HashMap<String, String>(1);
				params.put(UserRequest.KEY_EMAIL_NOTIFICATIONS, enabled ? "1" : "0");
				UserSettingsRequest request = UserSettingsRequest.newUpdateRequest(getActivity(),
						settings.getId(), params, mUserSettingsCallbacks, mUserSettingsCallbacks);
				VolleyManager.getInstance(getActivity()).getRequestQueue().add(request);
			}
		}

		private VolleyCallbacks<UserSettings> mUserSettingsCallbacks = new VolleyCallbacks<UserSettings>() {
			@Override
			public void onResponse(UserSettings settings) {
				mSessionManager.setCurrentUserSettings(settings);
				getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, null);
			}

			@Override
			public void onErrorResponse(VolleyError error) {
				Intent data = new Intent();
				IntentUtils.putErrorMessage(data, error.getLocalizedMessage());
				getTargetFragment().onActivityResult(getTargetRequestCode(), RESULT_ERROR, data);
			}
		};
	}

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
		public void onSigninClick();

		public void onSignoutClick();
	}
}
