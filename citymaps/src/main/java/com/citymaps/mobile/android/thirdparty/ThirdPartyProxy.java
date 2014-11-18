package com.citymaps.mobile.android.thirdparty;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import com.citymaps.mobile.android.util.LogEx;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class ThirdPartyProxy {

	protected Activity mActivity;

	protected final Queue<Request> mRequestQueue;

	public ThirdPartyProxy(Activity activity) {
		super();
		mActivity = activity;
		mRequestQueue = new ConcurrentLinkedQueue<Request>();
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

	public abstract void openConnection(boolean interactive);

	public abstract void closeConnection();

	public void addRequest(Request request) {
		synchronized (mRequestQueue) {
			boolean empty = mRequestQueue.isEmpty();
			mRequestQueue.add(request);
			if (empty) {
				new Handler().post(mProcessQueue);
			}
		}
	}

	private Runnable mProcessQueue = new Runnable() {
		@Override
		public void run() {
			synchronized (mRequestQueue) {
				int i = 0;
				Request request;
				while ((request = mRequestQueue.poll()) != null) {
					processRequest(request);
				}
			}
		}
	};

	protected void processRequest(Request request) {

	}

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
