package com.citymaps.mobile.android.view.housekeeping;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.citymaps.mobile.android.R;
import com.citymaps.mobile.android.app.SessionManager;
import com.citymaps.mobile.android.app.TrackedActionBarActivity;
import com.citymaps.mobile.android.app.VolleyManager;
import com.citymaps.mobile.android.model.ThirdParty;
import com.citymaps.mobile.android.model.ThirdPartyUser;
import com.citymaps.mobile.android.model.User;
import com.citymaps.mobile.android.model.request.UserRequest;
import com.citymaps.mobile.android.thirdparty_new.FacebookProxy;
import com.citymaps.mobile.android.thirdparty_new.GoogleProxy;
import com.citymaps.mobile.android.thirdparty_new.ThirdPartyProxy;
import com.citymaps.mobile.android.util.CommonUtils;
import com.citymaps.mobile.android.util.IntentUtils;
import com.citymaps.mobile.android.util.LogEx;
import com.citymaps.mobile.android.util.SharedPreferenceUtils;
import com.citymaps.mobile.android.view.MainActivity;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.google.android.gms.plus.model.people.Person;

import java.util.*;

import static com.citymaps.mobile.android.thirdparty_new.ThirdPartyProxy.DATA_NAME_TOKEN;
import static com.citymaps.mobile.android.thirdparty_new.FacebookProxy.DATA_NAME_ME;
import static com.citymaps.mobile.android.thirdparty_new.GoogleProxy.DATA_NAME_ACCOUNT_NAME;
import static com.citymaps.mobile.android.thirdparty_new.GoogleProxy.DATA_NAME_CURRENT_PERSON;

public class AuthenticateActivityNew extends TrackedActionBarActivity {

	private static final int REQUEST_CODE_LOGIN = 1;
	private static final int REQUEST_CODE_CREATE_ACCOUNT = 2;

	private static final String STATE_KEY_FACEBOOK_PROXY_CREATED = "facebookProxyCreated";
	private static final String STATE_KEY_GOOGLE_PROXY_CREATED = "googleProxyCreated";

	private static final List<String> FACEBOOK_READ_PERMISSIONS = Arrays.asList("public_profile", "email");

	private boolean mStartupMode;

