package com.citymaps.mobile.android.view.explore;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
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
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
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

import java.util.*;

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

	private TextView mHeroLabelView;

	private ProgressBar mHeroProgressBar;
	private ProgressBar mFeaturedCollectionsProgressBar;
	private ProgressBar mFeaturedMappersProgressBar;
	private ProgressBar mFeaturedDealsProgressBar;

	private RecyclerViewEx mHeroRecyclerView;
	private RecyclerViewEx mFeaturedCollectionsRecyclerView;
	private RecyclerViewEx mFeaturedMappersRecyclerView;
	private RecyclerViewEx mFeaturedDealsRecyclerView;

	private HeroAdapter mHeroAdapter;
	private FeaturedCollectionsAdapter mFeaturedCollectionsAdapter;
	private FeaturedMappersAdapter mFeaturedMappersAdapter;
	private FeaturedDealsAdapter mFeaturedDealsAdapter;

	private int mHeroDefaultCardSize;
	private Rect mHeroCardRect;
	private Rect mFeaturedCollectionsCardRect;
	private Rect mFeaturedMappersCardRect;
	private Rect mFeaturedDealsCardRect;

	private ParcelableLonLat mMapLocation;
	private float mMapRadius;
	private int mMapZoom;

	private HelperFragment mHelperFragment;

	private AnimationHelper mAnimationHelper;

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

		mHeroLabelView = (TextView) findViewById(R.id.explore_hero_label);

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

		// Get map data from intent passed to Activity

		Intent intent = getIntent();
		if (intent != null) {
			mMapLocation = IntentUtils.getMapLocation(intent);
			mMapRadius = IntentUtils.getMapRadius(intent, MapUtils.DEFAULT_SEARCH_RADIUS);
			mMapZoom = IntentUtils.getMapZoom(intent, MapUtils.DEFAULT_SEARCH_ZOOM);
		}

		// Set up helper fragment

		if (savedInstanceState == null) {
//			mAnimationHelper = new AnimationHelper();
			mHelperFragment = HelperFragment.newInstance(mMapLocation, mMapRadius, mMapZoom);
			getSupportFragmentManager()
					.beginTransaction()
					.add(mHelperFragment, HelperFragment.FRAGMENT_TAG)
					.commit();
		} else {
			mHelperFragment = (HelperFragment) getSupportFragmentManager().getFragment(savedInstanceState, STATE_KEY_HELPER_FRAGMENT);
			mHeroRecyclerView.setAdapter(mHeroAdapter = new HeroAdapter(mHelperFragment.mHeroItems));
			mFeaturedCollectionsRecyclerView.setAdapter(mFeaturedCollectionsAdapter = new FeaturedCollectionsAdapter(mHelperFragment.mFeaturedCollections));
			mFeaturedMappersRecyclerView.setAdapter(mFeaturedMappersAdapter = new FeaturedMappersAdapter(mHelperFragment.mFeaturedMappers));
			mFeaturedDealsRecyclerView.setAdapter(mFeaturedDealsAdapter = new FeaturedDealsAdapter(mHelperFragment.mFeaturedDeals));
			updateHeroLabel();
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

	protected void updateHeroLabel() {
		String city = null;
		if (mHelperFragment.mHeroItems != null && mHelperFragment.mHeroItems.size() > 0) {
			for (SearchResult searchResult : mHelperFragment.mHeroItems) {
				if (searchResult instanceof SearchResultPlace) {
					SearchResultPlace searchResultPlace = (SearchResultPlace) searchResult;
					city = searchResultPlace.getCity();
				} else if (searchResult instanceof SearchResultCollection) {
					SearchResultCollection searchResultCollection = (SearchResultCollection) searchResult;
					List<User> editors = searchResultCollection.getEditors();
					if (editors != null && editors.size() > 0) {
						city = editors.get(0).getCity();
					}
				}
				if (!TextUtils.isEmpty(city)) {
					break;
				}
			}
		}
		if (TextUtils.isEmpty(city)) {
			mHeroLabelView.setText(R.string.explore_best_around_me);
		} else {
			mHeroLabelView.setText(getString(R.string.explore_best_around, city));
		}
	}

	/*
	protected void onRequestsComplete(boolean isImmediate) {
		if (mAnimationHelper == null) {
			populateData();
		} else {
			mAnimationHelper.mProgressBarAnimatorSet.start();
		}
	}
	*/

	/*
	private void populateData() {
		mHeroProgressBar.setVisibility(View.GONE);
		mFeaturedCollectionsProgressBar.setVisibility(View.GONE);
		mFeaturedMappersProgressBar.setVisibility(View.GONE);
		mFeaturedDealsProgressBar.setVisibility(View.GONE);

		String city = null;
		if (mHelperFragment.mHeroItems != null && mHelperFragment.mHeroItems.size() > 0) {
			for (SearchResult searchResult : mHelperFragment.mHeroItems) {
				if (searchResult instanceof SearchResultPlace) {
					SearchResultPlace searchResultPlace = (SearchResultPlace) searchResult;
					city = searchResultPlace.getCity();
				} else if (searchResult instanceof SearchResultCollection) {
					SearchResultCollection searchResultCollection = (SearchResultCollection) searchResult;
					List<User> editors = searchResultCollection.getEditors();
					if (editors != null && editors.size() > 0) {
						city = editors.get(0).getCity();
					}
				}
				if (!TextUtils.isEmpty(city)) {
					break;
				}
			}
		}
		if (TextUtils.isEmpty(city)) {
			mHeroLabelView.setText(R.string.explore_best_around_me);
		} else {
			mHeroLabelView.setText(getString(R.string.explore_best_around, city));
		}

		if (mAnimationHelper != null) {
			int pendingRecyclerViewCount = 0;
			if (mHelperFragment.mHeroItems != null && mHelperFragment.mHeroItems.size() > 0) {
				mAnimationHelper.addPendingRecyclerView(mHeroRecyclerView);
				pendingRecyclerViewCount++;
			}
			if (mHelperFragment.mFeaturedCollections != null && mHelperFragment.mFeaturedCollections.size() > 0) {
				mAnimationHelper.addPendingRecyclerView(mFeaturedCollectionsRecyclerView);
				pendingRecyclerViewCount++;
			}
			if (mHelperFragment.mFeaturedMappers != null && mHelperFragment.mFeaturedMappers.size() > 0) {
				mAnimationHelper.addPendingRecyclerView(mFeaturedMappersRecyclerView);
				pendingRecyclerViewCount++;
			}
			if (mHelperFragment.mFeaturedDeals != null && mHelperFragment.mFeaturedDeals.size() > 0) {
				mAnimationHelper.addPendingRecyclerView(mFeaturedDealsRecyclerView);
				pendingRecyclerViewCount++;
			}
			if (pendingRecyclerViewCount == 0) {
				LogEx.d("!!!!! No RecyclerViews had any data !!!!!");
			}
		}

		mHeroAdapter = new HeroAdapter(mHelperFragment.mHeroItems);
		mFeaturedCollectionsAdapter = new FeaturedCollectionsAdapter(mHelperFragment.mFeaturedCollections);
		mFeaturedMappersAdapter = new FeaturedMappersAdapter(mHelperFragment.mFeaturedMappers);
		mFeaturedDealsAdapter = new FeaturedDealsAdapter(mHelperFragment.mFeaturedDeals);

		mHeroRecyclerView.setAdapter(mHeroAdapter);
		mFeaturedCollectionsRecyclerView.setAdapter(mFeaturedCollectionsAdapter);
		mFeaturedMappersRecyclerView.setAdapter(mFeaturedMappersAdapter);
		mFeaturedDealsRecyclerView.setAdapter(mFeaturedDealsAdapter);
	}
	*/

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
				v.getLayoutParams().height = v.getPaddingTop() + cardRect.height() + v.getPaddingBottom();
				v.requestLayout();
			}
		}
	};

	/**
	 * ********************************************************************************
	 * RecyclerView adapters
	 * ********************************************************************************
	 */

	private abstract class ExploreAdapter<D> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
		protected static final int VIEW_TYPE_VIEW_ALL = Integer.MIN_VALUE;

		protected boolean mHasViewAllCard = true;
