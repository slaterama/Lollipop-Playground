package com.citymaps.mobile.android.view.housekeeping;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import com.android.volley.*;
import com.citymaps.mobile.android.R;
import com.citymaps.mobile.android.app.TrackedActionBarActivity;
import com.citymaps.mobile.android.app.VolleyManager;
import com.citymaps.mobile.android.model.ThirdParty;
import com.citymaps.mobile.android.model.User;
import com.citymaps.mobile.android.model.volley.UserRequest;
import com.citymaps.mobile.android.util.IntentUtils;
import com.citymaps.mobile.android.util.LogEx;
import com.citymaps.mobile.android.util.objectscompat.ObjectsCompat;
import com.citymaps.mobile.android.view.MainActivity;
import com.facebook.*;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.model.GraphUser;

import java.util.Arrays;

public class AuthenticateActivity extends TrackedActionBarActivity {

	private static final int REQUEST_CODE_LOGIN = 1001;
	private static final int REQUEST_CODE_CREATE_ACCOUNT = 1002;

	private static final String[] FACEBOOK_READ_PERSMISSIONS = new String[] {
			"public_profile", "email", "user_friends"
	};

	private boolean mStartupMode;

	private UiLifecycleHelper mUiLifecycleHelper;

	private SessionState mLastProcessedState = null;

	Session.StatusCallback mStatusCallback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state, Exception exception) {
			// Facebook's "onResume" fix triggers onSessionStateChange twice in some
			// cases, so let's check the value to prevent that.
			if (!ObjectsCompat.equals(mLastProcessedState, state)) {
				mLastProcessedState = state;
				onSessionStateChange(session, state, exception);
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (!getResources().getBoolean(R.bool.authenticate_allow_orientation_change)) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		setContentView(R.layout.activity_authenticate);
		mStartupMode = IntentUtils.isStartupMode(getIntent(), false);
		mUiLifecycleHelper = new UiLifecycleHelper(this, mStatusCallback);
		mUiLifecycleHelper.onCreate(savedInstanceState);
	}

	@Override
	protected void onResume() {
		super.onResume();

		// For scenarios where the main activity is launched and user
		// session is not null, the session state change notification
		// may not be triggered. Trigger it if it's open/closed.
		Session session = Session.getActiveSession();
		if (session != null && (session.isOpened() || session.isClosed())) {
			mStatusCallback.call(session, session.getState(), null);
		}
		mUiLifecycleHelper.onResume();
	}

	@Override
	protected void onSaveInstanceState(@NonNull Bundle outState) {
		super.onSaveInstanceState(outState);
		mUiLifecycleHelper.onSaveInstanceState(outState);
	}

	@Override
	protected void onPause() {
		super.onPause();
		mUiLifecycleHelper.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mUiLifecycleHelper.onDestroy();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case REQUEST_CODE_CREATE_ACCOUNT:
			case REQUEST_CODE_LOGIN:
				if (resultCode == RESULT_OK) {
					onContinue();
				}
				break;
			default:
				mUiLifecycleHelper.onActivityResult(requestCode, resultCode, data);
				super.onActivityResult(requestCode, resultCode, data);
		}
	}

	private void onSessionStateChange(final Session session, SessionState state, Exception exception) {
			mLastProcessedState = state;

			if (LogEx.isLoggable(LogEx.INFO)) {
				LogEx.i(String.format("session=%s, state=%s, exception=%s", session, state, exception));
			}

			if (state.isOpened()) {
				// We have a session now. Get the user
				Request.newMeRequest(session, new Request.GraphUserCallback() {
					@Override
					public void onCompleted(final GraphUser user, Response response) {
						if (user == null) {
							if (getSupportFragmentManager().findFragmentByTag(LoginErrorDialogFragment.FRAGMENT_TAG) == null) {
								String title = response.getError().getErrorUserTitle();
								String message = response.getError().getErrorUserMessage();
								LoginErrorDialogFragment fragment = LoginErrorDialogFragment.newInstance(title, message);
								fragment.show(getSupportFragmentManager(), LoginErrorDialogFragment.FRAGMENT_TAG);
							}
						} else {
							final String id = user.getId();
							final String token = session.getAccessToken();
							UserRequest loginRequest = UserRequest.newLoginRequest(AuthenticateActivity.this,
									ThirdParty.FACEBOOK, id, token, new com.android.volley.Response.Listener<User>() {
										@Override
										public void onResponse(User response) {
											LogEx.d(String.format("response=%s", response));
										}
									}, new com.android.volley.Response.ErrorListener() {
										@Override
										public void onErrorResponse(VolleyError error) {
											LogEx.d(String.format("error=%s", error));

											// There is no CM user linked to this Facebook account. Take them to the Create Account screen
											Intent intent = new Intent(AuthenticateActivity.this, LoginActivity.class);
											IntentUtils.putLoginMode(intent, LoginActivity.CREATE_ACCOUNT_MODE);
											IntentUtils.putThirdPartyUser(intent, session, user);
											AuthenticateActivity.this.startActivityForResult(intent, REQUEST_CODE_CREATE_ACCOUNT);
										}
									});
							VolleyManager.getInstance(AuthenticateActivity.this).getRequestQueue().add(loginRequest);
						}
						LogEx.i(String.format("user=%s", user));
					}
				}).executeAsync();
			} else if (exception != null) {
				if (getSupportFragmentManager().findFragmentByTag(LoginErrorDialogFragment.FRAGMENT_TAG) == null) {
					LoginErrorDialogFragment fragment = LoginErrorDialogFragment.newInstance(getTitle(), exception.getMessage());
					fragment.show(getSupportFragmentManager(), LoginErrorDialogFragment.FRAGMENT_TAG);
				}
			}
	}

	public void onButtonClick(View view) {
		int id = view.getId();
		switch (id) {
			case R.id.login_authenticate_facebook_button: {
				Session session = Session.getActiveSession();
				if (session == null || session.isOpened() || session.isClosed()) {
					Session.openActiveSession(this, true, Arrays.asList(FACEBOOK_READ_PERSMISSIONS), mStatusCallback);
				} else {
					session.openForRead(new Session.OpenRequest(this)
							.setPermissions(Arrays.asList(FACEBOOK_READ_PERSMISSIONS))
							.setCallback(mStatusCallback));
				}
			}
			case R.id.login_authenticate_google_button: {
				LogEx.d(((Button) view).getText().toString());
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
				onContinue();
				break;
			}
		}
	}

	public void onContinue() {
		if (mStartupMode) {
			startActivity(new Intent(this, MainActivity.class));
		}
		finish();
	}
}
