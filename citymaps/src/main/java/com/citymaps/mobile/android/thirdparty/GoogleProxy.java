package com.citymaps.mobile.android.thirdparty;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import java.util.List;

public class GoogleProxy extends ThirdPartyProxy
		implements ConnectionCallbacks, OnConnectionFailedListener {

	private static final String STATE_KEY_CONNECTION_OPEN = GoogleProxy.class.getName() + ".connectionOpen";

	/* A magic number we will use to know that our sign-in error resolution activity has completed */
	private static final int GOOGLE_REQUEST_CODE = 49404;

	/* Request code used to invoke sign in user interactions. */
	private static final int REQUEST_CODE_GOOGLE_SIGN_IN = GOOGLE_REQUEST_CODE;

	/* */
	private List<Scope> mScopes;

	private Callbacks mCallbacks;

	/* Client used to interact with Google APIs. */
	private GoogleApiClient mGoogleApiClient;

	/* A flag indicating that a PendingIntent is in progress and prevents
	 * us from starting further intents.
	 */
	private boolean mIntentInProgress;

	/* Track whether the sign-in button has been clicked so that we know to resolve
	 * all issues preventing sign-in without waiting.
	 */
	private boolean mSignInClicked = false;

	/* Store the connection result from onConnectionFailed callbacks so that we can
	 * resolve them when the user clicks sign-in.
	 */
	private ConnectionResult mConnectionResult;

	public GoogleProxy(Activity activity, List<Scope> scopes, Callbacks callbacks) {
		super(activity);
		mScopes = scopes;
		mCallbacks = callbacks;
	}

	public GoogleProxy(Activity activity, List<Scope> scopes) {
		this(activity, scopes, null);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			boolean connectionOpen = savedInstanceState.getBoolean(STATE_KEY_CONNECTION_OPEN, false);
			if (connectionOpen) {
				mGoogleApiClient = newGoogleApiClient();
			}
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		if (mGoogleApiClient != null) {
			mGoogleApiClient.connect();
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean(STATE_KEY_CONNECTION_OPEN, mGoogleApiClient != null);
	}

	@Override
	public void onStop() {
		super.onStop();
		if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
			mGoogleApiClient.disconnect();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case REQUEST_CODE_GOOGLE_SIGN_IN:
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

	@Override
	public void openConnection(boolean interactive) {
		mGoogleApiClient = newGoogleApiClient();
		mSignInClicked = interactive;
		mGoogleApiClient.connect();
	}

	@Override
	public void closeConnection() {
		mScopes = null;
		if (mGoogleApiClient != null) {
			if (mGoogleApiClient.isConnected() || mGoogleApiClient.isConnecting()) {
				mGoogleApiClient.disconnect();
			}
			mGoogleApiClient = null;
		}
	}

	private GoogleApiClient newGoogleApiClient() {
		GoogleApiClient.Builder builder = new GoogleApiClient.Builder(mActivity)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.addApi(Plus.API);

		if (mScopes != null) {
			for (Scope scope : mScopes) {
				builder.addScope(scope);
			}
		}

		return builder.build();
	}

	/* Callbacks */

	@Override
	public void onConnected(Bundle connectionHint) {
		mSignInClicked = false;
		mConnectionResult = null;
		if (mCallbacks != null) {
			mCallbacks.onConnected(this, connectionHint);
		}
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

			if (mConnectionResult.hasResolution() && mSignInClicked) {
				try {
					mIntentInProgress = true;
					result.startResolutionForResult(mActivity, REQUEST_CODE_GOOGLE_SIGN_IN);
				} catch (IntentSender.SendIntentException e) {
					// The intent was canceled before it was sent.  Return to the default
					// state and attempt to connect to get an updated ConnectionResult.
					mIntentInProgress = false;
					if (mGoogleApiClient != null) {
						mGoogleApiClient.connect();
					}
				}
			} else {
				if (mCallbacks != null) {
					mCallbacks.onUnresolvedError(this, result);
				}
			}
		}
	}

	public static class PersonRequest extends Request<Person, Exception> {
		public PersonRequest(Listener<Person> listener, ErrorListener<Exception> errorListener) {
			super(listener, errorListener);
		}
	}

	public static interface Callbacks {
		public void onConnected(ThirdPartyProxy proxy, Bundle connectionHint);
		public void onUnresolvedError(ThirdPartyProxy proxy, ConnectionResult result);
	}
}
