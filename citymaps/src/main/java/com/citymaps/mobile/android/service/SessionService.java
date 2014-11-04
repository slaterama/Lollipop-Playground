package com.citymaps.mobile.android.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class SessionService extends Service {

	private SessionBinder mBinder;

    public SessionService() {
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