	private Set<ThirdPartyProxy> mThirdPartyProxySet;
	private FacebookProxy mFacebookProxy;
	private GoogleProxy mGoogleProxy;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_authenticate);
		mStartupMode = IntentUtils.isStartupMode(getIntent(), false);

		mThirdPartyProxySet = new HashSet<ThirdPartyProxy>(ThirdParty.values().length);
		if (savedInstanceState != null) {
			if (savedInstanceState.getBoolean(STATE_KEY_FACEBOOK_PROXY_CREATED)) {
				mFacebookProxy = new FacebookProxy(this, FACEBOOK_READ_PERMISSIONS, null, mFacebookCallbacks);
				mThirdPartyProxySet.add(mFacebookProxy);
			}
			if (savedInstanceState.getBoolean(STATE_KEY_GOOGLE_PROXY_CREATED)) {
				mGoogleProxy = new GoogleProxy(this, mGoogleCallbacks);
				mThirdPartyProxySet.add(mGoogleProxy);
			}
		}
		for (ThirdPartyProxy proxy : mThirdPartyProxySet) {
			proxy.onCreate(savedInstanceState);
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		for (ThirdPartyProxy proxy : mThirdPartyProxySet) {
			proxy.onStart();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		for (ThirdPartyProxy proxy : mThirdPartyProxySet) {
			proxy.onResume();
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean(STATE_KEY_FACEBOOK_PROXY_CREATED, mFacebookProxy != null);
		outState.putBoolean(STATE_KEY_GOOGLE_PROXY_CREATED, mGoogleProxy != null);
		for (ThirdPartyProxy proxy : mThirdPartyProxySet) {
			proxy.onSaveInstanceState(outState);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		for (ThirdPartyProxy proxy : mThirdPartyProxySet) {
			proxy.onPause();
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		for (ThirdPartyProxy proxy : mThirdPartyProxySet) {
			proxy.onStop();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		for (ThirdPartyProxy proxy : mThirdPartyProxySet) {
			proxy.onDestroy();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case REQUEST_CODE_CREATE_ACCOUNT:
				if (resultCode == RESULT_OK) {
					wrapUp();
				} else try {
					ThirdPartyUser user = IntentUtils.getThirdPartyUser(data);
					ThirdParty thirdParty = user.getThirdParty();
					switch (thirdParty) {
						case FACEBOOK:
							mThirdPartyProxySet.remove(mFacebookProxy);
							mFacebookProxy.deactivate(true);
							mFacebookProxy = null;
							break;
						case GOOGLE:
							mThirdPartyProxySet.remove(mGoogleProxy);
							mGoogleProxy.deactivate(true);
							mGoogleProxy = null;
							break;
					}
				} catch (NullPointerException e) {
					// No action
				}
				break;
			case REQUEST_CODE_LOGIN:
				if (resultCode == RESULT_OK) {
					wrapUp();
				}
				break;
			default:
				for (ThirdPartyProxy proxy : mThirdPartyProxySet) {
					proxy.onActivityResult(requestCode, resultCode, data);
				}
				super.onActivityResult(requestCode, resultCode, data);
		}
	}

	public void onButtonClick(View view) {
		int id = view.getId();
		switch (id) {
			case R.id.signin_authenticate_facebook_button: {
				if (CommonUtils.notifyIfNoNetwork(this)) {
					return;
				}

				// Probably not needed
				if (mFacebookProxy != null) {
					mFacebookProxy.deactivate(false);
					mThirdPartyProxySet.remove(mFacebookProxy);
				}

				mFacebookProxy = new FacebookProxy(this, FACEBOOK_READ_PERMISSIONS, null, mFacebookCallbacks);
				mThirdPartyProxySet.add(mFacebookProxy);
				mFacebookProxy.activate(true, mFacebookCallbacks);
				break;
			}
			case R.id.signin_authenticate_google_button: {
				if (CommonUtils.notifyIfNoNetwork(this)) {
					return;
				}
				break;
			}
			case R.id.signin_authenticate_create_account_button: {
				Intent intent = new Intent(this, SigninActivity.class);
				IntentUtils.putLoginMode(intent, SigninActivity.CREATE_ACCOUNT_MODE);
				startActivityForResult(intent, REQUEST_CODE_CREATE_ACCOUNT);
				break;
			}
			case R.id.signin_authenticate_signin_button: {
				Intent intent = new Intent(this, SigninActivity.class);
				IntentUtils.putLoginMode(intent, SigninActivity.SIGN_IN_MODE);
				startActivityForResult(intent, REQUEST_CODE_LOGIN);
				break;
			}
			case R.id.signin_authenticate_skip_button: {
				wrapUp();
				break;
			}
		}
	}

	private void processThirdPartyUser(final ThirdPartyUser user) {
		UserRequest loginRequest = UserRequest.newLoginRequest(AuthenticateActivityNew.this, user.getThirdParty(),
				user.getId(), user.getToken(), new Response.Listener<User>() {
					@Override
					public void onResponse(User response) {
						// We successfully logged in using a third party
						SessionManager.getInstance(AuthenticateActivityNew.this).setCurrentUser(response);
						SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(AuthenticateActivityNew.this);
						SharedPreferenceUtils.putString(sp, user.getThirdParty().getSharedPreferenceTokenKey(), user.getToken()).apply();
						wrapUp();
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						// There is no Citymaps user linked to the third party account. Take them to the Create Account screen
						Intent intent = new Intent(AuthenticateActivityNew.this, SigninActivity.class);
						IntentUtils.putLoginMode(intent, SigninActivity.CREATE_ACCOUNT_MODE);
						IntentUtils.putThirdPartyUser(intent, user);
						AuthenticateActivityNew.this.startActivityForResult(intent, REQUEST_CODE_CREATE_ACCOUNT);
					}
				});
		VolleyManager.getInstance(AuthenticateActivityNew.this).getRequestQueue().add(loginRequest);
	}

	private void wrapUp() {
		if (mStartupMode) {
			startActivity(new Intent(this, MainActivity.class));
		}
		finish();
	}

	private FacebookProxy.Callbacks mFacebookCallbacks = new FacebookProxy.SimpleCallbacks() {
		@Override
		public void onConnected(FacebookProxy proxy, Session session, SessionState state, Exception exception) {
			if (LogEx.isLoggable(LogEx.VERBOSE)) {
				LogEx.v(String.format("proxy=%s, session=%s, state=%s, exception=%s", proxy, session, state, exception));
			}
			// If we get to this point, we have a Facebook session. We need to get the current Facebook user and
			// see if their Facebook id is registered with Citymaps. If it is we can log them in and continue. If not
			// we need to pass along their Facebook info to LoginActivity.
			proxy.requestData(Arrays.asList(DATA_NAME_TOKEN, DATA_NAME_ME), mFacebookOnDataListener);
			// mFacebookProxy will be deactivated and nulled if/when user cancels out of CreateAccount screen
		}

		@Override
		public void onDisconnected(FacebookProxy proxy, Session session, SessionState state, Exception exception) {
			if (LogEx.isLoggable(LogEx.VERBOSE)) {
				LogEx.v(String.format("proxy=%s, session=%s, state=%s, exception=%s", proxy, session, state, exception));
			}
			// If we get to this point, just deactivate & remove the proxy as if it was never activated
			mThirdPartyProxySet.remove(mFacebookProxy);
			mFacebookProxy.deactivate(true);
			mFacebookProxy = null;
		}

		@Override
		public boolean onFailed(FacebookProxy proxy, boolean cancelled, Session session, SessionState state, Exception exception) {
			if (LogEx.isLoggable(LogEx.VERBOSE)) {
				LogEx.v(String.format("proxy=%s, cancelled=%b, session=%s, state=%s, exception=%s", proxy, cancelled, session, state, exception));
			}
			// If we get to this point, just deactivate & remove the proxy as if it was never activated
			mThirdPartyProxySet.remove(mFacebookProxy);
			mFacebookProxy.deactivate(true);
			mFacebookProxy = null;
			return true;
		}
	};

	private GoogleProxy.Callbacks mGoogleCallbacks = new GoogleProxy.SimpleCallbacks() {

	};

	private ThirdPartyProxy.OnDataListener mFacebookOnDataListener = new ThirdPartyProxy.OnDataListener() {
		@Override
		public void onData(ThirdPartyProxy proxy, Map<String, Object> data) {
			String token = (String) data.get(DATA_NAME_TOKEN);
			GraphUser graphUser = (GraphUser) data.get(DATA_NAME_ME);
			processThirdPartyUser(new ThirdPartyUser(token, graphUser));
		}

		@Override
		public void onError(ThirdPartyProxy proxy, Map<String, Object> errors) {
			// TODO Error handling
		}
	};

	private ThirdPartyProxy.OnDataListener mGoogleOnDataListener = new ThirdPartyProxy.OnDataListener() {
		@Override
		public void onData(ThirdPartyProxy proxy, Map<String, Object> data) {
			String token = (String) data.get(DATA_NAME_TOKEN);
			String accountName = (String) data.get(DATA_NAME_ACCOUNT_NAME);
			Person currentPerson = (Person) data.get(DATA_NAME_CURRENT_PERSON);
			processThirdPartyUser(new ThirdPartyUser(token, currentPerson, accountName));
		}

		@Override
		public void onError(ThirdPartyProxy proxy, Map<String, Object> errors) {
			// TODO Error handling
		}
	};
}