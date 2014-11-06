package com.citymaps.mobile.android.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import com.citymaps.mobile.android.confignew.Environment;

public class SessionService extends Service {

	private SessionBinder mBinder;

	/*
	private Environment mEnvironment;
	private Api mApi;
	*/

	private Environment mEnvironment;

	@Override
	public void onCreate() {
		super.onCreate();

		/*
		SoftwareVersion appVersion = PackageUtils.getAppVersion();
		mEnvironment = Environment.newInstance(this);
		int apiVersion = PackageUtils.getBaseApiVersion(this, 1);
		SoftwareVersion apiBuild = PackageUtils.getBaseApiBuild(this, SoftwareVersion.DEFAULT_VERSION);
		mApi = Api.newInstance(mEnvironment, apiVersion, apiBuild);
		*/

		mEnvironment = Environment.newInstance(this);

		/*
		try {
			String urlString = mApi.buildUrlString(Endpoint.Type.USER);
			LogEx.d(String.format("urlString=%s", urlString));

			urlString = mApi.buildUrlString(Endpoint.Type.USER, mEnvironment.getGhostUserId());
			LogEx.d(String.format("urlString=%s", urlString));

			urlString = mApi.buildUrlString(Endpoint.Type.CONFIG);
			LogEx.d(String.format("urlString=%s", urlString));
		} catch (MalformedURLException e) {

		}
		*/
	}

	@Override
    public IBinder onBind(Intent intent) {
		if (mBinder == null) {
			mBinder = new SessionBinder();
		}
		return mBinder;
    }

	public class SessionBinder extends Binder {

	}
}