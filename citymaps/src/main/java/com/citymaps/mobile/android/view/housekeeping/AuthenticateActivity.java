package com.citymaps.mobile.android.view.housekeeping;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import com.citymaps.mobile.android.R;
import com.citymaps.mobile.android.util.IntentUtils;
import com.citymaps.mobile.android.util.LogEx;
import com.citymaps.mobile.android.view.MainActivity;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;

public class AuthenticateActivity extends Activity {

	private static final int REQUEST_CODE_LOGIN = 1;
	private static final int REQUEST_CODE_CREATE_ACCOUNT = 2;

	boolean mStartupMode;

	UiLifecycleHelper mUiLifecycleHelper;

	Session mSession;

	Session.StatusCallback mStatusCallback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state, Exception exception) {
			onSessionStateChange(session, state, exception);
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
		if (session != null && (session.isOpened() || session.isClosed()) ) {
			onSessionStateChange(session, session.getState(), null);
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

	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
		if (LogEx.isLoggable(LogEx.INFO)) {
			if (state.isOpened()) {
				LogEx.i("Logged in to Facebook");
			} else {
				LogEx.i("Logged out of Facebook");
			}
		}
	}

	public void onButtonClick(View view) {
		int id = view.getId();
		switch (id) {
			case R.id.login_authenticate_facebook_button: {
				if (mSession == null || !mSession.isOpened()) {
					mSession = Session.openActiveSession(this, true, mStatusCallback);
				}
				break;
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
