package com.citymaps.mobile.android.thirdparty;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import com.citymaps.mobile.android.model.ThirdParty;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class ThirdPartyProxy<C extends ThirdPartyProxy.Callbacks> {

	private static final String STATE_KEY_ACTIVATED = ThirdPartyProxy.class.getName() + ".activated";

	public static final String DATA_TOKEN = "token";

	protected Context mContext;

	protected FragmentActivity mActivity;

	protected Fragment mFragment;

	protected boolean mActivated;

	protected String mToken;

	protected boolean mInteractive;

	protected C mCallbacks;

	public ThirdPartyProxy(FragmentActivity activity, C callbacks) {
		super();
		mContext = activity;
		mActivity = activity;
		mFragment = null;
		mCallbacks = callbacks;
	}

	public ThirdPartyProxy(Context context, Fragment fragment, C callbacks) {
		super();
		mContext = context;
		mActivity = null;
		mFragment = fragment;
		mCallbacks = callbacks;
	}

	public abstract ThirdParty getThirdParty();

	public boolean isActivated() {
		return mActivated;
	}

	public void activate(boolean interactive, C callbacks) {
		mActivated = true;
		mToken = null;
		mInteractive = interactive;
		mCallbacks = callbacks;
		onActivate(interactive, callbacks);
	}

	protected abstract void onActivate(boolean interactive, C callbacks);

	public void deactivate(boolean clearToken) {
		onDeactivate(clearToken);
		mActivated = false;
		mToken = null;
		mInteractive = false;
		mCallbacks = null;
	}

	protected abstract void onDeactivate(boolean clearToken);

	public abstract boolean requestData(List<String> names, OnDataListener listener);

	/* Lifecycle methods */

	public void onCreate(Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			mActivated = savedInstanceState.getBoolean(STATE_KEY_ACTIVATED);
		}
	}

	public void onStart() {

	}

	public void onResume() {

	}

	public void onSaveInstanceState(Bundle outState) {
		outState.putBoolean(STATE_KEY_ACTIVATED, mActivated);
	}

	public void onPause() {

	}

	public void onStop() {

	}

	public void onDestroy() {

	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {

	}

	/* Classes */

	protected abstract class DataTask extends AsyncTask<String, Void, Void> {
		/* protected List<String> mNames; */
		protected OnDataListener mListener;

		private Map<String, Object> mData = null;
		private Map<String, Object> mErrors = null;

		public DataTask(/*List<String> names,*/ OnDataListener listener) {
			super();
			/* mNames = names; */
			mListener = listener;
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			if (mListener != null) {
				if (mErrors != null) {
					mListener.onError(ThirdPartyProxy.this, Collections.unmodifiableMap(mErrors));
				} else {
					mListener.onData(ThirdPartyProxy.this, Collections.unmodifiableMap(mData));
				}
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
	}

	/* Interfaces */

	protected static abstract interface Callbacks {
	}

	public abstract static interface OnDataListener {
		public void onData(ThirdPartyProxy proxy, Map<String, Object> data);

		public void onError(ThirdPartyProxy proxy, Map<String, Object> errors);
	}
}
