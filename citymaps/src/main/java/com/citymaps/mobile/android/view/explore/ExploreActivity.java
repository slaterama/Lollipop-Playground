package com.citymaps.mobile.android.view.explore;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.MarginLayoutParamsCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
import com.citymaps.mobile.android.widget.OnSizeChangedListener;
import com.citymaps.mobile.android.widget.RecyclerViewEx;

import java.util.ArrayList;
import java.util.List;

public class ExploreActivity extends TrackedActionBarActivity {

	private static final String STATE_KEY_HELPER_FRAGMENT = "helperFragment";

	private boolean mUseCompatPadding;
	private int mCardMaxElevation;
	private int mCardPerceivedMargin;

	private float mDefaultCardsAcross;
	private float mHeroCardsAcross;
	private float mFeaturedCollectionsCardsAcross;
	private float mFeaturedMappersCardsAcross;
	private float mFeaturedDealsCardsAcross;

	private ProgressBar mHeroProgressBar;
	private ProgressBar mFeaturedCollectionsProgressBar;
	private ProgressBar mFeaturedMappersProgressBar;
	private ProgressBar mFeaturedDealsProgressBar;

	private RecyclerViewEx mHeroRecyclerView;
	private RecyclerViewEx mFeaturedCollectionsRecyclerView;
	private RecyclerViewEx mFeaturedMappersRecyclerView;
	private RecyclerViewEx mFeaturedDealsRecyclerView;

	private int mHeroDefaultCardSize;
	private Rect mHeroCardRect;
	private Rect mFeaturedCollectionsCardRect;
	private Rect mFeaturedMappersCardRect;
	private Rect mFeaturedDealsCardRect;

	private ParcelableLonLat mMapLocation;
	private float mMapRadius;
	private int mMapZoom;

	private HelperFragment mHelperFragment;

	private AnimatorSet mProgressBarAnimatorSet;

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
		mHeroCardsAcross = ResourcesUtils.getFloat(resources, R.dimen.explore_hero_cards_across, 1.0f);
		mFeaturedCollectionsCardsAcross = ResourcesUtils.getFloat(resources, R.dimen.explore_featured_collections_cards_across, 2.0f);
		mFeaturedMappersCardsAcross = ResourcesUtils.getFloat(resources, R.dimen.explore_featured_mappers_cards_across, 2.0f);
		mFeaturedDealsCardsAcross = ResourcesUtils.getFloat(resources, R.dimen.explore_featured_deals_cards_across, 2.0f);

		// Set up views

		mHeroProgressBar = (ProgressBar) findViewById(R.id.explore_hero_progressbar);
		mFeaturedCollectionsProgressBar = (ProgressBar) findViewById(R.id.explore_featured_collections_progressbar);
		mFeaturedMappersProgressBar = (ProgressBar) findViewById(R.id.explore_featured_mappers_progressbar);
		mFeaturedDealsProgressBar = (ProgressBar) findViewById(R.id.explore_featured_deals_progressbar);

		mHeroRecyclerView = (RecyclerViewEx) findViewById(R.id.explore_hero_recycler);
		mFeaturedCollectionsRecyclerView = (RecyclerViewEx) findViewById(R.id.explore_featured_collections_recycler);
		mFeaturedMappersRecyclerView = (RecyclerViewEx) findViewById(R.id.explore_featured_mappers_recycler);
		mFeaturedDealsRecyclerView = (RecyclerViewEx) findViewById(R.id.explore_featured_deals_recycler);

		mHeroRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
		mFeaturedCollectionsRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
		mFeaturedMappersRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
		mFeaturedDealsRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));

		mHeroRecyclerView.setOnSizeChangedListener(mRecyclerView_OnSizeChangedListener);
		mFeaturedCollectionsRecyclerView.setOnSizeChangedListener(mRecyclerView_OnSizeChangedListener);
		mFeaturedMappersRecyclerView.setOnSizeChangedListener(mRecyclerView_OnSizeChangedListener);
		mFeaturedDealsRecyclerView.setOnSizeChangedListener(mRecyclerView_OnSizeChangedListener);

		mHeroRecyclerView.addOnLayoutChangeListener(mRecyclerView_OnLayoutChangeListener);
		mFeaturedCollectionsRecyclerView.addOnLayoutChangeListener(mRecyclerView_OnLayoutChangeListener);
		mFeaturedMappersRecyclerView.addOnLayoutChangeListener(mRecyclerView_OnLayoutChangeListener);
		mFeaturedDealsRecyclerView.addOnLayoutChangeListener(mRecyclerView_OnLayoutChangeListener);

		// Set up animators

		ProgressBar[] progressBars = new ProgressBar[] {mHeroProgressBar, mFeaturedCollectionsProgressBar,
				mFeaturedMappersProgressBar, mFeaturedDealsProgressBar};
		Animator[] animators = new Animator[progressBars.length];
		for (int i = 0; i < progressBars.length; i++) {
			animators[i] = AnimatorInflater.loadAnimator(this, R.animator.shrink_to_zero);
			animators[i].setTarget(progressBars[i]);
		}
		mProgressBarAnimatorSet = new AnimatorSet();
		mProgressBarAnimatorSet.playTogether(animators);
		mProgressBarAnimatorSet.addListener(mProgressBar_AnimatorListener);

		// Get map data from intent passed to Activity

		Intent intent = getIntent();
		if (intent != null) {
			mMapLocation = IntentUtils.getMapLocation(intent);
			mMapRadius = IntentUtils.getMapRadius(intent, MapUtils.DEFAULT_SEARCH_RADIUS);
			mMapZoom = IntentUtils.getMapZoom(intent, MapUtils.DEFAULT_SEARCH_ZOOM);
		}

		// Set up helper fragment

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

	protected void onRequestsComplete(boolean isImmediate) {
		if (isImmediate) {
			populateRecyclerViews(false);
		} else {
			mProgressBarAnimatorSet.start();
		}
	}

	private int getCardSize(View view, float cardsAcross) {
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
		return Math.max((int) (perceivedAvailableWidth / cardsAcross), 0);
	}

	private void populateRecyclerViews(boolean animate) {
		mHeroProgressBar.setVisibility(View.GONE);
		mFeaturedCollectionsProgressBar.setVisibility(View.GONE);
		mFeaturedMappersProgressBar.setVisibility(View.GONE);
		mFeaturedDealsProgressBar.setVisibility(View.GONE);

		mHeroRecyclerView.setAdapter(new HeroAdapter(mHelperFragment.mHeroItems));
		/*
		mFeaturedCollectionsRecyclerView.setAdapter(new FeaturedCollectionsAdapter(mHelperFragment.mFeaturedCollections));
		mFeaturedMappersRecyclerView.setAdapter(new FeaturedMappersAdapter(mHelperFragment.mFeaturedMappers));
		mFeaturedDealsRecyclerView.setAdapter(new FeaturedDealsAdapter(mHelperFragment.mFeaturedDeals));
		*/
	}

	private OnSizeChangedListener mRecyclerView_OnSizeChangedListener = new OnSizeChangedListener() {
		@Override
		public void onSizeChanged(final View v, int w, int h, int oldw, int oldh) {
			if (w != oldw) {
				final Rect cardRect;
				if (v == mHeroRecyclerView) {
					mHeroDefaultCardSize = getCardSize(v, mDefaultCardsAcross); // Card size across for "default"
					int cardSize = getCardSize(v, mHeroCardsAcross);
					mHeroCardRect = new Rect(0, 0, cardSize,
							HeroCardView.getDesiredHeight(ExploreActivity.this, mHeroDefaultCardSize));
					cardRect = mHeroCardRect;
				} else if (v == mFeaturedCollectionsRecyclerView) {
					int cardSize = getCardSize(v, mFeaturedCollectionsCardsAcross);
					mFeaturedCollectionsCardRect = new Rect(0, 0, cardSize,
							CollectionCardView.getDesiredHeight(ExploreActivity.this, cardSize));
					cardRect = mFeaturedCollectionsCardRect;
				} else if (v == mFeaturedMappersRecyclerView) {
					int cardSize = getCardSize(v, mFeaturedMappersCardsAcross);
					mFeaturedMappersCardRect = new Rect(0, 0, cardSize,
							UserCardView.getDesiredHeight(ExploreActivity.this, cardSize));
					cardRect = mFeaturedMappersCardRect;
				} else if (v == mFeaturedDealsRecyclerView) {
					int cardSize = getCardSize(v, mFeaturedDealsCardsAcross);
					mFeaturedDealsCardRect = new Rect(0, 0, cardSize,
							DealCardView.getDesiredHeight(ExploreActivity.this, cardSize));
					cardRect = mFeaturedDealsCardRect;
				} else {
					return;
				}

				v.post(new Runnable() {
					@Override
					public void run() {
						v.getLayoutParams().height = v.getPaddingTop() + cardRect.height() + v.getPaddingBottom();
						v.requestLayout();
					}
				});
			}
		}
	};

	private View.OnLayoutChangeListener mRecyclerView_OnLayoutChangeListener = new View.OnLayoutChangeListener() {
		@Override
		public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
		}
	};

	private Animator.AnimatorListener mProgressBar_AnimatorListener = new Animator.AnimatorListener() {
		@Override
		public void onAnimationStart(Animator animation) {
		}

		@Override
		public void onAnimationEnd(Animator animation) {
			populateRecyclerViews(true);
		}

		@Override
		public void onAnimationCancel(Animator animation) {
		}

		@Override
		public void onAnimationRepeat(Animator animation) {
		}
	};

	/**
	 * *****************************************************************************
	 * RecyclerView adapters
	 * ******************************************************************************
	 */

	private abstract class ExploreAdapter<D> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
		protected static final int VIEW_TYPE_VIEW_ALL = Integer.MIN_VALUE;

		protected List<D> mItems;
		protected boolean mHasViewAllCard = false; // TODO

		public ExploreAdapter(List<D> items) {
			super();
			mItems = items;
		}

		public List<D> getItems() {
			return mItems;
		}

		public void setItems(List<D> items) {
			mItems = items;
			notifyDataSetChanged();
		}

		@Override
		public int getItemViewType(int position) {
			if (mHasViewAllCard && position == getItemCount() - 1) {
				return VIEW_TYPE_VIEW_ALL;
			} else {
				return super.getItemViewType(position);
			}
		}

		@Override
		public int getItemCount() {
			int count = 0;
			if (mItems != null) {
				count = mItems.size();
				if (mHasViewAllCard) {
					count++;
				}
			}
			return count;
		}

		@Override
		public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
			// Configure the correct margin depending on position
			if (holder.itemView.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
				ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) holder.itemView.getLayoutParams();
				int margin = (int) (mCardPerceivedMargin -
						2 * (holder.itemView instanceof CardView ? ((CardView) holder.itemView).getMaxCardElevation() : 0));
				MarginLayoutParamsCompat.setMarginEnd(mlp, position < getItemCount() - 1 ? margin : 0);
				holder.itemView.setLayoutParams(mlp);
			}
		}
	}

	private class HeroAdapter extends ExploreAdapter<SearchResult> {

		public HeroAdapter(List<SearchResult> items) {
			super(items);
			mHasViewAllCard = false;
		}

		@Override
		public int getItemViewType(int position) {
			int viewType = super.getItemViewType(position);
			if (viewType != VIEW_TYPE_VIEW_ALL) {
				SearchResult searchResult = mItems.get(position);
				viewType = searchResult.getType().value();
			}
			return viewType;
		}

		@Override
		public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			CitymapsCardView cardView = null;
			if (viewType == VIEW_TYPE_VIEW_ALL) {
				cardView = new PlaceHeroCardView(ExploreActivity.this); // TODO TEMP
			} else {
				CitymapsObject.ObjectType type = CitymapsObject.ObjectType.valueOf(viewType);
				switch (type) {
					case PLACE:
						cardView = new PlaceHeroCardView(ExploreActivity.this);
						break;
					case COLLECTION:
						cardView = new CollectionHeroCardView(ExploreActivity.this);
						break;
					default:
						throw new IllegalStateException("HeroAdapter expects only place or collection search results");
				}
			}
			int actualCardWidth = mHeroCardRect.width() + (mUseCompatPadding ? 2 * mCardMaxElevation : 0);
			int actualCardHeight = mHeroCardRect.height() + (mUseCompatPadding ? 2 * mCardMaxElevation : 0);
			cardView.setLayoutParams(new RecyclerView.LayoutParams(actualCardWidth, actualCardHeight));
			return new ExploreViewHolder(cardView);
		}

		@Override
		public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
			super.onBindViewHolder(holder, position);
			HeroCardView cardView = (HeroCardView) holder.itemView;
			cardView.bindData(mItems.get(position));
		}
	}

	private class FeaturedCollectionsAdapter extends ExploreAdapter<SearchResultCollection> {

		public FeaturedCollectionsAdapter(List<SearchResultCollection> items) {
			super(items);
		}

		@Override
		public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			return null;
		}

		@Override
		public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
			super.onBindViewHolder(holder, position);
		}
	}

	private class FeaturedMappersAdapter extends ExploreAdapter<User> {

		public FeaturedMappersAdapter(List<User> items) {
			super(items);
		}

		@Override
		public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			return null;
		}

		@Override
		public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
			super.onBindViewHolder(holder, position);
		}
	}

	private class FeaturedDealsAdapter extends ExploreAdapter<SearchResultPlace> {

		public FeaturedDealsAdapter(List<SearchResultPlace> items) {
			super(items);
		}

		@Override
		public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			return null;
		}

		@Override
		public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
			super.onBindViewHolder(holder, position);
		}
	}

	private class ExploreViewHolder extends RecyclerView.ViewHolder {
		public ExploreViewHolder(View itemView) {
			super(itemView);
		}
	}

	/**
	 * *****************************************************************************
	 * Helper fragment
	 * ******************************************************************************
	 */

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

		private List<SearchResult> mHeroItems;
		private List<SearchResultCollection> mFeaturedCollections;
		private List<User> mFeaturedMappers;
		private List<SearchResultPlace> mFeaturedDeals;

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
							mHeroItems = response;
							onRequestComplete();
						}
					},
					new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							if (LogEx.isLoggable(LogEx.ERROR)) {
								LogEx.e(error.getMessage(), error);
							}
							onRequestComplete();
						}
					});

			limit = 12;
			SearchResultsRequest featuredCollectionsRequest = SearchResultsRequest.newFeaturedCollectionsRequest(getActivity(),
					mMapLocation, mMapZoom, mMapRadius, offset, limit,
					new Response.Listener<List<SearchResult>>() {
						@Override
						public void onResponse(List<SearchResult> response) {
							mFeaturedCollections = new ArrayList<SearchResultCollection>(response.size());
							for (SearchResult searchResult : response) {
								if (searchResult instanceof SearchResultCollection) {
									mFeaturedCollections.add((SearchResultCollection) searchResult);
								}
							}
							onRequestComplete();
						}
					},
					new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							if (LogEx.isLoggable(LogEx.ERROR)) {
								LogEx.e(error.getMessage(), error);
							}
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
							if (LogEx.isLoggable(LogEx.ERROR)) {
								LogEx.e(error.getMessage(), error);
							}
							onRequestComplete();
						}
					});

			SearchResultsRequest featuredDealsRequest = SearchResultsRequest.newFeaturedDealsRequest(getActivity(),
					mMapLocation, mMapZoom, mMapRadius, offset, limit,
					new Response.Listener<List<SearchResult>>() {
						@Override
						public void onResponse(List<SearchResult> response) {
							mFeaturedDeals = new ArrayList<SearchResultPlace>(response.size());
							for (SearchResult searchResult : response) {
								if (searchResult instanceof SearchResultPlace) {
									mFeaturedDeals.add((SearchResultPlace) searchResult);
								}
							}
							onRequestComplete();
						}
					},
					new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							if (LogEx.isLoggable(LogEx.ERROR)) {
								LogEx.e(error.getMessage(), error);
							}
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
}
