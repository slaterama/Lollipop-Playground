package com.citymaps.mobile.android.view.housekeeping;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
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
import com.citymaps.mobile.android.thirdparty.FacebookProxy;
import com.citymaps.mobile.android.thirdparty.GoogleProxy;
import com.citymaps.mobile.android.thirdparty.ThirdPartyProxy;
import com.citymaps.mobile.android.util.CommonUtils;
import com.citymaps.mobile.android.util.IntentUtils;
import com.citymaps.mobile.android.util.LogEx;
import com.citymaps.mobile.android.util.SharedPrefUtils;
import com.citymaps.mobile.android.view.MainActivity;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import java.util.*;

import static com.citymaps.mobile.android.thirdparty.ThirdPartyProxy.DATA_TOKEN;
import static com.citymaps.mobile.android.thirdparty.FacebookProxy.DATA_ME;
import static com.citymaps.mobile.android.thirdparty.GoogleProxy.DATA_ACCOUNT_NAME;
import static com.citymaps.mobile.android.thirdparty.GoogleProxy.DATA_CURRENT_PERSON;

public class AuthenticateActivity extends TrackedActionBarActivity {

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
							deactivateProxy(ThirdParty.FACEBOOK, true);
							break;
						case GOOGLE:
							deactivateProxy(ThirdParty.GOOGLE, true);
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
				// deactivateProxy(ThirdParty.FACEBOOK, false);

