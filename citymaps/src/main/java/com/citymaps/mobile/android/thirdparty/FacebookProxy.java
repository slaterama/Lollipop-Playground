package com.citymaps.mobile.android.thirdparty;

public class FacebookProxy extends ThirdPartyProxy {

	@Override
	public Connection newConnection() {
		return new FacebookConnection();
	}

	public static class FacebookConnection extends Connection<String> {
		@Override
		public void connect() {

		}
	}
}
