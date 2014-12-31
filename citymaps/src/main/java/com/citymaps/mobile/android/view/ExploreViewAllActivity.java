package com.citymaps.mobile.android.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import com.citymaps.mobile.android.R;
import com.citymaps.mobile.android.app.TrackedActionBarActivity;
import com.citymaps.mobile.android.map.ParcelableLonLat;
import com.citymaps.mobile.android.model.SearchResult;
import com.citymaps.mobile.android.model.SearchResultCollection;
import com.citymaps.mobile.android.model.SearchResultPlace;
import com.citymaps.mobile.android.model.User;
import com.citymaps.mobile.android.util.IntentUtils;
import com.citymaps.mobile.android.util.LogEx;
import com.citymaps.mobile.android.util.MapUtils;
import com.citymaps.mobile.android.view.cards.CardType;

public abstract class ExploreViewAllActivity<D> extends TrackedActionBarActivity {

	private static final String STATE_KEY_DATA_FRAGMENT = "dataFragment";

	protected RecyclerView mRecyclerView;

	protected DataFragment mDataFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_explore_view_all);
		mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
		mRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

		if (savedInstanceState == null) {
			mDataFragment = DataFragment.newInstance(getCardType(), getIntent());
			getSupportFragmentManager()
					.beginTransaction()
					.add(mDataFragment, DataFragment.FRAGMENT_TAG)
					.commit();
		} else {
			mDataFragment = (DataFragment) getSupportFragmentManager().getFragment(savedInstanceState, STATE_KEY_DATA_FRAGMENT);
		}
	}

	protected abstract CardType getCardType();

	/*
	 * ******************************************************************************
	 * Concrete Activity classes
	 * ******************************************************************************
	 */

	public static class ExploreViewAllHeroActivity extends ExploreViewAllActivity<SearchResult> {
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setTitle(R.string.explore_best_around_me);
		}

		@Override
		protected CardType getCardType() {
			return CardType.HERO;
		}
	}

	public static class ExploreViewAllFeaturedCollectionsActivity extends ExploreViewAllActivity<SearchResultCollection> {
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setTitle(R.string.explore_featured_collections);
		}

		@Override
		protected CardType getCardType() {
			return CardType.FEATURED_COLLECTIONS;
		}
	}

	public static class ExploreViewAllFeaturedMappersActivity extends ExploreViewAllActivity<User> {
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setTitle(R.string.explore_featured_mappers);
		}

		@Override
		protected CardType getCardType() {
			return CardType.FEATURED_MAPPERS;
		}
	}

	public static class ExploreViewAllFeaturedDealsActivity extends ExploreViewAllActivity<SearchResultPlace> {
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setTitle(R.string.explore_featured_deals);
		}

		@Override
		protected CardType getCardType() {
			return CardType.FEATURED_DEALS;
		}
	}

	/*
	 * ******************************************************************************
	 * Data fragment
	 * ******************************************************************************
	 */

	public abstract static class DataFragment<D> extends Fragment {
		public static final String FRAGMENT_TAG = DataFragment.class.getName();

		private static final String ARG_MAP_LOCATION = "mapLocation";
		private static final String ARG_MAP_RADIUS = "mapRadius";
		private static final String ARG_MAP_ZOOM = "mapZoom";

		public static DataFragment newInstance(CardType cardType, Intent intent) {
			final DataFragment fragment;
			switch (cardType) {
				case FEATURED_COLLECTIONS:
					fragment = new FeaturedCollectionsDataFragment();
					break;
				case FEATURED_MAPPERS:
					fragment = new FeaturedMappersDataFragment();
					break;
				case FEATURED_DEALS:
					fragment = new FeaturedDealsDataFragment();
					break;
				case HERO:
				default:
					fragment = new HeroDataFragment();
			}
			Bundle args = new Bundle(3);
			args.putParcelable(ARG_MAP_LOCATION, IntentUtils.getMapLocation(intent));
			args.putFloat(ARG_MAP_RADIUS, IntentUtils.getMapRadius(intent, MapUtils.DEFAULT_SEARCH_RADIUS));
			args.putInt(ARG_MAP_ZOOM, IntentUtils.getMapZoom(intent, MapUtils.DEFAULT_SEARCH_ZOOM));
			fragment.setArguments(args);
			return fragment;
		}

		protected ExploreViewAllActivity mActivity;

		protected ParcelableLonLat mMapLocation;
		protected float mMapRadius;
		protected int mMapZoom;

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			try {
				mActivity = (ExploreViewAllActivity) activity;
			} catch (ClassCastException e) {
				throw new ClassCastException(
						"ExploreViewAllActivity.DataFragment must be attached to ExploreViewAllActivity");
			}
		}

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setRetainInstance(true);

			Bundle args = getArguments();
			if (args != null) {
				mMapLocation = args.getParcelable(ARG_MAP_LOCATION);
				mMapRadius = args.getFloat(ARG_MAP_RADIUS);
				mMapZoom = args.getInt(ARG_MAP_ZOOM);
			}
		}
	}

	/*
	 * ******************************************************************************
	 * Concrete data fragment classes
	 * ******************************************************************************
	 */

	public static class HeroDataFragment extends DataFragment<SearchResult> {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			LogEx.d(String.format("mMapLocation=%s, mMapRadius=%.2f, mMapZoom=%d", mMapLocation, mMapRadius, mMapZoom));
		}
	}

	public static class FeaturedCollectionsDataFragment extends DataFragment<SearchResultCollection> {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			LogEx.d(String.format("mMapLocation=%s, mMapRadius=%.2f, mMapZoom=%d", mMapLocation, mMapRadius, mMapZoom));
		}
	}

	public static class FeaturedMappersDataFragment extends DataFragment<User> {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			LogEx.d(String.format("mMapLocation=%s, mMapRadius=%.2f, mMapZoom=%d", mMapLocation, mMapRadius, mMapZoom));
		}
	}

	public static class FeaturedDealsDataFragment extends DataFragment<SearchResultPlace> {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			LogEx.d(String.format("mMapLocation=%s, mMapRadius=%.2f, mMapZoom=%d", mMapLocation, mMapRadius, mMapZoom));
		}
	}
}
