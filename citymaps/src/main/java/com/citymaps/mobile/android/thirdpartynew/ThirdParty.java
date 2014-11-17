package com.citymaps.mobile.android.thirdpartynew;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

public abstract class ThirdParty {

	public static ThirdParty newInstance(Type type, Activity activity, ConnectionCallbacks callbacks) {
		if (type == null) {
			return null;
		} else switch (type) {
			case FACEBOOK:
				return new ThirdPartyFacebook(activity, callbacks);
			case GOOGLE:
				return new ThirdPartyGoogle(activity, callbacks);
			default:
				return null;
		}
	}

	protected Activity mActivity;

	protected ConnectionCallbacks mConnectionCallbacks;

	public ThirdParty(Activity activity, ConnectionCallbacks callbacks) {
		super();
		mActivity = activity;
		mConnectionCallbacks = callbacks;
	}

	public abstract Type getType();

	final void callOnCreate(Bundle savedInstanceState) {
		if (savedInstanceState != null) {

		}
		onCreate(savedInstanceState);
	}

	final void callOnResume() {
		onResume();
	}

	final void callOnStart() {
		onStart();
	}

	final void callOnSaveInstanceState(@NonNull Bundle outState) {
		onSaveInstanceState(outState);
	}

	final void callOnPause() {
		onPause();
	}

	final void callOnStop() {
		onStop();
	}

	final void callOnDestroy() {
		onDestroy();
	}

	final void callOnActivityResult(int requestCode, int resultCode, Intent data) {
		onActivityResult(requestCode, resultCode, data);
	}

	public void onCreate(Bundle savedInstanceState) {
	}

	public void onResume() {
	}

	public void onStart() {
	}

	public void onSaveInstanceState(@NonNull Bundle outState) {
	}

	public void onPause() {
	}

	public void onStop() {
	}

	public void onDestroy() {
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	}

	public abstract void getToken(Mode mode, TokenCallbacks callbacks);

	public abstract void getUser(Mode mode, UserCallbacks callbacks);

	public static interface ConnectionCallbacks {
		public void onConnected(ThirdParty thirdParty);
		public void onDisconnected(ThirdParty thirdParty);
		public void onError(ThirdParty thirdParty);
	}

	protected static abstract interface ThirdPartyCallbacks<D, E> {
		public void onSuccess(D data);
		public void onError(E error);
	}

	public static interface TokenCallbacks extends ThirdPartyCallbacks<String, Exception> {
//		public void onToken(String token);
//		public void onTokenError(Throwable error);
	}

	public static interface UserCallbacks extends ThirdPartyCallbacks<UserProxy, Object> {
//		public void onUser(UserProxy user);
//		public void onUserError(Object error);
	}

	public static interface UserProxy {
		public String getFirstName();
		public String getLastName();
	}

	public static enum Type {
		FACEBOOK,
		GOOGLE
	}

	public static enum Mode {
		SILENT,
		INTERACTIVE
	}
}
