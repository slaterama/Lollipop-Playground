package com.citymaps.mobile.android.view.onboard;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import com.citymaps.mobile.android.R;
import com.citymaps.mobile.android.app.TrackedActionBarActivity;
import com.citymaps.mobile.android.view.MainActivity;

public class TourActivity extends TrackedActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour);
    }

	public void onButtonClick(View view) {
		int id = view.getId();
		switch (id) {
			case R.id.tour_skip_button:
				Intent intent;
				LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
				if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//					intent = new Intent(this, AuthenticateActivity.class);
					intent = new Intent(this, MainActivity.class);

					// TODO if we're in first run, open AuthenticateActivity instead of MainActivity

				} else {
					intent = new Intent(this, EnableLocationActivity.class);
				}
				startActivity(intent);
				finish();
				break;
		}
	}
}
