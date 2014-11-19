package com.citymaps.mobile.android.thirdpartynew;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import com.facebook.*;
import com.facebook.model.GraphUser;

import java.util.*;

public class FacebookProxy extends ThirdPartyProxy<Session, FacebookProxy.Callbacks>
		implements Session.StatusCallback {

	public static final String DATA_ME = "me";

	private List<String> mReadPermissions;
	private List<String> mWritePermissions;

	private UiLifecycleHelper mUiLifecycleHelper;

	public FacebookProxy(Activity activity, List<String> readPermissions, List<String> writePermissions) {
		super(activity);
		mReadPermissions = readPermissions;
		mWritePermissions = writePermissions;
		mUiLifecycleHelper = new UiLifecycleHelper(activity, this);
	}

	public FacebookProxy(Activity activity, List<String> readPermissions) {
		this(activity, readPermissions, null);
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
		Session session = Session.getActiveSession();
		if (session != null && !session.isOpened() && !session.isClosed()) {
			session.openForRead(new Session.OpenRequest(mActivity)
					.setPermissions(mReadPermissions)
					.setCallback(this));
		} else {
			Session.openActiveSession(mActivity, true, mReadPermissions, this);
		}
	}

	@Override
	public void requestData(final Session session, final List<String> names, final OnDataListener listener) {
		new DataTask(this, names, listener) {
			@Override
			protected Void doInBackground(Void... params) {
				if (mNames != null) {
					Map<Request, String> requestMap = new HashMap<Request, String>();
					for (String name : mNames) {
						if (TextUtils.equals(name, DATA_TOKEN)) {
							mData.put(DATA_TOKEN, session.getAccessToken());
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
							mErrors.put(name, error);
						} else if (TextUtils.equals(name, DATA_ME)) {
							mData.put(name, response.getGraphObjectAs(GraphUser.class));
						}
					}
				}
				return null;
			}
		}.executeOnExecutor(DataTask.THREAD_POOL_EXECUTOR);
	}

	/* Facebook callbacks */

	@Override
	public void call(Session session, SessionState state, Exception exception) {
		if (exception != null) {
			if (mCallbacks != null) {
				mCallbacks.onError(this, session, state, exception);
			}
		} else if (session.isOpened()) {
			if (mCallbacks != null) {
				mCallbacks.onConnected(this, session, state);
			}
		} else if (session.isClosed()) {
			if (mCallbacks != null) {
				mCallbacks.onDisconnected(this, session, state);
			}
		} else if (SessionState.OPENING.equals(state)) {
			if (mCallbacks != null) {
				mCallbacks.onConnecting(this, session, state);
			}
		}
	}

	public static interface Callbacks extends ThirdPartyProxy.Callbacks {
		public void onConnecting(FacebookProxy proxy, Session session, SessionState state);
		public void onConnected(FacebookProxy proxy, Session session, SessionState state);
		public void onDisconnected(FacebookProxy proxy, Session session, SessionState state);
		public void onError(FacebookProxy proxy, Session session, SessionState state, Exception exception);
	}

	public static abstract class SimpleCallbacks implements Callbacks {
		@Override
		public void onConnecting(FacebookProxy proxy, Session session, SessionState state) {

		}

		@Override
		public void onConnected(FacebookProxy proxy, Session session, SessionState state) {

		}

		@Override
		public void onDisconnected(FacebookProxy proxy, Session session, SessionState state) {

		}

		@Override
		public void onError(FacebookProxy proxy, Session session, SessionState state, Exception exception) {

		}
	}
}
