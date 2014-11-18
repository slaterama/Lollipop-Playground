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

import java.util.List;

public class FacebookProxy extends ThirdPartyProxy
		implements Session.StatusCallback {

	private static final String CONNECTION_OPEN = FacebookProxy.class.getName() + ".connectionOpen";

	private List<String> mReadPermissions;

	private List<String> mWritePermissions;

	private Session mSession;

	private UiLifecycleHelper mUiLifecycleHelper;

	public FacebookProxy(Activity activity, List<String> readPermissions, List<String> writePermissions) {
		super(activity);
		mReadPermissions = readPermissions;
		mWritePermissions = writePermissions;
	}

	public FacebookProxy(Activity activity, List<String> readPermissions) {
		this(activity, readPermissions, null);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			boolean connectionOpen = savedInstanceState.getBoolean(CONNECTION_OPEN, false);
			if (connectionOpen) {
				mUiLifecycleHelper = newUiLifecycleHelper();
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
		outState.putBoolean(CONNECTION_OPEN, mUiLifecycleHelper != null);
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

	/*
	@Override
	public Connection openConnection() {
		return new FacebookConnection();
	}
	*/

	@Override
	public void openConnection(boolean interactive) {
		mUiLifecycleHelper = newUiLifecycleHelper();
		mSession = Session.openActiveSession(mActivity, interactive, mReadPermissions, this);
	}

	@Override
	public void closeConnection() {
		if (mSession != null) {
			mSession.close();
			mSession = null;
		}
		mUiLifecycleHelper = null;
	}

	/*
	public class FacebookConnection extends Connection {

		@Override
		public void connect() {
			mUiLifecycleHelper = newUiLifecycleHelper();
			mSession = Session.openActiveSession(mActivity, mInteractive, mReadPermissions, FacebookProxy.this);
		}
	}
	*/

	private UiLifecycleHelper newUiLifecycleHelper() {
		return new UiLifecycleHelper(mActivity, this);
	}

	/* Callbacks */

	@Override
	public void call(Session session, SessionState state, Exception exception) {
		if (LogEx.isLoggable(LogEx.INFO)) {
			LogEx.i(String.format("session=%s, state=%s, exception=%s", session, state, exception));
		}

		if (session.isOpened()) {
			Toast.makeText(mActivity, "Facebook user is connected!", Toast.LENGTH_SHORT).show();
		}
	}

	public static class UserRequest extends Request<GraphUser, FacebookRequestError> {
		public UserRequest(Listener<GraphUser> listener, ErrorListener<FacebookRequestError> errorListener) {
			super(listener, errorListener);
		}
	}
}
