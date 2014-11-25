package com.citymaps.mobile.android.thirdparty_new;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import com.citymaps.mobile.android.model.ThirdParty;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

public class GoogleProxy extends ThirdPartyProxy<GoogleProxy.Callbacks>
		implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

	private GoogleApiClient mGoogleApiClient;

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
	protected void onActivate(boolean interactive, Callbacks callbacks) {

	}

	@Override
	protected void onActivateWithToken(String token, boolean interactive, Callbacks callbacks) {

	}

	@Override
	protected void onDeactivate(boolean clearToken) {

	}

	/* Callbacks */

	@Override
	public void onConnected(Bundle bundle) {

	}

	@Override
	public void onConnectionSuspended(int i) {

	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {

	}

	/* Interfaces */

	public static interface Callbacks extends ThirdPartyProxy.Callbacks {
		public void onPreBuild(@NonNull GoogleApiClient.Builder builder);

		public void onConnecting(GoogleProxy proxy);

		public void onConnected(GoogleProxy proxy, Bundle connectionHint);

		public void onDisconnected(GoogleProxy proxy);

		public boolean onFailed(GoogleProxy proxy, boolean cancelled, ConnectionResult result);

	}

	public static abstract class SimpleCallbacks implements Callbacks {
		@Override
		public void onPreBuild(@NonNull GoogleApiClient.Builder builder) {
		}

		@Override
		public void onConnecting(GoogleProxy proxy) {
		}

		@Override
		public void onConnected(GoogleProxy proxy, Bundle connectionHint) {
		}

		@Override
		public void onDisconnected(GoogleProxy proxy) {
		}

		@Override
		public boolean onFailed(GoogleProxy proxy, boolean cancelled, ConnectionResult result) {
			return false;
		}
	}
}
