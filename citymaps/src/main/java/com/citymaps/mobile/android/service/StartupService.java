package com.citymaps.mobile.android.service;

import android.app.Service;
import android.content.*;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import com.citymaps.mobile.android.app.Wrapper;
import com.citymaps.mobile.android.config.Api;
import com.citymaps.mobile.android.content.CitymapsIntent;
import com.citymaps.mobile.android.http.request.GetConfigHttpRequest;
import com.citymaps.mobile.android.map.MapViewService;
import com.citymaps.mobile.android.model.vo.Config;
import com.citymaps.mobile.android.util.LogEx;
/*
import com.citymaps.mobile.android.provider.ConfigDatabase;
*/

// TODO Should this be an IntentService?

public class StartupService extends Service
		implements ServiceConnection {

	/*
	This was a helpful site for integrating Facebook SDK using gradle
	http://trinitytuts.com/integrating-facebook-sdk-application-android-studio/
	 */

	protected static final IntentFilter CONNECTIVITY_FILTER =
			new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);

	// TODO I was unable to use this intent to start or bind to service
	// once I started using build variants
	protected static final CitymapsIntent SESSION_SERVICE_INTENT =
			new CitymapsIntent(CitymapsIntent.ACTION_SESSION_SERVICE);

	private StartupBinder mBinder;

	private SessionService.SessionBinder mSessionBinder;

	private ConnectivityManager mConnectivityManager;

	private boolean mConnected = false;

	private boolean mConfigLoaded = false;

	private boolean mVersionLoaded = false;

	private BroadcastReceiver mConnectivityReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			testConnectedState();
		}
	};

	@Override
	public void onCreate() {
		super.onCreate();
		mConnectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		testConnectedState();
		registerReceiver(mConnectivityReceiver, CONNECTIVITY_FILTER);
		bindService(new Intent(this, SessionService.class), this, BIND_AUTO_CREATE);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mConnectivityReceiver);
		unbindService(this);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		/*
		ConfigDatabase configDatabase = new ConfigDatabase(this);
		configDatabase.getWritableDatabase();
		*/

		// Start other services
		Context applicationContext = getApplicationContext();
		applicationContext.startService(new Intent(applicationContext, SessionService.class));
		applicationContext.startService(new Intent(applicationContext, MapViewService.class));

		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		if (mBinder == null) {
			mBinder = new StartupBinder();
		}

		return mBinder;
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		mSessionBinder = (SessionService.SessionBinder) service;
		testState();
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		mSessionBinder = null;
	}

	private void testConnectedState() {
		boolean connected = (mConnectivityManager.getActiveNetworkInfo() != null);
		if (mConnected != connected) {
			mConnected = connected;
			if (LogEx.isLoggable(LogEx.VERBOSE)) {
				LogEx.v(mConnected ? "Connected" : "Not connected");
			}

			testState();
		}
	}

	private void testState() {
		if (mSessionBinder == null) {
			return;
		}

		if (!mConnected) {
			return;
		}

		if (!mConfigLoaded) {
			loadConfig();
		}

		if (!mVersionLoaded) {
			loadVersion();
		}
	}

	private void loadConfig() {
		new AsyncTask<Void, Void, Wrapper<Config, Exception>>() {
			@Override
			protected Wrapper<Config, Exception> doInBackground(Void... params) {
				//Api api = mSessionBinder.getApi();
				//GetConfigHttpRequest request = GetConfigHttpRequest.makeRequest(api);
				//return request.execute();
				return null;
			}

			@Override
			protected void onPostExecute(Wrapper<Config, Exception> result) {

			}
		}.execute();
	}

	private void loadVersion() {

	}

	public class StartupBinder extends Binder {

		/*
		public void openOrCreateDatabase() {

		}
		*/

	}
}
