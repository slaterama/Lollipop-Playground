package com.citymaps.mobile.android.view.housekeeping;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import com.citymaps.mobile.android.R;
import com.citymaps.mobile.android.app.TrackedActionBarActivity;
import com.citymaps.mobile.android.util.IntentUtils;
import com.citymaps.mobile.android.util.SharedPreferenceUtils;

public class TourActivity extends TrackedActionBarActivity {

	private boolean mStartupMode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tour);
		mStartupMode = IntentUtils.isStartupMode(getIntent(), false);
	}

	public void onButtonClick(View view) {
		int id = view.getId();
		switch (id) {
			case R.id.tour_skip_button:
				if (mStartupMode) {
					Intent intent;
					LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
					boolean gpsEnabled = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
					if (gpsEnabled) {

						// TODO Some sort of logic to choose Authenticate vs. Main?

						intent = new Intent(this, AuthenticateActivity.class);
					} else {
						intent = new Intent(this, EnableLocationActivity.class);
					}
					IntentUtils.putStartupMode(intent, mStartupMode);
					startActivity(intent);

					SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
					SharedPreferenceUtils.putTourProcessed(sp, true).apply();

					finish();
				} else {
					finish();
				}
				break;
		}
	}
}
