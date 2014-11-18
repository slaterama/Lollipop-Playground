package com.citymaps.mobile.android.thirdparty;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.facebook.*;
import com.facebook.model.GraphUser;

import java.util.List;

public class FacebookProxy extends ThirdPartyProxy
		implements Session.StatusCallback {

	private static final String STATE_KEY_LAST_HANDLED_STATE = FacebookProxy.class.getName() + ".lastHandledState";

	private static final String STATE_KEY_CONNECTION_OPEN = FacebookProxy.class.getName() + ".connectionOpen";

	private List<String> mReadPermissions;

	private List<String> mWritePermissions;

	private Callbacks mCallbacks;

	private Session mSession;

	private UiLifecycleHelper mUiLifecycleHelper;

	private SessionState mLastHandledState;

	public FacebookProxy(Activity activity, List<String> readPermissions, List<String> writePermissions, Callbacks callbacks) {
		super(activity);
		mReadPermissions = readPermissions;
		mWritePermissions = writePermissions;
		mCallbacks = callbacks;
	}

	public FacebookProxy(Activity activity, List<String> readPermissions, Callbacks callbacks) {
		this(activity, readPermissions, null, callbacks);
	}

	public FacebookProxy(Activity activity, List<String> readPermissions) {
		this(activity, readPermissions, null, null);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			mLastHandledState = (SessionState) savedInstanceState.getSerializable(STATE_KEY_LAST_HANDLED_STATE);
			boolean connectionOpen = savedInstanceState.getBoolean(STATE_KEY_CONNECTION_OPEN, false);
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
			if (session != null && (session.isOpened() || session.isClosed())) {
				call(session, session.getState(), null);
			}

			mUiLifecycleHelper.onResume();
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable(STATE_KEY_LAST_HANDLED_STATE, mLastHandledState);
		outState.putBoolean(STATE_KEY_CONNECTION_OPEN, mUiLifecycleHelper != null);
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

	private UiLifecycleHelper newUiLifecycleHelper() {
		return new UiLifecycleHelper(mActivity, this);
	}

	@Override
	protected void processRequest(final Request request) {
		if (request instanceof UserRequest) {
			final UserRequest userRequest = (UserRequest) request;
			com.facebook.Request.newMeRequest(mSession, new com.facebook.Request.GraphUserCallback() {
				@Override
				public void onCompleted(GraphUser user, Response response) {
					if (user == null) {
						if (userRequest.mErrorListener != null) {
							userRequest.mErrorListener.onErrorResponse(response.getError());
						}
					} else {
						if (userRequest.mListener != null) {
							userRequest.mListener.onResponse(user);
						}
					}
				}
			}).executeAsync();
		} else {
			super.processRequest(request);
		}
	}

	/* Callbacks */

	@Override
	public void call(Session session, SessionState state, Exception exception) {
		mSession = session;

		// Always handle exceptions
		if (exception != null) {
			if (mCallbacks != null) {
				mCallbacks.onError(this, session, state, exception);
			}
		}

		if (state != mLastHandledState) {
			if (mCallbacks != null && exception == null) {
				mCallbacks.onSessionStateChange(this, session, state);
			}
			mLastHandledState = state;
		}
	}

	public static class UserRequest extends Request<GraphUser, FacebookRequestError> {
		public UserRequest(Listener<GraphUser> listener, ErrorListener<FacebookRequestError> errorListener) {
			super(listener, errorListener);
		}
	}

	public static interface Callbacks {
		public void onSessionStateChange(ThirdPartyProxy proxy, Session session, SessionState state);
		public void onError(ThirdPartyProxy proxy, Session session, SessionState state, Exception exception);
	}
}
