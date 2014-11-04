package com.citymaps.mobile.android.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import com.citymaps.mobile.android.config.Api;
import com.citymaps.mobile.android.config.Environment;
import com.citymaps.mobile.android.model.vo.ApiBuild;
import com.citymaps.mobile.android.os.BuildVersion;
import com.citymaps.mobile.android.util.PackageUtils;

public class SessionService extends Service {

	private SessionBinder mBinder;

	private Environment mEnvironment;
	private Api mApi;

    public SessionService() {
    }

	@Override
	public void onCreate() {
		super.onCreate();

		BuildVersion appBuildVersion = PackageUtils.getAppBuildVersion(this);
		mEnvironment = Environment.newInstance(appBuildVersion);

		int baseApiVersionNumber = PackageUtils.getBaseApiVersionNumber(this);
		BuildVersion baseApiBuildVersion = PackageUtils.getBaseApiBuildVersion(this);

		ApiBuild tempBuild = new ApiBuild(baseApiVersionNumber, baseApiBuildVersion.toString());
		mApi = Api.newInstance(mEnvironment, tempBuild);
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
