package com.citymaps.mobile.android.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import com.citymaps.mobile.android.config.Api;
import com.citymaps.mobile.android.config.Endpoint;
import com.citymaps.mobile.android.config.Environment;
import com.citymaps.mobile.android.os.SoftwareVersion;
import com.citymaps.mobile.android.util.LogEx;
import com.citymaps.mobile.android.util.PackageUtils;

import java.net.MalformedURLException;

public class SessionService extends Service {

	private SessionBinder mBinder;

	private Environment mEnvironment;
	private Api mApi;

    public SessionService() {
    }

	@Override
	public void onCreate() {
		super.onCreate();
		SoftwareVersion appVersion = PackageUtils.getAppVersion(this, SoftwareVersion.DEFAULT_VERSION);
		mEnvironment = Environment.newInstance(this, appVersion.isDevelopment()
				? Environment.Type.DEVELOPMENT : Environment.Type.PRODUCTION);
		int apiVersion = PackageUtils.getBaseApiVersion(this, 1);
		SoftwareVersion apiBuild = PackageUtils.getBaseApiBuild(this, SoftwareVersion.DEFAULT_VERSION);
		mApi = Api.newInstance(mEnvironment, apiVersion, apiBuild);

		try {
			String urlString = mApi.buildUrlString(Endpoint.Type.USER);
			LogEx.d(String.format("urlString=%s", urlString));

			urlString = mApi.buildUrlString(Endpoint.Type.USER, mEnvironment.getGhostUserId());
			LogEx.d(String.format("urlString=%s", urlString));

			urlString = mApi.buildUrlString(Endpoint.Type.CONFIG);
			LogEx.d(String.format("urlString=%s", urlString));
		} catch (MalformedURLException e) {

		}
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
