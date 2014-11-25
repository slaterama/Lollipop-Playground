package com.citymaps.mobile.android.thirdparty;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import com.citymaps.mobile.android.model.ThirdParty;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;

import java.util.List;

public class GoogleProxy extends ThirdPartyProxy<GoogleProxy.Callbacks>
		implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

	public static final String STATE_KEY_SIGN_IN_CLICKED = "signInClicked";

	public static final String DATA_ACCOUNT_NAME = "accountName";
	public static final String DATA_CURRENT_PERSON = "currentPerson";

	private static final String GOOGLE_ACCOUNT_NAME_SCOPE = String.format("oauth2:%s", Scopes.PLUS_LOGIN);

	/* A magic number we will use to know that our sign-in error resolution activity has completed */
	private static final int OUR_REQUEST_CODE = 49404;

	/* Request code used to invoke sign in user interactions. */
	private static final int RC_SIGN_IN = OUR_REQUEST_CODE;

	/* Request code used to invoke sign in user interactions. */
	private static final int RC_AUTH = OUR_REQUEST_CODE + 1;

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

	private boolean mSignInCancelled;

	/* Store the connection result from onConnectionFailed callbacks so that we can
	 * resolve them when the user clicks sign-in.
	 */
	private ConnectionResult mConnectionResult;

	public GoogleProxy(FragmentActivity activity, Callbacks callbacks) {
		super(activity, callbacks);
	}

	public GoogleProxy(Context context, Fragment fragment, Callbacks callbacks) {
		super(context, fragment, callbacks);
	}

	@Override
	public ThirdParty getThirdParty() {
		return ThirdParty.GOOGLE;
	}

	@Override
	protected boolean onActivate(boolean interactive, Callbacks callbacks) {
		mGoogleApiClient = createGoogleApiClient();
		mSignInClicked = interactive;
		mSignInCancelled = false;
		if (callbacks != null) {
			callbacks.onConnecting(this);
		}
		mGoogleApiClient.connect();
		return true;
	}

	@Override
	protected void onDeactivate(boolean clearToken) {
		mGoogleApiClient = null;
	}

	@Override
	public boolean requestData(List<String> names, OnDataListener listener) {
		if (mGoogleApiClient == null || names == null) {
			return false;
		}

		new DataTask(listener) {
			private String mAccountName;

			@Override
			protected Void doInBackground(String... params) {
				for (String name : params) {
					if (TextUtils.equals(name, DATA_TOKEN)) {
						try {
							putData(name, GoogleAuthUtil.getToken(mActivity, getAccountName(), GOOGLE_ACCOUNT_NAME_SCOPE));
						} catch (Exception e) {
							putError(name, e);
						}
					} else if (TextUtils.equals(name, DATA_ACCOUNT_NAME)) {
						putData(name, getAccountName());
					} else if (TextUtils.equals(name, DATA_CURRENT_PERSON)) {
						putData(name, Plus.PeopleApi.getCurrentPerson(mGoogleApiClient));
					}
				}
				return null;
			}

			String getAccountName() {
				if (mAccountName == null) {
					mAccountName = Plus.AccountApi.getAccountName(mGoogleApiClient);
				}
				return mAccountName;
			}
		}.executeOnExecutor(DataTask.SERIAL_EXECUTOR, names.toArray(new String[names.size()]));
		return true;
	}

	/* Lifecycle methods */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (mActivated) {
			mGoogleApiClient = createGoogleApiClient();
		}
		if (savedInstanceState != null) {
			mSignInClicked = savedInstanceState.getBoolean(STATE_KEY_SIGN_IN_CLICKED);
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
		outState.putBoolean(STATE_KEY_SIGN_IN_CLICKED, mSignInClicked);
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
			case RC_SIGN_IN:
				if (resultCode != FragmentActivity.RESULT_OK) {
					mSignInClicked = false;
					mSignInCancelled = true;
				}

				mIntentInProgress = false;

				if (!mGoogleApiClient.isConnecting()) {
					mGoogleApiClient.connect();
				}
				break;
			case RC_AUTH:
				// TODO
				break;
			default:
				super.onActivityResult(requestCode, resultCode, data);
		}
	}

	private GoogleApiClient createGoogleApiClient() {
		GoogleApiClient.Builder builder = new GoogleApiClient.Builder(mContext)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this);
		if (mCallbacks != null) {
			mCallbacks.onPreBuild(builder);
		}
		return builder.build();
	}

	private void resolveSignInError() {
		if (mConnectionResult.hasResolution()) {
			try {
				mIntentInProgress = true;
				FragmentActivity activity = (mActivity != null ? mActivity : mFragment.getActivity());
				activity.startIntentSenderForResult(mConnectionResult.getResolution().getIntentSender(),
						RC_SIGN_IN, null, 0, 0, 0);
			} catch (SendIntentException e) {
				// The intent was cancelled before it was sent. Return to the default state
				// and attempt to connect to get an updated ConnectionResult.
				mIntentInProgress = false;
				mGoogleApiClient.connect();
			}
		/*
		} else {

			boolean handled = false;
			if (mCallbacks != null) {
				handled = mCallbacks.onFailed(this, mSignInCancelled, mConnectionResult);
			}
			if (!handled) {
				// TODO Error handling
			}
		*/
		}
	}

	/* Callbacks */

	@Override
	public void onConnected(Bundle connectionHint) {
		// We've resolved any connection errors.  mGoogleApiClient can be used to
		// access Google APIs on behalf of the user.
		if (mCallbacks != null) {
			mCallbacks.onConnected(this, connectionHint);
		}
	}

	@Override
	public void onConnectionSuspended(int cause) {
		switch (cause) {
			case CAUSE_SERVICE_DISCONNECTED:
				if (mCallbacks != null) {
					mCallbacks.onDisconnected(this);
				}
				break;
			case GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST:
			default:
				mGoogleApiClient.connect();
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		if (!mIntentInProgress) {
			// Store the ConnectionResult so that we can use it later when the user clicks 'sign-in'.
			mConnectionResult = result;

			if (mSignInClicked) {
				// The user has already clicked 'sign-in' so we attempt to resolve all
				// errors until the user is signed in, or they cancel.
				resolveSignInError();
			} else {
				boolean handled = false;
				if (mCallbacks != null) {
					handled = mCallbacks.onFailed(this, mSignInCancelled, result);
				}
				mSignInCancelled = false;
				if (!handled) {
					// TODO Error handling
				}
			}
		}
	}

	/* Interfaces */

	public static interface Callbacks extends ThirdPartyProxy.Callbacks {
		public void onPreBuild(@NonNull GoogleApiClient.Builder builder);

		public void onConnecting(GoogleProxy proxy);

		public void onConnected(GoogleProxy proxy, Bundle connectionHint);

		public void onDisconnected(GoogleProxy proxy);

		public boolean onFailed(GoogleProxy proxy, boolean cancelled, ConnectionResult result);

	}

	public static abstract class SimpleCallbacks implements Callbacks {
		@Override
		public void onPreBuild(@NonNull GoogleApiClient.Builder builder) {
		}

		@Override
		public void onConnecting(GoogleProxy proxy) {
		}

		@Override
		public void onConnected(GoogleProxy proxy, Bundle connectionHint) {
		}

		@Override
		public void onDisconnected(GoogleProxy proxy) {
		}

		@Override
		public boolean onFailed(GoogleProxy proxy, boolean cancelled, ConnectionResult result) {
			return false;
		}
	}
}
