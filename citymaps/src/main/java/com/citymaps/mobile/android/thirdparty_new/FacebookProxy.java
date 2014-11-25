package com.citymaps.mobile.android.thirdparty_new;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import com.citymaps.mobile.android.model.ThirdParty;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;

import java.util.List;

public class FacebookProxy extends ThirdPartyProxy<FacebookProxy.Callbacks>
		implements Session.StatusCallback {

	protected List<String> mReadPermissions;

	protected List<String> mWritePermissions;

	protected UiLifecycleHelper mUiLifecycleHelper;

	protected Session mSession;

	protected SessionState mState;

	public FacebookProxy(FragmentActivity activity, List<String> readPermissions, List<String> writePermissions, Callbacks callbacks) {
		super(activity, callbacks);
		mReadPermissions = readPermissions;
		mWritePermissions = writePermissions;
	}

	public FacebookProxy(Fragment fragment, List<String> readPermissions, List<String> writePermissions, Callbacks callbacks) {
		super(fragment, callbacks);
		mReadPermissions = readPermissions;
		mWritePermissions = writePermissions;
	}

	@Override
	public ThirdParty getThirdParty() {
		return ThirdParty.FACEBOOK;
	}

	@Override
	protected void onActivate(boolean interactive, Callbacks callbacks) {
		mUiLifecycleHelper = new UiLifecycleHelper(mActivity, this);
		Session session = Session.getActiveSession();
		if (session != null && !session.isOpened() && !session.isClosed()) {
			Session.OpenRequest request = (mFragment == null
					? new Session.OpenRequest(mActivity)
					: new Session.OpenRequest(mFragment));
			session.openForRead(request
					.setPermissions(mReadPermissions)
					.setCallback(this));
		} else if (mFragment == null) {
			Session.openActiveSession(mActivity, interactive, mReadPermissions, this);
		} else {
			Session.openActiveSession(mActivity, mFragment, interactive, mReadPermissions, this);
		}
	}

	@Override
	protected void onActivateWithToken(String token, boolean interactive, Callbacks callbacks) {
		mUiLifecycleHelper = new UiLifecycleHelper(mActivity, this);

		// NOTE For now, trying this with cached token as opposed to creating accessToken myself
		/*
		AccessToken accessToken = AccessToken.createFromExistingAccessToken(token, null, null, null, mReadPermissions);
		Session.openActiveSessionWithAccessToken(mActivity, accessToken, this);
		*/
		Session.openActiveSessionFromCache(mActivity);
	}

	@Override
	protected void onDeactivate(boolean clearToken) {
		mUiLifecycleHelper = null;
	}

	/* Lifecycle methods */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (mActivated) {
			mUiLifecycleHelper = new UiLifecycleHelper(mActivity, this);
			mUiLifecycleHelper.onCreate(savedInstanceState);
		}
	}

	@Override
	public void onResume() {
		super.onResume();

		// For scenarios where the main activity is launched and user
		// session is not null, the session state change notification
		// may not be triggered. Trigger it if it's open/closed.
		Session session = Session.getActiveSession();
		if (session != null && (session.isOpened() || session.isClosed())) {
			call(session, session.getState(), null);
		}

		if (mUiLifecycleHelper != null) {
			mUiLifecycleHelper.onResume();
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
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

	/* Callbacks */

	@Override
	public void call(Session session, SessionState state, Exception exception) {
		mSession = session;
		if (state != mState) {
			mState = state;
			if (mCallbacks != null) {
				switch (state) {
					case OPENING:
						mCallbacks.onConnecting(this, session, state, exception);
						break;
					case OPENED:
					case OPENED_TOKEN_UPDATED:
						mCallbacks.onConnected(this, session, state, exception);
						break;
					case CLOSED:
					case CLOSED_LOGIN_FAILED:
						if (exception == null) {
							mCallbacks.onDisconnected(this, session, state, null);
						} else {
							boolean cancelled = (exception instanceof FacebookOperationCanceledException);
							mCallbacks.onFailed(this, cancelled, session, state, exception);
						}
						break;
				}
			}
		}
	}

	/* Interfaces */

	public static interface Callbacks extends ThirdPartyProxy.Callbacks {
		public void onConnecting(FacebookProxy proxy, Session session, SessionState state, Exception exception);

		public void onConnected(FacebookProxy proxy, Session session, SessionState state, Exception exception);

		public void onDisconnected(FacebookProxy proxy, Session session, SessionState state, Exception exception);

		public boolean onFailed(FacebookProxy proxy, boolean cancelled, Session session, SessionState state, Exception exception);
	}

	public static abstract class SimpleCallbacks implements Callbacks {
		@Override
		public void onConnecting(FacebookProxy proxy, Session session, SessionState state, Exception exception) {
		}

		@Override
		public void onConnected(FacebookProxy proxy, Session session, SessionState state, Exception exception) {
		}

		@Override
		public void onDisconnected(FacebookProxy proxy, Session session, SessionState state, Exception exception) {
		}

		@Override
		public boolean onFailed(FacebookProxy proxy, boolean cancelled, Session session, SessionState state, Exception exception) {
			return false;
		}
	}
}
