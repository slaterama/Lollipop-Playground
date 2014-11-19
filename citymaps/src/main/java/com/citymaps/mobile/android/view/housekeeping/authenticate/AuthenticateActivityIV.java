package com.citymaps.mobile.android.view.housekeeping.authenticate;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Toast;
import com.citymaps.mobile.android.R;
import com.citymaps.mobile.android.app.TrackedActionBarActivity;
import com.citymaps.mobile.android.thirdpartynew.FacebookProxy;
import com.citymaps.mobile.android.thirdpartynew.GoogleProxy;
import com.citymaps.mobile.android.thirdpartynew.GoogleProxy.OnPreBuildListener;
import com.citymaps.mobile.android.thirdpartynew.ThirdPartyProxy;
import com.citymaps.mobile.android.util.IntentUtils;
import com.citymaps.mobile.android.util.LogEx;
import com.citymaps.mobile.android.view.MainActivity;
import com.citymaps.mobile.android.view.housekeeping.LoginActivity;
import com.facebook.Session;
import com.facebook.SessionState;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/*
 * TODO
 * I might want to introduce a way to only have callbacks be active once "connect" has been
 * called. How to accomplish this? For one, GoogleProxy would need to be passed the onSaveInstanceState
 * callback. We can always set listeners (as we do now), but maintain an "active" flag
 * that is set on connect() and cleared on cancel(). THAT boolean is maintained by the proxies.
 */

public class AuthenticateActivityIV extends TrackedActionBarActivity {

	private static final List<String> FACEBOOK_READ_PERMISSIONS = Arrays.asList("public_profile", "email");

	private static final int REQUEST_CODE_LOGIN = 1;
	private static final int REQUEST_CODE_CREATE_ACCOUNT = 2;

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

		// Create third party proxies
		mGoogleProxy = new GoogleProxy(this, new OnPreBuildListener() {
			@Override
			public void onPreBuild(@NonNull GoogleApiClient.Builder builder) {
				builder.addApi(Plus.API);
				builder.addScope(Plus.SCOPE_PLUS_LOGIN);
			}
		}, mGoogleProxyCallbacks);
		mFacebookProxy = new FacebookProxy(this, FACEBOOK_READ_PERMISSIONS, mFacebookProxyCallbacks);
		mThirdPartyProxies = new HashSet<ThirdPartyProxy>(2);
		mThirdPartyProxies.add(mFacebookProxy);
		mThirdPartyProxies.add(mGoogleProxy);
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
					// TODO What to do here? I think ThirdParty needs a "cancel" method?
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
				mFacebookProxy.connect(true);
				break;
			case R.id.login_authenticate_google_button: {
				if (mConnectivityManager.getActiveNetworkInfo() == null) {
					Toast.makeText(this, R.string.error_message_no_connection, Toast.LENGTH_SHORT).show();
					return;
				}
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

	/* GoogleProxy callbacks */

	private GoogleProxy.ProxyCallbacks mGoogleProxyCallbacks = new GoogleProxy.ProxyCallbacks() {
		@Override
		public void onConnecting(GoogleProxy proxy) {
			if (LogEx.isLoggable(LogEx.INFO) && proxy.isActive()) {
				LogEx.i(String.format("proxy=%s", proxy));
			}
		}

		@Override
		public void onConnected(GoogleProxy proxy, Bundle connectionHint) {
			if (LogEx.isLoggable(LogEx.INFO) && proxy.isActive()) {
				LogEx.i(String.format("proxy=%s, connectionHint=%s", proxy, connectionHint));
			}
		}

		@Override
		public void onError(GoogleProxy proxy, ConnectionResult result) {
			if (LogEx.isLoggable(LogEx.INFO) && proxy.isActive()) {
				LogEx.i(String.format("proxy=%s, result=%s", proxy, result));
			}
		}

		@Override
		public void onDisconnected(GoogleProxy proxy) {
			if (LogEx.isLoggable(LogEx.INFO) && proxy.isActive()) {
				LogEx.i(String.format("proxy=%s", proxy));
			}
		}
	};

	/* FacebookProxy callbacks */

	private FacebookProxy.ProxyCallbacks mFacebookProxyCallbacks = new FacebookProxy.ProxyCallbacks() {
		@Override
		public void onConnecting(FacebookProxy proxy, Session session, SessionState state) {
			if (LogEx.isLoggable(LogEx.INFO) && proxy.isActive()) {
				LogEx.i(String.format("proxy=%s, session=%s, state=%s", proxy, session, state));
			}
		}

		@Override
		public void onConnected(FacebookProxy proxy, Session session, SessionState state) {
			if (LogEx.isLoggable(LogEx.INFO) && proxy.isActive()) {
				LogEx.i(String.format("proxy=%s, session=%s, state=%s", proxy, session, state));
			}
		}

		@Override
		public void onError(FacebookProxy proxy, Session session, SessionState state, Exception exception) {
			if (LogEx.isLoggable(LogEx.INFO) && proxy.isActive()) {
				LogEx.i(String.format("proxy=%s, session=%s, state=%s, exception=%s", proxy, session, state, exception));
			}
		}

		@Override
		public void onDisconnected(FacebookProxy proxy, Session session, SessionState state) {
			if (LogEx.isLoggable(LogEx.INFO) && proxy.isActive()) {
				LogEx.i(String.format("proxy=%s, session=%s, state=%s", proxy, session, state));
			}
		}
	};
}