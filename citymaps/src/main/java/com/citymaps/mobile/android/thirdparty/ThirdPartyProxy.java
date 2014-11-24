package com.citymaps.mobile.android.thirdparty;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import com.citymaps.mobile.android.model.ThirdParty;

import com.citymaps.mobile.android.thirdparty.ThirdPartyProxy.AbsCallbacks;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class ThirdPartyProxy<C extends AbsCallbacks> {

	private static final String CLASSNAME = ThirdPartyProxy.class.getName();
	private static final String STATE_KEY_ACTIVE = CLASSNAME + ".active";
	private static final String STATE_KEY_INTERACTIVE = CLASSNAME + ".interactive";

	public static final String DATA_TOKEN = "token";

	protected FragmentActivity mActivity;

	protected Fragment mFragment;

	private boolean mActive;

	private boolean mInteractive;

	private C mCallbacks;

	public ThirdPartyProxy(FragmentActivity activity, C callbacks) {
		mActivity = activity;
		mFragment = null;
		mCallbacks = callbacks;
	}

	public ThirdPartyProxy(Fragment fragment, C callbacks) {
		mActivity = fragment.getActivity();
		mFragment = fragment;
		mCallbacks = callbacks;
	}

	public abstract ThirdParty getThirdParty();

	public boolean isActive() {
		return mActive;
	}

	public boolean isInteractive() {
		return mInteractive;
	}

	public void setInteractive(boolean interactive) {
		mInteractive = interactive;
	}

	protected C getCallbacks() {
		return mCallbacks;
	}

	public void start(boolean interactive, C callbacks) {
		mActive = true;
		mInteractive = interactive;
		mCallbacks = callbacks;
		onProxyStart(interactive, callbacks);
	}

	public abstract void onProxyStart(boolean interactive, C callbacks);

	public void stop(boolean clear) {
		mActive = false;
		mInteractive = false;
		mCallbacks = null;
		onProxyStop(clear);
	}

	public abstract void onProxyStop(boolean clear);

	public abstract boolean requestData(List<String> names, OnDataListener listener);

	public void onCreate(Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			mActive = savedInstanceState.getBoolean(STATE_KEY_ACTIVE);
			mInteractive = savedInstanceState.getBoolean(STATE_KEY_INTERACTIVE);
		}
	}

	public void onStart() {

	}

	public void onResume() {

	}

	public void onSaveInstanceState(Bundle outState) {
		outState.putBoolean(STATE_KEY_ACTIVE, mActive);
		outState.putBoolean(STATE_KEY_INTERACTIVE, mInteractive);
	}

	public void onPause() {

	}

	public void onStop() {

	}

	public void onDestroy() {

	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {

	}

	protected abstract static class DataTask extends AsyncTask<Void, Void, Void> {
		protected ThirdPartyProxy mProxy;
		protected List<String> mNames;
		protected OnDataListener mListener;

		private Map<String, Object> mData = null;
		private Map<String, Object> mErrors = null;

		public DataTask(ThirdPartyProxy proxy, List<String> names, OnDataListener listener) {
			super();
			mProxy = proxy;
			mNames = names;
			//if (names != null) {
				//int size = names.size();
				//mData = new LinkedHashMap<String, Object>(size);
				//mErrors = new LinkedHashMap<String, Object>(size);
			//}
			mListener = listener;
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			if (mErrors != null) {
				if (mListener != null) {
					mListener.onError(mProxy, Collections.unmodifiableMap(mErrors));
				}
			} else {
				mListener.onData(mProxy, Collections.unmodifiableMap(mData));
			}
		}

		protected void putData(String name, Object data) {
			if (mData == null) {
				mData = new LinkedHashMap<String, Object>();
			}
			mData.put(name, data);
		}

		protected void putError(String name, Object error) {
			if (mErrors == null) {
				mErrors = new LinkedHashMap<String, Object>();
			}
			mErrors.put(name, error);
		}

		protected abstract void handleErrors(Map<String, Object> errors);
	}

	public abstract static interface AbsCallbacks {
	}

	public abstract static interface OnDataListener {
		public void onData(ThirdPartyProxy proxy, Map<String, Object> data);
		public void onError(ThirdPartyProxy proxy, Map<String, Object> errors);
	}
}
