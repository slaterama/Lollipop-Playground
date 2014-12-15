package com.citymaps.mobile.android.view;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.MarginLayoutParamsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.citymaps.mobile.android.R;
import com.citymaps.mobile.android.app.TrackedActionBarActivity;
import com.citymaps.mobile.android.app.VolleyManager;
import com.citymaps.mobile.android.map.ParcelableLonLat;
import com.citymaps.mobile.android.model.*;
import com.citymaps.mobile.android.model.request.SearchResultsRequest;
import com.citymaps.mobile.android.model.request.UsersRequest;
import com.citymaps.mobile.android.util.IntentUtils;
import com.citymaps.mobile.android.util.LogEx;
import com.citymaps.mobile.android.util.MapUtils;
import com.citymaps.mobile.android.util.ResourcesUtils;
import com.citymaps.mobile.android.view.cards.*;

import java.util.*;

public class ExploreActivity extends TrackedActionBarActivity
		implements View.OnLayoutChangeListener {

	private static final int CAROUSEL_INITIAL_LOAD_DELAY = 250;
	private static final int CAROUSEL_ITEM_LOAD_DELAY = 100;

	private static final String STATE_KEY_HELPER_FRAGMENT = "helperFragment";

	private boolean mUseCompatPadding;
	private int mCardMaxElevation;
	private int mCardPerceivedMargin;

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

	private Map<CarouselType, Carousel> mCarousels;

	private ParcelableLonLat mMapLocation;
	private float mMapRadius;
	private int mMapZoom;

	private HelperFragment mHelperFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_explore);

		// Get dimensions and other resources for recycler view card layout

		Resources resources = getResources();
		mUseCompatPadding = resources.getBoolean(R.bool.explore_card_use_compat_padding);
		mCardMaxElevation = resources.getDimensionPixelOffset(R.dimen.explore_card_max_elevation);
		mCardPerceivedMargin = resources.getDimensionPixelOffset(R.dimen.explore_card_inner_margin);
		mDefaultCardsAcross = ResourcesUtils.getFloat(resources, R.dimen.explore_default_cards_across, 2.0f);
		mBestAroundCardsAcross = ResourcesUtils.getFloat(resources, R.dimen.explore_best_around_cards_across, 1.0f);
		mFeaturedCollectionsCardsAcross = ResourcesUtils.getFloat(resources, R.dimen.explore_featured_collections_cards_across, 2.0f);
		mFeaturedMappersCardsAcross = ResourcesUtils.getFloat(resources, R.dimen.explore_featured_mappers_cards_across, 2.0f);
		mFeaturedDealsCardsAcross = ResourcesUtils.getFloat(resources, R.dimen.explore_featured_deals_cards_across, 2.0f);

		// Set up carousels

		boolean animateOnInitialLoad = (savedInstanceState == null);
		int length = CarouselType.values().length;
		mCarousels = new LinkedHashMap<CarouselType, Carousel>(length);
		Carousel newCarousel = new Carousel();
		newCarousel.mContainer = (FrameLayout) findViewById(R.id.explore_best_around_carousel_container);
		newCarousel.mProgressBar = (ProgressBar) findViewById(R.id.explore_best_around_progressbar);
		newCarousel.mRecyclerView = (RecyclerView) findViewById(R.id.explore_best_around_recycler);
		newCarousel.mAdapter = new BestAroundAdapter(animateOnInitialLoad);
		newCarousel.mRecyclerView.setAdapter(newCarousel.mAdapter);
		mCarousels.put(CarouselType.BEST_AROUND, newCarousel);

		newCarousel = new Carousel();
		newCarousel.mContainer = (FrameLayout) findViewById(R.id.explore_featured_collections_carousel_container);
		newCarousel.mProgressBar = (ProgressBar) findViewById(R.id.explore_featured_collections_progressbar);
		newCarousel.mRecyclerView = (RecyclerView) findViewById(R.id.explore_featured_collections_recycler);
		newCarousel.mAdapter = new FeaturedCollectionsAdapter(animateOnInitialLoad);
		newCarousel.mRecyclerView.setAdapter(newCarousel.mAdapter);
		mCarousels.put(CarouselType.FEATURED_COLLECTIONS, newCarousel);

		newCarousel = new Carousel();
		newCarousel.mContainer = (FrameLayout) findViewById(R.id.explore_featured_mappers_carousel_container);
		newCarousel.mProgressBar = (ProgressBar) findViewById(R.id.explore_featured_mappers_progressbar);
		newCarousel.mRecyclerView = (RecyclerView) findViewById(R.id.explore_featured_mappers_recycler);
		newCarousel.mAdapter = new FeaturedMappersAdapter(animateOnInitialLoad);
		newCarousel.mRecyclerView.setAdapter(newCarousel.mAdapter);
		mCarousels.put(CarouselType.FEATURED_MAPPERS, newCarousel);

		newCarousel = new Carousel();
		newCarousel.mContainer = (FrameLayout) findViewById(R.id.explore_featured_deals_carousel_container);
		newCarousel.mProgressBar = (ProgressBar) findViewById(R.id.explore_featured_deals_progressbar);
		newCarousel.mRecyclerView = (RecyclerView) findViewById(R.id.explore_featured_deals_recycler);
		newCarousel.mAdapter = new FeaturedDealsAdapter(animateOnInitialLoad);
		newCarousel.mRecyclerView.setAdapter(newCarousel.mAdapter);
		mCarousels.put(CarouselType.FEATURED_DEALS, newCarousel);

		for (Carousel carousel : mCarousels.values()) {
			carousel.mContainer.addOnLayoutChangeListener(this);
			carousel.mRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
			if (mUseCompatPadding) {
				int paddingLeft = carousel.mRecyclerView.getPaddingLeft();
				int paddingTop = carousel.mRecyclerView.getPaddingTop();
				int paddingRight = carousel.mRecyclerView.getPaddingRight();
				int paddingBottom = carousel.mRecyclerView.getPaddingBottom();
				paddingLeft -= mCardMaxElevation;
				paddingRight -= mCardMaxElevation;
				carousel.mRecyclerView.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
			}
		}

		// Get map data from intent passed to Activity

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
					onRequestsComplete(true);
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
		if (right - left > 0) {
			for (CarouselType type : CarouselType.values()) {
				Carousel carousel = mCarousels.get(type);
				if (v == carousel.mContainer) {
					int desiredHeight = 0;
					switch (type) {
						case BEST_AROUND:
							mBestAroundDefaultCardWidth = getCardWidth(v, mDefaultCardsAcross);
							mBestAroundCardWidth = getCardWidth(v, mBestAroundCardsAcross);
							desiredHeight = BestAroundPlaceCardView.getDesiredHeight(this, mBestAroundDefaultCardWidth);
							break;
						case FEATURED_COLLECTIONS:
							mFeaturedCollectionCardWidth = getCardWidth(v, mFeaturedCollectionsCardsAcross);
							desiredHeight = CollectionCardView.getDesiredHeight(this, mFeaturedCollectionCardWidth);
							break;
						case FEATURED_MAPPERS:
							mFeaturedMapperCardWidth = getCardWidth(v, mFeaturedMappersCardsAcross);
							desiredHeight = UserCardView.getDesiredHeight(this, mFeaturedMapperCardWidth);
							break;
						case FEATURED_DEALS:
							mFeaturedDealCardWidth = getCardWidth(v, mFeaturedDealsCardsAcross);
							desiredHeight = DealCardView.getDesiredHeight(this, mFeaturedDealCardWidth);
							break;
					}
					ViewGroup.LayoutParams layoutParams = carousel.mContainer.getLayoutParams();
					layoutParams.height = desiredHeight +
							carousel.mRecyclerView.getPaddingTop() + carousel.mRecyclerView.getPaddingBottom() +
							carousel.mContainer.getPaddingTop() + carousel.mContainer.getPaddingBottom();
					carousel.mContainer.setLayoutParams(layoutParams);
					break;
				}
			}

			// There should never be an instance where there are existing cards in the recycler views
			// when I am at this point, so don't worry about updating any cards here.

			v.removeOnLayoutChangeListener(this);
		}
	}

	private int getCardWidth(View view, float cardsAcross) {
		/*
		 * NOTE: This returns the "perceived" card width. That is, the width of the card minus any shadow/elevation element.
		 * When setting the actual width of cards (i.e. in the createViewHolder method of the various adapters),
		 * any "compat" padding will need to be added if appropriate.
		 */

		int elevationFactor = (mUseCompatPadding ? mCardMaxElevation : 0);
		int perceivedPaddingLeft = view.getPaddingLeft() + elevationFactor;
		int perceivedPaddingRight = view.getPaddingRight() + elevationFactor;
		int marginCount = (int) (Math.ceil(cardsAcross)) - 1;
		int perceivedTotalMargin = mCardPerceivedMargin * marginCount;
		int perceivedAvailableWidth = view.getWidth() - perceivedPaddingLeft - perceivedPaddingRight - perceivedTotalMargin;
		return (int) (perceivedAvailableWidth / cardsAcross);
	}

	protected void onRequestsComplete(boolean isImmediate) {
		if (isImmediate) {
			Carousel carousel = mCarousels.get(CarouselType.BEST_AROUND);
			carousel.mProgressBar.setVisibility(View.GONE);
			((BestAroundAdapter) carousel.mAdapter).setData(mHelperFragment.mBestAround);
			carousel = mCarousels.get(CarouselType.FEATURED_COLLECTIONS);
			carousel.mProgressBar.setVisibility(View.GONE);
			((FeaturedCollectionsAdapter) carousel.mAdapter).setData(mHelperFragment.mFeaturedCollections);
			carousel = mCarousels.get(CarouselType.FEATURED_MAPPERS);
			carousel.mProgressBar.setVisibility(View.GONE);
			((FeaturedMappersAdapter) carousel.mAdapter).setData(mHelperFragment.mFeaturedMappers);
			carousel = mCarousels.get(CarouselType.FEATURED_DEALS);
			carousel.mProgressBar.setVisibility(View.GONE);
			((FeaturedDealsAdapter) carousel.mAdapter).setData(mHelperFragment.mFeaturedDeals);
		} else {
			new AsyncTask<CarouselType, CarouselType, Void>() {
				@Override
				protected Void doInBackground(CarouselType... params) {
					for (CarouselType type : params) {
						publishProgress(type);
						try {
							Thread.sleep(CAROUSEL_INITIAL_LOAD_DELAY);
						} catch (InterruptedException e) {
							// No action
						}
					}
					return null;
				}

				@Override
				protected void onProgressUpdate(CarouselType... values) {
					CarouselType type = values[0];
					Carousel carousel = mCarousels.get(type);
					carousel.mProgressBar.setVisibility(View.GONE);
					switch (type) {
						case BEST_AROUND:
							((BestAroundAdapter) carousel.mAdapter).setData(mHelperFragment.mBestAround);
							break;
						case FEATURED_COLLECTIONS:
							((FeaturedCollectionsAdapter) carousel.mAdapter).setData(mHelperFragment.mFeaturedCollections);
							break;
						case FEATURED_MAPPERS:
							((FeaturedMappersAdapter) carousel.mAdapter).setData(mHelperFragment.mFeaturedMappers);
							break;
						case FEATURED_DEALS:
							((FeaturedDealsAdapter) carousel.mAdapter).setData(mHelperFragment.mFeaturedDeals);
							break;
					}
				}
			}.execute(CarouselType.BEST_AROUND, CarouselType.FEATURED_COLLECTIONS, CarouselType.FEATURED_MAPPERS, CarouselType.FEATURED_DEALS);
		}
	}

	protected abstract class ExploreAdapter<D> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
		protected RecyclerView mRecyclerView;
		protected boolean mAnimateOnInitialLoad;
		protected Set<ViewHolder> mHolders;

		protected List<D> mData;

		public ExploreAdapter(boolean animateOnInitialLoad) {
			super();
			mAnimateOnInitialLoad = animateOnInitialLoad;
			mHolders = new HashSet<ViewHolder>();
		}

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

		@Override
		public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
			if (mRecyclerView == null) {
				mRecyclerView = (RecyclerView) viewGroup;
			}
			return null;
		}

		@Override
		public void onBindViewHolder(ViewHolder holder, int position) {
			if (mAnimateOnInitialLoad && !mHolders.contains(holder)) {
				int scrollDirection = (ViewCompat.getLayoutDirection(mRecyclerView) == ViewCompat.LAYOUT_DIRECTION_LTR ? -1 : 1);
				boolean isAtStart = !mRecyclerView.canScrollHorizontally(scrollDirection);
				if (isAtStart) {
					Animation animation = AnimationUtils.loadAnimation(ExploreActivity.this, R.anim.overshoot_in_right);
					animation.setStartOffset(position * CAROUSEL_ITEM_LOAD_DELAY);
					holder.itemView.startAnimation(animation);
					mHolders.add(holder);
				}
			}
		}
	}

	public class BestAroundAdapter extends ExploreAdapter<SearchResult> {
		public BestAroundAdapter(boolean animateOnInitialLoad) {
			super(animateOnInitialLoad);
		}

		@Override
		public int getItemViewType(int position) {
			SearchResult result = mData.get(position);
			return result.getType().value();
		}

		@Override
		public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
			super.onCreateViewHolder(viewGroup, viewType);
			CitymapsCardView view;
			CitymapsObject.ObjectType type = CitymapsObject.ObjectType.valueOf(viewType);
			switch (type) {
				case PLACE: {
					view = new BestAroundPlaceCardView(ExploreActivity.this);
					break;
				}
				case COLLECTION: {
					view = new BestAroundCollectionCardView(ExploreActivity.this);
					break;
				}
				default:
					throw new IllegalStateException("Only expecting places or collections in Best Around adapter");
			}

			view.setBaseSize(mBestAroundDefaultCardWidth);
			int actualCardWidth = mBestAroundCardWidth + (mUseCompatPadding ? 2 * mCardMaxElevation : 0);
			view.setLayoutParams(new RecyclerView.LayoutParams(actualCardWidth, RecyclerView.LayoutParams.WRAP_CONTENT));
			return new ViewHolder(view) {
			};
		}

		@Override
		public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
			super.onBindViewHolder(holder, position);
			SearchResult searchResult = mData.get(position);

			if (holder.itemView instanceof BestAroundPlaceCardView) {
				((BestAroundPlaceCardView) holder.itemView).setData((SearchResultPlace) searchResult);
			} else if (holder.itemView instanceof BestAroundCollectionCardView) {
				((BestAroundCollectionCardView) holder.itemView).setData((SearchResultCollection) searchResult);
			}

			if (holder.itemView.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
				ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) holder.itemView.getLayoutParams();
				int margin = (int) (mCardPerceivedMargin -
						2 * (holder.itemView instanceof CardView ? ((CardView) holder.itemView).getMaxCardElevation() : 0));
				MarginLayoutParamsCompat.setMarginEnd(mlp, position < getItemCount() - 1 ? margin : 0);
				holder.itemView.setLayoutParams(mlp);
			}
		}
	}

	public class FeaturedCollectionsAdapter extends ExploreAdapter<SearchResult> {
		public FeaturedCollectionsAdapter(boolean animateOnInitialLoad) {
			super(animateOnInitialLoad);
		}

		@Override
		public int getItemViewType(int position) {
			SearchResult result = mData.get(position);
			return result.getType().value();
		}

		@Override
		public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
			super.onCreateViewHolder(viewGroup, viewType);
			View view = new CollectionCardView(ExploreActivity.this);
			int actualCardWidth = mFeaturedCollectionCardWidth + (mUseCompatPadding ? 2 * mCardMaxElevation : 0);
			view.setLayoutParams(new RecyclerView.LayoutParams(actualCardWidth, RecyclerView.LayoutParams.WRAP_CONTENT));
			return new ViewHolder(view) {
			};
		}

		@Override
		public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
			super.onBindViewHolder(holder, position);
			SearchResultCollection searchResult = (SearchResultCollection) mData.get(position);
			CollectionCardView cardView = (CollectionCardView) holder.itemView;
			cardView.setData(searchResult);

			if (cardView.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
				ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) cardView.getLayoutParams();
				int margin = (int) (mCardPerceivedMargin - 2 * cardView.getMaxCardElevation());
				MarginLayoutParamsCompat.setMarginEnd(mlp, position < getItemCount() - 1 ? margin : 0);
				cardView.setLayoutParams(mlp);
			}
		}
	}

	public class FeaturedMappersAdapter extends ExploreAdapter<User> {
		public FeaturedMappersAdapter(boolean animateOnInitialLoad) {
			super(animateOnInitialLoad);
		}

		@Override
		public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
			super.onCreateViewHolder(viewGroup, viewType);
			View view = new UserCardView(ExploreActivity.this);
			int actualCardWidth = mFeaturedMapperCardWidth + (mUseCompatPadding ? 2 * mCardMaxElevation : 0);
			view.setLayoutParams(new RecyclerView.LayoutParams(actualCardWidth, RecyclerView.LayoutParams.WRAP_CONTENT));
			return new ViewHolder(view) {
			};
		}

		@Override
		public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
			super.onBindViewHolder(holder, position);
			User user = mData.get(position);
			UserCardView cardView = (UserCardView) holder.itemView;
			cardView.setData(user);

			if (cardView.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
				ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) cardView.getLayoutParams();
				int margin = (int) (mCardPerceivedMargin - 2 * cardView.getMaxCardElevation());
				MarginLayoutParamsCompat.setMarginEnd(mlp, position < getItemCount() - 1 ? margin : 0);
				cardView.setLayoutParams(mlp);
			}
		}
	}

	public class FeaturedDealsAdapter extends ExploreAdapter<SearchResult> {
		public FeaturedDealsAdapter(boolean animateOnInitialLoad) {
			super(animateOnInitialLoad);
		}

		@Override
		public int getItemViewType(int position) {
			SearchResult result = mData.get(position);
			return result.getType().value();
		}

		@Override
		public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
			super.onCreateViewHolder(viewGroup, viewType);
			View view = new DealCardView(ExploreActivity.this);
			int actualCardWidth = mFeaturedDealCardWidth + (mUseCompatPadding ? 2 * mCardMaxElevation : 0);
			view.setLayoutParams(new RecyclerView.LayoutParams(actualCardWidth, RecyclerView.LayoutParams.WRAP_CONTENT));
			return new ViewHolder(view) {
			};
		}

		@Override
		public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
			super.onBindViewHolder(holder, position);
			SearchResultPlace searchResult = (SearchResultPlace) mData.get(position);
			DealCardView cardView = (DealCardView) holder.itemView;
			cardView.setData(searchResult);

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

		public static HelperFragment newInstance(ParcelableLonLat location, float radius, int zoom) {
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
				mActivity.onRequestsComplete(false);
			}
		}
	}
	
	private static class Carousel {
		public FrameLayout mContainer;
		public ProgressBar mProgressBar;
		public RecyclerView mRecyclerView;
		public Adapter mAdapter;
	}

	private static enum CarouselType {
		BEST_AROUND,
		FEATURED_COLLECTIONS,
		FEATURED_MAPPERS,
		FEATURED_DEALS
	}
}
