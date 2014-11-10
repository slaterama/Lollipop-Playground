package com.citymaps.mobile.android.app;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import com.segment.android.Analytics;

public class TrackedActionBarActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Analytics.onCreate(this);
	}

	@Override
	protected void onStart() {
		super.onStart();
		Analytics.activityStart(this);
	}

	@Override
	protected void onPause() {
		Analytics.activityPause(this);
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		Analytics.activityResume(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		Analytics.activityStop(this);
	}

}
