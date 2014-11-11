package com.citymaps.mobile.android.view.housekeeping;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import com.citymaps.mobile.android.R;
import com.citymaps.mobile.android.app.TrackedActionBarActivity;
import com.citymaps.mobile.android.util.UpdateUtils;

public class HardUpdateActivity extends TrackedActionBarActivity {

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		if (!getResources().getBoolean(R.bool.hard_update_allow_orientation_change)) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
        setContentView(R.layout.activity_hard_update);
    }

	public void onUpgradeClick(View view) {
		UpdateUtils.goToPlayStore(this);
		finish();
	}
}
