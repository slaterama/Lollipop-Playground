package com.citymaps.mobile.android.view.housekeeping;

import android.app.Activity;
import android.content.*;
import android.content.pm.ActivityInfo;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.citymaps.mobile.android.R;
import com.citymaps.mobile.android.app.SessionManager;
import com.citymaps.mobile.android.app.TrackedActionBarActivity;
import com.citymaps.mobile.android.app.VolleyManager;
import com.citymaps.mobile.android.model.Config;
import com.citymaps.mobile.android.model.User;
import com.citymaps.mobile.android.model.request.UserRequest;
import com.citymaps.mobile.android.util.*;
import com.citymaps.mobile.android.util.UpdateUtils.UpdateType;
import com.citymaps.mobile.android.view.MainActivity;

import java.util.Timer;
import java.util.TimerTask;

import static com.citymaps.mobile.android.util.IntentUtils.ACTION_CONFIG_LOADED;

public class LaunchActivity extends TrackedActionBarActivity
		implements SharedPreferences.OnSharedPreferenceChangeListener {

	private static final String STATE_KEY_LAUNCH_FRAGMENT = "launchFragment";

	private LaunchFragment mLaunchFragment;

	private LocalBroadcastManager mLocalBroadcastManager;

	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (ACTION_CONFIG_LOADED.equals(action)) {
				processConfig(IntentUtils.getConfig(intent));
			}
		}
	};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		if (!getResources().getBoolean(R.bool.launch_allow_orientation_change)) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		setContentView(R.layout.activity_launch);

		mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
    }

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		if (savedInstanceState == null) {
			// First of all, examine any saved config for hard/soft update
			SharedPreferences sp = SharedPrefUtils.getConfigSharedPreferences(this);
			processConfig(SharedPrefUtils.getConfig(sp));
			if (isFinishing()) {
				return;
			}

			mLaunchFragment = new LaunchFragment();
			getSupportFragmentManager()
					.beginTransaction()
					.add(mLaunchFragment, null)
					.commit();
		} else {
			mLaunchFragment = (LaunchFragment) getSupportFragmentManager().getFragment(savedInstanceState, STATE_KEY_LAUNCH_FRAGMENT);
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		mLocalBroadcastManager.registerReceiver(mBroadcastReceiver, new IntentFilter(ACTION_CONFIG_LOADED));
	}

	@Override
	protected void onResume() {
		super.onResume();
		PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		mLocalBroadcastManager.unregisterReceiver(mBroadcastReceiver);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		LogEx.d();
	}

	private void processConfig(Config config) {
		UpdateType updateType = UpdateUtils.getUpdateType(this, config);
		switch (updateType) {
			case HARD:
				startActivity(new Intent(this, HardUpdateActivity.class));
				finish();
		}
	}

	public static class LaunchFragment extends Fragment {

		private static final int TIMER_TASK_DELAY = 1500;

		private Timer mTimer;

		private TimerTask mTimerTask;

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setRetainInstance(true);

			mTimer = new Timer();
			mTimerTask = new TimerTask() {
				@Override
				public void run() {
					completeLaunch();
				}
			};
			mTimer.schedule(mTimerTask, TIMER_TASK_DELAY);
		}

		@Override
		public void onDestroy() {
			super.onDetach();
			mTimerTask.cancel();
		}

		private void completeLaunch() {
			final Activity activity = getActivity();
			if (activity != null) {

				// TODO -- API problem with this type of animation

//				Bundle bundle = ActivityOptions
//						.makeCustomAnimation(activity, 0, android.R.anim.fade_out)
//						.toBundle();
//				activity.startActivity(new Intent(activity, MainActivity.class), bundle);

				// If we have never processed the Tour activity, start it now

				SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity);
				boolean tourProcessed = SharedPrefUtils.getBoolean(sp, CitymapsPreference.TOUR_PROCESSED, false);
				if (!tourProcessed) {
					Intent intent = new Intent(activity, TourActivity.class);
					IntentUtils.putStartupMode(intent, true);
					activity.startActivity(intent);
					activity.finish();
					return;
				}

				// If we have never processed the Enable Location activity:
				// If Location Services are enabled, mark the Enable Location activity as processed and continue
				// Otherwise, start the Enable Location activity now

				boolean enableLocationProcessed = SharedPrefUtils.getBoolean(sp, CitymapsPreference.ENABLE_LOCATION_PROCESSED, false);
				if (!enableLocationProcessed) {
					LocationManager manager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
					boolean gpsEnabled = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
					if (gpsEnabled) {
						SharedPrefUtils.putBoolean(sp.edit(), CitymapsPreference.ENABLE_LOCATION_PROCESSED, true).apply();
					} else {
						Intent intent = new Intent(activity, EnableLocationActivity.class);
						IntentUtils.putStartupMode(intent, true);
						activity.startActivity(intent);
						activity.finish();
						return;
					}
				}

				// Get the saved Citymaps Token from SharedPreferences (if any)

				String citymapsToken = SharedPrefUtils.getString(sp, CitymapsPreference.CITYMAPS_TOKEN, null);
				if (TextUtils.isEmpty(citymapsToken)) {
					Intent intent = new Intent(activity, AuthenticateActivity.class);
					IntentUtils.putStartupMode(intent, true);
					activity.startActivity(intent);
					activity.finish();
					return;
				}

				// Try to log in silently. If successful, start Main activity
				// If unsuccessful, start Authenticate activity

				UserRequest request = UserRequest.newLoginRequest(activity, citymapsToken,
						new Response.Listener<User>() {
							@Override
							public void onResponse(User response) {
								SessionManager.getInstance(activity).setCurrentUser(response);
								Intent intent = new Intent(activity, MainActivity.class);
								activity.startActivity(intent);
								activity.finish();
							}
						},
						new Response.ErrorListener() {
							@Override
							public void onErrorResponse(VolleyError error) {
								Intent intent = new Intent(activity, AuthenticateActivity.class);
								IntentUtils.putStartupMode(intent, true);
								activity.startActivity(intent);
								activity.finish();
							}
						});
				VolleyManager.getInstance(getActivity()).getRequestQueue().add(request);
			}
		}
	}
}
