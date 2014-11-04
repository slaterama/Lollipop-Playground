package com.citymaps.mobile.android.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import com.citymaps.mobile.android.os.MyVersion3;
/*
import com.citymaps.mobile.android.provider.ConfigDatabase;
*/

public class StartupService extends Service {

	/*
	This was a helpful site for integrating Facebook SDK using gradle
	http://trinitytuts.com/integrating-facebook-sdk-application-android-studio/
	 */

	private StartupBinder mBinder;

	public StartupService() {
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		/*
		ConfigDatabase configDatabase = new ConfigDatabase(this);
		configDatabase.getWritableDatabase();
		*/

		MyVersion3.testMyVersion3();

		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		if (mBinder == null) {
			mBinder = new StartupBinder();
		}

		return mBinder;
	}

	public class StartupBinder extends Binder {

		/*
		public void openOrCreateDatabase() {

		}
		*/

	}
}
