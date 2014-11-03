package com.citymaps.mobile.android.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SpinnerAdapter;
import com.citymaps.mobile.android.R;
import com.citymaps.mobile.android.map.MapViewService;

public class MainActivity extends ActionBarActivity
		implements MainFragment.OnFragmentInteractionListener {

	private SpinnerAdapter mAppBarNavigationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
			startService(new Intent(getApplicationContext(), MapViewService.class));
        }
    }

	@Override
	protected void onPause() {
		super.onPause();
		if (isFinishing()) {
			stopService(new Intent(getApplicationContext(), MapViewService.class));
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
				return true;
			case R.id.action_profile:

				// TODO VERY TEMP. Just trying to make an Http request
				/*
				new AsyncTask<Void, Void, Void>() {
					@Override
					protected Void doInBackground(Void... params) {
						GetBuildHttpRequest r = GetBuildHttpRequest.makeRequest(MainActivity.this, "Production", "", "");
						Wrapper<ApiBuild, Exception> result = r.execute();

						return null;
					}
				}.execute();
				*/

				startActivity(new Intent(this, ProfileActivity.class));

				return true;
		}
        return super.onOptionsItemSelected(item);
    }

	@Override
	public void onFragmentInteraction(Uri uri) {

	}
}
