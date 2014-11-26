package com.citymaps.mobile.android.view.settings;

import android.app.Activity;
import android.content.*;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.View;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.citymaps.mobile.android.BuildConfig;
import com.citymaps.mobile.android.R;
import com.citymaps.mobile.android.app.SessionManager;
import com.citymaps.mobile.android.app.VolleyManager;
import com.citymaps.mobile.android.model.ThirdParty;
import com.citymaps.mobile.android.model.User;
import com.citymaps.mobile.android.model.UserSettings;
import com.citymaps.mobile.android.model.request.UserRequest;
import com.citymaps.mobile.android.model.request.UserSettingsRequest;
import com.citymaps.mobile.android.model.request.VolleyCallbacks;
import com.citymaps.mobile.android.preference.SwitchPreferenceEx;
import com.citymaps.mobile.android.thirdparty.FacebookProxy;
import com.citymaps.mobile.android.thirdparty.GoogleProxy;
import com.citymaps.mobile.android.thirdparty.ThirdPartyProxy;
import com.citymaps.mobile.android.util.CommonUtils;
import com.citymaps.mobile.android.util.LogEx;
import com.citymaps.mobile.android.util.ShareUtils;
import com.citymaps.mobile.android.util.SharedPreferenceUtils;
import com.citymaps.mobile.android.view.MainActivity;
import com.citymaps.mobile.android.view.housekeeping.SignoutDialogFragment;
import com.facebook.Session;
import com.facebook.SessionState;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;

import java.util.*;

import static com.citymaps.mobile.android.thirdparty.ThirdPartyProxy.DATA_TOKEN;
import static com.citymaps.mobile.android.thirdparty.FacebookProxy.DATA_ME;
import static com.citymaps.mobile.android.thirdparty.GoogleProxy.DATA_ACCOUNT_NAME;
import static com.citymaps.mobile.android.thirdparty.GoogleProxy.DATA_CURRENT_PERSON;

