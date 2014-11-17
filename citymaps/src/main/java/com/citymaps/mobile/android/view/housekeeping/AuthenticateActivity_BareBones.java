package com.citymaps.mobile.android.view.housekeeping;

import android.content.Intent;
import android.content.IntentSender;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Toast;
import com.citymaps.mobile.android.R;
import com.citymaps.mobile.android.util.LogEx;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;

public class AuthenticateActivity_BareBones extends ActionBarActivity
		implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

	private static final String STATE_KEY_INTENT_IN_PROGRESS = "intentInProgress";
	private static final String STATE_KEY_SIGN_IN_CLICKED = "signInClicked";

	/* Request code used to invoke sign in user interactions. */
	private static final int RC_SIGN_IN = 1;

	/* Client used to interact with Google APIs. */
	private GoogleApiClient mGoogleApiClient;

	/* A flag indicating that a PendingIntent is in progress and prevents
	 * us from starting further intents.
	 */
	private boolean mIntentInProgress;

	/* Track whether the sign-in button has been clicked so that we know to resolve
	 * all issues preventing sign-in without waiting.
	 */
	private boolean mSignInClicked;

	/* Store the connection result from onConnectionFailed callbacks so that we can
	 * resolve them when the user clicks sign-in.
	 */
	private ConnectionResult mConnectionResult;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_authenticate);

		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.addApi(Plus.API)
				.addScope(Plus.SCOPE_PLUS_LOGIN)
				.build();

		if (savedInstanceState != null) {
			mIntentInProgress = savedInstanceState.getBoolean(STATE_KEY_INTENT_IN_PROGRESS);
			mSignInClicked = savedInstanceState.getBoolean(STATE_KEY_SIGN_IN_CLICKED);
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (mSignInClicked) {
			mGoogleApiClient.connect();
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (mGoogleApiClient.isConnected()) {
			mGoogleApiClient.disconnect();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
			case RC_SIGN_IN:
				if (resultCode != RESULT_OK) {
					mSignInClicked = false;
					mConnectionResult = null;
				}

				mIntentInProgress = false;

				if (!mGoogleApiClient.isConnecting()) {
					mGoogleApiClient.connect();
				}
				break;
		}
	}

	public void onButtonClick(View view) {
		int id = view.getId();
		switch (id) {
			case R.id.login_authenticate_google_button:
				if (!mGoogleApiClient.isConnecting()) {
					if (!mSignInClicked && !mIntentInProgress) {
						mSignInClicked = true;
						mGoogleApiClient.connect();
					}
				}
				break;
		}
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		// We've resolved any connection errors.  mGoogleApiClient can be used to
		// access Google APIs on behalf of the user.
		mSignInClicked = false;
		mConnectionResult = null;
		Toast.makeText(this, "Connected to Google!", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onConnectionSuspended(int cause) {
		mGoogleApiClient.connect();
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
				startIntentSenderForResult(sender, RC_SIGN_IN, null, 0, 0, 0);
			} catch (IntentSender.SendIntentException e) {
				// The intent was canceled before it was sent.  Return to the default
				// state and attempt to connect to get an updated ConnectionResult.
				mIntentInProgress = false;
				mGoogleApiClient.connect();
			}
		}
	}
}
