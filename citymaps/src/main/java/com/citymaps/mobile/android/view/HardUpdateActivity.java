package com.citymaps.mobile.android.view;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import com.citymaps.mobile.android.BuildConfig;
import com.citymaps.mobile.android.R;

public class HardUpdateActivity extends Activity {

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hard_update);
    }

	public void onUpgradeClick(View view) {
		try {
			String urlString = "market://details?id=" + BuildConfig.PLAY_STORE_ID;
			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(urlString)));
		} catch (ActivityNotFoundException e) {
			String urlString = "http://play.google.com/store/apps/details?id=" + BuildConfig.PLAY_STORE_ID;
			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(urlString)));
		}
		finish();
	}
}
