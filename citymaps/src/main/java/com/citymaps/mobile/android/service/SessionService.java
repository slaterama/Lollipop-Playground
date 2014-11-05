package com.citymaps.mobile.android.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import com.citymaps.mobile.android.config.Api;
import com.citymaps.mobile.android.config.Environment;

public class SessionService extends Service {

	private SessionBinder mBinder;

	private Environment mEnvironment;
	private Api mApi;

    public SessionService() {
    }

	@Override
	public void onCreate() {
		super.onCreate();

		/* TODO catch exceptions
		BuildVersion appBuildVersion = PackageUtils.getAppBuildVersion(this);

		mEnvironment = Environment.newInstance(appBuildVersion);

		int baseApiVersionNumber = PackageUtils.getBaseApiVersionNumber(this);
		BuildVersion baseApiBuildVersion = PackageUtils.getBaseApiBuildVersion(this);

		ApiBuild tempBuild = new ApiBuild(baseApiVersionNumber, baseApiBuildVersion.toString());
		mApi = Api.newInstance(mEnvironment, tempBuild);
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
