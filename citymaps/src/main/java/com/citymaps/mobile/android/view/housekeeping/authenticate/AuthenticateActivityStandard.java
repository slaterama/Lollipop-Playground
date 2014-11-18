package com.citymaps.mobile.android.view.housekeeping.authenticate;

import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.citymaps.mobile.android.R;
import com.citymaps.mobile.android.app.TrackedActionBarActivity;
import com.citymaps.mobile.android.util.IntentUtils;
import com.citymaps.mobile.android.util.LogEx;
import com.citymaps.mobile.android.view.MainActivity;
import com.citymaps.mobile.android.view.housekeeping.LoginActivity;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;

import java.util.ArrayList;
import java.util.List;

public class AuthenticateActivityStandard extends TrackedActionBarActivity
		implements Session.StatusCallback,
		GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

	private static final String STATE_KEY_FACEBOOK_SIGN_IN_CLICKED = "facebookSignInClicked";
	private static final String STATE_KEY_GOOGLE_SIGN_IN_CLICKED = "googleSignInClicked";

	private static final int REQUEST_CODE_LOGIN = 1001;
	private static final int REQUEST_CODE_CREATE_ACCOUNT = 1002;

	/* A magic number we will use to know that our sign-in error resolution activity has completed */
	private static final int GOOGLE_REQUEST_CODE = 49404;

	/* Request code used to invoke sign in user interactions. */
	private static final int REQUEST_CODE_GOOGLE_SIGN_IN = GOOGLE_REQUEST_CODE;

	private boolean mStartupMode;

	private ConnectivityManager mConnectivityManager;

	private UiLifecycleHelper mUiLifecycleHelper;

	private SessionState mLastState = null;

	private boolean mFacebookSigninClicked = false;

	private Session mSession;

	/* Client used to interact with Google APIs. */
	private GoogleApiClient mGoogleApiClient;

	/* A flag indicating that a PendingIntent is in progress and prevents
	 * us from starting further intents.
	 */
	private boolean mIntentInProgress;

	/* Track whether the sign-in button has been clicked so that we know to resolve
	 * all issues preventing sign-in without waiting.
	 */
	private boolean mGoogleSignInClicked = false;

	/* Store the connection result from onConnectionFailed callbacks so that we can
	 * resolve them when the user clicks sign-in.
	 */
	private ConnectionResult mConnectionResult;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_authenticate);
		mStartupMode = IntentUtils.isStartupMode(getIntent(), false);

		mConnectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

		if (savedInstanceState != null) {
			mFacebookSigninClicked = savedInstanceState.getBoolean(STATE_KEY_FACEBOOK_SIGN_IN_CLICKED, false);
			mGoogleSignInClicked = savedInstanceState.getBoolean(STATE_KEY_GOOGLE_SIGN_IN_CLICKED, false);
		}

		if (mFacebookSigninClicked) {
			mUiLifecycleHelper = makeUiLifecycleHelper();
			mUiLifecycleHelper.onCreate(savedInstanceState);
		}

		if (mGoogleSignInClicked) {
			mGoogleApiClient = new GoogleApiClient.Builder(this)
					.addConnectionCallbacks(this)
					.addOnConnectionFailedListener(this)
					.addApi(Plus.API)
					.addScope(Plus.SCOPE_PLUS_LOGIN)
					.build();
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (mGoogleApiClient != null) {
			mGoogleApiClient.connect();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (mFacebookSigninClicked) {
			// For scenarios where the main activity is launched and user
			// session is not null, the session state change notification
			// may not be triggered. Trigger it if it's open/closed.
			Session session = Session.getActiveSession();
			if (session != null && (session.isOpened() || session.isClosed()) ) {
				call(session, session.getState(), null);
			}
		}

		if (mUiLifecycleHelper != null) {
			mUiLifecycleHelper.onResume();
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean(STATE_KEY_FACEBOOK_SIGN_IN_CLICKED, mFacebookSigninClicked);
		outState.putBoolean(STATE_KEY_GOOGLE_SIGN_IN_CLICKED, mGoogleSignInClicked);
		if (mUiLifecycleHelper != null) {
			mUiLifecycleHelper.onSaveInstanceState(outState);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (mUiLifecycleHelper != null) {
			mUiLifecycleHelper.onPause();
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
			mGoogleApiClient.disconnect();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mUiLifecycleHelper != null) {
			mUiLifecycleHelper.onDestroy();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case REQUEST_CODE_GOOGLE_SIGN_IN:
				if (resultCode != RESULT_OK) {
					mGoogleSignInClicked = false;
					mConnectionResult = null;
				}

				mIntentInProgress = false;

				if (!mGoogleApiClient.isConnecting()) {
					mGoogleApiClient.connect();
				}
				break;
			case REQUEST_CODE_CREATE_ACCOUNT:
				if (resultCode != RESULT_OK) {
					mUiLifecycleHelper = null;
					mGoogleApiClient = null;
				}
			case REQUEST_CODE_LOGIN:
				if (resultCode == RESULT_OK) {
					wrapUp();
				}
				break;
			default:
				super.onActivityResult(requestCode, resultCode, data);
				if (mUiLifecycleHelper != null) {
					mUiLifecycleHelper.onActivityResult(requestCode, resultCode, data);
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

				if (mUiLifecycleHelper == null) {
					mUiLifecycleHelper = makeUiLifecycleHelper();
				}

				if (mSession == null) {
					mFacebookSigninClicked = true;
					List<String> permissions = new ArrayList<String>();
					mSession = Session.openActiveSession(this, true, permissions, this);
				} else if (mSession.isOpened()) {
					Toast.makeText(this, "Facebook user is connected!", Toast.LENGTH_SHORT).show();
				}

				break;
			case R.id.login_authenticate_google_button: {
				if (mConnectivityManager.getActiveNetworkInfo() == null) {
					Toast.makeText(this, R.string.error_message_no_connection, Toast.LENGTH_SHORT).show();
					return;
				}

				if (mGoogleApiClient == null) {
					mGoogleApiClient = makeGoogleApiClient();
				}

				if (mConnectionResult != null) {
					resolveSignInError();
				} else if (mGoogleApiClient.isConnected()) {
					Toast.makeText(this, "Google user is connected!", Toast.LENGTH_SHORT).show();
				} else if (!mGoogleApiClient.isConnecting()) {
					mGoogleSignInClicked = true;
					mGoogleApiClient.connect();
				}

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

	private UiLifecycleHelper makeUiLifecycleHelper() {
		return new UiLifecycleHelper(this, this);
	}

	private GoogleApiClient makeGoogleApiClient() {
		return new GoogleApiClient.Builder(this)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.addApi(Plus.API)
				.addScope(Plus.SCOPE_PLUS_LOGIN)
				.build();
	}

	/* A helper method to resolve the current ConnectionResult error. */
	private void resolveSignInError() {
		if (mConnectionResult.hasResolution()) {
			try {
				mIntentInProgress = true;
				startIntentSenderForResult(mConnectionResult.getResolution().getIntentSender(),
						REQUEST_CODE_GOOGLE_SIGN_IN, null, 0, 0, 0);
			} catch (SendIntentException e) {
				// The intent was canceled before it was sent.  Return to the default
				// state and attempt to connect to get an updated ConnectionResult.
				mIntentInProgress = false;
				if (mGoogleApiClient != null) {
					mGoogleApiClient.connect();
				}
			}
		}
	}

	private void wrapUp() {
		if (mStartupMode) {
			startActivity(new Intent(this, MainActivity.class));
		}
		finish();
	}

	/* Facebook callbacks */

	@Override
	public void call(Session session, SessionState state, Exception exception) {
		if (state != mLastState) {
			mLastState = state;

			if (LogEx.isLoggable(LogEx.INFO)) {
				LogEx.i(String.format("state=%s", state));
			}

			// Process state
			if (state.isOpened()) {
				mFacebookSigninClicked = false;
				Toast.makeText(this, "Facebook user is connected!", Toast.LENGTH_SHORT).show();
			} else if (state.isClosed()) {
				mFacebookSigninClicked = false;
			}
		}
	}

	/* Google callbacks */

	@Override
	public void onConnected(Bundle connectionHint) {
		// We've resolved any connection errors.  mGoogleApiClient can be used to
		// access Google APIs on behalf of the user.
		mGoogleSignInClicked = false;
		mConnectionResult = null;
		Toast.makeText(this, "Google user is connected!", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onConnectionSuspended(int cause) {
		if (mGoogleApiClient != null) {
			mGoogleApiClient.connect();
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		if (!mIntentInProgress) {
			// Store the ConnectionResult so that we can use it later when the user clicks
			// 'sign-in'.
			mConnectionResult = result;

			if (mGoogleSignInClicked) {
				// The user has already clicked 'sign-in' so we attempt to resolve all
				// errors until the user is signed in, or they cancel.
				resolveSignInError();
			}
		}
	}
}