package com.citymaps.mobile.android.thirdparty;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import java.util.Map;

public abstract class ThirdPartyConnection<ThirdPartyUser, UserError> {

	private static final String CLASSNAME = ThirdPartyConnection.class.getName();
	private static final String STATE_KEY_ACTIVE = String.format("%s.thirdPartyActive", CLASSNAME);

	public static ThirdPartyConnection newInstance(ThirdParty thirdParty, Activity activity) {
		if (thirdParty == null) {
			return null;
		} else switch (thirdParty) {
			case FACEBOOK:
				return new ThirdPartyConnectionFacebook(activity);
			case GOOGLE:
				return new ThirdPartyConnectionGoogle(activity);
			default:
				return null;
		}
	}

	protected Activity mActivity;

	private boolean mActive;

	protected Callbacks mCallbacks;

	protected ThirdPartyConnection(Activity activity) {
		mActivity = activity;
	}

	public void onCreate(Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			mActive = savedInstanceState.getBoolean(STATE_KEY_ACTIVE, false);
		}
	}

	public void onResume() {
	}

	public void onStart() {
	}

	public void onSaveInstanceState(@NonNull Bundle outState) {
		outState.putBoolean(STATE_KEY_ACTIVE, mActive);
	}

	public void onPause() {
	}

	public void onStop() {
	}

	public void onDestroy() {
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	}

	public abstract ThirdParty getThirdParty();

	public abstract void getToken(TokenCallbacks callbacks);

	public abstract void getUser(UserCallbacks<ThirdPartyUser, UserError> callbacks);

	public boolean isActive() {
		return mActive;
	}

	public abstract boolean isConnecting();

	public abstract boolean isConnected();

	public void connect(boolean silent, Callbacks callbacks) {
		mActive = true;
		mCallbacks = callbacks;
	}

	public void disconnect() {
		mActive = false;
		mCallbacks = null;
	}

	public static interface Callbacks {
		public void onConnectionStateChange(ThirdPartyConnection connection, Map<String, Object> args);
	}

	public static interface TokenCallbacks {
		public void onToken(String token);
		public void onTokenError(Throwable error);
	}

	public static interface UserCallbacks<ThirdPartyUser, UserError> {
		public void onUser(ThirdPartyUser user);
		public void onUserError(UserError error);
	}
}
