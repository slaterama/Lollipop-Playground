package com.citymaps.mobile.android.thirdpartynew;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.support.annotation.NonNull;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

public class GoogleProxy extends ThirdPartyProxy
		implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

	/* A magic number we will use to know that our sign-in error resolution activity has completed */
	private static final int OUR_REQUEST_CODE = 49404;

	/* Request code used to invoke sign in user interactions. */
	private static final int RC_SIGN_IN = OUR_REQUEST_CODE;

	private ProxyCallbacks mProxyCallbacks;

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

	/* Store the connection hint from onConnected so that we can pass it
	 * to mProxyCallbacks on subsequent calls to connect().
	 */
	private Bundle mConnectionHint;

	/* Constructors */

	public GoogleProxy(Activity activity, OnPreBuildListener onPreBuildListener, ProxyCallbacks proxyCallbacks) {
		super(activity);
		GoogleApiClient.Builder builder = new GoogleApiClient.Builder(activity)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this);
		if (onPreBuildListener != null) {
			onPreBuildListener.onPreBuild(builder);
		}
		mGoogleApiClient = builder.build();
		mProxyCallbacks = proxyCallbacks;
	}

	/* Lifecycle methods */

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
		if (requestCode == RC_SIGN_IN) {
			if (resultCode != Activity.RESULT_OK) {
				mSignInClicked = false;
			}

			mIntentInProgress = false;

			if (!mGoogleApiClient.isConnecting()) {
				mGoogleApiClient.connect();
			}
		} else {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	/* Methods */

	/* A helper method to resolve the current ConnectionResult error. */
	private void resolveSignInError() {
		if (mConnectionResult.hasResolution()) {
			try {
				mIntentInProgress = true;
				mActivity.startIntentSenderForResult(mConnectionResult.getResolution().getIntentSender(),
						RC_SIGN_IN, null, 0, 0, 0);
			} catch (SendIntentException e) {
				// The intent was canceled before it was sent.  Return to the default
				// state and attempt to connect to get an updated ConnectionResult.
				mIntentInProgress = false;
				mGoogleApiClient.connect();
			}
		}
	}

	@Override
	public void connect(boolean interactive) {
		super.connect(interactive);
		if (mProxyCallbacks != null) {
			mProxyCallbacks.onConnecting(this);
		}
		if (mGoogleApiClient.isConnected()) {
			onConnected(mConnectionHint);
		} else {
			mConnectionHint = null;
			if (!mGoogleApiClient.isConnecting()) {
				mSignInClicked = interactive;
				resolveSignInError();
			}
		}
	}

	/* Google callbacks */

	@Override
	public void onConnected(Bundle connectionHint) {
		mSignInClicked = false;
		mConnectionResult = null;
		mConnectionHint = connectionHint;
		if (mProxyCallbacks != null) {
			mProxyCallbacks.onConnected(this, connectionHint);
		}
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
			} else {
				// TODO Is it ok to relay this error to listener?
				// (Listener might not yet be "interested")
				if (mProxyCallbacks != null) {
					mProxyCallbacks.onError(this, result);
				}
			}
		}
	}

	/* Interfaces */

	public static interface OnPreBuildListener {
		public void onPreBuild(@NonNull GoogleApiClient.Builder builder);
	}

	public static interface ProxyCallbacks {
		public void onConnecting(GoogleProxy proxy);
		public void onConnected(GoogleProxy proxy, Bundle connectionHint);
		public void onError(GoogleProxy proxy, ConnectionResult result);
		public void onDisconnected(GoogleProxy proxy);
	}
}
