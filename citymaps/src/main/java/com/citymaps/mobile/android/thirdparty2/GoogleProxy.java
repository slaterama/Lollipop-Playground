package com.citymaps.mobile.android.thirdparty2;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import com.citymaps.mobile.android.model.ThirdParty;
import com.google.android.gms.common.ConnectionResult;

public class GoogleProxy extends ThirdPartyProxy<GoogleProxy.Callbacks>
{

	public GoogleProxy(FragmentActivity activity, Callbacks callbacks) {
		super(activity, callbacks);
	}

	public GoogleProxy(Fragment fragment, Callbacks callbacks) {
		super(fragment, callbacks);
	}

	@Override
	public ThirdParty getThirdParty() {
		return ThirdParty.GOOGLE;
	}

	@Override
	public void onProxyStart(boolean interactive, Callbacks callbacks) {

	}

	@Override
	public void onProxyStop(boolean clear) {

	}

	public static interface Callbacks extends ThirdPartyProxy.AbsCallbacks {
		public void onConnecting(GoogleProxy proxy);

		public void onConnected(GoogleProxy proxy, Bundle connectionHint);

		public void onDisconnected(GoogleProxy proxy);

		public void onError(GoogleProxy proxy, ConnectionResult result);

	}
}
