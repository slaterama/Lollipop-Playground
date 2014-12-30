package com.citymaps.mobile.android.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import com.citymaps.mobile.android.R;
import com.citymaps.mobile.android.app.TrackedActionBarActivity;

public class CollectionActivity extends TrackedActionBarActivity {

	private TextView mHelloWorld;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_collection);
		mHelloWorld = (TextView) findViewById(R.id.hello_world);

		Intent intent = getIntent();
		Uri data = intent.getData();
		String id = data.getLastPathSegment();
		if (TextUtils.isEmpty(id)) {
			mHelloWorld.setText("Empty collection id passed to activity");
		} else {
			mHelloWorld.setText(String.format("Map id=%s", id));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.collection, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
