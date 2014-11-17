package com.citymaps.mobile.android.thirdpartynew;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;
import com.citymaps.mobile.android.util.LogEx;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;

import java.io.IOException;

public class ThirdPartyGoogle extends ThirdParty
		implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

	// A magic number we will use to know that our sign-in error resolution activity has completed
	private static final int OUR_REQUEST_CODE = 49404;

	/* Request code used to invoke sign in user interactions. */
	private static final int RC_SIGN_IN = OUR_REQUEST_CODE + 1;

	private static final int RC_USER_RECOVERABLE_AUTH_EXCEPTION = OUR_REQUEST_CODE + 2;

	/* Client used to interact with Google APIs. */
	private GoogleApiClient mGoogleApiClient;

	/* A flag indicating that a PendingIntent is in progress and prevents
	 * us from starting further intents.
	 */
	private boolean mIntentInProgress;

	/* Track whether the sign-in button has been clicked so that we know to resolve
	 * all issues preventing sign-in without waiting.
	 */
	private boolean mSignInClicked; // ???? mActive?

	/* Store the connection result from onConnectionFailed callbacks so that we can
	 * resolve them when the user clicks sign-in.
	 */
	private ConnectionResult mConnectionResult;

	private TokenCallbacks mPendingTokenCallbacks = null;

	public ThirdPartyGoogle(Activity activity, ConnectionCallbacks callbacks) {
		super(activity, callbacks);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mGoogleApiClient = new GoogleApiClient.Builder(mActivity)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.addApi(Plus.API)
				.addScope(Plus.SCOPE_PLUS_LOGIN)
				.build();
	}

	@Override
	public Type getType() {
		return Type.GOOGLE;
	}

	@Override
	public void onStart() {
		super.onStart();
		mGoogleApiClient.connect();
	}

	@Override
	public void onStop() {
		super.onStop();
		if (mGoogleApiClient.isConnected()) {
			mGoogleApiClient.disconnect();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case RC_SIGN_IN:
				if (resultCode != Activity.RESULT_OK) {
					mSignInClicked = false;
					mConnectionResult = null;
				}

				mIntentInProgress = false;

				if (!mGoogleApiClient.isConnecting()) {
					mGoogleApiClient.connect();
				}
				break;
			default:
				super.onActivityResult(requestCode, resultCode, data);
		}
	}

	/*
	@Override
	public void onActivate(boolean silent) {
//		mSignInClicked = true;
		if (mConnectionResult != null) {
			resolveSignInError();
		}
	}
	*/

	@Override
	public void getToken(final TokenCallbacks callbacks /*, TODO interactive i.e. signInClicked */) {
		if (mGoogleApiClient.isConnected()) {
			new TokenTask(mActivity, mGoogleApiClient, callbacks).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} else {
			mPendingTokenCallbacks = null;
			mSignInClicked = true; // TODO interactive
			if (!mGoogleApiClient.isConnecting()) {
				mGoogleApiClient.connect();
			}
		}
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		// We've resolved any connection errors.  mGoogleApiClient can be used to
		// access Google APIs on behalf of the user.
		mSignInClicked = false;
		mConnectionResult = null;
		Toast.makeText(mActivity, "Connected to Google!", Toast.LENGTH_SHORT).show();

		if (mConnectionCallbacks != null) {
			mConnectionCallbacks.onConnected(this);
		}
	}

	@Override
	public void onConnectionSuspended(int cause) {
		mGoogleApiClient.connect();

		if (mPendingTokenCallbacks != null) {
			new TokenTask(mActivity, mGoogleApiClient, mPendingTokenCallbacks).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			mPendingTokenCallbacks = null;
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		if (!mIntentInProgress) {
			// Store the ConnectionResult so that we can use it later when the user clicks
			// 'sign-in'.
			mConnectionResult = result;

			if (mSignInClicked) {
				// The user has already clicked 'sign-in' so we attempt to resolve all
				// errors until the user is signed in, or they cancel.
				resolveSignInError();
			}
		}
	}

	/* A helper method to resolve the current ConnectionResult error. */
	private void resolveSignInError() {
		if (mConnectionResult.hasResolution()) {
			try {
				mIntentInProgress = true;
				IntentSender sender = mConnectionResult.getResolution().getIntentSender();
				LogEx.i(String.format("sender=%s", sender));
				mActivity.startIntentSenderForResult(sender, RC_SIGN_IN, null, 0, 0, 0);
			} catch (IntentSender.SendIntentException e) {
				// The intent was canceled before it was sent.  Return to the default
				// state and attempt to connect to get an updated ConnectionResult.
				mIntentInProgress = false;
				mGoogleApiClient.connect();
			}
		} else {
			if (mConnectionCallbacks != null) {
				mConnectionCallbacks.onError(this);
			}
		}
	}

	private static class TokenTask extends AsyncTask<Void, Void, Void> {
		private Activity mActivity;
		private GoogleApiClient mGoogleApiClient;
		private TokenCallbacks mCallbacks;

		private String mToken;
		private Exception mException;

		public TokenTask(Activity activity, GoogleApiClient googleApiClient, TokenCallbacks callbacks) {
			super();
			mActivity = activity;
			mGoogleApiClient = googleApiClient;
			mCallbacks = callbacks;
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				Bundle extras = new Bundle();
				extras.putBoolean(GoogleAuthUtil.KEY_SUPPRESS_PROGRESS_SCREEN, true);
				mToken = GoogleAuthUtil.getToken(mActivity, Plus.AccountApi.getAccountName(mGoogleApiClient),
						String.format("oauth2:%s", TextUtils.join(" ", new String[]{Scopes.PLUS_LOGIN})), extras);
			} catch (Exception e) {
				mException = e;
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (mException == null) {
				if (mCallbacks != null) {
					mCallbacks.onToken(mToken);
				}
			} else if (mException instanceof UserRecoverableAuthException) {
				Intent recoveryIntent = ((UserRecoverableAuthException) mException).getIntent();
				// Use the intent in a custom dialog or just startActivityForResult.
				mActivity.startActivityForResult(recoveryIntent, RC_USER_RECOVERABLE_AUTH_EXCEPTION);
			} else {
				if (mCallbacks != null) {
					mCallbacks.onTokenError(mException);
				}
			}
		}
	}
}
