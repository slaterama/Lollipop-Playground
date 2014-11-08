package com.citymaps.mobile.android.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.citymaps.mobile.android.app.SessionManager;
import com.citymaps.mobile.android.util.SharedPreferenceUtils;
import com.citymaps.mobile.android.app.VolleyManager;
import com.citymaps.mobile.android.config.Environment;
import com.citymaps.mobile.android.content.CitymapsIntent;
import com.citymaps.mobile.android.exception.CitymapsVolleyException;
import com.citymaps.mobile.android.map.MapViewService;
import com.citymaps.mobile.android.model.vo.Config;
import com.citymaps.mobile.android.model.vo.Version;
import com.citymaps.mobile.android.model.vo.User;
import com.citymaps.mobile.android.util.LogEx;

import static com.citymaps.mobile.android.content.CitymapsIntent.ACTION_CONFIG_LOADED;

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

	private Config.GetRequest mGetConfigRequest;

	private Config mConfig;

	private Version.GetRequest mGetVersionRequest;

	private Version mVersion = null;

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
					mGetConfigRequest = new Config.GetRequest(this, new Response.Listener<Config>() {
						@Override
						public void onResponse(Config response) {
							mConfig = response;

//							SharedPreferenceUtils sharedPreferenceManager = SharedPreferenceUtils.getInstance(StartupService.this);
//							sharedPreferenceManager.applyConfig(mConfig);

							// TODO Might not need this if I monitor Shared Preference change ... ?

							CitymapsIntent intent = new CitymapsIntent(ACTION_CONFIG_LOADED);
							CitymapsIntent.putConfig(intent, mConfig);
							mLocalBroadcastManager.sendBroadcast(intent);
							LogEx.d();
							checkState();
						}
					}, new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							if (LogEx.isLoggable(LogEx.ERROR)) {
								CitymapsVolleyException e = new CitymapsVolleyException(error);
								LogEx.e(e.getMessage(), e);
							}
						}
					});
					VolleyManager.getInstance(this).getRequestQueue().add(mGetConfigRequest);
				}

				if (mGetVersionRequest == null) {
					mGetVersionRequest = new Version.GetRequest(this, new Response.Listener<Version>() {
						@Override
						public void onResponse(Version response) {
							mVersion = response;

							// TODO
//							SharedPreferenceUtils sharedPreferenceManager = SharedPreferenceUtils.getInstance(StartupService.this);
//							sharedPreferenceManager.applyApiVersion(mVersion.getVersion());
//							sharedPreferenceManager.applyApiBuild(mVersion.getBuild());

							checkState();
						}
					}, new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							if (LogEx.isLoggable(LogEx.ERROR)) {
								CitymapsVolleyException e = new CitymapsVolleyException(error);
								LogEx.e(e.getMessage(), e);
							}
						}
					});
					VolleyManager.getInstance(this).getRequestQueue().add(mGetVersionRequest);
				}

				// TODO Temp
				Environment environment = SessionManager.getInstance(this).getEnvironment();
				if (environment.getApi() != null) {
					User currentUser = new User();
					currentUser.setId("8ad760c4-3eb5-42e8-aa23-8259856e7763");
					currentUser.setCitymapsToken("N0uCaPGjdHwuedfBvyvg8MrqXzmsHJ");
					User.GetRequest r = new User.GetRequest(this, currentUser, "8ad760c4-3eb5-42e8-aa23-8259856e7763", new Response.Listener<User>() {
						@Override
						public void onResponse(User response) {
							User user = response;
							LogEx.d(String.format("user=%s", user));
						}
					}, new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							if (LogEx.isLoggable(LogEx.ERROR)) {
								LogEx.e(error.getMessage(), error);
							}
						}
					});
					VolleyManager.getInstance(this).getRequestQueue().add(r);
				}
			}

			if (mConfig != null && mVersion != null) {
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
}
