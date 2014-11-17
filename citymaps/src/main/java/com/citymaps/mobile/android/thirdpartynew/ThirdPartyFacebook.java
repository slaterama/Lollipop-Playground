package com.citymaps.mobile.android.thirdpartynew;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import com.citymaps.mobile.android.util.FacebookUtils;
import com.citymaps.mobile.android.util.LogEx;
import com.facebook.*;
import com.facebook.model.GraphUser;

public class ThirdPartyFacebook extends ThirdParty
		implements Session.StatusCallback {

	private UiLifecycleHelper mHelper;

	private SessionState mLastState = null;

	private TokenCallbacks mPendingTokenCallbacks = null;

	private UserCallbacks mPendingUserCallbacks = null;

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

	@Override
	public void getToken(Mode mode, TokenCallbacks callbacks) {
		Session session = Session.getActiveSession();
		if (LogEx.isLoggable(LogEx.INFO)) {
			LogEx.i(String.format("session=%s", session));
		}
		if (session == null || !session.isOpened()) {
			mPendingTokenCallbacks = callbacks;
			boolean allowLoginUI = (mode != null && mode.equals(Mode.INTERACTIVE));
			Session.setActiveSession(Session.openActiveSession(mActivity, allowLoginUI, FacebookUtils.FACEBOOK_READ_PERMISSIONS_LIST, this));
		} else {
			if (callbacks != null) {
				callbacks.onSuccess(session.getAccessToken());
			}
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
	public void getUser(Mode mode, final UserCallbacks callbacks) {
		Session session = Session.getActiveSession();
		if (LogEx.isLoggable(LogEx.INFO)) {
			LogEx.i(String.format("session=%s", session));
		}
		if (session == null || !session.isOpened()) {
			mPendingUserCallbacks = callbacks;
			boolean allowLoginUI = (mode != null && mode.equals(Mode.INTERACTIVE));
			Session.setActiveSession(Session.openActiveSession(mActivity, allowLoginUI, FacebookUtils.FACEBOOK_READ_PERMISSIONS_LIST, this));
		} else {
			if (callbacks != null) {
				Request.newMeRequest(session, new Request.GraphUserCallback() {
					@Override
					public void onCompleted(GraphUser user, Response response) {
						if (user == null) {
							callbacks.onError(response);
						} else {
							UserProxy proxy = new FacebookUserProxy(user);
							callbacks.onSuccess(proxy);
						}
					}
				}).executeAsync();
			}
		}
	}

	@Override
	public void call(Session session, @NonNull SessionState state, Exception exception) {
		if (state != mLastState) {
			mLastState = state;
			if (state.isOpened()) {
				if (mConnectionCallbacks != null) {
					mConnectionCallbacks.onConnected(this);
				}

				if (mPendingTokenCallbacks != null) {
					mPendingTokenCallbacks.onSuccess(session.getAccessToken());
					mPendingTokenCallbacks = null;
				}

				if (mPendingUserCallbacks != null) {
					Request.newMeRequest(session, new Request.GraphUserCallback() {
						@Override
						public void onCompleted(GraphUser user, Response response) {
							if (user == null) {
								mPendingUserCallbacks.onError(response);
								mPendingUserCallbacks = null;
							} else {
								UserProxy proxy = new FacebookUserProxy(user);
								mPendingUserCallbacks.onSuccess(proxy);
								mPendingUserCallbacks = null;
							}
						}
					}).executeAsync();
				}
			} else if (exception != null) {
				if (mConnectionCallbacks != null) {
					mConnectionCallbacks.onError(this);
				}

				if (mPendingTokenCallbacks != null) {
					mPendingTokenCallbacks.onError(null);
					mPendingTokenCallbacks = null;
				}

				if (mPendingUserCallbacks != null) {
					mPendingUserCallbacks.onError(null);
					mPendingUserCallbacks = null;
				}
			}
		}
	}

	public class FacebookUserProxy implements UserProxy {
		private GraphUser mGraphUser;

		public FacebookUserProxy(@NonNull GraphUser graphUser) {
			super();
			mGraphUser = graphUser;
		}

		@Override
		public String getFirstName() {
			return mGraphUser.getFirstName();
		}

		@Override
		public String getLastName() {
			return mGraphUser.getLastName();
		}
	}
}
