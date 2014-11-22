package com.citymaps.mobile.android.thirdparty;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import com.citymaps.mobile.android.util.CollectionUtils;
import com.citymaps.mobile.android.util.CommonUtils;
import com.facebook.*;
import com.facebook.model.GraphUser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FacebookProxy extends ThirdPartyProxy<Session, FacebookProxy.Callbacks>
		implements Session.StatusCallback {

	public static final String DATA_ME = "me";

	private List<String> mReadPermissions;
	private List<String> mWritePermissions;

	private UiLifecycleHelper mUiLifecycleHelper;

	public FacebookProxy(FragmentActivity activity, List<String> readPermissions, List<String> writePermissions) {
		super(activity);
		mReadPermissions = readPermissions;
		mWritePermissions = writePermissions;
		mUiLifecycleHelper = new UiLifecycleHelper(activity, this);
	}

	public FacebookProxy(FragmentActivity activity, List<String> readPermissions) {
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
			Session.openActiveSession(mActivity, interactive, mReadPermissions, this);
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
				}
				return null;
			}

			@Override
			protected void handleErrors(Map<String, Object> errors) {
				Object error = CollectionUtils.getFirstValue(errors);
				if (error instanceof FacebookRequestError) {
					FacebookRequestError requestError = (FacebookRequestError) error;
					if (requestError.shouldNotifyUser()) {
						String message = requestError.getErrorUserMessage();
						if (!TextUtils.isEmpty(message)) {
							CommonUtils.showSimpleDialogFragment(mActivity.getSupportFragmentManager(),
									mActivity.getTitle(), message);
						}
					}
				}
			}
		}.executeOnExecutor(DataTask.THREAD_POOL_EXECUTOR);
	}

	/* Facebook callbacks */

	@Override
	public void call(Session session, SessionState state, Exception exception) {
		if (exception != null) {
			boolean handled = false;
			if (mCallbacks != null) {
				handled = mCallbacks.onError(this, session, state, exception);
			}
			if (!handled) {
				if (exception instanceof FacebookOperationCanceledException) {
					// The default behavior is not to show a message
				} else /* if (exception instanceof FacebookException) */ {
					CommonUtils.showSimpleDialogFragment(mActivity.getSupportFragmentManager(),
							mActivity.getTitle(), exception.getLocalizedMessage());
				}
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
		public boolean onError(FacebookProxy proxy, Session session, SessionState state, Exception exception);
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
		public boolean onError(FacebookProxy proxy, Session session, SessionState state, Exception exception) {
			return false;
		}
	}
}
