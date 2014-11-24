package com.citymaps.mobile.android.view.housekeeping;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.citymaps.mobile.android.R;
import com.citymaps.mobile.android.app.SessionManager;
import com.citymaps.mobile.android.app.TrackedActionBarActivity;
import com.citymaps.mobile.android.app.VolleyManager;
import com.citymaps.mobile.android.model.ThirdPartyUser;
import com.citymaps.mobile.android.model.User;
import com.citymaps.mobile.android.model.request.UserRequest;
import com.citymaps.mobile.android.thirdparty.FacebookProxy;
import com.citymaps.mobile.android.thirdparty.GoogleProxy;
import com.citymaps.mobile.android.thirdparty.ThirdPartyProxy;
import com.citymaps.mobile.android.util.CommonUtils;
import com.citymaps.mobile.android.util.IntentUtils;
import com.citymaps.mobile.android.view.MainActivity;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import java.util.*;

import static com.citymaps.mobile.android.thirdparty.FacebookProxy.DATA_ME;
import static com.citymaps.mobile.android.thirdparty.GoogleProxy.DATA_ACCOUNT_NAME;
import static com.citymaps.mobile.android.thirdparty.GoogleProxy.DATA_CURRENT_PERSON;
import static com.citymaps.mobile.android.thirdparty.ThirdPartyProxy.DATA_TOKEN;

// TODO Where/when to call "stop()" on proxies?

public class AuthenticateActivity extends TrackedActionBarActivity {

	private static final String STATE_KEY_FACEBOOK_INVOKED = "facebookInvoked";
	private static final String STATE_KEY_GOOGLE_INVOKED = "googleInvoked";

	private static final List<String> FACEBOOK_READ_PERMISSIONS = Arrays.asList("public_profile", "email");

	private static final int REQUEST_CODE_LOGIN = 1;
	private static final int REQUEST_CODE_CREATE_ACCOUNT = 2;
	private static final int REQUEST_CODE_CREATE_ACCOUNT_WITH_FACEBOOK = 3;
	private static final int REQUEST_CODE_CREATE_ACCOUNT_WITH_GOOGLE = 4;

	private boolean mStartupMode;

