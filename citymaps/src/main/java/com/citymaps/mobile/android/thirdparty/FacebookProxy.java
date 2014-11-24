package com.citymaps.mobile.android.thirdparty;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import com.citymaps.mobile.android.model.ThirdParty;
import com.citymaps.mobile.android.util.CollectionUtils;
import com.citymaps.mobile.android.util.CommonUtils;
import com.facebook.*;
import com.facebook.model.GraphUser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FacebookProxy extends ThirdPartyProxy<FacebookProxy.Callbacks>
		implements Session.StatusCallback {

	public static final String DATA_ME = "me";

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
		if (mSession != null) {
			if (clear) {
				mSession.closeAndClearTokenInformation();
			} else {
				mSession.close();
			}
		}
		mUiLifecycleHelper = null;
	}

	@Override
	public boolean requestData(List<String> names, OnDataListener listener) {
		if (mSession == null) {
			return false;
		} else {
			new DataTask(this, names, listener) {
				@Override
				protected Void doInBackground(Void... params) {
					if (mNames != null) {
						Map<Request, String> requestMap = new HashMap<Request, String>();
						for (String name : mNames) {
							if (TextUtils.equals(name, DATA_TOKEN)) {
								putData(DATA_TOKEN, mSession.getAccessToken());
							} else if (TextUtils.equals(name, DATA_ME)) {
								requestMap.put(Request.newMeRequest(mSession, null), name);
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
			return true;
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
					if (exception == null) {
						callbacks.onDisconnected(this, session, state, null);
					} else {
						boolean cancelled = (exception instanceof FacebookOperationCanceledException);
						callbacks.onFailed(this, cancelled, session, state, exception);
					}
					break;
			}
		}
	}

	public static interface Callbacks extends ThirdPartyProxy.AbsCallbacks {
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
