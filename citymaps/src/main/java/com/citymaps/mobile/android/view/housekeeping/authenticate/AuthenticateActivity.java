package com.citymaps.mobile.android.view.housekeeping.authenticate;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.citymaps.mobile.android.R;
import com.citymaps.mobile.android.app.TrackedActionBarActivity;
import com.citymaps.mobile.android.thirdparty.FacebookProxy;
import com.citymaps.mobile.android.thirdparty.GoogleProxy;
import com.citymaps.mobile.android.thirdparty.ThirdPartyProxy;
import com.citymaps.mobile.android.util.IntentUtils;
import com.citymaps.mobile.android.util.LogEx;
import com.citymaps.mobile.android.view.MainActivity;
import com.citymaps.mobile.android.view.housekeeping.LoginActivity;
import com.facebook.Session;
import com.facebook.SessionState;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class AuthenticateActivity extends TrackedActionBarActivity {

	private static final int REQUEST_CODE_LOGIN = 1001;
	private static final int REQUEST_CODE_CREATE_ACCOUNT = 1002;

	private static final List<String> FACEBOOK_READ_PERMISSIONS = Arrays.asList("public_profile", "email");
	private static final List<Scope> GOOGLE_SCOPES = Arrays.asList(Plus.SCOPE_PLUS_LOGIN);

	private boolean mStartupMode;

	private ConnectivityManager mConnectivityManager;

	private Set<ThirdPartyProxy> mThirdPartyProxies;
	private FacebookProxy mFacebookProxy;
	private GoogleProxy mGoogleProxy;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_authenticate);
		mStartupMode = IntentUtils.isStartupMode(getIntent(), false);

		mConnectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

		mThirdPartyProxies = new LinkedHashSet<ThirdPartyProxy>();

		mFacebookProxy = new FacebookProxy(this, FACEBOOK_READ_PERMISSIONS, mFacebookCallbacks);
		mGoogleProxy = new GoogleProxy(this, GOOGLE_SCOPES, mGoogleCallbacks);
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
					mFacebookProxy.closeConnection();
					mGoogleProxy.closeConnection();
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
			case R.id.login_authenticate_facebook_button: {
				if (mConnectivityManager.getActiveNetworkInfo() == null) {
					Toast.makeText(this, R.string.error_message_no_connection, Toast.LENGTH_SHORT).show();
					return;
				}
				mFacebookProxy.openConnection(true);
				break;
			}
			case R.id.login_authenticate_google_button: {
				if (mConnectivityManager.getActiveNetworkInfo() == null) {
					Toast.makeText(this, R.string.error_message_no_connection, Toast.LENGTH_SHORT).show();
					return;
				}
				mGoogleProxy.openConnection(true);
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

	private FacebookProxy.Callbacks mFacebookCallbacks = new FacebookProxy.Callbacks() {
		@Override
		public void onSessionStateChange(ThirdPartyProxy proxy, Session session, SessionState state) {
			if (LogEx.isLoggable(LogEx.INFO)) {
				LogEx.i(String.format("session=%s, state=%s", session, state));
			}
		}

		@Override
		public void onError(ThirdPartyProxy proxy, Session session, SessionState state, Exception exception) {
			if (LogEx.isLoggable(LogEx.INFO)) {
				LogEx.i(String.format("session=%s, state=%s, exception=%s", session, state, exception));
			}
		}
	};

	private GoogleProxy.Callbacks mGoogleCallbacks = new GoogleProxy.Callbacks() {
		@Override
		public void onConnected(ThirdPartyProxy proxy, Bundle connectionHint) {
			if (LogEx.isLoggable(LogEx.INFO)) {
				LogEx.i(String.format("connectionHint=%s", connectionHint));
			}
		}

		@Override
		public void onUnresolvedError(ThirdPartyProxy proxy, ConnectionResult result) {
			if (LogEx.isLoggable(LogEx.INFO)) {
				LogEx.i(String.format("result=%s", result));
			}
		}
	};
}