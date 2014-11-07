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
import com.android.volley.toolbox.Volley;
import com.citymaps.mobile.android.app.SessionManager;
import com.citymaps.mobile.android.app.VolleyManager;
import com.citymaps.mobile.android.config.Environment;
import com.citymaps.mobile.android.content.CitymapsIntent;
import com.citymaps.mobile.android.http.volley.GetConfigRequest;
import com.citymaps.mobile.android.http.volley.GetStatusRequest;
import com.citymaps.mobile.android.http.volley.GetUserRequest;
import com.citymaps.mobile.android.map.MapViewService;
import com.citymaps.mobile.android.model.vo.Config;
import com.citymaps.mobile.android.model.vo.Status;
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

	private GetConfigRequest mGetConfigRequest;

	private Config mConfig;

	private GetStatusRequest mGetStatusRequest;

	private Status mStatus = null;

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

				//SessionManager sessionManager = SessionManager.getInstance(this);

				if (mGetConfigRequest == null) {
					mGetConfigRequest = new GetConfigRequest(this, new Response.Listener<Config>() {
						@Override
						public void onResponse(Config response) {
							mConfig = response;
							CitymapsIntent intent = new CitymapsIntent(ACTION_CONFIG_LOADED);
							CitymapsIntent.putConfig(intent, mConfig);
							mLocalBroadcastManager.sendBroadcast(intent);
							checkState();
						}
					}, new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							if (LogEx.isLoggable(LogEx.ERROR)) {
								LogEx.e(error.getMessage(), error);
							}
						}
					});
					VolleyManager.getInstance(this).getRequestQueue().add(mGetConfigRequest);
				}

				if (mGetStatusRequest == null) {
					mGetStatusRequest = new GetStatusRequest(this, new Response.Listener<Status>() {
						@Override
						public void onResponse(Status response) {
							mStatus = response;
							SessionManager.getInstance(StartupService.this).registerVersion(mStatus.getVersion(), mStatus.getBuild());
							checkState();
						}
					}, new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							if (LogEx.isLoggable(LogEx.ERROR)) {
								LogEx.e(error.getMessage(), error);
							}
						}
					});
					VolleyManager.getInstance(this).getRequestQueue().add(mGetStatusRequest);
				}

				// TODO Temp
				Environment environment = SessionManager.getEnvironment(this);
				if (environment.getApi() != null) {
					User currentUser = new User();
					currentUser.setId("8ad760c4-3eb5-42e8-aa23-8259856e7763");
					currentUser.setCitymapsToken("N0uCaPGjdHwuedfBvyvg8MrqXzmsHJ");
					GetUserRequest r = new GetUserRequest(this, currentUser, "8ad760c4-3eb5-42e8-aa23-8259856e7763", new Response.Listener<User>() {
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

			if (mConfig != null && mStatus != null) {
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
