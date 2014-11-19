package com.citymaps.mobile.android.thirdpartynew;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;

import java.util.List;

public class FacebookProxy extends ThirdPartyProxy
		implements Session.StatusCallback {

	private List<String> mReadPermissions;
	private List<String> mWritePermissions;

	private ProxyCallbacks mProxyCallbacks;

	private UiLifecycleHelper mUiLifecycleHelper;

	public FacebookProxy(Activity activity, List<String> readPermissions, List<String> writePermissions,
						 ProxyCallbacks proxyCallbacks) {
		super(activity);
		mReadPermissions = readPermissions;
		mWritePermissions = writePermissions;
		mProxyCallbacks = proxyCallbacks;
		mUiLifecycleHelper = new UiLifecycleHelper(activity, this);
	}

	public FacebookProxy(Activity activity, List<String> readPermissions, ProxyCallbacks proxyCallbacks) {
		this(activity, readPermissions, null, proxyCallbacks);
	}

	/* Lifecycle methods */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mUiLifecycleHelper.onCreate(savedInstanceState);
	}

	@Override
	public void onResume() {
		super.onResume();
		mUiLifecycleHelper.onResume();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mUiLifecycleHelper.onSaveInstanceState(outState);
	}

	@Override
	public void onPause() {
		super.onPause();
		mUiLifecycleHelper.onPause();
	}

	@Override
	public void onStop() {
		super.onStop();
		mUiLifecycleHelper.onStop();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mUiLifecycleHelper.onDestroy();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		mUiLifecycleHelper.onActivityResult(requestCode, resultCode, data);
	}

	/* Methods */

	@Override
	public void connect(boolean interactive) {
		super.connect(interactive);
		Session session = Session.getActiveSession();
		if (session != null && !session.isOpened() && !session.isClosed()) {
			session.openForRead(new Session.OpenRequest(mActivity)
					.setPermissions(mReadPermissions)
					.setCallback(this));
		} else {
			Session.openActiveSession(mActivity, true, mReadPermissions, this);
		}
	}

	/* Facebook callbacks */

	@Override
	public void call(Session session, SessionState state, Exception exception) {
		if (exception != null) {
			if (mProxyCallbacks != null) {
				mProxyCallbacks.onError(this, session, state, exception);
			}
		} else if (session.isOpened()) {
			if (mProxyCallbacks != null) {
				mProxyCallbacks.onConnected(this, session, state);
			}
		} else if (session.isClosed()) {
			if (mProxyCallbacks != null) {
				mProxyCallbacks.onDisconnected(this, session, state);
			}
		} else if (SessionState.OPENING.equals(state)) {
			if (mProxyCallbacks != null) {
				mProxyCallbacks.onConnecting(this, session, state);
			}
		}
	}

	public static interface ProxyCallbacks {
		public void onConnecting(FacebookProxy proxy, Session session, SessionState state);
		public void onConnected(FacebookProxy proxy, Session session, SessionState state);
		public void onError(FacebookProxy proxy, Session session, SessionState state, Exception exception);
		public void onDisconnected(FacebookProxy proxy, Session session, SessionState state);
	}
}
