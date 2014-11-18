package com.citymaps.mobile.android.thirdparty;

import com.google.android.gms.common.api.Scope;

public class GoogleProxy extends ThirdPartyProxy {

	@Override
	public Connection newConnection() {
		return new GoogleConnection();
	}

	public static class GoogleConnection extends Connection<Scope> {
		@Override
		public void connect() {

		}
	}
}
