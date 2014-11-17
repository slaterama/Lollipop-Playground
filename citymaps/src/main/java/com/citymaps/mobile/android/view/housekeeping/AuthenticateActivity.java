package com.citymaps.mobile.android.view.housekeeping;

import android.content.Intent;
import android.content.IntentSender;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Toast;
import com.android.volley.VolleyError;
import com.citymaps.mobile.android.R;
import com.citymaps.mobile.android.app.TrackedActionBarActivity;
import com.citymaps.mobile.android.app.VolleyManager;
import com.citymaps.mobile.android.model.ThirdParty;
import com.citymaps.mobile.android.model.User;
import com.citymaps.mobile.android.model.volley.UserRequest;
import com.citymaps.mobile.android.util.FacebookUtils;
import com.citymaps.mobile.android.util.GoogleUtils;
import com.citymaps.mobile.android.util.IntentUtils;
import com.citymaps.mobile.android.util.LogEx;
import com.citymaps.mobile.android.util.objectscompat.ObjectsCompat;
import com.citymaps.mobile.android.view.MainActivity;
import com.facebook.*;
import com.facebook.model.GraphUser;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import java.io.IOException;

public class AuthenticateActivity extends TrackedActionBarActivity {

	private static final String STATE_KEY_FACEBOOK_INVOKED = "facebookInvoked";

	private static final String STATE_KEY_GOOGLE_INVOKED = "googleInvoked";

	private static final int REQUEST_CODE_GOOGLE_SIGN_IN = 1001;
	private static final int REQUEST_CODE_LOGIN = 1002;
	private static final int REQUEST_CODE_CREATE_ACCOUNT = 1003;

	private boolean mStartupMode;

	private ConnectivityManager mConnectivityManager;

	private UiLifecycleHelper mUiLifecycleHelper;

	private SessionState mLastProcessedState = null;

	private FacebookCallbacks mFacebookCallbacks = new FacebookCallbacks();

	/* Client used to interact with Google APIs. */
	private GoogleApiClient mGoogleApiClient;

	/* A flag indicating that a PendingIntent is in progress and prevents
	 * us from starting further intents.
     */
	private boolean mGoogleIntentInProgress;

	/* Track whether the Google sign-in button has been clicked so that we know to resolve
 	 * all issues preventing sign-in without waiting.
 	 */
	private boolean mGoogleSignInClicked = false;

	/* Store the connection result from onConnectionFailed callbacks so that we can
 	 * resolve them when the user clicks sign-in.
 	 */
	private ConnectionResult mConnectionResult;

	private GoogleCallbacks mGoogleCallbacks = new GoogleCallbacks();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/* TODO Need to restore this
		if (!getResources().getBoolean(R.bool.authenticate_allow_orientation_change)) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		*/
		setContentView(R.layout.activity_authenticate);
		mStartupMode = IntentUtils.isStartupMode(getIntent(), false);

		mConnectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

