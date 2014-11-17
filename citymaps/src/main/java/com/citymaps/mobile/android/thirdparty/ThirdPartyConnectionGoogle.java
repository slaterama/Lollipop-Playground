package com.citymaps.mobile.android.thirdparty;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.Toast;
import com.citymaps.mobile.android.util.LogEx;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

public class ThirdPartyConnectionGoogle extends ThirdPartyConnection<Person, Throwable>
		implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

	private static final String CLASSNAME = ThirdPartyConnectionGoogle.class.getName();
	private static final String STATE_KEY_INTENT_IN_PROGRESS = String.format("%s.intentInProgress", CLASSNAME);

	/* Request code used to invoke sign in user interactions.
	 *  "600613" is a sort-of numeric representation of "google" to help ensure a unique request code. */
	private static final int RC_SIGN_IN = 6006130;

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

	protected ThirdPartyConnectionGoogle(Activity activity) {
		super(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		if (savedInstanceState != null) {
//			mIntentInProgress = savedInstanceState.getBoolean(STATE_KEY_INTENT_IN_PROGRESS, false);
//		}
		mGoogleApiClient = new GoogleApiClient.Builder(mActivity)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.addApi(Plus.API)
				.addScope(Plus.SCOPE_PLUS_LOGIN)
				.build();
	}

	@Override
	public void onStart() {
		super.onStart();
		if (isActive()) {
			mGoogleApiClient.connect();
		}
	}

	@Override
	public void onSaveInstanceState(@NonNull Bundle outState) {
		super.onSaveInstanceState(outState);
//		outState.putBoolean(STATE_KEY_INTENT_IN_PROGRESS, mIntentInProgress);
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
				if (resultCode == Activity.RESULT_OK) {
					if (!mGoogleApiClient.isConnecting()) {
						mGoogleApiClient.connect();
					}
				} else {
					mSignInClicked = false;
					disconnect(); // TODO ???? Or just set the active flag to false?
				}
				mIntentInProgress = false;
				break;
			default:
				super.onActivityResult(requestCode, resultCode, data);
		}
	}

	@Override
	public ThirdParty getThirdParty() {
		return ThirdParty.GOOGLE;
	}

	@Override
	public void getToken(TokenCallbacks callbacks) {
		// TODO
	}

	@Override
	public void getUser(UserCallbacks<Person, Throwable> callbacks) {
		// TODO
	}

	@Override
	public boolean isConnecting() {
		return mGoogleApiClient.isConnecting();
	}

	@Override
	public boolean isConnected() {
		return mGoogleApiClient.isConnected();
	}

	@Override
	public void connect(boolean silent, Callbacks callbacks) {
		super.connect(silent, callbacks);
		if (!mGoogleApiClient.isConnecting()) {
			mSignInClicked = true;
			mGoogleApiClient.connect();
		}
	}

	@Override
	public void disconnect() {
		super.disconnect();
		mGoogleApiClient.disconnect();
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		// We've resolved any connection errors. mGoogleApiClient can be used to
		// access Google APIs on behalf of the user.
		Toast.makeText(mActivity, "User is connected!", Toast.LENGTH_LONG).show();
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
//			try {
				mIntentInProgress = true;

//				mActivity.startIntentSenderForResult(mConnectionResult.getResolution().getIntentSender(),
//						RC_SIGN_IN, null, 0, 0, 0);
				LogEx.i();
//			} catch (SendIntentException e) {
				// The intent was canceled before it was sent.  Return to the default
				// state and attempt to connect to get an updated ConnectionResult.
				mIntentInProgress = false;

//				LogEx.i();
//				mGoogleApiClient.connect();
//			}
		}
	}
}
