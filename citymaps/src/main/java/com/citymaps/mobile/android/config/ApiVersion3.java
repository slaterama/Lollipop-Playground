package com.citymaps.mobile.android.config;

import com.citymaps.mobile.android.os.SoftwareVersion;

public class ApiVersion3 extends ApiBase {

	private SoftwareVersion mApiBuild;

	protected ApiVersion3(Environment environment) {
		super(environment);
		mApiBuild = new SoftwareVersion(3, 0, 0);
	}

	@Override
	public int getApiVersion() {
		return 3;
	}

	@Override
	public SoftwareVersion getApiBuild() {
		return mApiBuild;
	}
}
