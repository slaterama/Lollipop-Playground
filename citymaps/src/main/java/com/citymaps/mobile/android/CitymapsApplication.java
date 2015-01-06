package com.citymaps.mobile.android;

import android.content.Intent;
import android.support.multidex.MultiDexApplication;
import com.citymaps.mobile.android.service.StartupService;
import com.citymaps.mobile.android.util.LogEx;

public class CitymapsApplication extends MultiDexApplication {

	public CitymapsApplication() {
		super();
		LogEx.setTagFormat("CM|%s", LogEx.Placeholder.SIMPLE_CLASS_NAME);
		LogEx.setLogLevel(BuildConfig.LOG_LEVEL);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		startService(new Intent(this, StartupService.class));
	}
}
