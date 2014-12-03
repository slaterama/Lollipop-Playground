package com.citymaps.mobile.android.view;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.citymaps.mobile.android.R;
import com.citymaps.mobile.android.app.TrackedActionBarActivity;
import com.citymaps.mobile.android.app.VolleyManager;
import com.citymaps.mobile.android.map.ParcelableLonLat;
import com.citymaps.mobile.android.model.SearchResult;
import com.citymaps.mobile.android.model.User;
import com.citymaps.mobile.android.model.request.SearchResultsRequest;
import com.citymaps.mobile.android.model.request.UsersRequest;
import com.citymaps.mobile.android.util.IntentUtils;
import com.citymaps.mobile.android.util.LogEx;
import com.citymaps.mobile.android.util.MapUtils;
import com.segment.android.info.Screen;

import java.util.UUID;

public class ExploreActivity extends TrackedActionBarActivity {

	private static final String STATE_KEY_HELPER_FRAGMENT = "helperFragment";

	private RecyclerView mBestAroundRecyclerView;
	private RecyclerView mFeaturedCollectionsRecyclerView;
	private RecyclerView mFeaturedMappersRecyclerView;
	private RecyclerView mFeaturedDealsRecyclerView;

	private int mBestAroundCardsAcross;
	private int mFeaturedCollectionsCardsAcross;
	private int mFeaturedMappersCardsAcross;
	private int mFeaturedDealsCardsAcross;

	private ParcelableLonLat mMapLocation;
	private float mMapRadius;
	private int mMapZoom;