@SuppressWarnings("SpellCheckingInspection")
public class MainPreferencesFragment extends PreferencesFragment
		implements Preference.OnPreferenceClickListener,
		Preference.OnPreferenceChangeListener {

	private static final String STATE_KEY_HELPER_FRAGMENT = "helperFragment";

	private static final List<String> FACEBOOK_READ_PERMISSIONS = Arrays.asList("public_profile", "email");

	private static final int REQUEST_CODE_USER_SETTINGS = 0;

	private static final int REQUEST_CODE_SIGNOUT = 1;

	private static final String EMAIL_INTENT_TYPE = "message/rfc822";

	// General preferences
	private Preference mShareAppPreference;
	private Preference mFeedbackPreference;

	// Signed-out preferences
	private Preference mSigninPreference;

	// Signed-in preferences
	private SwitchPreferenceEx mFacebookPreference;
	private SwitchPreferenceEx mGooglePreference;
	private SwitchPreferenceEx mEmailNotificationsPreference;
	private Preference mSignoutPreference;

	public static MainPreferencesFragment newInstance() {
		return new MainPreferencesFragment();
	}

	protected SessionManager mSessionManager;

	protected ConnectivityManager mConnectivityManager;

	protected User mCurrentUser;
	protected User.ThirdPartyCredential mFacebookCredential;
	protected User.ThirdPartyCredential mGoogleCredential;

	protected HelperFragment mHelperFragment;

	protected Set<ThirdPartyProxy> mThirdPartyProxySet;
	protected FacebookProxy mFacebookProxy;
	protected GoogleProxy mGoogleProxy;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		mSessionManager = SessionManager.getInstance(activity);
		mConnectivityManager = (ConnectivityManager) activity.getSystemService(Activity.CONNECTIVITY_SERVICE);

		mCurrentUser = mSessionManager.getCurrentUser();
		if (mCurrentUser != null) {
			User.ThirdPartyCredentials credentials = mCurrentUser.getThirdPartyCredentials();
			if (credentials != null) {
				mFacebookCredential = credentials.getFacebook();
				mGoogleCredential = credentials.getGoogle();
			}
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mThirdPartyProxySet = new HashSet<ThirdPartyProxy>(ThirdParty.values().length);
		FragmentManager manager = getFragmentManager();
		if (savedInstanceState == null) {
			mHelperFragment = new HelperFragment();
			manager.beginTransaction()
					.add(mHelperFragment, HelperFragment.FRAGMENT_TAG)
					.commit();
		} else {
			mHelperFragment = (HelperFragment) manager.getFragment(savedInstanceState, STATE_KEY_HELPER_FRAGMENT);
			SignoutDialogFragment signoutDialogFragment =
					(SignoutDialogFragment) manager.findFragmentByTag(SignoutDialogFragment.FRAGMENT_TAG);
			if (signoutDialogFragment != null) {
				signoutDialogFragment.setTargetFragment(this, REQUEST_CODE_SIGNOUT);
			}
		}
		mHelperFragment.setTargetFragment(this, REQUEST_CODE_USER_SETTINGS);
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

		mShareAppPreference.setOnPreferenceClickListener(this);
		mFeedbackPreference.setOnPreferenceClickListener(this);

		if (mCurrentUser == null) {
			mSigninPreference = findPreference(PreferenceType.SIGNIN.toString());
			mSigninPreference.setOnPreferenceClickListener(this);
		} else {
			mFacebookPreference = (SwitchPreferenceEx) findPreference(PreferenceType.CONNECT_FACEBOOK.toString());
			mGooglePreference = (SwitchPreferenceEx) findPreference(PreferenceType.CONNECT_GOOGLE.toString());
			mEmailNotificationsPreference = (SwitchPreferenceEx) findPreference(PreferenceType.EMAIL_NOTIFICATIONS.toString());
			mSignoutPreference = findPreference(PreferenceType.SIGNOUT.toString());

			mFacebookPreference.setOnPreferenceClickListener(this);
			mFacebookPreference.setOnPreferenceChangeListener(this);
			mGooglePreference.setOnPreferenceClickListener(this);
			mGooglePreference.setOnPreferenceChangeListener(this);
			mEmailNotificationsPreference.setOnPreferenceChangeListener(this);
			mSignoutPreference.setOnPreferenceClickListener(this);

			if (mFacebookCredential != null) {
				mFacebookPreference.setChecked(true);
				mFacebookProxy = new FacebookProxy(getActivity(), this, FACEBOOK_READ_PERMISSIONS, null, mFacebookCallbacks);
				mThirdPartyProxySet.add(mFacebookProxy);
				boolean activated = mFacebookProxy.activate(false, mFacebookCallbacks);
				if (!activated) {
					mFacebookPreference.setSummary(R.string.error_pref_third_party_connection_lost);
					mFacebookPreference.setSecondaryIcon(R.drawable.ic_sync_problem_red_24dp);
				}
			}
			if (mGoogleCredential != null) {
				mGooglePreference.setChecked(true);
				mGoogleProxy = new GoogleProxy(getActivity(), this, mGoogleCallbacks);
				mThirdPartyProxySet.add(mGoogleProxy);
				boolean activated = mGoogleProxy.activate(false, mGoogleCallbacks);
				if (!activated) {
					mGooglePreference.setSummary(R.string.error_pref_third_party_connection_lost);
					mGooglePreference.setSecondaryIcon(R.drawable.ic_sync_problem_red_24dp);
				}
			}
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (mCurrentUser != null) {
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
		if (mCurrentUser != null) {
			getActivity().unregisterReceiver(mConnectivityReceiver);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		LogEx.d(String.format("requestCode=%d", requestCode));
		switch (requestCode) {
			case REQUEST_CODE_USER_SETTINGS:
				if (resultCode == FragmentActivity.RESULT_OK) {
					UserSettings settings = mSessionManager.getCurrentUserSettings();
					if (mEmailNotificationsPreference != null) {
						mEmailNotificationsPreference.setChecked(settings.isEmailNotifications());
					}
				}
				break;
			case REQUEST_CODE_SIGNOUT:
				if (resultCode == FragmentActivity.RESULT_OK) {
					final FragmentActivity activity = getActivity();
					final SessionManager sessionManager = SessionManager.getInstance(activity);
					User currentUser = sessionManager.getCurrentUser();
					if (currentUser != null) {
						UserRequest request = UserRequest.newLogoutRequest(activity, currentUser.getId(),
								new Response.Listener<User>() {
									@Override
									public void onResponse(User response) {
										sessionManager.setCurrentUser(null);

										// Clear third party tokens from shared preferences
										SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity);
										sp.edit().remove(SharedPreferenceUtils.Key.FACEBOOK_TOKEN.getKeyName())
												.remove(SharedPreferenceUtils.Key.GOOGLE_TOKEN.getKeyName()).apply();

										// Deactivate third party proxies & clear tokens
										for (ThirdPartyProxy proxy : mThirdPartyProxySet) {
											proxy.deactivate(true);
										}

										activity.setResult(MainActivity.RESULT_LOGOUT);
										activity.finish();
									}
								},
								new Response.ErrorListener() {
									@Override
									public void onErrorResponse(VolleyError error) {
										// TODO Error handling
									}
								});
						VolleyManager.getInstance(activity).getRequestQueue().add(request);
					}
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
			case CONNECT_FACEBOOK: {
				LogEx.d();
				break;
			}
			case CONNECT_GOOGLE: {
				LogEx.d();
				break;
			}
			case FEEDBACK: {
				String subject;
				if (mCurrentUser == null) {
					subject = BuildConfig.FEEDBACK_SUBJECT_VISITOR;
				} else {
					String fullName = mCurrentUser.getName(getActivity());
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
				FragmentActivity activity = getActivity();
				activity.setResult(MainActivity.RESULT_LOGIN);
				activity.finish();
				break;
			}
			case SIGNOUT: {
				FragmentManager manager = getFragmentManager();
				if (manager.findFragmentByTag(SignoutDialogFragment.FRAGMENT_TAG) == null) {
					SignoutDialogFragment fragment = SignoutDialogFragment.newInstance();
					fragment.setTargetFragment(this, REQUEST_CODE_SIGNOUT);
					fragment.show(manager, SignoutDialogFragment.FRAGMENT_TAG);
				}
			}
		}
		return false;
	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		PreferenceType type = PreferenceType.fromKey(preference.getKey());
		switch (type) {
			case CONNECT_FACEBOOK:
				LogEx.d();
				if (!CommonUtils.notifyIfNoNetwork(getActivity())) {
					boolean checked = (Boolean) newValue;
					if (checked) {

					} else {

					}
				}
				return true;
			case CONNECT_GOOGLE:
				LogEx.d();
				if (!CommonUtils.notifyIfNoNetwork(getActivity())) {
					boolean checked = (Boolean) newValue;
					if (checked) {

					} else {

					}
				}
				return true;
			case EMAIL_NOTIFICATIONS:
				if (!CommonUtils.notifyIfNoNetwork(getActivity())) {
					boolean checked = (Boolean) newValue;
					mHelperFragment.setEmailNotifications(checked);
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

	private FacebookProxy.Callbacks mFacebookCallbacks = new FacebookProxy.SimpleCallbacks() {
		@Override
		public void onConnected(FacebookProxy proxy, Session session, SessionState state, Exception exception) {
			if (LogEx.isLoggable(LogEx.INFO)) {
				LogEx.i(String.format("proxy=%s, session=%s, state=%s, exception=%s", proxy, session, state, exception));
			}

			// When we get here, we need to request info about the session. Specifically, the Facebook user's ID.
			proxy.requestData(Arrays.asList(DATA_TOKEN, DATA_ME), mFacebookOnDataListener);
		}

		@Override
		public void onDisconnected(FacebookProxy proxy, Session session, SessionState state, Exception exception) {
			if (LogEx.isLoggable(LogEx.INFO)) {
				LogEx.i(String.format("proxy=%s, session=%s, state=%s, exception=%s", proxy, session, state, exception));
			}
		}

		@Override
		public boolean onFailed(FacebookProxy proxy, boolean cancelled, Session session, SessionState state, Exception exception) {
			if (LogEx.isLoggable(LogEx.INFO)) {
				LogEx.i(String.format("proxy=%s, cancelled=%b, session=%s, state=%s, exception=%s",
						proxy, cancelled, session, state, exception));
			}
			return false;
		}
	};

	private GoogleProxy.Callbacks mGoogleCallbacks = new GoogleProxy.SimpleCallbacks() {
		@Override
		public void onPreBuild(@NonNull GoogleApiClient.Builder builder) {
			builder.addApi(Plus.API);
			builder.addScope(Plus.SCOPE_PLUS_LOGIN);
		}

		@Override
		public void onConnected(GoogleProxy proxy, Bundle connectionHint) {
			if (LogEx.isLoggable(LogEx.INFO)) {
				LogEx.i(String.format("proxy=%s, connectionHint=%s", proxy, connectionHint));
			}

			// When we get here, we need to request info about the session. Specifically, the Facebook user's ID.
			proxy.requestData(Arrays.asList(DATA_TOKEN, DATA_CURRENT_PERSON, DATA_ACCOUNT_NAME), mGoogleOnDataListener);
		}

		@Override
		public void onDisconnected(GoogleProxy proxy) {
			if (LogEx.isLoggable(LogEx.INFO)) {
				LogEx.i(String.format("proxy=%s", proxy));
			}
		}

		@Override
		public boolean onFailed(GoogleProxy proxy, boolean cancelled, ConnectionResult result) {
			if (LogEx.isLoggable(LogEx.INFO)) {
				LogEx.i(String.format("proxy=%s, cancelled=%b, result=%s", proxy, cancelled, result));
			}
			mGooglePreference.setSummary(R.string.error_pref_third_party_connection_lost);
			return true;
		}
	};

	private ThirdPartyProxy.OnDataListener mFacebookOnDataListener = new ThirdPartyProxy.OnDataListener() {
		@Override
		public void onData(ThirdPartyProxy proxy, Map<String, Object> data) {

		}

		@Override
		public void onError(ThirdPartyProxy proxy, Map<String, Object> errors) {

		}
	};

	private ThirdPartyProxy.OnDataListener mGoogleOnDataListener = new ThirdPartyProxy.OnDataListener() {
		@Override
		public void onData(ThirdPartyProxy proxy, Map<String, Object> data) {

		}

		@Override
		public void onError(ThirdPartyProxy proxy, Map<String, Object> errors) {

		}
	};

	/*
	private ThirdPartyProxy.OnDataListener mOnDataListener = new ThirdPartyProxy.OnDataListener() {
		@Override
		public void onData(ThirdPartyProxy proxy, Map<String, Object> data) {
			ThirdParty thirdParty = proxy.getThirdParty();
			switch (thirdParty) {
				case FACEBOOK:
					// TODO Are all these null checks necessary?
					String facebookId = mCurrentUser.getFacebookId();
					GraphUser graphUser = (GraphUser) data.get(FacebookProxy.DATA_ME);
					if (graphUser == null || !TextUtils.equals(facebookId, graphUser.getId())) {
						mFacebookPreference.setSummary(R.string.error_pref_third_party_connection_lost);
					} else {
						mFacebookPreference.setSummary(graphUser.getName());
					}
					break;
				case GOOGLE:
					if (mCurrentUser != null) {
						String googleId = mCurrentUser.getGoogleId();
						Person person = (Person) data.get(GoogleProxy.DATA_CURRENT_PERSON);
					}
					break;
			}
		}

		@Override
		public void onError(ThirdPartyProxy proxy, Map<String, Object> errors) {

		}
	};
	*/

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
				CommonUtils.showSimpleDialogFragment(getFragmentManager(),
						getActivity().getTitle(), error.getLocalizedMessage());
			}
		};
	}
}
