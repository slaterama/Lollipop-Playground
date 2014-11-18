package com.citymaps.mobile.android.thirdparty;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public abstract class ThirdPartyProxy {

	protected Activity mActivity;

	public ThirdPartyProxy(Activity activity) {
		super();
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

	/*
	public abstract Connection openConnection();
	*/

	public abstract void openConnection(boolean interactive);

	public abstract void closeConnection();

//	public abstract void disconnect();

	/*
	public abstract class Connection {
		protected boolean mInteractive;

		public Connection setInteractive(boolean interactive) {
			mInteractive = interactive;
			return this;
		}

		public abstract void connect();
	}
	*/

	public static abstract class Request<T, E> {
		protected Listener<T> mListener;
		protected ErrorListener<E> mErrorListener;

		public Request(Listener<T> listener, ErrorListener<E> errorListener) {
			mListener = listener;
			mErrorListener = errorListener;
		}

		public static interface Listener<T> {
			public void onResponse(T response);
		}

		public static interface ErrorListener<E> {
			public void onErrorResponse(E error);
		}
	}

	public static class TokenRequest extends Request<String, Exception> {
		public TokenRequest(Listener<String> listener, ErrorListener<Exception> errorListener) {
			super(listener, errorListener);
		}
	}
}
