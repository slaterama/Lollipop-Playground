package com.citymaps.mobile.android.thirdparty;

import android.content.Intent;
import android.os.Bundle;

public abstract class ThirdPartyProxy {

	public ThirdPartyProxy() {
		super();
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

	public abstract Connection newConnection();

	public static abstract class Connection<P> {
		protected P[] mPermissions;
		protected boolean mInteractive;

		public Connection setPermissions(P... permissions) {
			mPermissions = permissions;
			return this;
		}

		public Connection setInteractive(boolean interactive) {
			mInteractive = interactive;
			return this;
		}

		public abstract void connect();
	}
}
