package com.citymaps.mobile.android.thirdpartynew;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public abstract class ThirdPartyProxy {

	protected Activity mActivity;

	protected boolean mActive;

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

	public void connect(boolean interactive) {
		mActive = true;
	}

	public void cancel() {
		mActive = false;
	}

	public boolean isActive() {
		return mActive;
	}
}
