package com.citymaps.mobile.android.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import com.citymaps.mobile.android.config.Environment;
import com.citymaps.mobile.android.content.CitymapsIntent;
import com.citymaps.mobile.android.model.vo.ApiStatus;

public class SessionService extends Service {

	private SessionBinder mBinder;

	/*
	private Environment mEnvironment;
	private Api mApi;
	*/

	private Environment mEnvironment;

	private ApiStatus mApiStatus;

	@Override
	public void onCreate() {
		super.onCreate();
		mEnvironment = Environment.newInstance(this);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		int result = super.onStartCommand(intent, flags, startId);
		if (intent != null) {
			mApiStatus = CitymapsIntent.getApiStatus(intent); // ???
		}

		// TODO Set up mEnvironment HERE. Also write version & build to System Preferences to they're saved.

		return result;
	}

	@Override
    public IBinder onBind(Intent intent) {
		if (mBinder == null) {
			mBinder = new SessionBinder();
		}
		return mBinder;
    }

	public class SessionBinder extends Binder {
		public Environment getEnvironment() {
			return mEnvironment;
		}
	}
}