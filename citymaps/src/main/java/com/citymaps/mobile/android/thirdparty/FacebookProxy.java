package com.citymaps.mobile.android.thirdparty;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import com.citymaps.mobile.android.model.ThirdParty;
import com.facebook.*;
import com.facebook.model.GraphUser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FacebookProxy extends ThirdPartyProxy<FacebookProxy.Callbacks>
		implements Session.StatusCallback {

	public static final String DATA_ME = "me";

	protected List<String> mReadPermissions;

	protected List<String> mWritePermissions;

	protected UiLifecycleHelper mUiLifecycleHelper;

	public FacebookProxy(FragmentActivity activity, List<String> readPermissions, List<String> writePermissions, Callbacks callbacks) {
		super(activity, callbacks);
		mReadPermissions = readPermissions;
		mWritePermissions = writePermissions;
	}

	public FacebookProxy(Context context, Fragment fragment, List<String> readPermissions, List<String> writePermissions, Callbacks callbacks) {
		super(context, fragment, callbacks);
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
			Session.OpenRequest request = (mActivity != null
					? new Session.OpenRequest(mActivity)
					: new Session.OpenRequest(mFragment));
			session.openForRead(request
					.setPermissions(mReadPermissions)
					.setCallback(this));
		} else if (mActivity != null) {
			Session.openActiveSession(mActivity, interactive, mReadPermissions, this);
		} else if (mFragment != null) {
			Session.openActiveSession(mContext, mFragment, interactive, mReadPermissions, this);
		}
	}

	@Override
	protected void onDeactivate(boolean clearToken) {
		Session session = Session.getActiveSession();
		if (session != null) {
			if (clearToken) {
				session.closeAndClearTokenInformation();
			} else {
				session.close();
			}
			// mSession = null;
		}
		mUiLifecycleHelper = null;
	}

	@Override
	public boolean requestData(List<String> names, OnDataListener listener) {
		final Session session = Session.getActiveSession();
		if (session == null || names == null) {
			return false;
		}

		new DataTask(listener) {
			@Override
			protected Void doInBackground(String... params) {
				Map<Request, String> requestMap = new HashMap<Request, String>();
				for (String name : params) {
					if (TextUtils.equals(name, DATA_TOKEN)) {
						putData(DATA_TOKEN, session.getAccessToken());
					} else if (TextUtils.equals(name, DATA_ME)) {
						requestMap.put(Request.newMeRequest(session, null), name);
					}
				}
				List<Response> responses = Request.executeBatchAndWait(requestMap.keySet());
				for (Response response : responses) {
					Request request = response.getRequest();
					String name = requestMap.get(request);
					FacebookRequestError error = response.getError();
					if (error != null) {
						putError(name, error);
					} else if (TextUtils.equals(name, DATA_ME)) {
						putData(name, response.getGraphObjectAs(GraphUser.class));
					}
				}
				return null;
			}
		}.executeOnExecutor(DataTask.SERIAL_EXECUTOR, names.toArray(new String[names.size()]));
		return true;
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

		// NOTE: As this is in a proxy and not in a "main activity",
		// I don't think we need to do the code below (taken from FB developer site)

		// For scenarios where the main activity is launched and user
		// session is not null, the session state change notification
		// may not be triggered. Trigger it if it's open/closed.
		/*
		Session session = Session.getActiveSession();
		if (session != null && (session.isOpened() || session.isClosed())) {
			call(session, session.getState(), null);
		}
		*/

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
		// mSession = session;
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
