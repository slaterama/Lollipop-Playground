package com.citymaps.mobile.android.thirdparty_new;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import com.citymaps.mobile.android.model.ThirdParty;

public abstract class ThirdPartyProxy<C extends ThirdPartyProxy.Callbacks> {

	private static final String STATE_KEY_ACTIVATED = ThirdPartyProxy.class.getName() + ".activated";

	protected FragmentActivity mActivity;

	protected Fragment mFragment;

	protected boolean mActivated;

	protected String mToken;

	protected boolean mInteractive;

	protected C mCallbacks;

	protected ThirdPartyProxy(FragmentActivity activity, Fragment fragment, C callbacks) {
		super();
		if (activity == null) {
			throw new IllegalArgumentException("Activity can not be null");
		}
		mActivity = activity;
		mFragment = fragment;
		mCallbacks = callbacks;
	}

	public ThirdPartyProxy(FragmentActivity activity, C callbacks) {
		this(activity, null, callbacks);
	}

	public ThirdPartyProxy(Fragment fragment, C callbacks) {
		this(fragment == null ? null : fragment.getActivity(), fragment, callbacks);
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

	public void activateWithToken(String token, boolean interactive, C callbacks) {
		mActivated = true;
		mToken = token;
		mInteractive = interactive;
		mCallbacks = callbacks;
		onActivateWithToken(token, interactive, callbacks);
	}

	protected abstract void onActivateWithToken(String token, boolean interactive, C callbacks);

	public void deactivate(boolean clearToken) {
		mActivated = false;
		mToken = null;
		mInteractive = false;
		mCallbacks = null;
		onDeactivate(clearToken);
	}

	protected abstract void onDeactivate(boolean clearToken);

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

	/* Interfaces */

	protected static abstract interface Callbacks {
	}
}
