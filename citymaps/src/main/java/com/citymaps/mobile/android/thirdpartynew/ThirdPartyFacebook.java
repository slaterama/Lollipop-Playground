package com.citymaps.mobile.android.thirdpartynew;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import com.citymaps.mobile.android.util.FacebookUtils;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;

public class ThirdPartyFacebook extends ThirdParty
		implements Session.StatusCallback {

	private UiLifecycleHelper mHelper;

	private SessionState mLastState = null;

	private TokenCallbacks mPendingTokenCallbacks = null;

	public ThirdPartyFacebook(Activity activity, ConnectionCallbacks callbacks) {
		super(activity, callbacks);
	}

	@Override
	public Type getType() {
		return Type.FACEBOOK;
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

		// For scenarios where the main activity is launched and user
		// session is not null, the session state change notification
		// may not be triggered. Trigger it if it's open/closed.
		Session session = Session.getActiveSession();
		if (session != null && (session.isOpened() || session.isClosed())) {
			call(session, session.getState(), null);
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

	/*
	@Override
	public void onActivate(boolean silent) {
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
	public void onDeactivate() {
		Session session = Session.getActiveSession();
		if (session.isOpened()) {
			session.close(); // ???
		}
	}
	*/

	@Override
	public void getToken(TokenCallbacks callbacks) {
		Session session = Session.getActiveSession();
		if (session == null) {
			if (callbacks != null) {
				callbacks.onToken(session.getAccessToken());
			}
		} else {
			mPendingTokenCallbacks = callbacks;
			Session.openActiveSession(mActivity, true, FacebookUtils.FACEBOOK_READ_PERMISSIONS_LIST, this);
		}

		/*
		if (session == null || session.isOpened() || session.isClosed()) {
			Session.openActiveSession(mActivity, true, FacebookUtils.FACEBOOK_READ_PERMISSIONS_LIST, this);
		} else {
			session.openForRead(new Session.OpenRequest(mActivity)
					.setPermissions(FacebookUtils.FACEBOOK_READ_PERMISSIONS_LIST)
					.setCallback(this));
		}
		*/
	}

	@Override
	public void call(Session session, @NonNull SessionState state, Exception exception) {
//		mSession = session;
		if (state != mLastState) {
//			boolean justConnected = ((mLastState == null || !mLastState.isOpened()) && (state != null && state.isOpened()));

			mLastState = state;

			/*
			if (justConnected) {
				if (mCallbacks != null) {
					mCallbacks.onConnected(this);
				}
			}
			*/

			if (state.isOpened()) {
				if (mConnectionCallbacks != null) {
					mConnectionCallbacks.onConnected(this);
				}

				if (mPendingTokenCallbacks != null) {
					mPendingTokenCallbacks.onToken(session.getAccessToken());
					mPendingTokenCallbacks = null;
				}
			} else if (exception != null) {
				if (mConnectionCallbacks != null) {
					mConnectionCallbacks.onError(this);
				}

				if (mPendingTokenCallbacks != null) {
					mPendingTokenCallbacks.onTokenError(null);
					mPendingTokenCallbacks = null;
				}
			}
		}
	}
}
