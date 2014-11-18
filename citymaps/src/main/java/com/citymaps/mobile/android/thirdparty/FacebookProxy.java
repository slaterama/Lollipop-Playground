package com.citymaps.mobile.android.thirdparty;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import com.citymaps.mobile.android.util.LogEx;
import com.facebook.FacebookRequestError;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;

import java.util.Arrays;

public class FacebookProxy extends ThirdPartyProxy
		implements Session.StatusCallback {

	private static final String STATE_KEY_INVOKED = FacebookProxy.class.getName() + ".invoked";

	private Session mSession;

	private UiLifecycleHelper mUiLifecycleHelper;

	public FacebookProxy(Activity activity) {
		super(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			boolean invoked = savedInstanceState.getBoolean(STATE_KEY_INVOKED, false);
			if (invoked) {
				mUiLifecycleHelper = new UiLifecycleHelper(mActivity, this);
				mUiLifecycleHelper.onCreate(savedInstanceState);
			}
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (mUiLifecycleHelper != null) {

			// For scenarios where the main activity is launched and user
			// session is not null, the session state change notification
			// may not be triggered. Trigger it if it's open/closed.
			Session session = Session.getActiveSession();
			if (session != null && (session.isOpened() || session.isClosed()) ) {
				call(session, session.getState(), null);
			}

			mUiLifecycleHelper.onResume();
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean(STATE_KEY_INVOKED, mUiLifecycleHelper != null);
		if (mUiLifecycleHelper != null) {
			mUiLifecycleHelper.onSaveInstanceState(outState);
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		if (mUiLifecycleHelper != null) {
			mUiLifecycleHelper.onPause();
		}
	}

	@Override
	public void onStop() {
		super.onStop();
		if (mUiLifecycleHelper != null) {
			mUiLifecycleHelper.onStop();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mUiLifecycleHelper != null) {
			mUiLifecycleHelper.onDestroy();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (mUiLifecycleHelper != null) {
			mUiLifecycleHelper.onActivityResult(requestCode, resultCode, data);
		}
	}

	@Override
	public Connection newConnection() {
		return new FacebookConnection();
	}

	@Override
	public void disconnect() {
		mSession = null;
		mUiLifecycleHelper = null;
	}

	public class FacebookConnection extends Connection<String> {

		@Override
		public void connect() {
			mUiLifecycleHelper = new UiLifecycleHelper(mActivity, FacebookProxy.this);
			mSession = Session.getActiveSession();
			if (mSession == null) {
				mSession = Session.openActiveSession(mActivity, mInteractive, Arrays.asList(mPermissions), FacebookProxy.this);
			} else if (mSession.isOpened()) {
				Toast.makeText(mActivity, "Facebook user is connected!", Toast.LENGTH_SHORT).show();
			}
		}
	}

	@Override
	public void call(Session session, SessionState state, Exception exception) {
		if (LogEx.isLoggable(LogEx.INFO)) {
			LogEx.i(String.format("session=%s, state=%s, exception=%s", session, state, exception));
		}
	}

	public static class UserRequest extends Request<GraphUser, FacebookRequestError> {
		public UserRequest(Listener<GraphUser> listener, ErrorListener<FacebookRequestError> errorListener) {
			super(listener, errorListener);
		}
	}
}
