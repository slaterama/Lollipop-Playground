package com.citymaps.mobile.android.view.housekeeping;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.View;
import com.citymaps.mobile.android.R;
import com.citymaps.mobile.android.app.TrackedActionBarActivity;
import com.citymaps.mobile.android.util.Pref;
import com.citymaps.mobile.android.util.IntentUtils;
import com.citymaps.mobile.android.util.SharedPrefUtils;

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

		// TODO Some sort of logic to choose Authenticate vs. Main?

		Intent intent = new Intent(this, AuthenticateActivity.class);
		IntentUtils.putStartupMode(intent, true);
		startActivity(intent);

		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPrefUtils.putBoolean(sp.edit(), Pref.ENABLE_LOCATION_PROCESSED, true).apply();

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
