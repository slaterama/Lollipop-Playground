package com.citymaps.mobile.android.thirdparty;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import com.citymaps.mobile.android.util.FacebookUtils;
import com.facebook.*;
import com.facebook.model.GraphUser;

import java.util.HashMap;
import java.util.Map;

public class ThirdPartyConnectionFacebook extends ThirdPartyConnection<GraphUser, FacebookRequestError>
		implements Session.StatusCallback {

	private UiLifecycleHelper mHelper;

	private Session mSession = null;

	private SessionState mLastState = null;

	protected ThirdPartyConnectionFacebook(Activity activity) {
		super(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mHelper = new UiLifecycleHelper(mActivity, this);
		mHelper.onCreate(savedInstanceState);
	}

	@Override
	public void onResume() {
		super.onResume();
		if (isActive()) {
			// For scenarios where the main activity is launched and user
			// session is not null, the session state change notification
			// may not be triggered. Trigger it if it's open/closed.
			Session session = Session.getActiveSession();
			if (session != null && (session.isOpened() || session.isClosed())) {
				call(session, session.getState(), null);
			}
		}
		mHelper.onResume();
	}

	@Override
	public void onSaveInstanceState(@NonNull Bundle outState) {
		super.onSaveInstanceState(outState);
		mHelper.onSaveInstanceState(outState);
	}

	@Override
	public void onPause() {
		super.onPause();
		mHelper.onPause();
	}

	@Override
	public void onStop() {
		super.onStop();
		mHelper.onStop();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mHelper.onDestroy();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		mHelper.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public ThirdParty getThirdParty() {
		return ThirdParty.FACEBOOK;
	}

	@Override
	public void getToken(TokenCallbacks callbacks) {
		if (mSession == null) {
			callbacks.onTokenError(new FacebookAuthorizationException("No active session"));
		} else {
			callbacks.onToken(mSession.getAccessToken());
		}
	}

	@Override
	public void getUser(final UserCallbacks<GraphUser, FacebookRequestError> callbacks) {
		Request.newMeRequest(Session.getActiveSession(), new Request.GraphUserCallback() {
			@Override
			public void onCompleted(GraphUser user, Response response) {
				if (user == null) {
					callbacks.onUserError(response.getError());
				} else {
					callbacks.onUser(user);
				}
			}
		}).executeAsync();
	}

	@Override
	public boolean isConnecting() {
		boolean connecting = false;
		if (mSession != null) {
			SessionState state = mSession.getState();
			if (state != null) {
				connecting = state.equals(SessionState.OPENING);
			}
		}
		return connecting;
	}

	@Override
	public boolean isConnected() {
		boolean connecting = false;
		if (mSession != null) {
			SessionState state = mSession.getState();
			if (state != null) {
				connecting = state.isOpened();
			}
		}
		return connecting;
	}

	@Override
	public void connect(boolean silent, Callbacks callbacks) {
		super.connect(silent, callbacks);
		Session session = Session.getActiveSession();
		if (session == null || session.isOpened() || session.isClosed()) {
			Session.openActiveSession(mActivity, !silent, FacebookUtils.FACEBOOK_READ_PERMISSIONS_LIST, this);
		} else {
			if (!silent) {
				session.openForRead(new Session.OpenRequest(mActivity)
						.setPermissions(FacebookUtils.FACEBOOK_READ_PERMISSIONS_LIST)
						.setCallback(this));
			}
		}
	}

	@Override
	public void disconnect() {
		super.disconnect();
		if (mSession != null) {
			mSession.close();
		}
	}

	@Override
	public void call(Session session, SessionState state, Exception exception) {
		mSession = session;
		if (state != mLastState) {
			mLastState = state;
			if (mCallbacks != null) {
				Map<String, Object> args = new HashMap<String, Object>(3);
				args.put("session", session);
				args.put("state", state);
				args.put("exception", exception);
				mCallbacks.onConnectionStateChange(this, args);
			}
		}
	}
}
