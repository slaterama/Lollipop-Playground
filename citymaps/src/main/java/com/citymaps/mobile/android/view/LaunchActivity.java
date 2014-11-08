package com.citymaps.mobile.android.view;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.*;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import com.citymaps.mobile.android.BuildConfig;
import com.citymaps.mobile.android.R;
import com.citymaps.mobile.android.util.SharedPreferenceUtils;
import com.citymaps.mobile.android.content.CitymapsIntent;
import com.citymaps.mobile.android.model.vo.Config;
import com.citymaps.mobile.android.os.SoftwareVersion;
import com.citymaps.mobile.android.util.LogEx;

import java.util.Timer;
import java.util.TimerTask;

import static com.citymaps.mobile.android.content.CitymapsIntent.ACTION_CONFIG_LOADED;

public class LaunchActivity extends ActionBarActivity
		implements SharedPreferences.OnSharedPreferenceChangeListener {

	private static final String STATE_KEY_LAUNCH_FRAGMENT = "launchFragment";

	private LaunchFragment mLaunchFragment;

	private LocalBroadcastManager mLocalBroadcastManager;

	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (ACTION_CONFIG_LOADED.equals(action)) {
				Config config = CitymapsIntent.getConfig(intent);

				SoftwareVersion currentVersion = SoftwareVersion.parse(BuildConfig.VERSION_NAME);
				SoftwareVersion appVersion = SoftwareVersion.parse(config.getAppVersion());
				SoftwareVersion minVersion = SoftwareVersion.parse(config.getMinVersion());

				LogEx.d(String.format("config=%s", config));

				if (currentVersion.compareTo(minVersion) < 0) {
					startActivity(new Intent(LaunchActivity.this, HardUpdateActivity.class));
					finish();
				} else if (currentVersion.compareTo(appVersion) < 0) {

				}
			}
		}
	};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

		if (savedInstanceState == null) {
			mLaunchFragment = new LaunchFragment();
			getSupportFragmentManager()
					.beginTransaction()
					.add(mLaunchFragment, null)
					.commit();
		} else {
			mLaunchFragment = (LaunchFragment) getSupportFragmentManager().getFragment(savedInstanceState, STATE_KEY_LAUNCH_FRAGMENT);
		}

		mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);

    }

	@Override
	protected void onStart() {
		super.onStart();

// TODO		SharedPreferenceUtils sharedPreferenceManager = SharedPreferenceUtils.getInstance(this);
		SoftwareVersion currentVersion = SoftwareVersion.parse(BuildConfig.VERSION_NAME);

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

				// TODO

				Bundle bundle = ActivityOptions
						.makeCustomAnimation(activity, 0, android.R.anim.fade_out)
						.toBundle();
				activity.startActivity(new Intent(activity, MainActivity.class), bundle);
				activity.finish();
			}
		}
	}
}