	private HelperFragment mHelperFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_explore);

		mBestAroundRecyclerView = (RecyclerView) findViewById(R.id.explore_best_around_recycler);
		mFeaturedCollectionsRecyclerView = (RecyclerView) findViewById(R.id.explore_featured_collections_recycler);
		mFeaturedMappersRecyclerView = (RecyclerView) findViewById(R.id.explore_featured_mappers_recycler);
		mFeaturedDealsRecyclerView = (RecyclerView) findViewById(R.id.explore_featured_deals_recycler);

		mBestAroundRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
		mFeaturedCollectionsRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
		mFeaturedMappersRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
		mFeaturedDealsRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));

		// TODO TEMP
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		int tempHeight = (int) (200 * metrics.density);
		mBestAroundRecyclerView.getLayoutParams().height = tempHeight;
		mFeaturedCollectionsRecyclerView.getLayoutParams().height = tempHeight;
		mFeaturedMappersRecyclerView.getLayoutParams().height = tempHeight;
		mFeaturedDealsRecyclerView.getLayoutParams().height = tempHeight;
		// END TEMP

		Resources resources = getResources();
		mBestAroundCardsAcross = resources.getInteger(R.integer.explore_best_around_cards_across);
		mFeaturedCollectionsCardsAcross = resources.getInteger(R.integer.explore_featured_collections_cards_across);
		mFeaturedMappersCardsAcross = resources.getInteger(R.integer.explore_featured_mappers_cards_across);
		mFeaturedDealsCardsAcross = resources.getInteger(R.integer.explore_featured_deals_cards_across);

		Intent intent = getIntent();
		if (intent != null) {
			mMapLocation = IntentUtils.getMapLocation(intent);
			mMapRadius = IntentUtils.getMapRadius(intent, MapUtils.DEFAULT_SEARCH_RADIUS);
			mMapZoom = IntentUtils.getMapZoom(intent, MapUtils.DEFAULT_SEARCH_ZOOM);

			String searchId = UUID.randomUUID().toString();

			LogEx.d(String.format("mMapLocation=%s, mMapRadius=%f, mMapZoom=%d, searchId=%s", mMapLocation, mMapRadius, mMapZoom, searchId));
		}

		if (savedInstanceState == null) {
			mHelperFragment = HelperFragment.newInstance(mMapLocation, mMapRadius, mMapZoom);
			getSupportFragmentManager()
					.beginTransaction()
					.add(mHelperFragment, HelperFragment.FRAGMENT_TAG)
					.commit();
		} else {
			mHelperFragment = (HelperFragment) getSupportFragmentManager().getFragment(savedInstanceState, STATE_KEY_HELPER_FRAGMENT);
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		getSupportFragmentManager().putFragment(outState, STATE_KEY_HELPER_FRAGMENT, mHelperFragment);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.explore, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	protected void onRequestsComplete() {
		LogEx.d();
	}

	public static class BestAroundAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {



		@Override
		public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
			return null;
		}

		@Override
		public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {

		}

		@Override
		public int getItemCount() {
			return 0;
		}
	}

	public static class HelperFragment extends Fragment {
		public static final String FRAGMENT_TAG = HelperFragment.class.getName();

		private static final String ARG_MAP_LOCATION = "mapLocation";
		private static final String ARG_MAP_RADIUS = "mapRadius";
		private static final String ARG_MAP_ZOOM = "mapZoom";

		public static final HelperFragment newInstance(ParcelableLonLat location, float radius, int zoom) {
			HelperFragment fragment = new HelperFragment();
			Bundle args = new Bundle(3);
			args.putParcelable(ARG_MAP_LOCATION, location);
			args.putFloat(ARG_MAP_RADIUS, radius);
			args.putInt(ARG_MAP_ZOOM, zoom);
			fragment.setArguments(args);
			return fragment;
		}

		private ExploreActivity mActivity;

		private ParcelableLonLat mMapLocation;
		private float mMapRadius;
		private int mMapZoom;

		private int mCompletedRequests = 0;

		private SearchResult[] mFeaturedHeroItems;
		private SearchResult[] mFeaturedCollections;
		private User[] mFeaturedMappers;
		private SearchResult[] mFeaturedDeals;

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			try {
				mActivity = (ExploreActivity) activity;
			} catch (ClassCastException e) {
				throw new ClassCastException(
						"ExploreActivity.HelperFragment must be attached to ExploreActivity");
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

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);

			int offset = 0;
			int limit = 6;
			SearchResultsRequest featuredHeroItemsRequest = SearchResultsRequest.newFeaturedHeroItemsRequest(getActivity(),
					mMapLocation, mMapZoom, mMapRadius, offset, limit,
					new Response.Listener<SearchResult[]>() {
						@Override
						public void onResponse(SearchResult[] response) {
							mFeaturedHeroItems = response;
							onRequestComplete();
						}
					},
					new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							LogEx.d(String.format("error=%s", error));
							onRequestComplete();
						}
					});

			limit = 12;
			SearchResultsRequest featuredCollectionsRequest = SearchResultsRequest.newFeaturedCollectionsRequest(getActivity(),
					mMapLocation, mMapZoom, mMapRadius, offset, limit,
					new Response.Listener<SearchResult[]>() {
						@Override
						public void onResponse(SearchResult[] response) {
							mFeaturedCollections = response;
							onRequestComplete();
						}
					},
					new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							LogEx.d(String.format("error=%s", error));
							onRequestComplete();
						}
					});

			UsersRequest featuredMappersRequest = UsersRequest.newFeaturedMappersRequest(getActivity(),
					mMapLocation, mMapRadius, offset, limit,
					new Response.Listener<User[]>() {
						@Override
						public void onResponse(User[] response) {
							mFeaturedMappers = response;
							onRequestComplete();
						}
					},
					new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							LogEx.d(String.format("error=%s", error));
							onRequestComplete();
						}
					});

			SearchResultsRequest featuredDealsRequest = SearchResultsRequest.newFeaturedDealsRequest(getActivity(),
					mMapLocation, mMapZoom, mMapRadius, offset, limit,
					new Response.Listener<SearchResult[]>() {
						@Override
						public void onResponse(SearchResult[] response) {
							mFeaturedDeals = response;
							onRequestComplete();
						}
					},
					new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							LogEx.d(String.format("error=%s", error));
							onRequestComplete();
						}
					});

			RequestQueue queue = VolleyManager.getInstance(getActivity()).getRequestQueue();
			queue.add(featuredHeroItemsRequest);
			queue.add(featuredCollectionsRequest);
			queue.add(featuredMappersRequest);
			queue.add(featuredDealsRequest);
		}

		private void onRequestComplete() {
			mCompletedRequests++;
			if (mCompletedRequests == 4) {
				mActivity.onRequestsComplete();
			}
		}
	}
}
