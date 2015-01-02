package com.citymaps.mobile.android.view;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import com.citymaps.mobile.android.R;
import com.citymaps.mobile.android.app.TrackedActionBarActivity;
import com.citymaps.mobile.android.util.IntentUtils;
import com.citymaps.mobile.android.view.cards.CardType;

public class ExploreViewAllActivity extends TrackedActionBarActivity {

	private static final String STATE_KEY_EXPLORE_VIEW_ALL_FRAGMENT = "exploreViewAllFragment";

	private CardType mCardType;

	private ExploreViewAllFragment mFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mCardType = IntentUtils.getCardType(getIntent());
		if (savedInstanceState != null) {
			mFragment = (ExploreViewAllFragment)
					getSupportFragmentManager().getFragment(savedInstanceState, STATE_KEY_EXPLORE_VIEW_ALL_FRAGMENT);
		}
		if (mFragment == null) {
			mFragment = ExploreViewAllFragment.newInstance(getIntent());
			getSupportFragmentManager()
					.beginTransaction()
					.add(android.R.id.content, mFragment, null)
					.commit();
		}
		setTitle(mFragment.getTitleResId());
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		getSupportFragmentManager().putFragment(outState, STATE_KEY_EXPLORE_VIEW_ALL_FRAGMENT, mFragment);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.explore_view_all, menu);
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
