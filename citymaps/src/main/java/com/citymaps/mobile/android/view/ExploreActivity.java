package com.citymaps.mobile.android.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MarginLayoutParamsCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.*;
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
import com.citymaps.mobile.android.view.cards.explore.BestAroundCollectionViewHolder;

import java.util.List;

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
		}

		if (savedInstanceState == null) {
			mHelperFragment = HelperFragment.newInstance(mMapLocation, mMapRadius, mMapZoom);
			getSupportFragmentManager()
					.beginTransaction()
					.add(mHelperFragment, HelperFragment.FRAGMENT_TAG)
					.commit();
		} else {
			mHelperFragment = (HelperFragment) getSupportFragmentManager().getFragment(savedInstanceState, STATE_KEY_HELPER_FRAGMENT);
			onRequestsComplete();
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

		if (mHelperFragment.mFeaturedHeroItems != null) {
			mBestAroundRecyclerView.setAdapter(new BestAroundAdapter(this, mHelperFragment.mFeaturedHeroItems));
		}
		if (mHelperFragment.mFeaturedCollections != null) {
			mFeaturedCollectionsRecyclerView.setAdapter(new FeaturedCollectionsAdapter(this, mHelperFragment.mFeaturedCollections));
		}
		if (mHelperFragment.mFeaturedMappers != null) {
			mFeaturedMappersRecyclerView.setAdapter(new FeaturedMappersAdapter(this, mHelperFragment.mFeaturedMappers));
		}
		if (mHelperFragment.mFeaturedDeals != null) {
			mFeaturedDealsRecyclerView.setAdapter(new FeaturedDealsAdapter(this, mHelperFragment.mFeaturedDeals));
		}
	}

	public static class BestAroundAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
		private Context mContext;
		private List<SearchResult> mSearchResults;
		private int mComponentBaselineGrid;

		public BestAroundAdapter(Context context, List<SearchResult> searchResults) {
			super();
			mContext = context;
			mSearchResults = searchResults;
			mComponentBaselineGrid = context.getResources().getDimensionPixelOffset(R.dimen.component_baseline_grid);
		}

		@Override
		public int getItemCount() {
			return mSearchResults == null ? 0 : mSearchResults.size();
		}

		@Override
		public int getItemViewType(int position) {
			SearchResult result = mSearchResults.get(position);
			return result.getType().value();
		}

		@Override
		public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
			// TODO TEMP

			View view = LayoutInflater.from(mContext).inflate(R.layout.explore_card_best_around_collection, viewGroup, false);
			return new BestAroundCollectionViewHolder(view);
			// END TEMP
		}

		@Override
		public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
			SearchResult searchResult = mSearchResults.get(position);
			BestAroundCollectionViewHolder holder = (BestAroundCollectionViewHolder) viewHolder;
			holder.getNameView().setText(searchResult.getName());

			if (viewHolder.itemView.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
				LogEx.d();
				ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) viewHolder.itemView.getLayoutParams();
				MarginLayoutParamsCompat.setMarginStart(lp, position == 0 ? 0 : mComponentBaselineGrid);
				viewHolder.itemView.setLayoutParams(lp);
			}
		}
	}

	public static class FeaturedCollectionsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
		private Context mContext;
		private List<SearchResult> mSearchResults;
		private int mComponentBaselineGrid;

		public FeaturedCollectionsAdapter(Context context, List<SearchResult> searchResults) {
			super();
			mContext = context;
			mSearchResults = searchResults;
			mComponentBaselineGrid = context.getResources().getDimensionPixelOffset(R.dimen.component_baseline_grid);
		}

		@Override
		public int getItemCount() {
			return mSearchResults == null ? 0 : mSearchResults.size();
		}

		@Override
		public int getItemViewType(int position) {
			SearchResult result = mSearchResults.get(position);
			return result.getType().value();
		}

		@Override
		public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
			// TODO TEMP
			View view = LayoutInflater.from(mContext).inflate(R.layout.explore_card_featured_collection, viewGroup, false);
			return new BestAroundCollectionViewHolder(view);
			// END TEMP
		}

		@Override
		public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
			SearchResult searchResult = mSearchResults.get(position);
			BestAroundCollectionViewHolder holder = (BestAroundCollectionViewHolder) viewHolder;
			holder.getNameView().setText(searchResult.getName());

			if (viewHolder.itemView.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
				LogEx.d();
				ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) viewHolder.itemView.getLayoutParams();
				MarginLayoutParamsCompat.setMarginStart(lp, position == 0 ? 0 : mComponentBaselineGrid);
				viewHolder.itemView.setLayoutParams(lp);
			}
		}
	}

	public static class FeaturedMappersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
		private Context mContext;
		private List<User> mFeaturedMappers;
		private int mComponentBaselineGrid;

		public FeaturedMappersAdapter(Context context, List<User> featuredMappers) {
			super();
			mContext = context;
			mFeaturedMappers = featuredMappers;
			mComponentBaselineGrid = context.getResources().getDimensionPixelOffset(R.dimen.component_baseline_grid);
		}

		@Override
		public int getItemCount() {
			return mFeaturedMappers == null ? 0 : mFeaturedMappers.size();
		}

		/*
		@Override
		public int getItemViewType(int position) {
			User user = mFeaturedMappers.get(position);
			return user.getType().value();
		}
		*/

		@Override
		public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
			// TODO TEMP

			View view = LayoutInflater.from(mContext).inflate(R.layout.explore_card_featured_mapper, viewGroup, false);
			return new BestAroundCollectionViewHolder(view);
			// END TEMP
		}

		@Override
		public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
			User user = mFeaturedMappers.get(position);
			BestAroundCollectionViewHolder holder = (BestAroundCollectionViewHolder) viewHolder;
			holder.getNameView().setText(user.getName());

			if (viewHolder.itemView.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
				LogEx.d();
				ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) viewHolder.itemView.getLayoutParams();
				MarginLayoutParamsCompat.setMarginStart(lp, position == 0 ? 0 : mComponentBaselineGrid);
				viewHolder.itemView.setLayoutParams(lp);
			}
		}
	}

	public static class FeaturedDealsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
		private Context mContext;
		private List<SearchResult> mSearchResults;
		private int mComponentBaselineGrid;

		public FeaturedDealsAdapter(Context context, List<SearchResult> searchResults) {
			super();
			mContext = context;
			mSearchResults = searchResults;
			mComponentBaselineGrid = context.getResources().getDimensionPixelOffset(R.dimen.component_baseline_grid);
		}

		@Override
		public int getItemCount() {
			return mSearchResults == null ? 0 : mSearchResults.size();
		}

		@Override
		public int getItemViewType(int position) {
			SearchResult result = mSearchResults.get(position);
			return result.getType().value();
		}

		@Override
		public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
			// TODO TEMP
			View view = LayoutInflater.from(mContext).inflate(R.layout.explore_card_featured_deal, viewGroup, false);
			return new BestAroundCollectionViewHolder(view);
			// END TEMP
		}

		@Override
		public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
			SearchResult searchResult = mSearchResults.get(position);
			BestAroundCollectionViewHolder holder = (BestAroundCollectionViewHolder) viewHolder;
			holder.getNameView().setText(searchResult.getName());

			if (viewHolder.itemView.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
				LogEx.d();
				ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) viewHolder.itemView.getLayoutParams();
				MarginLayoutParamsCompat.setMarginStart(lp, position == 0 ? 0 : mComponentBaselineGrid);
				viewHolder.itemView.setLayoutParams(lp);
			}
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

		private List<SearchResult> mFeaturedHeroItems;
		private List<SearchResult> mFeaturedCollections;
		private List<User> mFeaturedMappers;
		private List<SearchResult> mFeaturedDeals;

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
					new Response.Listener<List<SearchResult>>() {
						@Override
						public void onResponse(List<SearchResult> response) {
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
					new Response.Listener<List<SearchResult>>() {
						@Override
						public void onResponse(List<SearchResult> response) {
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
					new Response.Listener<List<User>>() {
						@Override
						public void onResponse(List<User> response) {
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
					new Response.Listener<List<SearchResult>>() {
						@Override
						public void onResponse(List<SearchResult> response) {
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
