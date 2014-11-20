package com.citymaps.mobile.android.view.housekeeping;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Toast;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.citymaps.mobile.android.R;
import com.citymaps.mobile.android.app.SessionManager;
import com.citymaps.mobile.android.app.TrackedActionBarActivity;
import com.citymaps.mobile.android.app.VolleyManager;
import com.citymaps.mobile.android.model.ThirdPartyUser;
import com.citymaps.mobile.android.model.User;
import com.citymaps.mobile.android.model.volley.UserRequest;
import com.citymaps.mobile.android.thirdparty.FacebookProxy;
import com.citymaps.mobile.android.thirdparty.GoogleProxy;
import com.citymaps.mobile.android.thirdparty.GoogleProxy.OnPreBuildListener;
import com.citymaps.mobile.android.thirdparty.ThirdPartyProxy;
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

public class AuthenticateActivity extends TrackedActionBarActivity {

	private static final String STATE_KEY_MONITORING_FACEBOOK = "monitoringFacbook";
	private static final String STATE_KEY_MONITORING_GOOGLE = "monitoringGoogle";

	private static final List<String> FACEBOOK_READ_PERMISSIONS = Arrays.asList("public_profile", "email");

	private static final int REQUEST_CODE_LOGIN = 1;
	private static final int REQUEST_CODE_CREATE_ACCOUNT = 2;

	private boolean mStartupMode;

	private ConnectivityManager mConnectivityManager;

	private FacebookProxy mFacebookProxy;
	private GoogleProxy mGoogleProxy;
	private Set<ThirdPartyProxy> mThirdPartyProxies;

	private boolean mMonitoringFacebook;
	private boolean mMonitoringGoogle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_authenticate);
		mStartupMode = IntentUtils.isStartupMode(getIntent(), false);

		mConnectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

		// Create third party proxies
		mFacebookProxy = new FacebookProxy(this, FACEBOOK_READ_PERMISSIONS);
		mGoogleProxy = new GoogleProxy(this, new OnPreBuildListener() {
			@Override
			public void onPreBuild(@NonNull GoogleApiClient.Builder builder) {
				builder.addApi(Plus.API);
				builder.addScope(Plus.SCOPE_PLUS_LOGIN);
			}
		});
		mFacebookProxy.setCallbacks(mFacebookProxyCallbacks);
		mGoogleProxy.setCallbacks(mGoogleProxyCallbacks);

		mThirdPartyProxies = new HashSet<ThirdPartyProxy>(2);
		mThirdPartyProxies.add(mFacebookProxy);
		mThirdPartyProxies.add(mGoogleProxy);

		if (savedInstanceState != null) {
			mMonitoringFacebook = savedInstanceState.getBoolean(STATE_KEY_MONITORING_FACEBOOK, false);
			mMonitoringGoogle = savedInstanceState.getBoolean(STATE_KEY_MONITORING_GOOGLE, false);
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
		outState.putBoolean(STATE_KEY_MONITORING_FACEBOOK, mMonitoringFacebook);
		outState.putBoolean(STATE_KEY_MONITORING_GOOGLE, mMonitoringGoogle);
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
				if (resultCode != RESULT_OK) {
					// TODO Only toggle the appropriate one
					mMonitoringFacebook = false;
					mMonitoringGoogle = false;
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
			case R.id.login_authenticate_facebook_button:
				if (mConnectivityManager.getActiveNetworkInfo() == null) {
					Toast.makeText(this, R.string.error_message_no_connection, Toast.LENGTH_SHORT).show();
					return;
				}
				mMonitoringFacebook = true;
				mFacebookProxy.connect(true);
				break;
			case R.id.login_authenticate_google_button: {
				if (mConnectivityManager.getActiveNetworkInfo() == null) {
					Toast.makeText(this, R.string.error_message_no_connection, Toast.LENGTH_SHORT).show();
					return;
				}
				mMonitoringGoogle = true;
				mGoogleProxy.connect(true);
				break;
			}
			case R.id.login_authenticate_create_account_button: {
				Intent intent = new Intent(this, LoginActivity.class);
				IntentUtils.putLoginMode(intent, LoginActivity.CREATE_ACCOUNT_MODE);
				startActivityForResult(intent, REQUEST_CODE_CREATE_ACCOUNT);
				break;
			}
			case R.id.login_authenticate_signin_button: {
				Intent intent = new Intent(this, LoginActivity.class);
				IntentUtils.putLoginMode(intent, LoginActivity.SIGN_IN_MODE);
				startActivityForResult(intent, REQUEST_CODE_LOGIN);
				break;
			}
			case R.id.login_authenticate_skip_button: {
				wrapUp();
				break;
			}
		}
	}

	private void wrapUp() {
		if (mStartupMode) {
			startActivity(new Intent(this, MainActivity.class));
		}
		finish();
	}

	/* FacebookProxy callbacks */

	private FacebookProxy.Callbacks mFacebookProxyCallbacks = new FacebookProxy.SimpleCallbacks() {
		@Override
		public void onConnected(FacebookProxy proxy, Session session, SessionState state) {
			if (mMonitoringFacebook) {
				proxy.requestData(session, Arrays.asList(DATA_TOKEN, DATA_ME), mOnDataListener);
			}
		}

		@Override
		public boolean onError(FacebookProxy proxy, Session session, SessionState state, Exception exception) {
			// TODO examine mMonitoringFacebook?
			return false;
		}
	};

	/* GoogleProxy callbacks */

	private GoogleProxy.Callbacks mGoogleProxyCallbacks = new GoogleProxy.SimpleCallbacks() {
		@Override
		public void onConnected(GoogleProxy proxy, Bundle connectionHint) {
			if (mMonitoringGoogle) {
				proxy.requestData(proxy.getGoogleApiClient(),
						Arrays.asList(DATA_TOKEN, DATA_ACCOUNT_NAME, DATA_CURRENT_PERSON), mOnDataListener);
			}
		}

		@Override
		public boolean onError(GoogleProxy proxy, ConnectionResult result) {
			// TODO examine mMonitoringGoogle?
			return false;
		}
	};

	/* Proxy request data callbacks */

	private ThirdPartyProxy.OnDataListener mOnDataListener = new ThirdPartyProxy.OnDataListener() {
		@Override
		public void onError(ThirdPartyProxy proxy, Map<String, Object> errors) {

		}

		@Override
		public void onData(ThirdPartyProxy proxy, Map<String, Object> data) {
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
								Intent intent = new Intent(AuthenticateActivity.this, LoginActivity.class);
								IntentUtils.putLoginMode(intent, LoginActivity.CREATE_ACCOUNT_MODE);
								IntentUtils.putThirdPartyUser(intent, thirdPartyUser);
								AuthenticateActivity.this.startActivityForResult(intent, REQUEST_CODE_CREATE_ACCOUNT);
							}
						});
				VolleyManager.getInstance(AuthenticateActivity.this).getRequestQueue().add(loginRequest);
			}
		}
	};
}