				mFacebookProxy = new FacebookProxy(this, FACEBOOK_READ_PERMISSIONS, null, mFacebookCallbacks);
				mThirdPartyProxySet.add(mFacebookProxy);
				mFacebookProxy.activate(true, mFacebookCallbacks);
				break;
			}
			case R.id.signin_authenticate_google_button: {
				if (CommonUtils.notifyIfNoNetwork(this)) {
					return;
				}

				// Probably not needed
				// deactivateProxy(ThirdParty.GOOGLE, false);

				mGoogleProxy = new GoogleProxy(this, mGoogleCallbacks);
				mThirdPartyProxySet.add(mGoogleProxy);
				mGoogleProxy.activate(true, mGoogleCallbacks);
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

	private void deactivateProxy(ThirdParty thirdParty, boolean clearToken) {
		switch (thirdParty) {
			case FACEBOOK:
				if (mFacebookProxy != null) {
					mThirdPartyProxySet.remove(mFacebookProxy);
					mFacebookProxy.deactivate(clearToken);
					mFacebookProxy = null;
				}
				break;
			case GOOGLE:
				if (mGoogleProxy != null) {
					mThirdPartyProxySet.remove(mGoogleProxy);
					mGoogleProxy.deactivate(clearToken);
					mGoogleProxy = null;
				}
				break;
		}
	}

	private void processThirdPartyUser(final ThirdPartyUser user) {
		UserRequest loginRequest = UserRequest.newLoginRequest(AuthenticateActivity.this, user.getThirdParty(),
				user.getId(), user.getToken(), new Response.Listener<User>() {
					@Override
					public void onResponse(User response) {
						// We successfully logged in using a third party
						SessionManager.getInstance(AuthenticateActivity.this).setCurrentUser(response);
						SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(AuthenticateActivity.this);
						SharedPrefUtils.putString(sp.edit(), user.getThirdParty().getPreference(),
								user.getToken()).apply();
						wrapUp();
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						// There is no Citymaps user linked to the third party account. Take them to the Create Account screen
						Intent intent = new Intent(AuthenticateActivity.this, SigninActivity.class);
						IntentUtils.putLoginMode(intent, SigninActivity.CREATE_ACCOUNT_MODE);
						IntentUtils.putThirdPartyUser(intent, user);
						AuthenticateActivity.this.startActivityForResult(intent, REQUEST_CODE_CREATE_ACCOUNT);
					}
				});
		VolleyManager.getInstance(AuthenticateActivity.this).getRequestQueue().add(loginRequest);
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
			proxy.requestData(Arrays.asList(DATA_TOKEN, DATA_ME), mFacebookOnDataListener);
			// mFacebookProxy will be deactivated and nulled if/when user cancels out of CreateAccount screen
		}

		@Override
		public void onDisconnected(FacebookProxy proxy, Session session, SessionState state, Exception exception) {
			if (LogEx.isLoggable(LogEx.VERBOSE)) {
				LogEx.v(String.format("proxy=%s, session=%s, state=%s, exception=%s", proxy, session, state, exception));
			}
			// If we get to this point, just deactivate & remove the proxy as if it was never activated
			deactivateProxy(ThirdParty.FACEBOOK, true);
		}

		@Override
		public boolean onFailed(FacebookProxy proxy, boolean cancelled, Session session, SessionState state, Exception exception) {
			if (LogEx.isLoggable(LogEx.VERBOSE)) {
				LogEx.v(String.format("proxy=%s, cancelled=%b, session=%s, state=%s, exception=%s", proxy, cancelled, session, state, exception));
			}
			// If we get to this point, just deactivate & remove the proxy as if it was never activated
			deactivateProxy(ThirdParty.FACEBOOK, true);
			return true;
		}
	};

	private GoogleProxy.Callbacks mGoogleCallbacks = new GoogleProxy.SimpleCallbacks() {
		@Override
		public void onPreBuild(@NonNull GoogleApiClient.Builder builder) {
			builder.addApi(Plus.API)
					.addScope(Plus.SCOPE_PLUS_LOGIN);
		}

		@Override
		public void onConnected(GoogleProxy proxy, Bundle connectionHint) {
			super.onConnected(proxy, connectionHint);
			if (LogEx.isLoggable(LogEx.VERBOSE)) {
				LogEx.v(String.format("proxy=%s, connectionHint=%s", proxy, connectionHint));
			}
			// If we get to this point, we have a Facebook session. We need to get the current Facebook user and
			// see if their Facebook id is registered with Citymaps. If it is we can log them in and continue. If not
			// we need to pass along their Facebook info to LoginActivity.
			proxy.requestData(Arrays.asList(DATA_TOKEN, DATA_CURRENT_PERSON, DATA_ACCOUNT_NAME), mGoogleOnDataListener);
			// mGoogleProxy will be deactivated and nulled if/when user cancels out of CreateAccount screen
		}

		@Override
		public void onDisconnected(GoogleProxy proxy) {
			if (LogEx.isLoggable(LogEx.VERBOSE)) {
				LogEx.v(String.format("proxy=%s", proxy));
			}
			// If we get to this point, just deactivate & remove the proxy as if it was never activated
			deactivateProxy(ThirdParty.GOOGLE, true);
		}

		@Override
		public boolean onFailed(GoogleProxy proxy, boolean cancelled, ConnectionResult result) {
			if (LogEx.isLoggable(LogEx.VERBOSE)) {
				LogEx.v(String.format("proxy=%s, cancelled=%b, result=%s", proxy, cancelled, result));
			}
			// If we get to this point, just deactivate & remove the proxy as if it was never activated
			deactivateProxy(ThirdParty.GOOGLE, true);
			return true;
		}
	};

	private ThirdPartyProxy.OnDataListener mFacebookOnDataListener = new ThirdPartyProxy.OnDataListener() {
		@Override
		public void onData(ThirdPartyProxy proxy, Map<String, Object> data) {
			String token = (String) data.get(DATA_TOKEN);
			GraphUser graphUser = (GraphUser) data.get(DATA_ME);
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
			String token = (String) data.get(DATA_TOKEN);
			String accountName = (String) data.get(DATA_ACCOUNT_NAME);
			Person currentPerson = (Person) data.get(DATA_CURRENT_PERSON);
			processThirdPartyUser(new ThirdPartyUser(token, currentPerson, accountName));
		}

		@Override
		public void onError(ThirdPartyProxy proxy, Map<String, Object> errors) {
			// TODO Error handling
		}
	};
}