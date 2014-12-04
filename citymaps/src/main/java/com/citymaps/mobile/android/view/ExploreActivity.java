package com.citymaps.mobile.android.view;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.MarginLayoutParamsCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
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
import com.citymaps.mobile.android.util.ResourcesUtils;
import com.citymaps.mobile.android.view.cards.BestAroundCollectionFixedHeightCardView;
import com.citymaps.mobile.android.view.cards.CollectionFixedHeightCardView;
import com.citymaps.mobile.android.view.cards.DealFixedHeightCardView;
import com.citymaps.mobile.android.view.cards.UserFixedHeightCardView;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ExploreActivity extends TrackedActionBarActivity
		implements View.OnLayoutChangeListener {

	private static final String STATE_KEY_HELPER_FRAGMENT = "helperFragment";

	/*
	private static int getCardWidth(View view, float cardsAcross, int cardMargin) {
		int initialWidth = view.getWidth() - view.getPaddingLeft() - view.getPaddingRight();
		int totalMargin = ((int) Math.ceil(cardsAcross) - 1) * cardMargin;
		return (int) ((initialWidth - totalMargin) / cardsAcross);
	}
	*/

	private LayoutInflater mInflater;

	private boolean mUseCompatPadding;
	private int mCardMaxElevation;
	private int mCardPerceivedMargin;
	private int mCardActualMargin;

	private float mDefaultCardsAcross;
	private float mBestAroundCardsAcross;
	private float mFeaturedCollectionsCardsAcross;
	private float mFeaturedMappersCardsAcross;
	private float mFeaturedDealsCardsAcross;

	private int mBestAroundDefaultCardWidth;
	private int mBestAroundCardWidth;
	private int mFeaturedCollectionCardWidth;
	private int mFeaturedMapperCardWidth;
	private int mFeaturedDealCardWidth;

	private Map<RecyclerType, RecyclerView> mRecyclerViews;
	private Map<RecyclerType, Adapter> mAdapters;

	private ParcelableLonLat mMapLocation;
	private float mMapRadius;
	private int mMapZoom;

	private HelperFragment mHelperFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_explore);
		
		mInflater = LayoutInflater.from(this);

		// Get dimensions and other resources for recycler view card layout

		Resources resources = getResources();
		mUseCompatPadding = resources.getBoolean(R.bool.explore_card_use_compat_padding);
		mCardMaxElevation = resources.getDimensionPixelOffset(R.dimen.explore_card_max_elevation);
		mCardPerceivedMargin = resources.getDimensionPixelOffset(R.dimen.explore_card_inner_margin);
		mCardActualMargin = mCardPerceivedMargin - (mUseCompatPadding ? 2 * mCardMaxElevation : 0);
		mDefaultCardsAcross = ResourcesUtils.getFloat(resources, R.dimen.explore_default_cards_across, 2.0f);
		mBestAroundCardsAcross = ResourcesUtils.getFloat(resources, R.dimen.explore_best_around_cards_across, 1.0f);
		mFeaturedCollectionsCardsAcross = ResourcesUtils.getFloat(resources, R.dimen.explore_featured_collections_cards_across, 2.0f);
		mFeaturedMappersCardsAcross = ResourcesUtils.getFloat(resources, R.dimen.explore_featured_mappers_cards_across, 2.0f);
		mFeaturedDealsCardsAcross = ResourcesUtils.getFloat(resources, R.dimen.explore_featured_deals_cards_across, 2.0f);

		// Set up views

		int length = RecyclerType.values().length;
		mRecyclerViews = new LinkedHashMap<RecyclerType, RecyclerView>(length);
		mRecyclerViews.put(RecyclerType.BEST_AROUND, (RecyclerView) findViewById(R.id.explore_best_around_recycler));
		mRecyclerViews.put(RecyclerType.FEATURED_COLLECTIONS, (RecyclerView) findViewById(R.id.explore_featured_collections_recycler));
		mRecyclerViews.put(RecyclerType.FEATURED_MAPPERS, (RecyclerView) findViewById(R.id.explore_featured_mappers_recycler));
		mRecyclerViews.put(RecyclerType.FEATURED_DEALS, (RecyclerView) findViewById(R.id.explore_featured_deals_recycler));
		for (RecyclerView view : mRecyclerViews.values()) {
			view.addOnLayoutChangeListener(this);
			view.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));

			// TODO TEMP
			view.getLayoutParams().height = (int) (resources.getDisplayMetrics().density * 225);

			if (mUseCompatPadding) {
				int paddingLeft = view.getPaddingLeft();
				int paddingTop = view.getPaddingTop();
				int paddingRight = view.getPaddingRight();
				int paddingBottom = view.getPaddingBottom();
				paddingLeft -= mCardMaxElevation;
				paddingRight -= mCardMaxElevation;
				view.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
			}
		}

		// Set up adapters

		mAdapters = new LinkedHashMap<RecyclerType, Adapter>(length);
		mAdapters.put(RecyclerType.BEST_AROUND, new BestAroundAdapter());
		mAdapters.put(RecyclerType.FEATURED_COLLECTIONS, new FeaturedCollectionsAdapter());
		mAdapters.put(RecyclerType.FEATURED_MAPPERS, new FeaturedMappersAdapter());
		mAdapters.put(RecyclerType.FEATURED_DEALS, new FeaturedDealsAdapter());
		for (RecyclerType type : RecyclerType.values()) {
			mRecyclerViews.get(type).setAdapter(mAdapters.get(type));
		}

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
			new Handler().post(new Runnable() {
				@Override
				public void run() {
					onRequestsComplete();
				}
			});
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

	@Override
	public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
		if (v.getWidth() > 0) {
			if (v == mRecyclerViews.get(RecyclerType.BEST_AROUND)) {
				mBestAroundDefaultCardWidth = getCardWidth(v, mDefaultCardsAcross);
				mBestAroundCardWidth = getCardWidth(v, mBestAroundCardsAcross);

				/*
				View view = mInflater.inflate(R.layout.card_explore_best_around_collection, mBestAroundRecyclerView, false);
				LinearLayout mainContainer = (LinearLayout) view.findViewById(R.id.explore_best_around_main_container);
				RelativeLayout infoContainer = (RelativeLayout) view.findViewById(R.id.explore_best_around_info_container);
				infoContainer.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, mBestAroundDefaultCardWidth));
				view.measure(0, 0);
				int height = mainContainer.getMeasuredHeight(); // - 24;
				LogEx.d(String.format("mBestAroundDefaultCardWidth=%d, height=%d", mBestAroundDefaultCardWidth, height));
				LogEx.d(String.format("paddingLeft=%d, paddingTop=%d, paddingRight=%d, paddingBottom=%d",
						view.getPaddingLeft(), view.getPaddingTop(), view.getPaddingRight(), view.getPaddingBottom()));
				LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mBestAroundRecyclerView.getLayoutParams();
				lp.height = 200;
				mBestAroundRecyclerView.setLayoutParams(lp);
				*/

				// TODO Since image is set to match_parent but card is wrap_content, the entire photo is shown and the imageview
				// is too tall. Need to constrain that imageview size somehow.

			} else if (v == mRecyclerViews.get(RecyclerType.FEATURED_COLLECTIONS)) {
				mFeaturedCollectionCardWidth= getCardWidth(v, mFeaturedCollectionsCardsAcross);
			} else if (v == mRecyclerViews.get(RecyclerType.FEATURED_MAPPERS)) {
				mFeaturedMapperCardWidth = getCardWidth(v, mFeaturedMappersCardsAcross);
			} else if (v == mRecyclerViews.get(RecyclerType.FEATURED_DEALS)) {
				mFeaturedDealCardWidth = getCardWidth(v, mFeaturedDealsCardsAcross);
			}

			// There should never be an instance where there are existing cards in the recycler views
			// when I am at this point, so don't worry about updating any cards here.

			v.removeOnLayoutChangeListener(this);
		}
	}

	private int getCardWidth(View view, float cardsAcross) {
		int elevationFactor = (mUseCompatPadding ? mCardMaxElevation : 0);
		int perceivedPaddingLeft = view.getPaddingLeft() + elevationFactor;
		int perceivedPaddingRight = view.getPaddingRight() + elevationFactor;
		int marginCount = (int) (Math.ceil(cardsAcross)) - 1;
		int perceivedTotalMargin = mCardPerceivedMargin * marginCount;
		int perceivedAvailableWidth = view.getWidth() - perceivedPaddingLeft - perceivedPaddingRight - perceivedTotalMargin;
		int perceivedCardWidth = (int) (perceivedAvailableWidth / cardsAcross);
		return perceivedCardWidth + 2 * elevationFactor;
	}

	protected void onRequestsComplete() {
		((BestAroundAdapter) mAdapters.get(RecyclerType.BEST_AROUND)).setData(mHelperFragment.mBestAround);
		((FeaturedCollectionsAdapter) mAdapters.get(RecyclerType.FEATURED_COLLECTIONS)).setData(mHelperFragment.mFeaturedCollections);
		((FeaturedMappersAdapter) mAdapters.get(RecyclerType.FEATURED_MAPPERS)).setData(mHelperFragment.mFeaturedMappers);
		((FeaturedDealsAdapter) mAdapters.get(RecyclerType.FEATURED_DEALS)).setData(mHelperFragment.mFeaturedDeals);
	}

	protected abstract class ExploreAdapter<D> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
		protected List<D> mData;

		public void setData(List<D> data) {
			mData = data;
			notifyDataSetChanged();
		}

		public List<D> getData() {
			return mData;
		}

		@Override
		public int getItemCount() {
			return (mData == null ? 0 : mData.size());
		}
	}

	public class BestAroundAdapter extends ExploreAdapter<SearchResult> {
		@Override
		public int getItemViewType(int position) {
			SearchResult result = mData.get(position);
			return result.getType().value();
		}

		@Override
		public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
			View view = new BestAroundCollectionFixedHeightCardView(ExploreActivity.this);
			view.setLayoutParams(new RecyclerView.LayoutParams(mBestAroundCardWidth, RecyclerView.LayoutParams.WRAP_CONTENT));
			return new ViewHolder(view) {};
		}

		@Override
		public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
			SearchResult searchResult = mData.get(position);
			BestAroundCollectionFixedHeightCardView cardView = (BestAroundCollectionFixedHeightCardView) holder.itemView;
			cardView.getNameView().setText(searchResult.getName());
			if (cardView.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
				ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) cardView.getLayoutParams();
				int margin = (int) (mCardPerceivedMargin - 2 * cardView.getMaxCardElevation());
				MarginLayoutParamsCompat.setMarginEnd(mlp, position < getItemCount() - 1 ? margin : 0);
				cardView.setLayoutParams(mlp);
			}
		}
	}

	public class FeaturedCollectionsAdapter extends ExploreAdapter<SearchResult> {
		@Override
		public int getItemViewType(int position) {
			SearchResult result = mData.get(position);
			return result.getType().value();
		}

		@Override
		public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
			View view = new CollectionFixedHeightCardView(ExploreActivity.this);
			view.setLayoutParams(new RecyclerView.LayoutParams(mFeaturedCollectionCardWidth, RecyclerView.LayoutParams.WRAP_CONTENT));
			return new ViewHolder(view) {};
		}

		@Override
		public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
			SearchResult searchResult = mData.get(position);
			CollectionFixedHeightCardView cardView = (CollectionFixedHeightCardView) holder.itemView;
			cardView.getNameView().setText(searchResult.getName());
			if (cardView.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
				ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) cardView.getLayoutParams();
				int margin = (int) (mCardPerceivedMargin - 2 * cardView.getMaxCardElevation());
				MarginLayoutParamsCompat.setMarginEnd(mlp, position < getItemCount() - 1 ? margin : 0);
				cardView.setLayoutParams(mlp);
			}
		}
	}

	public class FeaturedMappersAdapter extends ExploreAdapter<User> {
		/*
		@Override
		public int getItemViewType(int position) {
			User user = mFeaturedMappers.get(position);
			return user.getType().value();
		}
		*/

		@Override
		public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
			View view = new UserFixedHeightCardView(ExploreActivity.this);
			view.setLayoutParams(new RecyclerView.LayoutParams(mFeaturedMapperCardWidth, RecyclerView.LayoutParams.WRAP_CONTENT));
			return new ViewHolder(view) {};
		}

		@Override
		public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
			User user = mData.get(position);
			UserFixedHeightCardView cardView = (UserFixedHeightCardView) holder.itemView;
			cardView.getNameView().setText(user.getName());
			if (cardView.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
				ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) cardView.getLayoutParams();
				int margin = (int) (mCardPerceivedMargin - 2 * cardView.getMaxCardElevation());
				MarginLayoutParamsCompat.setMarginEnd(mlp, position < getItemCount() - 1 ? margin : 0);
				cardView.setLayoutParams(mlp);
			}
		}
	}

	public class FeaturedDealsAdapter extends ExploreAdapter<SearchResult> {
		@Override
		public int getItemViewType(int position) {
			SearchResult result = mData.get(position);
			return result.getType().value();
		}

		@Override
		public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
			View view = new DealFixedHeightCardView(ExploreActivity.this);
			view.setLayoutParams(new RecyclerView.LayoutParams(mFeaturedDealCardWidth, RecyclerView.LayoutParams.WRAP_CONTENT));
			return new ViewHolder(view) {};
		}

		@Override
		public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
			SearchResult searchResult = mData.get(position);
			DealFixedHeightCardView cardView = (DealFixedHeightCardView) holder.itemView;
			cardView.getNameView().setText(searchResult.getName());
			if (cardView.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
				ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) cardView.getLayoutParams();
				int margin = (int) (mCardPerceivedMargin - 2 * cardView.getMaxCardElevation());
				MarginLayoutParamsCompat.setMarginEnd(mlp, position < getItemCount() - 1 ? margin : 0);
				cardView.setLayoutParams(mlp);
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

		private List<SearchResult> mBestAround;
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
							mBestAround = response;
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

	private static enum RecyclerType {
		BEST_AROUND,
		FEATURED_COLLECTIONS,
		FEATURED_MAPPERS,
		FEATURED_DEALS
	}
}
