package com.citymaps.mobile.android.thirdparty2;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import com.citymaps.mobile.android.R;
import com.citymaps.mobile.android.app.TrackedActionBarActivity;
import com.citymaps.mobile.android.util.CommonUtils;
import com.citymaps.mobile.android.util.IntentUtils;
import com.citymaps.mobile.android.util.LogEx;
import com.citymaps.mobile.android.view.MainActivity;
import com.citymaps.mobile.android.view.housekeeping.SigninActivity;
import com.facebook.Session;
import com.facebook.SessionState;
import com.google.android.gms.common.ConnectionResult;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

	private ConnectivityManager mConnectivityManager;

	private FacebookProxy mFacebookProxy;
	private GoogleProxy mGoogleProxy;
	private Set<ThirdPartyProxy> mThirdPartyProxies;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_authenticate);
		mStartupMode = IntentUtils.isStartupMode(getIntent(), false);

		mConnectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

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
				if (resultCode != RESULT_OK) {
					// TODO Only toggle the appropriate one
				}
				break;
			case REQUEST_CODE_CREATE_ACCOUNT_WITH_FACEBOOK:
				if (resultCode != RESULT_OK) {
					mFacebookProxy.stop(false);
				}
				break;
			case REQUEST_CODE_CREATE_ACCOUNT_WITH_GOOGLE:
				if (resultCode != RESULT_OK) {
					mGoogleProxy.stop(false);
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

	private FacebookProxy.Callbacks mFacebookCallbacks = new FacebookProxy.Callbacks() {
		@Override
		public void onConnecting(FacebookProxy proxy, Session session, SessionState state, Exception exception) {
			if (LogEx.isLoggable(LogEx.INFO)) {
				LogEx.i(String.format("proxy=%s, session=%s, state=%s, exception=%s",
						proxy, session, state, exception));
			}
		}

		@Override
		public void onConnected(FacebookProxy proxy, Session session, SessionState state, Exception exception) {
			if (LogEx.isLoggable(LogEx.INFO)) {
				LogEx.i(String.format("proxy=%s, session=%s, state=%s, exception=%s",
						proxy, session, state, exception));
			}
		}

		@Override
		public void onDisconnected(FacebookProxy proxy, Session session, SessionState state, Exception exception) {
			if (LogEx.isLoggable(LogEx.INFO)) {
				LogEx.i(String.format("proxy=%s, session=%s, state=%s, exception=%s",
						proxy, session, state, exception));
			}
		}

		@Override
		public void onError(FacebookProxy proxy, Session session, SessionState state, Exception exception) {
			if (LogEx.isLoggable(LogEx.INFO)) {
				LogEx.i(String.format("proxy=%s, session=%s, state=%s, exception=%s",
						proxy, session, state, exception));
			}
		}
	};

	private GoogleProxy.Callbacks mGoogleCallbacks = new GoogleProxy.Callbacks() {
		@Override
		public void onConnecting(GoogleProxy proxy) {
			if (LogEx.isLoggable(LogEx.INFO)) {
				LogEx.i(String.format("proxy=%s", proxy));
			}
		}

		@Override
		public void onConnected(GoogleProxy proxy, Bundle connectionHint) {
			if (LogEx.isLoggable(LogEx.INFO)) {
				LogEx.i(String.format("proxy=%s, connectionHint=%s", proxy, connectionHint));
			}
		}

		@Override
		public void onDisconnected(GoogleProxy proxy) {
			if (LogEx.isLoggable(LogEx.INFO)) {
				LogEx.i(String.format("proxy=%s", proxy));
			}
		}

		@Override
		public void onError(GoogleProxy proxy, ConnectionResult result) {
			if (LogEx.isLoggable(LogEx.INFO)) {
				LogEx.i(String.format("proxy=%s, result=%s", proxy, result));
			}
		}
	};
}