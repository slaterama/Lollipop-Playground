package com.citymaps.mobile.android.view.housekeeping;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Toast;
import com.citymaps.mobile.android.R;
import com.citymaps.mobile.android.app.TrackedActionBarActivity;
import com.citymaps.mobile.android.thirdpartynew.ThirdParty;
import com.citymaps.mobile.android.thirdpartynew.ThirdPartyManager;
import com.citymaps.mobile.android.util.IntentUtils;
import com.citymaps.mobile.android.util.LogEx;
import com.citymaps.mobile.android.view.MainActivity;

public class AuthenticateActivity_New extends TrackedActionBarActivity
		implements ThirdParty.ConnectionCallbacks {

	private static final int REQUEST_CODE_LOGIN = 1001;
	private static final int REQUEST_CODE_CREATE_ACCOUNT = 1002;

	private boolean mStartupMode;

	private ConnectivityManager mConnectivityManager;

	private ThirdPartyManager mThirdPartyManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_authenticate);
		mStartupMode = IntentUtils.isStartupMode(getIntent(), false);

		mConnectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		mThirdPartyManager = new ThirdPartyManager(this, this);
		mThirdPartyManager.add(ThirdParty.Type.FACEBOOK);
		mThirdPartyManager.add(ThirdParty.Type.GOOGLE);
		mThirdPartyManager.onCreate(savedInstanceState);
	}

	@Override
	public void onResume() {
		super.onResume();
		mThirdPartyManager.onResume();
	}

	@Override
	public void onStart() {
		super.onStart();
		mThirdPartyManager.onStart();
	}

	@Override
	public void onSaveInstanceState(@NonNull Bundle outState) {
		super.onSaveInstanceState(outState);
		mThirdPartyManager.onSaveInstanceState(outState);
	}

	@Override
	public void onPause() {
		super.onPause();
		mThirdPartyManager.onPause();
	}

	@Override
	public void onStop() {
		super.onStop();
		mThirdPartyManager.onStop();

		// TODO Begin MY Google
		if (isChangingConfigurations()) {

		}
		// End MY Google
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mThirdPartyManager.onDestroy();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case REQUEST_CODE_CREATE_ACCOUNT:
				if (resultCode != RESULT_OK) {
					// TODO What to do here? I think ThirdParty needs a "cancel" method?
				}
			case REQUEST_CODE_LOGIN:
				if (resultCode == RESULT_OK) {
					wrapUp();
				}
				break;
			default:
				super.onActivityResult(requestCode, resultCode, data);
				mThirdPartyManager.onActivityResult(requestCode, resultCode, data);
		}
	}

	/*
	@Override
	public void onConnectionStateChange(ThirdPartyConnection connection, Map<String, Object> args) {
		if (LogEx.isLoggable(LogEx.INFO)) {
			LogEx.i(String.format("connection=%s, args=%s", connection, args));
		}

		ThirdParty thirdParty = connection.getThirdParty();
		switch (thirdParty) {
			case FACEBOOK: {
				ThirdPartyConnectionFacebook facebookConnection = (ThirdPartyConnectionFacebook) connection;
				Session session = (Session) args.get("session");
				SessionState state = (SessionState) args.get("state");
				Exception exception = (Exception) args.get("exception");

				if (connection.isConnected()) {
					// We have a session now. Get the user
					facebookConnection.getToken(mFacebookCallbacks);
					facebookConnection.getUser(mFacebookCallbacks);
				} else if (exception != null) {
					if (getSupportFragmentManager().findFragmentByTag(LoginErrorDialogFragment.FRAGMENT_TAG) == null) {
						LoginErrorDialogFragment fragment =
								LoginErrorDialogFragment.newInstance(getTitle(), exception.getMessage());
						fragment.show(getSupportFragmentManager(), LoginErrorDialogFragment.FRAGMENT_TAG);
					}
				}
				break;
			}
			case GOOGLE: {

				break;
			}
		}
	}
	*/

	public void onButtonClick(View view) {
		int id = view.getId();
		switch (id) {
			case R.id.login_authenticate_facebook_button:
			case R.id.login_authenticate_google_button: {
				if (mConnectivityManager.getActiveNetworkInfo() == null) {
					Toast.makeText(this, R.string.error_message_no_connection, Toast.LENGTH_SHORT).show();
					return;
				}
				ThirdParty thirdParty = null;
				switch (id) {
					case R.id.login_authenticate_facebook_button:
						thirdParty = mThirdPartyManager.get(ThirdParty.Type.FACEBOOK);
						break;
					case R.id.login_authenticate_google_button:
						thirdParty = mThirdPartyManager.get(ThirdParty.Type.GOOGLE);
						break;
				}

				if (thirdParty != null) {
					thirdParty.getToken(new ThirdParty.TokenCallbacks() {
						@Override
						public void onToken(String token) {
							if (LogEx.isLoggable(LogEx.INFO)) {
								LogEx.i(String.format("token=%s", token));
							}


						}

						@Override
						public void onTokenError(Throwable error) {
							if (LogEx.isLoggable(LogEx.INFO)) {
								LogEx.i();
							}
						}
					});
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

	private void wrapUp() {
		if (mStartupMode) {
			startActivity(new Intent(this, MainActivity.class));
		}
		finish();
	}

	@Override
	public void onConnected(ThirdParty thirdParty) {
		if (LogEx.isLoggable(LogEx.INFO)) {
			LogEx.i(String.format("thirdParty=%s", thirdParty));
		}
	}

	@Override
	public void onDisconnected(ThirdParty thirdParty) {
		if (LogEx.isLoggable(LogEx.INFO)) {
			LogEx.i(String.format("thirdParty=%s", thirdParty));
		}
	}

	@Override
	public void onError(ThirdParty thirdParty) {
		if (LogEx.isLoggable(LogEx.INFO)) {
			LogEx.i(String.format("thirdParty=%s", thirdParty));
		}
	}

	/*
	private class FacebookCallbacks implements TokenCallbacks,
			UserCallbacks<GraphUser, FacebookRequestError> {
		String mToken;
		GraphUser mUser;

		@Override
		public void onToken(String token) {
			mToken = token;
			checkState();
		}

		@Override
		public void onTokenError(Throwable error) {

		}

		@Override
		public void onUser(GraphUser user) {
			mUser = user;
			checkState();
		}

		@Override
		public void onUserError(FacebookRequestError error) {

		}

		private void checkState() {
			if (mToken != null && mUser != null) {
				if (LogEx.isLoggable(LogEx.INFO)) {
					LogEx.i("Got token and user!");
				}

				final String id = mUser.getId();
				UserRequest loginRequest = UserRequest.newLoginRequest(AuthenticateActivity_New.this,
						com.citymaps.mobile.android.model.ThirdParty.FACEBOOK, id, mToken, new com.android.volley.Response.Listener<User>() {
							@Override
							public void onResponse(User response) {
								wrapUp();
							}
						}, new com.android.volley.Response.ErrorListener() {
							@Override
							public void onErrorResponse(VolleyError error) {
								// There is no CM user linked to this Facebook account. Take them to the Create Account screen
								Intent intent = new Intent(AuthenticateActivity_New.this, LoginActivity.class);
								IntentUtils.putLoginMode(intent, LoginActivity.CREATE_ACCOUNT_MODE);
								IntentUtils.putThirdPartyUser(intent, mToken, mUser);
								mToken = null;
								mUser = null;
								AuthenticateActivity_New.this.startActivityForResult(intent, REQUEST_CODE_CREATE_ACCOUNT);
							}
						});
				VolleyManager.getInstance(AuthenticateActivity_New.this).getRequestQueue().add(loginRequest);
			}
		}
	}
	*/
}