		if (savedInstanceState != null) {
			// We only want to start Facebook's UiLifecycleHelper if the user has already tapped the "Log in with Facebook" button
			boolean facebookInvoked = savedInstanceState.getBoolean(STATE_KEY_FACEBOOK_INVOKED, false);
			if (facebookInvoked) {
				mUiLifecycleHelper = new UiLifecycleHelper(this, mFacebookCallbacks);
				mUiLifecycleHelper.onCreate(savedInstanceState);
			}

			// We only want to start Google's API client if the user has already tapped the "Sign in with Google" button
			boolean googleInvoked = savedInstanceState.getBoolean(STATE_KEY_GOOGLE_INVOKED, false);
			if (googleInvoked) {
				mGoogleApiClient = new GoogleApiClient.Builder(this)
						.addConnectionCallbacks(mGoogleCallbacks)
						.addOnConnectionFailedListener(mGoogleCallbacks)
						.addApi(Plus.API)
						.addScope(Plus.SCOPE_PLUS_LOGIN)
						.build();
			}
		}
	}

	@Override
	protected void onStart() {
		super.onStart();

		// TODO NEXT
		// ERROR: When already in Google permissions dialog
		// and we switch orientation over and over again,
		// we are getting many copies of the dialog. In other
		// words, some combination of mGoogleSignInClicked and
		// mGoogleIntentInProgress is not getting set correctly.

		if (mGoogleApiClient != null) {
			mGoogleApiClient.connect();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mUiLifecycleHelper != null) {
			// For scenarios where the main activity is launched and user
			// session is not null, the session state change notification
			// may not be triggered. Trigger it if it's open/closed.
			Session session = Session.getActiveSession();
			if (session != null && (session.isOpened() || session.isClosed())) {
				mFacebookCallbacks.call(session, session.getState(), null);
			}
			mUiLifecycleHelper.onResume();
		}
	}

	@Override
	protected void onSaveInstanceState(@NonNull Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean(STATE_KEY_FACEBOOK_INVOKED, mUiLifecycleHelper != null);
		if (mUiLifecycleHelper != null) {
			mUiLifecycleHelper.onSaveInstanceState(outState);
		}
		outState.putBoolean(STATE_KEY_GOOGLE_INVOKED, mGoogleApiClient != null);
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (mUiLifecycleHelper != null) {
			mUiLifecycleHelper.onPause();
		}
	}

	protected void onStop() {
		super.onStop();

		mGoogleApiClient.stopAutoManage(this);

		/*
		if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
			mGoogleApiClient.disconnect();
		}
		*/
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mUiLifecycleHelper != null) {
			mUiLifecycleHelper.onDestroy();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case REQUEST_CODE_GOOGLE_SIGN_IN:
				// TODO This is messy. Need to really figure out mGoogleSignInClicked,
				// mGoogleIntentInProgress, etc.
				// Could go into a Google helper, just like the Facebook one does
				if (resultCode != RESULT_OK) {
					mGoogleSignInClicked = false;
				}
				mGoogleIntentInProgress = false;
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
				if (mUiLifecycleHelper != null) {
					mUiLifecycleHelper.onActivityResult(requestCode, resultCode, data);
				}
				super.onActivityResult(requestCode, resultCode, data);
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
				if (mUiLifecycleHelper == null) {
					mUiLifecycleHelper = new UiLifecycleHelper(this, mFacebookCallbacks);
				}
				Session session = Session.getActiveSession();
				if (session == null || session.isOpened() || session.isClosed()) {
					Session.openActiveSession(this, true, FacebookUtils.FACEBOOK_READ_PERMISSIONS_LIST, mFacebookCallbacks);
				} else {
					session.openForRead(new Session.OpenRequest(this)
							.setPermissions(FacebookUtils.FACEBOOK_READ_PERMISSIONS_LIST)
							.setCallback(mFacebookCallbacks));
				}
				break;
			}
			case R.id.login_authenticate_google_button: {
				if (mConnectivityManager.getActiveNetworkInfo() == null) {
					Toast.makeText(this, R.string.error_message_no_connection, Toast.LENGTH_SHORT).show();
					return;
				}
				if (mGoogleApiClient == null) {
					mGoogleApiClient = new GoogleApiClient.Builder(this)
							.addConnectionCallbacks(mGoogleCallbacks)
							.addOnConnectionFailedListener(mGoogleCallbacks)
							.addApi(Plus.API)
							.addScope(Plus.SCOPE_PLUS_LOGIN)
							.build();
				}
				if (!mGoogleApiClient.isConnected()) {
					mGoogleSignInClicked = true;
					mGoogleApiClient.connect();
				}
				/*
				if (!mGoogleApiClient.isConnecting()) {
					mGoogleSignInClicked = true;
					resolveGoogleSignInError();
				}
				*/
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

	/* A helper method to resolve the current ConnectionResult error. */
	private void resolveGoogleSignInError() {
		if (mConnectionResult.hasResolution()) {
			try {
				mGoogleIntentInProgress = true;
				startIntentSenderForResult(mConnectionResult.getResolution().getIntentSender(),
						REQUEST_CODE_GOOGLE_SIGN_IN, null, 0, 0, 0);
			} catch (IntentSender.SendIntentException e) {
				// The intent was canceled before it was sent.  Return to the default
				// state and attempt to connect to get an updated ConnectionResult.
				mGoogleIntentInProgress = false;
				mGoogleApiClient.connect();
			}
		}
	}

	public void wrapUp() {
		if (mStartupMode) {
			startActivity(new Intent(this, MainActivity.class));
		}
		finish();
	}

	private class FacebookCallbacks implements Session.StatusCallback {
		@Override
		public void call(final Session session, SessionState state, Exception exception) {
			// Facebook's "onResume" fix triggers onSessionStateChange twice in some
			// cases, so let's check the value to prevent that.
			if (ObjectsCompat.equals(mLastProcessedState, state)) {
				return;
			}

			mLastProcessedState = state;

			if (LogEx.isLoggable(LogEx.INFO)) {
				LogEx.i(String.format("session=%s, state=%s, exception=%s", session, state, exception));
			}

			if (state.isOpened()) {
				// We have a session now. Get the user
				Request.newMeRequest(session, new Request.GraphUserCallback() {
					@Override
					public void onCompleted(final GraphUser user, Response response) {
						if (LogEx.isLoggable(LogEx.INFO)) {
							LogEx.i(String.format("Facebook: user=%s", user));
						}

						if (user == null) {
							if (getSupportFragmentManager().findFragmentByTag(LoginErrorDialogFragment.FRAGMENT_TAG) == null) {
								FacebookRequestError error = response.getError();
								String title = error.getErrorUserTitle();
								String message = error.getErrorUserMessage();
								LoginErrorDialogFragment fragment = LoginErrorDialogFragment.newInstance(title, message);
								fragment.show(getSupportFragmentManager(), LoginErrorDialogFragment.FRAGMENT_TAG);
							}
						} else {
							if (LogEx.isLoggable(LogEx.INFO)) {
								LogEx.i(String.format("user=%s", user));
							}

							final String id = user.getId();
							final String token = session.getAccessToken();
							UserRequest loginRequest = UserRequest.newLoginRequest(AuthenticateActivity.this,
									ThirdParty.FACEBOOK, id, token, new com.android.volley.Response.Listener<User>() {
										@Override
										public void onResponse(User response) {
											wrapUp();
										}
									}, new com.android.volley.Response.ErrorListener() {
										@Override
										public void onErrorResponse(VolleyError error) {
											// There is no CM user linked to this Facebook account. Take them to the Create Account screen
											Intent intent = new Intent(AuthenticateActivity.this, LoginActivity.class);
											IntentUtils.putLoginMode(intent, LoginActivity.CREATE_ACCOUNT_MODE);
											IntentUtils.putThirdPartyUser(intent, token, user);
											AuthenticateActivity.this.startActivityForResult(intent, REQUEST_CODE_CREATE_ACCOUNT);
										}
									});
							VolleyManager.getInstance(AuthenticateActivity.this).getRequestQueue().add(loginRequest);
						}
					}
				}).executeAsync();
			} else if (exception != null) {
				if (getSupportFragmentManager().findFragmentByTag(LoginErrorDialogFragment.FRAGMENT_TAG) == null) {
					LoginErrorDialogFragment fragment = LoginErrorDialogFragment.newInstance(getTitle(), exception.getMessage());
					fragment.show(getSupportFragmentManager(), LoginErrorDialogFragment.FRAGMENT_TAG);
				}
			}
		}
	}

	private class GoogleCallbacks implements GoogleApiClient.ConnectionCallbacks,
			GoogleApiClient.OnConnectionFailedListener {
		@Override
		public void onConnected(Bundle connectionHint) {
			if (LogEx.isLoggable(LogEx.INFO)) {
				LogEx.i(String.format("connectionHint=%s", connectionHint));
			}

			final Person person = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
			if (person == null) {
				// TODO Handle null user
			} else {
				if (LogEx.isLoggable(LogEx.INFO)) {
					LogEx.i(String.format("person=%s", person));
				}

				new AsyncTask<Void, Void, Object>() {
					@Override
					protected Object doInBackground(Void... voids) {
						try {
							return GoogleAuthUtil.getToken(AuthenticateActivity.this,
									Plus.AccountApi.getAccountName(mGoogleApiClient),
									GoogleUtils.getScope());
						} catch (IOException e) {
							return e;
						} catch (GoogleAuthException e) {
							return e;
						}
					}

					@Override
					protected void onPostExecute(Object result) {
						if (result instanceof String) {
							String id = person.getId();
							final String token = (String) result;
							UserRequest loginRequest = UserRequest.newLoginRequest(AuthenticateActivity.this,
									ThirdParty.GOOGLE, id, token, new com.android.volley.Response.Listener<User>() {
										@Override
										public void onResponse(User response) {
											wrapUp();
										}
									}, new com.android.volley.Response.ErrorListener() {
										@Override
										public void onErrorResponse(VolleyError error) {
											// There is no CM user linked to this Google account. Take them to the Create Account screen
											Intent intent = new Intent(AuthenticateActivity.this, LoginActivity.class);
											IntentUtils.putLoginMode(intent, LoginActivity.CREATE_ACCOUNT_MODE);
											IntentUtils.putThirdPartyUser(intent, mGoogleApiClient, token, person);
											AuthenticateActivity.this.startActivityForResult(intent, REQUEST_CODE_CREATE_ACCOUNT);
										}
									});
							VolleyManager.getInstance(AuthenticateActivity.this).getRequestQueue().add(loginRequest);
						} else if (result instanceof UserRecoverableAuthException
								|| result instanceof GoogleAuthException
								|| result instanceof IOException) {

							// TODO Any special handling when UserRecoverableAuthException?

							Exception exception = (Exception) result;
							if (getSupportFragmentManager().findFragmentByTag(LoginErrorDialogFragment.FRAGMENT_TAG) == null) {
								LoginErrorDialogFragment fragment = LoginErrorDialogFragment.newInstance(getTitle(), exception.getMessage());
								fragment.show(getSupportFragmentManager(), LoginErrorDialogFragment.FRAGMENT_TAG);
							}
						}
					}
				}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			}
		}

		@Override
		public void onConnectionSuspended(int cause) {
			if (LogEx.isLoggable(LogEx.INFO)) {
				LogEx.i(String.format("cause=%d", cause));
			}

			mGoogleApiClient.connect();
		}

		@Override
		public void onConnectionFailed(ConnectionResult result) {
			if (LogEx.isLoggable(LogEx.INFO)) {
				LogEx.i(String.format("result=%s", result));
			}

			if (!mGoogleIntentInProgress) {
				// Store the ConnectionResult so that we can use it later when the user clicks
				// 'sign-in'.
				mConnectionResult = result;

				if (mGoogleSignInClicked) {
					// The user has already clicked 'sign-in' so we attempt to resolve all
					// errors until the user is signed in, or they cancel.
					resolveGoogleSignInError();
				}
			}
		}
	}
}