//		protected boolean mIsInInitialLoad;
		protected List<D> mItems;

		public ExploreAdapter(List<D> items) {
			super();
//			mIsInInitialLoad = true;
			mItems = items;
		}

//		public void setInInitialLoad(boolean isInInitialLoad) {
//			mIsInInitialLoad = isInInitialLoad;
//		}

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
				if (count > 0 && mHasViewAllCard) {
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
			CitymapsCardView cardView;
			if (viewType == VIEW_TYPE_VIEW_ALL) {
				cardView = new ViewAllCardView(ExploreActivity.this);
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
			cardView.setDefaultCardSize(mHeroDefaultCardSize);
			cardView.setOnBindCompleteListener(mAnimationHelper);
			int actualCardWidth = mHeroCardRect.width() + (mUseCompatPadding ? 2 * mCardMaxElevation : 0);
			cardView.setLayoutParams(new RecyclerView.LayoutParams(actualCardWidth, mHeroCardRect.height()));
			return new ExploreViewHolder(cardView);
		}

		@Override
		public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
			boolean animateImages = false; // TODO (mAnimationHelper != null);
			super.onBindViewHolder(holder, position);
			if (holder.itemView instanceof CollectionHeroCardView) {
				((CollectionHeroCardView) holder.itemView).setData((SearchResultCollection) mItems.get(position), animateImages);
			} else if (holder.itemView instanceof PlaceHeroCardView) {
				((PlaceHeroCardView) holder.itemView).setData((SearchResultPlace) mItems.get(position), animateImages);
			} else if (holder.itemView instanceof ViewAllCardView) {
				((ViewAllCardView) holder.itemView).setData(R.string.card_view_all_hero, animateImages);
			}
		}
	}

	private class FeaturedCollectionsAdapter extends ExploreAdapter<SearchResultCollection> {

		public FeaturedCollectionsAdapter(List<SearchResultCollection> items) {
			super(items);
		}

		@Override
		public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			CitymapsCardView cardView;
			if (viewType == VIEW_TYPE_VIEW_ALL) {
				cardView = new ViewAllCardView(ExploreActivity.this);
			} else {
				cardView = new CollectionCardView(ExploreActivity.this);
			}
			int perceivedWidth = mFeaturedCollectionsCardRect.width();
			int actualCardWidth = perceivedWidth + (mUseCompatPadding ? 2 * mCardMaxElevation : 0);
			cardView.setDefaultCardSize(perceivedWidth);
			cardView.setOnBindCompleteListener(mAnimationHelper);
			cardView.setLayoutParams(new RecyclerView.LayoutParams(actualCardWidth, mFeaturedCollectionsCardRect.height()));
			return new ExploreViewHolder(cardView);
		}

		@Override
		public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
			boolean animateImages = false; // TODO (mAnimationHelper != null);
			super.onBindViewHolder(holder, position);
			if (holder.itemView instanceof CollectionCardView) {
				((CollectionCardView) holder.itemView).setData(mItems.get(position), animateImages);
			} else if (holder.itemView instanceof ViewAllCardView) {
				((ViewAllCardView) holder.itemView).setData(R.string.card_view_all_featured_collections, animateImages);
			}
		}
	}

	private class FeaturedMappersAdapter extends ExploreAdapter<User> {

		public FeaturedMappersAdapter(List<User> items) {
			super(items);
		}

		@Override
		public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			CitymapsCardView cardView;
			if (viewType == VIEW_TYPE_VIEW_ALL) {
				cardView = new ViewAllCardView(ExploreActivity.this);
			} else {
				cardView = new UserCardView(ExploreActivity.this);
			}
			int perceivedWidth = mFeaturedMappersCardRect.width();
			int actualCardWidth = perceivedWidth + (mUseCompatPadding ? 2 * mCardMaxElevation : 0);
			cardView.setDefaultCardSize(perceivedWidth);
			cardView.setOnBindCompleteListener(mAnimationHelper);
			cardView.setLayoutParams(new RecyclerView.LayoutParams(actualCardWidth, mFeaturedMappersCardRect.height()));
			return new ExploreViewHolder(cardView);
		}

		@Override
		public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
			boolean animateImages = false; // TODO (mAnimationHelper != null);
			super.onBindViewHolder(holder, position);
			if (holder.itemView instanceof UserCardView) {
				((UserCardView) holder.itemView).setData(mItems.get(position), animateImages);
			} else if (holder.itemView instanceof ViewAllCardView) {
				((ViewAllCardView) holder.itemView).setData(R.string.card_view_all_featured_mappers, animateImages);
			}
		}
	}

	private class FeaturedDealsAdapter extends ExploreAdapter<SearchResultPlace> {

		public FeaturedDealsAdapter(List<SearchResultPlace> items) {
			super(items);
		}

		@Override
		public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			CitymapsCardView cardView;
			if (viewType == VIEW_TYPE_VIEW_ALL) {
				cardView = new ViewAllCardView(ExploreActivity.this);
			} else {
				cardView = new DealCardView(ExploreActivity.this);
			}
			int perceivedWidth = mFeaturedDealsCardRect.width();
			int actualCardWidth = perceivedWidth + (mUseCompatPadding ? 2 * mCardMaxElevation : 0);
			cardView.setDefaultCardSize(perceivedWidth);
			cardView.setOnBindCompleteListener(mAnimationHelper);
			cardView.setLayoutParams(new RecyclerView.LayoutParams(actualCardWidth, mFeaturedDealsCardRect.height()));
			return new ExploreViewHolder(cardView);
		}

		@Override
		public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
			boolean animateImages = false; // TODO (mAnimationHelper != null);
			super.onBindViewHolder(holder, position);
			if (holder.itemView instanceof DealCardView) {
				((DealCardView) holder.itemView).setData(mItems.get(position), animateImages);
			} else if (holder.itemView instanceof ViewAllCardView) {
				((ViewAllCardView) holder.itemView).setData(R.string.card_view_all_featured_deals, false);
			}
		}
	}

	private class ExploreViewHolder extends RecyclerView.ViewHolder {
		public ExploreViewHolder(View itemView) {
			super(itemView);
		}
	}

	/**
	 * ******************************************************************************
	 * Animation helper
	 * ******************************************************************************
	 */

	protected AnimationHelper getAnimationHelper() {
		if (mAnimationHelper == null) {
			mAnimationHelper = new AnimationHelper();
		}
		return mAnimationHelper;
	}

	protected class AnimationHelper
			implements View.OnLayoutChangeListener,
			CitymapsCardView.OnBindCompleteListener {

		private Set<RecyclerView> mPendingRecyclerViews;

		private Set<CitymapsCardView> mPendingCardViews;

		private Timer mTimer;
		private TimerTask mTimerTask;

		public AnimationHelper() {
			mHeroRecyclerView.setVisibility(View.INVISIBLE);
			mHeroProgressBar.setVisibility(View.VISIBLE);
			mFeaturedCollectionsRecyclerView.setVisibility(View.INVISIBLE);
			mFeaturedCollectionsProgressBar.setVisibility(View.VISIBLE);
			mFeaturedMappersRecyclerView.setVisibility(View.INVISIBLE);
			mFeaturedMappersProgressBar.setVisibility(View.VISIBLE);
			mFeaturedDealsRecyclerView.setVisibility(View.INVISIBLE);
			mFeaturedDealsProgressBar.setVisibility(View.VISIBLE);

			mPendingRecyclerViews = new HashSet<RecyclerView>(Arrays.asList(
					mHeroRecyclerView, mFeaturedCollectionsRecyclerView,
					mFeaturedMappersRecyclerView, mFeaturedDealsRecyclerView));

			mPendingCardViews = new HashSet<CitymapsCardView>();

			mTimerTask = new TimerTask() {
				@Override
				public void run() {
					LogEx.d();
				}
			};

			mTimer = new Timer();
			mTimer.schedule(mTimerTask, 5000);
		}

		protected void markRecyclerViewAsProcessed(RecyclerView recyclerView) {
			mPendingRecyclerViews.remove(recyclerView);
			if (mPendingRecyclerViews.size() == 0) {
				LogEx.d(); // TODO
			}
		}

		private void setupHeroAdapter(List<SearchResult> searchResults, boolean listenForLayout) {
			if (searchResults != null && searchResults.size() > 0) {
				if (listenForLayout) {
					mHeroRecyclerView.addOnLayoutChangeListener(this);
				}
				mHeroAdapter = new HeroAdapter(mHelperFragment.mHeroItems);
				mHeroRecyclerView.setAdapter(mHeroAdapter);
			} else {
				markRecyclerViewAsProcessed(mHeroRecyclerView);
			}
			updateHeroLabel();
		}

		private void setupFeaturedCollectionsAdapter(List<SearchResultCollection> searchResults, boolean listenForLayout) {
			if (searchResults != null && searchResults.size() > 0) {
				if (listenForLayout) {
					mFeaturedCollectionsRecyclerView.addOnLayoutChangeListener(this);
				}
				mFeaturedCollectionsAdapter = new FeaturedCollectionsAdapter(mHelperFragment.mFeaturedCollections);
				mFeaturedCollectionsRecyclerView.setAdapter(mFeaturedCollectionsAdapter);
			} else {
				markRecyclerViewAsProcessed(mFeaturedCollectionsRecyclerView);
			}
		}

		private void setupFeaturedMappersAdapter(List<User> users, boolean listenForLayout) {
			if (users != null && users.size() > 0) {
				if (listenForLayout) {
					mFeaturedMappersRecyclerView.addOnLayoutChangeListener(this);
				}
				mFeaturedMappersAdapter = new FeaturedMappersAdapter(mHelperFragment.mFeaturedMappers);
				mFeaturedMappersRecyclerView.setAdapter(mFeaturedMappersAdapter);
			} else {
				markRecyclerViewAsProcessed(mFeaturedMappersRecyclerView);
			}
		}

		private void setupFeaturedDealsAdapter(List<SearchResultPlace> searchResults, boolean listenForLayout) {
			if (searchResults != null && searchResults.size() > 0) {
				if (listenForLayout) {
					mFeaturedDealsRecyclerView.addOnLayoutChangeListener(this);
				}
				mFeaturedDealsAdapter = new FeaturedDealsAdapter(mHelperFragment.mFeaturedDeals);
				mFeaturedDealsRecyclerView.setAdapter(mFeaturedDealsAdapter);
			} else {
				markRecyclerViewAsProcessed(mFeaturedDealsRecyclerView);
			}
		}

		@Override
		public void onLayoutChange(View v, int left, int top, int right, int bottom,
								   int oldLeft, int oldTop, int oldRight, int oldBottom) {
			RecyclerView recyclerView = (RecyclerView) v;
			int childCount = recyclerView.getChildCount();
			if (childCount > 0) {
				markRecyclerViewAsProcessed(recyclerView);
				recyclerView.removeOnLayoutChangeListener(this);
			}
		}

		@Override
		public void onBindComplete(CitymapsCardView v) {
			LogEx.d();
		}
	}

	/*
	protected class AnimationHelper implements View.OnLayoutChangeListener, CitymapsCardView.OnLoadCompleteListener {

		private Set<View> mPendingRecyclerViews;

		private AnimatorSet mProgressBarAnimatorSet;

		private Set<CitymapsCardView> mPendingCardViews;

		public AnimationHelper() {
			super();
			mPendingRecyclerViews = new HashSet<View>();

			ProgressBar[] progressBars = new ProgressBar[]{mHeroProgressBar, mFeaturedCollectionsProgressBar,
					mFeaturedMappersProgressBar, mFeaturedDealsProgressBar};
			Animator[] animators = new Animator[progressBars.length];
			for (int i = 0; i < progressBars.length; i++) {
				animators[i] = AnimatorInflater.loadAnimator(ExploreActivity.this, R.animator.shrink_to_zero);
				animators[i].setTarget(progressBars[i]);
			}
			mProgressBarAnimatorSet = new AnimatorSet();
			mProgressBarAnimatorSet.playTogether(animators);
			mProgressBarAnimatorSet.addListener(mProgressBar_AnimatorListener);

			mPendingCardViews = new HashSet<CitymapsCardView>();

			mHeroRecyclerView.setVisibility(View.INVISIBLE);
			mHeroRecyclerView.addOnLayoutChangeListener(this);
			mHeroProgressBar.setVisibility(View.VISIBLE);
			mFeaturedCollectionsRecyclerView.setVisibility(View.INVISIBLE);
			mFeaturedCollectionsRecyclerView.addOnLayoutChangeListener(this);
			mFeaturedCollectionsProgressBar.setVisibility(View.VISIBLE);
			mFeaturedMappersRecyclerView.setVisibility(View.INVISIBLE);
			mFeaturedMappersRecyclerView.addOnLayoutChangeListener(this);
			mFeaturedMappersProgressBar.setVisibility(View.VISIBLE);
			mFeaturedDealsRecyclerView.setVisibility(View.INVISIBLE);
			mFeaturedDealsRecyclerView.addOnLayoutChangeListener(this);
			mFeaturedDealsProgressBar.setVisibility(View.VISIBLE);

			// At this point:
			// * All data will start to retrieve
			// * If the data in a recycler view > 0, onLayoutChange will pick it up and
			// "process" that recycler view.
			// * Otherwise, we have to catch that there is no data and "process" it manually
		}

		private void onDataRequestComplete() {
			LogEx.d("");
		}

		private Animator.AnimatorListener mProgressBar_AnimatorListener = new Animator.AnimatorListener() {
			@Override
			public void onAnimationStart(Animator animation) {
			}

			@Override
			public void onAnimationEnd(Animator animation) {
//				populateData();
			}

			@Override
			public void onAnimationCancel(Animator animation) {
			}

			@Override
			public void onAnimationRepeat(Animator animation) {
			}
		};

		@Override
		public void onLayoutChange(View v, int left, int top, int right, int bottom,
								   int oldLeft, int oldTop, int oldRight, int oldBottom) {
			if (v instanceof ViewGroup) {
				ViewGroup viewGroup = (ViewGroup) v;
				int childCount = viewGroup.getChildCount();
				if (childCount > 0) {
					// Temporarily hide the RecyclerView until all cards have loaded their images
//					v.setVisibility(View.INVISIBLE);

					LogEx.d(String.format("v=%s", v));

					for (int i = 0; i < childCount; i++) {
						View child = viewGroup.getChildAt(i);
						if (child instanceof CitymapsCardView) {
							CitymapsCardView cardView = (CitymapsCardView) child;
							if (!cardView.isLoadComplete()) {
								mPendingCardViews.add(cardView);
							}
						}
					}

					v.removeOnLayoutChangeListener(this);
				}
			}
			mPendingRecyclerViews.remove(v);
			if (mPendingRecyclerViews.size() == 0) {
//				LogEx.d("!!!!! All views have been laid out !!!!!");

				if (!checkPendingCardViews()) {
//					LogEx.d(String.format("Number of pending card views=%d", mPendingCardViews.size()));
				}
			}
		}

		@Override
		public void onLoadComplete(CitymapsCardView v) {
			mPendingCardViews.remove(v);
			checkPendingCardViews();
		}

		protected boolean checkPendingCardViews() {
			int size = mPendingCardViews.size();
//			LogEx.d(String.format("Number of pending card views=%d", size));
			if (size == 0) {
//				LogEx.d("!!!!! All cards have completed loading !!!!!");

//				startAnimation();

//				mAnimationHelper = null;
				return true;
			}

			return false;
		}
	}
	*/

	/**
	 * ******************************************************************************
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

			int offset = 0;
			int limit = 6;
			SearchResultsRequest featuredHeroItemsRequest = SearchResultsRequest.newFeaturedHeroItemsRequest(getActivity(),
					mMapLocation, mMapZoom, mMapRadius, offset, limit,
					new Response.Listener<List<SearchResult>>() {
						@Override
						public void onResponse(List<SearchResult> response) {
							mHeroItems = response;
							mActivity.getAnimationHelper().setupHeroAdapter(mHeroItems, true);
						}
					},
					new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							if (LogEx.isLoggable(LogEx.ERROR)) {
								LogEx.e(error.getMessage(), error);
							}
							mHeroItems = null;
							mActivity.getAnimationHelper().setupHeroAdapter(null, false);
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
							mActivity.getAnimationHelper().setupFeaturedCollectionsAdapter(mFeaturedCollections, true);
						}
					},
					new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							if (LogEx.isLoggable(LogEx.ERROR)) {
								LogEx.e(error.getMessage(), error);
							}
							mFeaturedCollections = null;
							mActivity.getAnimationHelper().setupFeaturedCollectionsAdapter(null, false);
						}
					});

			UsersRequest featuredMappersRequest = UsersRequest.newFeaturedMappersRequest(getActivity(),
					mMapLocation, mMapRadius, offset, limit,
					new Response.Listener<List<User>>() {
						@Override
						public void onResponse(List<User> response) {
							mFeaturedMappers = response;
							mActivity.getAnimationHelper().setupFeaturedMappersAdapter(mFeaturedMappers, true);
						}
					},
					new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							if (LogEx.isLoggable(LogEx.ERROR)) {
								LogEx.e(error.getMessage(), error);
							}
							mFeaturedMappers = null;
							mActivity.getAnimationHelper().setupFeaturedMappersAdapter(null, false);
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
							mActivity.getAnimationHelper().setupFeaturedDealsAdapter(mFeaturedDeals, true);
						}
					},
					new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							if (LogEx.isLoggable(LogEx.ERROR)) {
								LogEx.e(error.getMessage(), error);
							}
							mFeaturedDeals = null;
							mActivity.getAnimationHelper().setupFeaturedDealsAdapter(null, true);
						}
					});

			RequestQueue queue = VolleyManager.getInstance(getActivity()).getRequestQueue();
			queue.add(featuredHeroItemsRequest);
			queue.add(featuredCollectionsRequest);
			queue.add(featuredMappersRequest);
			queue.add(featuredDealsRequest);
		}
	}
}
