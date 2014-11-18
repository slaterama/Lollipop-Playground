package com.citymaps.mobile.android.view.housekeeping.authenticate;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.Toast;
import com.citymaps.mobile.android.R;
import com.citymaps.mobile.android.app.TrackedActionBarActivity;
import com.citymaps.mobile.android.model.ThirdPartyUser;
import com.citymaps.mobile.android.thirdparty.FacebookProxy;
import com.citymaps.mobile.android.thirdparty.GoogleProxy;
import com.citymaps.mobile.android.thirdparty.ThirdPartyProxy;
import com.citymaps.mobile.android.util.IntentUtils;
import com.citymaps.mobile.android.util.LogEx;
import com.citymaps.mobile.android.view.MainActivity;
import com.citymaps.mobile.android.view.housekeeping.LoginActivity;
import com.citymaps.mobile.android.view.housekeeping.LoginErrorDialogFragment;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class AuthenticateActivity extends TrackedActionBarActivity {

	private static final int REQUEST_CODE_LOGIN = 1001;
	private static final int REQUEST_CODE_CREATE_ACCOUNT = 1002;

	private static final List<String> FACEBOOK_READ_PERMISSIONS = Arrays.asList("public_profile", "email");
	private static final List<Scope> GOOGLE_SCOPES = Arrays.asList(Plus.SCOPE_PLUS_LOGIN, Plus.SCOPE_PLUS_PROFILE);

	private static final String GOOGLE_ACCOUNT_NAME_SCOPE = String.format("oauth2:%s", Scopes.PLUS_LOGIN);

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

	private void handleThirdPartyUser(ThirdPartyUser user) {
		// TODO
		LogEx.i(String.format("thirdPartyUser=%s", user));
	}

	private void handleError(String message) {
		FragmentManager manager = getSupportFragmentManager();
		if (manager.findFragmentByTag(LoginErrorDialogFragment.FRAGMENT_TAG) == null) {
			LoginErrorDialogFragment fragment =
					LoginErrorDialogFragment.newInstance(getTitle(), message);
			fragment.show(manager, LoginErrorDialogFragment.FRAGMENT_TAG);
		}
	}

	private FacebookProxy.Callbacks mFacebookCallbacks = new FacebookProxy.Callbacks() {
		@Override
		public void onSessionStateChange(ThirdPartyProxy proxy, Session session, SessionState state) {
			if (LogEx.isLoggable(LogEx.INFO)) {
				LogEx.i(String.format("session=%s, state=%s", session, state));
			}

			if (session.isOpened()) {
				final String token = session.getAccessToken();
				Request.newMeRequest(session, new Request.GraphUserCallback() {
					@Override
					public void onCompleted(GraphUser user, Response response) {
						if (user != null) {
							ThirdPartyUser thirdPartyUser = new ThirdPartyUser(token, user);
							handleThirdPartyUser(thirdPartyUser);
						} else if (response != null && response.getError() != null) {
							// Error. Display a dialog fragment.
							handleError(response.getError().getErrorUserMessage());
						}
					}
				}).executeAsync();
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

			new AsyncTask<Void, Void, ThirdPartyUser>() {
				@Override
				protected ThirdPartyUser doInBackground(Void... params) {
					ThirdPartyUser user = null;

					try {
						GoogleApiClient googleApiClient = mGoogleProxy.getGoogleApiClient();
						Person person = Plus.PeopleApi.getCurrentPerson(googleApiClient);
						String accountName = Plus.AccountApi.getAccountName(googleApiClient);
						String token = GoogleAuthUtil.getToken(AuthenticateActivity.this, accountName, GOOGLE_ACCOUNT_NAME_SCOPE);
						user = new ThirdPartyUser(token, person, accountName);
					} catch (UserRecoverableAuthException e) {
						AuthenticateActivity.this.startActivity(e.getIntent()); // No "startActivityForResult" at the moment. User will have to re-try
					} catch (Exception e) {
						handleError(e.getLocalizedMessage());
					}

					return user;
				}

				@Override
				protected void onPostExecute(ThirdPartyUser result) {
					if (result != null) {
						handleThirdPartyUser(result);
					}
				}
			}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}

		@Override
		public void onUnresolvedError(ThirdPartyProxy proxy, ConnectionResult result) {
			if (LogEx.isLoggable(LogEx.INFO)) {
				LogEx.i(String.format("result=%s", result));
			}

			int errorCode = result.getErrorCode();
			// TODO
		}
	};
}