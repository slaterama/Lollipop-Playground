package com.citymaps.mobile.android.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import com.citymaps.mobile.android.R;
import com.citymaps.mobile.android.content.CitymapsIntent;
import com.citymaps.mobile.android.map.MapViewService;
import com.citymaps.mobile.android.model.vo.Config;
import com.citymaps.mobile.android.util.LogEx;

import static com.citymaps.mobile.android.content.CitymapsIntent.ACTION_CONFIG_LOADED;

public class MainActivity extends ActionBarActivity
		implements MainFragment.OnFragmentInteractionListener {

	private LocalBroadcastManager mLocalBroadcastManager;

	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (ACTION_CONFIG_LOADED.equals(action)) {
				Config config = CitymapsIntent.getConfig(intent);
				LogEx.d(String.format("config=%s", config));
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
		}
        return super.onOptionsItemSelected(item);
    }

	@Override
	public void onFragmentInteraction(Uri uri) {

	}
}
