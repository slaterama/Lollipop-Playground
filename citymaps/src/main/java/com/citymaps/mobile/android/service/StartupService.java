package com.citymaps.mobile.android.service;

import android.app.Service;
import android.content.*;
import android.net.ConnectivityManager;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.citymaps.mobile.android.app.VolleyManager;
import com.citymaps.mobile.android.map.MapViewService;
import com.citymaps.mobile.android.modelnew.Config;
import com.citymaps.mobile.android.modelnew.User;
import com.citymaps.mobile.android.modelnew.Version;
import com.citymaps.mobile.android.modelnew.volley.ConfigRequest;
import com.citymaps.mobile.android.modelnew.volley.UserRequest;
import com.citymaps.mobile.android.modelnew.volley.VersionRequest;
import com.citymaps.mobile.android.util.IntentUtils;
import com.citymaps.mobile.android.util.LogEx;
import com.citymaps.mobile.android.util.SharedPreferenceUtils;

import static com.citymaps.mobile.android.util.IntentUtils.ACTION_CONFIG_LOADED;

/*
import com.citymaps.mobile.android.provider.ConfigDatabase;
*/

// TODO Should this be an IntentService?

public class StartupService extends Service {

	/*
	This was a helpful site for integrating Facebook SDK using gradle
	http://trinitytuts.com/integrating-facebook-sdk-application-android-studio/
	 */

	protected static final IntentFilter CONNECTIVITY_FILTER =
			new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);

	private LocalBroadcastManager mLocalBroadcastManager;

	private ConnectivityManager mConnectivityManager;

	private StartupBinder mBinder;

	private ConfigRequest mGetConfigRequest;

	private Config mConfig;

	private VersionRequest mGetVersionRequest;

	private Version mVersion = null;

	private VolleyListeners mListeners = new VolleyListeners();

	// TODO TEMP
	private boolean mAttemptedLoginTests = false;
	// END TEMP

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
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mConnectivityReceiver);
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

	private void checkState() {
		synchronized (this) {
			if (mConnectivityManager.getActiveNetworkInfo() != null) {
				if (mGetConfigRequest == null) {
					mGetConfigRequest = ConfigRequest.newGetRequest(this,
							mListeners.mConfigListener, mListeners.mErrorListener);
					VolleyManager.getInstance(this).getRequestQueue().add(mGetConfigRequest);
				}

				if (mGetVersionRequest == null) {
					mGetVersionRequest = VersionRequest.newGetRequest(this,
							mListeners.mVersionListener, mListeners.mErrorListener);
					VolleyManager.getInstance(this).getRequestQueue().add(mGetVersionRequest);
				}

				// TODO TEMP
				if (!mAttemptedLoginTests) {
					mAttemptedLoginTests = true;

					// This version is username/password
					UserRequest loginRequest = UserRequest.newLoginRequest(this, "slaterama", "r3stlandC",
							mListeners.mUserListener, mListeners.mErrorListener);

					// This version is Citymaps token
//					mUserLoginRequest = UserRequest.newLoginRequest(this, "N0uCaPGjdHwuedfBvyvg8MrqXzmsHJ",
//							mListeners.mUserListener, mListeners.mErrorListener);
					RequestQueue q = VolleyManager.getInstance(this).getRequestQueue();
					q.add(loginRequest);
				}
				// END TEMP
			}

			if (mConfig != null && mVersion != null) {
				stopSelf();
			}
		}
	}

	protected class VolleyListeners {

		public Response.Listener<Config> mConfigListener = new Response.Listener<Config>() {
			@Override
			public void onResponse(Config response) {
				mConfig = response;

				SharedPreferences sp = SharedPreferenceUtils.getConfigSharedPreferences(StartupService.this);
				long configTimestamp = SharedPreferenceUtils.getConfigTimestamp(sp, 0);
				if (mConfig.getTimestamp() > configTimestamp) {
					SharedPreferenceUtils.putConfig(sp, mConfig).apply();
					sp.edit().remove(SharedPreferenceUtils.Key.CONFIG_PROCESSED_ACTION.toString())
							.remove(SharedPreferenceUtils.Key.CONFIG_PROCESSED_TIMESTAMP.toString())
							.apply();
				}

				Intent intent = new Intent(ACTION_CONFIG_LOADED);
				IntentUtils.putConfig(intent, mConfig);
				mLocalBroadcastManager.sendBroadcast(intent);
				checkState();
			}
		};

		public Response.Listener<Version> mVersionListener = new Response.Listener<Version>() {
			@Override
			public void onResponse(Version response) {
				mVersion = response;

				// TODO
				// SharedPreferenceUtils sharedPreferenceManager = SharedPreferenceUtils.getInstance(StartupService.this);
				// sharedPreferenceManager.applyApiVersion(mVersion.getVersion());
				// sharedPreferenceManager.applyApiBuild(mVersion.getBuild());

				checkState();
			}
		};


		public Response.Listener<User> mUserListener = new Response.Listener<User>() {
			@Override
			public void onResponse(User response) {

//				mCurrentUser = response;
				LogEx.v(String.format("response=%s", response));
			}
		};

		public Response.ErrorListener mErrorListener = new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				LogEx.v(error.getMessage(), error);
			}
		};
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
}
