package com.citymaps.mobile.android.service;

import android.app.Service;
import android.content.*;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import com.citymaps.mobile.android.app.CitymapsException;
import com.citymaps.mobile.android.app.Wrapper;
import com.citymaps.mobile.android.content.CitymapsIntent;
import com.citymaps.mobile.android.http.request.GetApiStatusHttpRequest;
import com.citymaps.mobile.android.http.request.GetConfigHttpRequest;
import com.citymaps.mobile.android.map.MapViewService;
import com.citymaps.mobile.android.model.vo.ApiStatus;
import com.citymaps.mobile.android.model.vo.Config;
import com.citymaps.mobile.android.util.LogEx;
/*
import com.citymaps.mobile.android.provider.ConfigDatabase;
*/

import static com.citymaps.mobile.android.content.CitymapsIntent.ACTION_CONFIG_LOADED;

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

	private LocalBroadcastManager mLocalBroadcastManager;

	private ConnectivityManager mConnectivityManager;

	private StartupBinder mBinder;

	private SessionService.SessionBinder mSessionBinder;

	private ConfigTask mConfigTask;

	private StatusTask mStatusTask;

	private Config mConfig;

	private BroadcastReceiver mConnectivityReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			checkState();
		}
	};

	@Override
	public void onCreate() {
		super.onCreate();
		mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
		mConnectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		checkState();
		registerReceiver(mConnectivityReceiver, CONNECTIVITY_FILTER);
		bindService(new Intent(this, SessionService.class), this, BIND_AUTO_CREATE);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mConnectivityReceiver);
		unbindService(this);
		LogEx.d("Byeeeeeeeee....");
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
		checkState();
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		mSessionBinder = null;
	}

	private void checkState() {
		synchronized (this) {
			if (mSessionBinder != null
					&& mConnectivityManager.getActiveNetworkInfo() != null) {
				if (mConfigTask == null) {
					mConfigTask = new ConfigTask();
					mConfigTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				}

				if (mStatusTask == null) {
					mStatusTask = new StatusTask();
					mStatusTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				}
			}

			if (mConfigTask != null && mConfigTask.getStatus() == AsyncTask.Status.FINISHED
					&& mStatusTask != null && mStatusTask.getStatus() == AsyncTask.Status.FINISHED) {
				stopSelf();
			}
		}
	}

	public class StartupBinder extends Binder {

		/*
		public void openOrCreateDatabase() {

		}
		*/

		public Config getConfig() {
			return mConfig;
		}

	}

	private class ConfigTask extends AsyncTask<Void, Void, Wrapper<Config>> {
		@Override
		protected Wrapper<Config> doInBackground(Void... params) {
			return new GetConfigHttpRequest().execute(mSessionBinder.getEnvironment());
		}

		@Override
		protected void onPostExecute(Wrapper<Config> result) {
			try {
				mConfig = result.getData();
				CitymapsIntent intent = new CitymapsIntent(ACTION_CONFIG_LOADED);
				CitymapsIntent.putConfig(intent, mConfig);
				mLocalBroadcastManager.sendBroadcast(intent);
			} catch (CitymapsException e) {
				// TODO Error handling
			} finally {
				checkState();
			}
		}
	}

	private class StatusTask extends AsyncTask<Void, Void, Wrapper<ApiStatus>> {
		@Override
		protected Wrapper<ApiStatus> doInBackground(Void... params) {
			return new GetApiStatusHttpRequest().execute(mSessionBinder.getEnvironment());
		}

		@Override
		protected void onPostExecute(Wrapper<ApiStatus> result) {
			try {
				// TODO bind to SessionService with api status intent -OR- send broadcast?
				ApiStatus status = result.getData();
//				CitymapsIntent intent = new CitymapsIntent(StartupService.this, SessionService.class);
//				CitymapsIntent.putApiStatus(intent, status);
//				startService(intent);
			} catch (CitymapsException e) {
				// TODO Error handling
			} finally {
				checkState();
			}
		}
	}
}
