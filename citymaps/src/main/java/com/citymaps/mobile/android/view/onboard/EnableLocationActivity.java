package com.citymaps.mobile.android.view.onboard;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import com.citymaps.mobile.android.R;
import com.citymaps.mobile.android.app.TrackedActionBarActivity;
import com.citymaps.mobile.android.view.MainActivity;

public class EnableLocationActivity extends TrackedActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		if (!getResources().getBoolean(R.bool.enable_location_allow_orientation_change)) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		setContentView(R.layout.activity_enable_location);
    }

	@Override
	protected void onResume() {
		super.onResume();
		LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			onContinue();
		}
	}

	private void onContinue() {
		// TODO Try to authenticate ?? What about StartupService?
		// Also what to do if connected to the internet vs. not connected?

		startActivity(new Intent(this, MainActivity.class));
		finish();
	}

	public void onButtonClick(View view) {
		int id = view.getId();
		switch (id) {
			case R.id.loc_settings_button:
				startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
				break;
			case R.id.loc_skip_button:
				onContinue();
				break;
		}
	}
}