	private FacebookProxy mFacebookProxy;
	private GoogleProxy mGoogleProxy;
	private Set<ThirdPartyProxy> mThirdPartyProxies;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_authenticate);
		mStartupMode = IntentUtils.isStartupMode(getIntent(), false);

		mThirdPartyProxies = new HashSet<ThirdPartyProxy>(2);
		if (savedInstanceState != null) {
			if (savedInstanceState.getBoolean(STATE_KEY_FACEBOOK_INVOKED)) {
				mFacebookProxy = newFacebookProxy();
				mThirdPartyProxies.add(mFacebookProxy);
			}
			if (savedInstanceState.getBoolean(STATE_KEY_GOOGLE_INVOKED)) {
				mGoogleProxy = newGoogleProxy();
				mThirdPartyProxies.add(mGoogleProxy);
			}
		}
		for (ThirdPartyProxy proxy : mThirdPartyProxies) {
			proxy.onCreate(savedInstanceState);
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		for (ThirdPartyProxy proxy : mThirdPartyProxies) {
			proxy.onStart();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		for (ThirdPartyProxy proxy : mThirdPartyProxies) {
			proxy.onResume();
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		for (ThirdPartyProxy proxy : mThirdPartyProxies) {
			proxy.onSaveInstanceState(outState);
		}
		outState.putBoolean(STATE_KEY_FACEBOOK_INVOKED, mFacebookProxy != null);
		outState.putBoolean(STATE_KEY_GOOGLE_INVOKED, mGoogleProxy != null);
	}

	@Override
	protected void onPause() {
		super.onPause();
		for (ThirdPartyProxy proxy : mThirdPartyProxies) {
			proxy.onPause();
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		for (ThirdPartyProxy proxy : mThirdPartyProxies) {
			proxy.onStop();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		for (ThirdPartyProxy proxy : mThirdPartyProxies) {
			proxy.onDestroy();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case REQUEST_CODE_CREATE_ACCOUNT:
				if (resultCode == RESULT_OK) {
					wrapUp();
				}
				break;
			case REQUEST_CODE_CREATE_ACCOUNT_WITH_FACEBOOK:
				if (resultCode == RESULT_OK) {
					wrapUp();
				} else {
					mThirdPartyProxies.remove(mFacebookProxy);
					mFacebookProxy.stop(false);
					mFacebookProxy = null;
				}
				break;
			case REQUEST_CODE_CREATE_ACCOUNT_WITH_GOOGLE:
				if (resultCode == RESULT_OK) {
					wrapUp();
				} else {
					mThirdPartyProxies.remove(mGoogleProxy);
					mGoogleProxy.stop(false);
					mGoogleProxy = null;
				}
			case REQUEST_CODE_LOGIN:
				if (resultCode == RESULT_OK) {
					wrapUp();
				}
				break;
			default:
				super.onActivityResult(requestCode, resultCode, data);
				for (ThirdPartyProxy proxy : mThirdPartyProxies) {
					proxy.onActivityResult(requestCode, resultCode, data);
				}
		}
	}

	public void onButtonClick(View view) {
		int id = view.getId();
		switch (id) {
			case R.id.signin_authenticate_facebook_button:
				if (CommonUtils.notifyIfNoNetwork(this)) {
					return;
				}
				if (mFacebookProxy == null) {
					mFacebookProxy = newFacebookProxy();
					mThirdPartyProxies.add(mFacebookProxy);
				}
				mFacebookProxy.start(true, mFacebookCallbacks);
				break;
			case R.id.signin_authenticate_google_button: {
				if (CommonUtils.notifyIfNoNetwork(this)) {
					return;
				}
				if (mGoogleProxy == null) {
					mGoogleProxy = newGoogleProxy();
					mThirdPartyProxies.add(mGoogleProxy);
				}
				mGoogleProxy.start(true, mGoogleCallbacks);
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

	private FacebookProxy newFacebookProxy() {
		return new FacebookProxy(this, FACEBOOK_READ_PERMISSIONS, null, mFacebookCallbacks);
	}

	private GoogleProxy newGoogleProxy() {
		return new GoogleProxy(this, mGoogleCallbacks);
	}

	private void wrapUp() {
		if (mStartupMode) {
			startActivity(new Intent(this, MainActivity.class));
		}
		finish();
	}

	/* FacebookProxy callbacks */

	private FacebookProxy.Callbacks mFacebookCallbacks = new FacebookProxy.SimpleCallbacks() {
		@Override
		public void onConnected(FacebookProxy proxy, Session session, SessionState state, Exception exception) {
			proxy.requestData(Arrays.asList(DATA_TOKEN, DATA_ME), mOnDataListener);
		}

		@Override
		public void onDisconnected(FacebookProxy proxy, Session session, SessionState state, Exception exception) {
			mThirdPartyProxies.remove(mFacebookProxy);
			mFacebookProxy.stop(true); // TODO clear? Or not?
			mFacebookProxy = null;
		}

		@Override
		public boolean onFailed(FacebookProxy proxy, boolean cancelled, Session session, SessionState state, Exception exception) {
			// TODO Error handling

			mThirdPartyProxies.remove(mFacebookProxy);
			mFacebookProxy.stop(true); // TODO clear? Or not?
			mFacebookProxy = null;
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
			proxy.requestData(Arrays.asList(DATA_TOKEN, DATA_ACCOUNT_NAME, DATA_CURRENT_PERSON), mOnDataListener);
		}

		@Override
		public void onDisconnected(GoogleProxy proxy) {
			mThirdPartyProxies.remove(mGoogleProxy);
			mGoogleProxy.stop(true); // TODO clear? Or not?
			mGoogleProxy = null;
		}

		@Override
		public boolean onFailed(GoogleProxy proxy, boolean cancelled, ConnectionResult result) {
			// TODO Error handling

			mThirdPartyProxies.remove(mGoogleProxy);
			mGoogleProxy.stop(true); // TODO clear? Or not?
			mGoogleProxy = null;
			return false;
		}
	};

	private ThirdPartyProxy.OnDataListener mOnDataListener = new ThirdPartyProxy.OnDataListener() {
		@Override
		public void onData(final ThirdPartyProxy proxy, Map<String, Object> data) {
			final ThirdPartyUser thirdPartyUser;
			if (proxy == mFacebookProxy) {
				String token = (String) data.get(DATA_TOKEN);
				GraphUser graphUser = (GraphUser) data.get(DATA_ME);
				thirdPartyUser = new ThirdPartyUser(token, graphUser);
			} else if (proxy == mGoogleProxy) {
				String token = (String) data.get(DATA_TOKEN);
				Person person = (Person) data.get(DATA_CURRENT_PERSON);
				String accountName = (String) data.get(DATA_ACCOUNT_NAME);
				thirdPartyUser = new ThirdPartyUser(token, person, accountName);
			} else {
				thirdPartyUser = null;
			}

			// Attempt to log in to Citymaps using the third party user info
			if (thirdPartyUser != null) {
				UserRequest loginRequest = UserRequest.newLoginRequest(AuthenticateActivity.this, thirdPartyUser.getThirdParty(),
						thirdPartyUser.getId(), thirdPartyUser.getToken(), new Response.Listener<User>() {
							@Override
							public void onResponse(User response) {
								SessionManager.getInstance(AuthenticateActivity.this).setCurrentUser(response);
								wrapUp();
							}
						}, new Response.ErrorListener() {
							@Override
							public void onErrorResponse(VolleyError error) {
								// There is no Citymaps user linked to the third party account. Take them to the Create Account screen
								int requestCode;
								if (proxy == mFacebookProxy) {
									requestCode = REQUEST_CODE_CREATE_ACCOUNT_WITH_FACEBOOK;
								} else if (proxy == mGoogleProxy) {
									requestCode = REQUEST_CODE_CREATE_ACCOUNT_WITH_GOOGLE;
								} else {
									return;
								}
								Intent intent = new Intent(AuthenticateActivity.this, SigninActivity.class);
								IntentUtils.putLoginMode(intent, SigninActivity.CREATE_ACCOUNT_MODE);
								IntentUtils.putThirdPartyUser(intent, thirdPartyUser);
								AuthenticateActivity.this.startActivityForResult(intent, requestCode);
							}
						});
				VolleyManager.getInstance(AuthenticateActivity.this).getRequestQueue().add(loginRequest);
			}
		}

		@Override
		public void onError(ThirdPartyProxy proxy, Map<String, Object> errors) {
			// TODO Error handling
		}
	};
}