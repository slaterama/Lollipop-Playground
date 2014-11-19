package com.citymaps.mobile.android.thirdpartynew;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ThirdPartyProxy<D, C extends ThirdPartyProxy.Callbacks> {

	public static final String DATA_TOKEN = "token";

	protected Activity mActivity;

	protected C mCallbacks;

	public ThirdPartyProxy(Activity activity) {
		mActivity = activity;
	}

	public void onCreate(Bundle savedInstanceState) {

	}

	public void onStart() {

	}

	public void onResume() {

	}

	public void onSaveInstanceState(Bundle outState) {

	}

	public void onPause() {

	}

	public void onStop() {

	}

	public void onDestroy() {

	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {

	}

	public void setCallbacks(C callbacks) {
		mCallbacks = callbacks;
	}

	public C getCallbacks() {
		return mCallbacks;
	}

	public abstract void connect(boolean interactive);

	public abstract void requestData(D delegate, List<String> names, OnDataListener listener);

	protected abstract static class DataTask extends AsyncTask<Void, Void, Void> {
		protected ThirdPartyProxy mProxy;
		protected List<String> mNames;
		protected OnDataListener mListener;

		protected Map<String, Object> mData = null;
		protected Map<String, Object> mErrors = null;

		public DataTask(ThirdPartyProxy proxy, List<String> names, OnDataListener listener) {
			super();
			mProxy = proxy;
			mNames = names;
			if (names != null) {
				int size = names.size();
				mData = new HashMap<String, Object>(size);
				mErrors = new HashMap<String, Object>(size);
			}
			mListener = listener;
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			if (mListener != null) {
				mListener.onData(mProxy, mData, mErrors);
			}
		}
	}

	public abstract static interface Callbacks {

	}

	public abstract static interface OnDataListener {
		public void onData(ThirdPartyProxy proxy, Map<String, Object> data, Map<String, Object> errors);
	}
}
