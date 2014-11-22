package com.citymaps.mobile.android.thirdparty;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class ThirdPartyProxy<D, C extends ThirdPartyProxy.Callbacks> {

	public static final String DATA_TOKEN = "token";

	protected FragmentActivity mActivity;

	protected C mCallbacks;

	public ThirdPartyProxy(FragmentActivity activity) {
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

		private Map<String, Object> mData = null;
		private Map<String, Object> mErrors = null;

		public DataTask(ThirdPartyProxy proxy, List<String> names, OnDataListener listener) {
			super();
			mProxy = proxy;
			mNames = names;
			if (names != null) {
				int size = names.size();
				mData = new LinkedHashMap<String, Object>(size);
				mErrors = new LinkedHashMap<String, Object>(size);
			}
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

	public abstract static interface Callbacks {

	}

	public abstract static interface OnDataListener {
		public void onError(ThirdPartyProxy proxy, Map<String, Object> errors);
		public void onData(ThirdPartyProxy proxy, Map<String, Object> data);
	}
}
