package com.citymaps.mobile.android.view;

import android.content.*;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.citymaps.mobile.android.BuildConfig;
import com.citymaps.mobile.android.R;
import com.citymaps.mobile.android.content.CitymapsIntent;
import com.citymaps.mobile.android.map.MapViewService;
import com.citymaps.mobile.android.model.vo.Config;
import com.citymaps.mobile.android.os.SoftwareVersion;
import com.citymaps.mobile.android.provider.ConfigProvider;
import com.citymaps.mobile.android.util.LogEx;

import java.net.URI;

import static com.citymaps.mobile.android.content.CitymapsIntent.ACTION_CONFIG_LOADED;

public class MainActivity extends ActionBarActivity
		implements SharedPreferences.OnSharedPreferenceChangeListener, MainFragment.OnFragmentInteractionListener {

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
					startActivity(new Intent(MainActivity.this, HardUpdateActivity.class));
					finish();
				} else if (currentVersion.compareTo(appVersion) < 0) {
					// Show dialog
				}
			}
		}
	};

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
		mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
    }

	@Override
	protected void onStart() {
		super.onStart();
		mLocalBroadcastManager.registerReceiver(mBroadcastReceiver, new IntentFilter(ACTION_CONFIG_LOADED));
	}

	@Override
	protected void onResume() {
		super.onResume();
		startService(new Intent(getApplicationContext(), MapViewService.class));
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (isFinishing()) {
			stopService(new Intent(getApplicationContext(), MapViewService.class));
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		mLocalBroadcastManager.unregisterReceiver(mBroadcastReceiver);
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
		switch (id) {
			case R.id.action_settings:
				return true;
			case R.id.action_profile:
				startActivity(new Intent(this, ProfileActivity.class));
				return true;
			case R.id.action_friend_finder:
				doTest();
				return true;
		}
        return super.onOptionsItemSelected(item);
    }

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		LogEx.d();
	}

	@Override
	public void onFragmentInteraction(Uri uri) {

	}

	public void doTest() {
		ContentResolver resolver = getContentResolver();

		ContentValues values = new ContentValues();
		values.put(ConfigProvider.KEY, "app_version_code");
		values.put(ConfigProvider.VALUE, 6);

		Uri uri = resolver.insert(ConfigProvider.CONTENT_URI, values);
		Toast.makeText(getBaseContext(), uri.toString(), Toast.LENGTH_LONG).show();
	}
}
