package com.citymaps.mobile.android.view;

import android.os.Bundle;
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
}
