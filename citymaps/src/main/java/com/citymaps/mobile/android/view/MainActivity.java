package com.citymaps.mobile.android.view;

import android.content.*;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.citymaps.mobile.android.R;
import com.citymaps.mobile.android.app.TrackedActionBarActivity;
import com.citymaps.mobile.android.map.MapViewService;
import com.citymaps.mobile.android.model.Config;
import com.citymaps.mobile.android.notused_provider.config.ConfigContract.Settings;
import com.citymaps.mobile.android.util.IntentUtils;
import com.citymaps.mobile.android.util.LogEx;
import com.citymaps.mobile.android.util.SharedPreferenceUtils;
import com.citymaps.mobile.android.util.UpdateUtils;
import com.citymaps.mobile.android.view.housekeeping.AuthenticateActivityNew;
import com.citymaps.mobile.android.view.housekeeping.HardUpdateActivity;
import com.citymaps.mobile.android.view.housekeeping.SoftUpdateDialogFragment;
import com.citymaps.mobile.android.view.settings.PreferencesActivity;

import static com.citymaps.mobile.android.util.IntentUtils.ACTION_CONFIG_LOADED;

public class MainActivity extends TrackedActionBarActivity
		implements SharedPreferences.OnSharedPreferenceChangeListener, MainFragment.OnFragmentInteractionListener {

	private static final int REQUEST_CODE_PREFERENCES = 0;
	public static final int RESULT_LOGOUT = RESULT_FIRST_USER;
	public static final int RESULT_LOGIN = RESULT_FIRST_USER + 1;

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
        setContentView(R.layout.activity_main);
		mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
    }

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		if (savedInstanceState == null) {
			// First of all, examine any saved config for hard/soft update
			SharedPreferences sp = SharedPreferenceUtils.getConfigSharedPreferences(this);
			processConfig(SharedPreferenceUtils.getConfig(sp));
			/*
			if (isFinishing()) {
				return;
			}
			*/
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case REQUEST_CODE_PREFERENCES:
				switch (resultCode) {
					case RESULT_LOGIN:
					case RESULT_LOGOUT:
						startActivity(new Intent(this, AuthenticateActivityNew.class));
						break;
				}
				break;
			default:
				super.onActivityResult(requestCode, resultCode, data);
		}
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
				startActivityForResult(new Intent(this, PreferencesActivity.class), REQUEST_CODE_PREFERENCES);
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

	private void processConfig(Config config) {
		UpdateUtils.UpdateType updateType = UpdateUtils.getUpdateType(this, config);
		switch (updateType) {
			case HARD:
				startActivity(new Intent(this, HardUpdateActivity.class));
				finish();
				break;
			case SOFT:
				FragmentManager manager = getSupportFragmentManager();
				if (manager.findFragmentByTag(SoftUpdateDialogFragment.FRAGMENT_TAG) == null) {
					SoftUpdateDialogFragment.newInstance().show(manager, SoftUpdateDialogFragment.FRAGMENT_TAG);
				}
		}
	}

	public void doTest() {
		ContentResolver resolver = getContentResolver();

		ContentValues values = new ContentValues();
		values.put(Settings.KEY, "app_version_code");
		values.put(Settings.VALUE, 6);

		Uri uri = resolver.insert(Settings.CONTENT_URI, values);
		Toast.makeText(getBaseContext(), uri.toString(), Toast.LENGTH_LONG).show();

		Cursor cursor = resolver.query(Settings.CONTENT_URI,
				new String[]{Settings.VALUE},
				Settings.KEY + "=?",
				new String[]{"app_version_code"},
				null);
	}
}
