package com.citymaps.mobile.android.thirdparty;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import com.citymaps.mobile.android.R;
import com.citymaps.mobile.android.model.ThirdParty;
import com.citymaps.mobile.android.util.CollectionUtils;
import com.citymaps.mobile.android.util.CommonUtils;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class GoogleProxy extends ThirdPartyProxy<GoogleProxy.Callbacks>
			implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

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

	/* Store the connection result from onConnectionFailed callbacks so that we can
	 * resolve them when the user clicks sign-in.
	 */
	private ConnectionResult mConnectionResult;

	/* Store the connection hint from onConnected so that we can pass it
	 * to mProxyCallbacks on subsequent calls to connect().
	 */
	private Bundle mConnectionHint;

	public GoogleProxy(FragmentActivity activity, Callbacks callbacks) {
		super(activity, callbacks);
	}

	public GoogleProxy(Fragment fragment, Callbacks callbacks) {
		super(fragment, callbacks);
	}

	@Override
	public ThirdParty getThirdParty() {
		return ThirdParty.GOOGLE;
	}

	@Override
	public void onProxyStart(boolean interactive, Callbacks callbacks) {
		GoogleApiClient.Builder builder = new GoogleApiClient.Builder(mActivity)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this);
		if (callbacks != null) {
			callbacks.onPreBuild(builder);
		}
		mGoogleApiClient = builder.build();
		if (callbacks != null) {
			callbacks.onConnecting(this);
		}
		mGoogleApiClient.connect();
	}

	@Override
	public void onProxyStop(boolean clear) {
		if (mGoogleApiClient != null) {
			mGoogleApiClient.disconnect();
		}
		mGoogleApiClient = null;

		// TODO how to clear token?
	}

	@Override
	public boolean requestData(List<String> names, OnDataListener listener) {
		if (mGoogleApiClient == null) {
			return false;
		} else {
			new DataTask(this, names, listener) {
				private String mAccountName;

				@Override
				protected Void doInBackground(Void... params) {
					if (mNames != null) {
						for (String name : mNames) {
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
					}
					return null;
				}

				@Override
				protected void handleErrors(Map<String, Object> errors) {
					Object error = CollectionUtils.getFirstValue(errors);
					if (error instanceof UserRecoverableAuthException) {
						// Start the user recoverable action using the intent returned by getIntent
						Intent intent = ((UserRecoverableAuthException) error).getIntent();
						mActivity.startActivityForResult(intent, RC_AUTH);
					} else if (error instanceof IOException) {
						// Network or server error, the call is expected to succeed if you try again later.
						// Don't attempt to call again immediately - the request is likely to
						// fail, you'll hit quotas or back-off.
						CommonUtils.showSimpleDialogFragment(mActivity.getSupportFragmentManager(),
								mActivity.getTitle(),
								mActivity.getString(R.string.error_generic_third_party_network_or_server_error));
					} else if (error instanceof GoogleAuthException) {
						// Failure. The call is not expected to ever succeed so it should not be
						// retried.
						CommonUtils.showSimpleDialogFragment(mActivity.getSupportFragmentManager(),
								mActivity.getTitle(), mActivity.getString(R.string.error_generic_third_party_auth));
					}
				}

				String getAccountName() {
					if (mAccountName == null) {
						mAccountName = Plus.AccountApi.getAccountName(mGoogleApiClient);
					}
					return mAccountName;
				}
			}.executeOnExecutor(DataTask.THREAD_POOL_EXECUTOR);
			return true;
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		if (isActive() && mGoogleApiClient != null) {
			mGoogleApiClient.connect();
		}
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
					setInteractive(false);
				}

				mIntentInProgress = false;

				if (!mGoogleApiClient.isConnecting()) {
					mGoogleApiClient.connect();
				}
				break;
			case RC_AUTH:
				// TODO
				super.onActivityResult(requestCode, resultCode, data);
				break;
			default:
				super.onActivityResult(requestCode, resultCode, data);
		}
	}

	/* A helper method to resolve the current ConnectionResult error. */
	private void resolveSignInError() {
		if (mConnectionResult.hasResolution()) {
			try {
				mIntentInProgress = true;
				mActivity.startIntentSenderForResult(mConnectionResult.getResolution().getIntentSender(),
						RC_SIGN_IN, null, 0, 0, 0);
			} catch (IntentSender.SendIntentException e) {
				// The intent was canceled before it was sent.  Return to the default
				// state and attempt to connect to get an updated ConnectionResult.
				mIntentInProgress = false;
				mGoogleApiClient.connect();
			}
		} else {
			boolean handled = false;
			Callbacks callbacks = getCallbacks();
			if (callbacks != null) {
				handled = callbacks.onFailed(this, false, mConnectionResult);
			}
			if (!handled) {
				mIntentInProgress = true;
				GooglePlayServicesUtil.showErrorDialogFragment(mConnectionResult.getErrorCode(), mActivity, RC_SIGN_IN, null);
			}
		}
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		setInteractive(false);
		mConnectionResult = null;
		mConnectionHint = connectionHint;
		Callbacks callbacks = getCallbacks();
		if (callbacks != null) {
			callbacks.onConnected(this, connectionHint);
		}
	}

	@Override
	public void onConnectionSuspended(int cause) {
		if (isActive() && mGoogleApiClient != null) {
			mGoogleApiClient.connect();
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		if (!mIntentInProgress) {
			// Store the ConnectionResult so that we can use it later when the user clicks 'sign-in'.
			mConnectionResult = result;

			if (isInteractive()) {
				// The user has already clicked 'sign-in' so we attempt to resolve all
				// errors until the user is signed in, or they cancel.
				resolveSignInError();
			} else {
				boolean handled = false;
				Callbacks callbacks = getCallbacks();
				if (callbacks != null) {
					callbacks.onFailed(this, result.getErrorCode() == ConnectionResult.SIGN_IN_REQUIRED, result);
				}
				if (!handled) {
					// TODO
				}
			}
		}
	}

	public static interface Callbacks extends ThirdPartyProxy.AbsCallbacks {
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
