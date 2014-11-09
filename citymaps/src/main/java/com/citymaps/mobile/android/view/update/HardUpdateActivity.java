package com.citymaps.mobile.android.view.update;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import com.citymaps.mobile.android.R;
import com.citymaps.mobile.android.util.UpdateUtils;

public class HardUpdateActivity extends Activity {

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hard_update);
    }

	public void onUpgradeClick(View view) {
		UpdateUtils.goToPlayStore(this);
		finish();
	}
}
