package com.citymaps.mobile.android.thirdparty2;

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

	private List<String> mReadPermissions;

	private List<String> mWritePermissions;

	private UiLifecycleHelper mUiLifecycleHelper;

	private Session mSession;

	public FacebookProxy(FragmentActivity activity, List<String> readPermissions,
						 List<String> writePermissions, Callbacks callbacks) {
		super(activity, callbacks);
		mReadPermissions = readPermissions;
		mWritePermissions = writePermissions;
	}

	public FacebookProxy(Fragment fragment, List<String> readPermissions,
						 List<String> writePermissions, Callbacks callbacks) {
		super(fragment, callbacks);
		mReadPermissions = readPermissions;
		mWritePermissions = writePermissions;
	}

	@Override
	public ThirdParty getThirdParty() {
		return ThirdParty.FACEBOOK;
	}

	@Override
	public void onProxyStart(boolean interactive, Callbacks callbacks) {
		mUiLifecycleHelper = new UiLifecycleHelper(mActivity, this);
		Session session = Session.getActiveSession();
		if (session != null && !session.isOpened() && !session.isClosed()) {
			session.openForRead(new Session.OpenRequest(mActivity)
					.setPermissions(mReadPermissions)
					.setCallback(this));
		} else {
			Session.openActiveSession(mActivity, interactive, mReadPermissions, this);
		}
	}

	@Override
	public void onProxyStop(boolean clear) {
		mUiLifecycleHelper = null;
		if (mSession != null) {
			if (clear) {
				mSession.closeAndClearTokenInformation();
			} else {
				mSession.close();
			}
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (mUiLifecycleHelper != null) {
			mUiLifecycleHelper.onCreate(savedInstanceState);
		}
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onResume() {
		super.onResume();
		if (mUiLifecycleHelper != null) {
			mUiLifecycleHelper.onResume();
		}

		// TODO Do that "onResume" trick of calling call() here
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

	@Override
	public void call(Session session, SessionState state, Exception exception) {
		mSession = session;
		Callbacks callbacks = getCallbacks();
		if (callbacks != null) {
			switch (state) {
				case OPENING:
					callbacks.onConnecting(this, session, state, exception);
					break;
				case OPENED:
				case OPENED_TOKEN_UPDATED:
					callbacks.onConnected(this, session, state, exception);
					break;
				case CLOSED:
				case CLOSED_LOGIN_FAILED:
					if (exception == null || exception instanceof FacebookOperationCanceledException) {
						callbacks.onDisconnected(this, session, state, exception);
					} else {
						callbacks.onError(this, session, state, exception);
					}
					break;
			}
		}
	}

	public static interface Callbacks extends ThirdPartyProxy.AbsCallbacks {
		public void onConnecting(FacebookProxy proxy, Session session, SessionState state, Exception exception);

		public void onConnected(FacebookProxy proxy, Session session, SessionState state, Exception exception);

		public void onDisconnected(FacebookProxy proxy, Session session, SessionState state, Exception exception);

		public void onError(FacebookProxy proxy, Session session, SessionState state, Exception exception);

	}
}
