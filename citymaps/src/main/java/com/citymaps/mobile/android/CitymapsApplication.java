package com.citymaps.mobile.android;

import android.app.Application;
import android.content.Intent;
import com.citymaps.mobile.android.map.MapViewService;
import com.citymaps.mobile.android.service.StartupService;
import com.citymaps.mobile.android.util.LogEx;

public class CitymapsApplication extends Application {

	public CitymapsApplication() {
		super();
		LogEx.setTagFormat("CM|%s", LogEx.Placeholder.SIMPLE_CLASS_NAME);
		LogEx.setLogLevel(LogEx.VERBOSE);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		startService(new Intent(this, StartupService.class));
		startService(new Intent(this, MapViewService.class));
	}
}
