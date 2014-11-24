package com.citymaps.mobile.android.thirdparty2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import com.citymaps.mobile.android.model.ThirdParty;

import com.citymaps.mobile.android.thirdparty2.ThirdPartyProxy.AbsCallbacks;

public abstract class ThirdPartyProxy<C extends AbsCallbacks> {

	private static final String CLASSNAME = ThirdPartyProxy.class.getName();
	private static final String STATE_KEY_STARTED = CLASSNAME + ".started";
	private static final String STATE_KEY_INTERACTIVE = CLASSNAME + ".interactive";

	protected FragmentActivity mActivity;

	protected Fragment mFragment;

	private boolean mStarted;

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

	public boolean isStarted() {
		return mStarted;
	}

	public boolean isInteractive() {
		return mInteractive;
	}

	protected C getCallbacks() {
		return mCallbacks;
	}

	public void start(boolean interactive, C callbacks) {
		mStarted = true;
		mInteractive = interactive;
		mCallbacks = callbacks;
		onProxyStart(interactive, callbacks);
	}

	public abstract void onProxyStart(boolean interactive, C callbacks);

	public void stop(boolean clear) {
		mStarted = false;
		mInteractive = false;
		mCallbacks = null;
		onProxyStop(clear);
	}

	public abstract void onProxyStop(boolean clear);

	public void onCreate(Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			mStarted = savedInstanceState.getBoolean(STATE_KEY_STARTED);
			mInteractive = savedInstanceState.getBoolean(STATE_KEY_INTERACTIVE);
		}
	}

	public void onStart() {

	}

	public void onResume() {

	}

	public void onSaveInstanceState(Bundle outState) {
		outState.putBoolean(STATE_KEY_STARTED, mStarted);
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

	public abstract static interface AbsCallbacks {
	}
}
