package com.citymaps.mobile.android.view.explorenew;

import android.animation.*;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MarginLayoutParamsCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.OvershootInterpolator;
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

	private static final String STATE_KEY_DATA_FRAGMENT = "dataFragment";

	private static final int ANIMATOR_OFFSET = 100;

	private boolean mInInitialLayout;

	private CardSizeHelper mCardSizeHelper;

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

	private Set<ExploreDataType> mExploreDataTypeSet;

	private DataFragment mDataFragment;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_explore);
		mCardSizeHelper = new CardSizeHelper(this);

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

		mHeroRecyclerView.setTag(ExploreDataType.HERO);
		mFeaturedCollectionsRecyclerView.setTag(ExploreDataType.FEATURED_COLLECTIONS);
		mFeaturedMappersRecyclerView.setTag(ExploreDataType.FEATURED_MAPPERS);
		mFeaturedDealsRecyclerView.setTag(ExploreDataType.FEATURED_DEALS);

		mHeroRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
		mFeaturedCollectionsRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
		mFeaturedMappersRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
		mFeaturedDealsRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));

		mHeroRecyclerView.setOnSizeChangedListener(mCardSizeHelper);
		mFeaturedCollectionsRecyclerView.setOnSizeChangedListener(mCardSizeHelper);
		mFeaturedMappersRecyclerView.setOnSizeChangedListener(mCardSizeHelper);
		mFeaturedDealsRecyclerView.setOnSizeChangedListener(mCardSizeHelper);

		// Set up helper fragment

		if (savedInstanceState == null) {
			mInInitialLayout = true;
			mHeroRecyclerView.setVisibility(View.INVISIBLE);
			mFeaturedCollectionsRecyclerView.setVisibility(View.INVISIBLE);
			mFeaturedMappersRecyclerView.setVisibility(View.INVISIBLE);
			mFeaturedDealsRecyclerView.setVisibility(View.INVISIBLE);
			mHeroProgressBar.setVisibility(View.VISIBLE);
			mFeaturedCollectionsProgressBar.setVisibility(View.VISIBLE);
			mFeaturedMappersProgressBar.setVisibility(View.VISIBLE);
			mFeaturedDealsProgressBar.setVisibility(View.VISIBLE);
			mExploreDataTypeSet = new HashSet<ExploreDataType>(Arrays.asList(ExploreDataType.values()));
			mDataFragment = DataFragment.newInstance(getIntent());
			getSupportFragmentManager()
					.beginTransaction()
					.add(mDataFragment, DataFragment.FRAGMENT_TAG)
					.commit();
		} else {
			mDataFragment = (DataFragment) getSupportFragmentManager().getFragment(savedInstanceState, STATE_KEY_DATA_FRAGMENT);
			onHeroRequestResponse(mDataFragment.mHeroItems);
			onFeaturedCollectionsRequestResponse(mDataFragment.mFeaturedCollections);
			onFeaturedMappersRequestResponse(mDataFragment.mFeaturedMappers);
			onFeaturedDealsRequestResponse(mDataFragment.mFeaturedDeals);
			updateHeroLabel();
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		getSupportFragmentManager().putFragment(outState, STATE_KEY_DATA_FRAGMENT, mDataFragment);
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

	protected void updateHeroLabel() {
		String city = null;
		if (mDataFragment.mHeroItems != null && mDataFragment.mHeroItems.size() > 0) {
			for (SearchResult searchResult : mDataFragment.mHeroItems) {
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

	private void onHeroRequestResponse(List<SearchResult> response) {
		if (mHeroAdapter == null) {
			mHeroRecyclerView.setAdapter(
					mHeroAdapter = new HeroAdapter(this, response));
		} else {
			mHeroAdapter.setItems(response);
		}
	}

	private void onFeaturedCollectionsRequestResponse(List<SearchResultCollection> response) {
		if (mFeaturedCollectionsAdapter == null) {
			mFeaturedCollectionsRecyclerView.setAdapter(
					mFeaturedCollectionsAdapter = new FeaturedCollectionsAdapter(this, response));
		} else {
			mFeaturedCollectionsAdapter.setItems(response);
		}
	}

	private void onFeaturedMappersRequestResponse(List<User> response) {
		if (mFeaturedMappersAdapter == null) {
			mFeaturedMappersRecyclerView.setAdapter(
					mFeaturedMappersAdapter = new FeaturedMappersAdapter(this, response));
		} else {
			mFeaturedMappersAdapter.setItems(response);
		}
	}

	private void onFeaturedDealsRequestResponse(List<SearchResultPlace> response) {
		if (mFeaturedDealsAdapter == null) {
			mFeaturedDealsRecyclerView.setAdapter(
					mFeaturedDealsAdapter = new FeaturedDealsAdapter(this, response));
		} else {
			mFeaturedDealsAdapter.setItems(response);
		}
	}

	private void onExploreDataTypeLoaded(ExploreDataType type) {
		if (mExploreDataTypeSet != null) {
			mExploreDataTypeSet.remove(type);
			if (mExploreDataTypeSet.size() == 0) {
				new AnimatorHelper().start();
			}
		}
	}

	private View.OnLayoutChangeListener mRecyclerView_OnLayoutChangeListener = new View.OnLayoutChangeListener() {
		@Override
		public void onLayoutChange(View v, int left, int top, int right, int bottom,
								   int oldLeft, int oldTop, int oldRight, int oldBottom) {
			if (v instanceof RecyclerView && ((RecyclerView) v).getChildCount() > 0) {
				onExploreDataTypeLoaded((ExploreDataType) v.getTag());
				v.removeOnLayoutChangeListener(this);
			}
		}
	};

	/*
	 * ********************************************************************************
	 * RecyclerView adapters
	 * ********************************************************************************
	 */

	private abstract class ExploreAdapter<D> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
		protected static final int VIEW_TYPE_VIEW_ALL = Integer.MIN_VALUE;

		protected Context mContext;
		protected List<D> mItems;
		protected ExploreDataType mExploreDataType;
		protected RecyclerView mRecyclerView;

		protected boolean mHasViewAllCard = true;

		public ExploreAdapter(Context context, List<D> items) {
			super();
			mContext = context;
			init(context, items);
		}

		protected void init(Context context, List<D> items) {
			setItems(items);
		}

		public List<D> getItems() {
			return mItems;
		}

		public void setItems(List<D> items) {
			mItems = items;
			if (mInInitialLayout) {
				if (items == null || items.size() == 0) {
					onExploreDataTypeLoaded(mExploreDataType);
				} else {
					mRecyclerView.addOnLayoutChangeListener(mRecyclerView_OnLayoutChangeListener);
				}
			}
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
			if (mItems == null) {
				return 0;
			} else {
				return mItems.size() + (mHasViewAllCard ? 1 : 0);
			}
		}

		@Override
		public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			return (viewType == VIEW_TYPE_VIEW_ALL ?
					new ExploreViewHolder(new ViewAllCardView(mContext)) : null);
		}

		@Override
		public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
			// Configure the correct margin depending on position
			if (holder.itemView.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
				ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) holder.itemView.getLayoutParams();
				int margin = (int) (mCardSizeHelper.mCardPerceivedMargin -
						2 * (holder.itemView instanceof CardView ? ((CardView) holder.itemView).getMaxCardElevation() : 0));
				MarginLayoutParamsCompat.setMarginEnd(mlp, position < getItemCount() - 1 ? margin : 0);
				holder.itemView.setLayoutParams(mlp);
			}
		}
	}

	private class HeroAdapter extends ExploreAdapter<SearchResult> {

		public HeroAdapter(Context context, List<SearchResult> items) {
			super(context, items);
		}

		@Override
		protected void init(Context context, List<SearchResult> items) {
			mExploreDataType = ExploreDataType.HERO;
			mRecyclerView = mHeroRecyclerView;
			mHasViewAllCard = false;
			super.init(context, items);
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
		public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			CitymapsCardView cardView;
			ViewHolder holder = super.onCreateViewHolder(parent, viewType);
			if (holder == null) {
				CitymapsObject.ObjectType type = CitymapsObject.ObjectType.valueOf(viewType);
				switch (type) {
					case PLACE:
						cardView = new PlaceHeroCardView(mContext);
						break;
					case COLLECTION:
						cardView = new CollectionHeroCardView(mContext);
						break;
					default:
						throw new IllegalStateException("HeroAdapter expects only place or collection search results");
				}
				holder = new ExploreViewHolder(cardView);
			} else {
				cardView = (CitymapsCardView) holder.itemView;
			}
			cardView.setTag(ExploreDataType.HERO);
			mCardSizeHelper.updateCardLayoutParams(cardView);
			return holder;
		}

		@Override
		public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
			super.onBindViewHolder(holder, position);
			if (holder.itemView instanceof CollectionHeroCardView) {
				((CollectionHeroCardView) holder.itemView).setData((SearchResultCollection) mItems.get(position), mInInitialLayout);
			} else if (holder.itemView instanceof PlaceHeroCardView) {
				((PlaceHeroCardView) holder.itemView).setData((SearchResultPlace) mItems.get(position), mInInitialLayout);
			} else if (holder.itemView instanceof ViewAllCardView) {
				((ViewAllCardView) holder.itemView).setData(R.string.card_view_all_hero);
			}
		}
	}

	private class FeaturedCollectionsAdapter extends ExploreAdapter<SearchResultCollection> {

		public FeaturedCollectionsAdapter(Context context, List<SearchResultCollection> items) {
			super(context, items);
		}

		@Override
		protected void init(Context context, List<SearchResultCollection> items) {
			mExploreDataType = ExploreDataType.FEATURED_COLLECTIONS;
			mRecyclerView = mFeaturedCollectionsRecyclerView;
			super.init(context, items);
		}

		@Override
		public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			CitymapsCardView cardView;
			ViewHolder holder = super.onCreateViewHolder(parent, viewType);
			if (holder == null) {
				cardView = new CollectionCardView(mContext);
				holder = new ExploreViewHolder(cardView);
			} else {
				cardView = (CitymapsCardView) holder.itemView;
			}
			cardView.setTag(ExploreDataType.FEATURED_COLLECTIONS);
			mCardSizeHelper.updateCardLayoutParams(cardView);
			return holder;
		}

		@Override
		public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
			super.onBindViewHolder(holder, position);
			if (holder.itemView instanceof CollectionCardView) {
				((CollectionCardView) holder.itemView).setData(mItems.get(position), mInInitialLayout);
			} else if (holder.itemView instanceof ViewAllCardView) {
				((ViewAllCardView) holder.itemView).setData(R.string.card_view_all_featured_collections);
			}
		}
	}

	private class FeaturedMappersAdapter extends ExploreAdapter<User> {

		public FeaturedMappersAdapter(Context context, List<User> items) {
			super(context, items);
		}

		@Override
		protected void init(Context context, List<User> items) {
			mExploreDataType = ExploreDataType.FEATURED_MAPPERS;
			mRecyclerView = mFeaturedMappersRecyclerView;
			super.init(context, items);
		}

		@Override
		public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			CitymapsCardView cardView;
			ViewHolder holder = super.onCreateViewHolder(parent, viewType);
			if (holder == null) {
				cardView = new UserCardView(mContext);
				holder = new ExploreViewHolder(cardView);
			} else {
				cardView = (CitymapsCardView) holder.itemView;
			}
			cardView.setTag(ExploreDataType.FEATURED_MAPPERS);
			mCardSizeHelper.updateCardLayoutParams(cardView);
			return holder;
		}

		@Override
		public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
			super.onBindViewHolder(holder, position);
			if (holder.itemView instanceof UserCardView) {
				((UserCardView) holder.itemView).setData(mItems.get(position), mInInitialLayout);
			} else if (holder.itemView instanceof ViewAllCardView) {
				((ViewAllCardView) holder.itemView).setData(R.string.card_view_all_featured_mappers);
			}
		}
	}

	private class FeaturedDealsAdapter extends ExploreAdapter<SearchResultPlace> {

		public FeaturedDealsAdapter(Context context, List<SearchResultPlace> items) {
			super(context, items);
		}

		@Override
		protected void init(Context context, List<SearchResultPlace> items) {
			mExploreDataType = ExploreDataType.FEATURED_DEALS;
			mRecyclerView = mFeaturedDealsRecyclerView;
			super.init(context, items);
		}

		@Override
		public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			CitymapsCardView cardView;
			ViewHolder holder = super.onCreateViewHolder(parent, viewType);
			if (holder == null) {
				cardView = new DealCardView(mContext);
				holder = new ExploreViewHolder(cardView);
			} else {
				cardView = (CitymapsCardView) holder.itemView;
			}
			cardView.setTag(ExploreDataType.FEATURED_DEALS);
			mCardSizeHelper.updateCardLayoutParams(cardView);
			return holder;
		}

		@Override
		public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
			super.onBindViewHolder(holder, position);
			if (holder.itemView instanceof DealCardView) {
				((DealCardView) holder.itemView).setData(mItems.get(position), mInInitialLayout);
			} else if (holder.itemView instanceof ViewAllCardView) {
				((ViewAllCardView) holder.itemView).setData(R.string.card_view_all_featured_deals);
			}
		}
	}

	private class ExploreViewHolder extends ViewHolder {
		public ExploreViewHolder(View itemView) {
			super(itemView);
			if (mInInitialLayout) {
				itemView.setVisibility(View.INVISIBLE);
			}
		}
	}

	/*
	 * ******************************************************************************
	 * Card size helper
	 * ******************************************************************************
	 */

	protected static class CardSizeHelper
			implements OnSizeChangedListener {
		private Context mContext;

		private boolean mUseCompatPadding;
		private int mCardMaxElevation;
		private int mCardPerceivedMargin;

		private float mDefaultCardsAcross;
		private float mHeroCardsAcross;
		private float mFeaturedCollectionsCardsAcross;
		private float mFeaturedMappersCardsAcross;
		private float mFeaturedDealsCardsAcross;

		private int mHeroDefaultCardSize;
		private Rect mHeroCardRect;
		private Rect mFeaturedCollectionsCardRect;
		private Rect mFeaturedMappersCardRect;
		private Rect mFeaturedDealsCardRect;

		public CardSizeHelper(Context context) {
			mContext = context;

			// Get dimensions and other resources for recycler view card layout

			Resources resources = context.getResources();
			mUseCompatPadding = resources.getBoolean(R.bool.explore_card_use_compat_padding);
			mCardMaxElevation = resources.getDimensionPixelOffset(R.dimen.explore_card_max_elevation);
			mCardPerceivedMargin = resources.getDimensionPixelOffset(R.dimen.explore_card_inner_margin);
			mDefaultCardsAcross = ResourcesUtils.getFloat(resources, R.dimen.explore_default_cards_across, 2.0f);
			mHeroCardsAcross = ResourcesUtils.getFloat(resources, R.dimen.explore_hero_cards_across, 1.0f);
			mFeaturedCollectionsCardsAcross = ResourcesUtils.getFloat(resources, R.dimen.explore_featured_collections_cards_across, 2.0f);
			mFeaturedMappersCardsAcross = ResourcesUtils.getFloat(resources, R.dimen.explore_featured_mappers_cards_across, 2.0f);
			mFeaturedDealsCardsAcross = ResourcesUtils.getFloat(resources, R.dimen.explore_featured_deals_cards_across, 2.0f);
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

		private void updateCardLayoutParams(CitymapsCardView cardView) {
			Rect rect;
			ExploreDataType type = (ExploreDataType) cardView.getTag();
			if (type == null) {
				return;
			}
			switch (type) {
				case HERO:
					rect = mHeroCardRect;
					cardView.setDefaultCardSize(mHeroDefaultCardSize);
					break;
				case FEATURED_COLLECTIONS:
					rect = mFeaturedCollectionsCardRect;
					cardView.setDefaultCardSize(rect.width());
					break;
				case FEATURED_MAPPERS:
					rect = mFeaturedMappersCardRect;
					cardView.setDefaultCardSize(rect.width());
					break;
				case FEATURED_DEALS:
					rect = mFeaturedDealsCardRect;
					cardView.setDefaultCardSize(rect.width());
					break;
				default:
					return;
			}
			int actualCardWidth = rect.width() + (mUseCompatPadding ? 2 * mCardMaxElevation : 0);
			cardView.setLayoutParams(new RecyclerView.LayoutParams(actualCardWidth, rect.height()));
		}

		@Override
		public void onSizeChanged(View v, int w, int h, int oldw, int oldh) {
			if (w != oldw) {
				final Rect cardRect;
				ExploreDataType type = (ExploreDataType) v.getTag();
				switch (type) {
					case HERO: {
						mHeroDefaultCardSize = getCardSize(v, mDefaultCardsAcross); // Card size across for "default"
						int cardSize = getCardSize(v, mHeroCardsAcross);
						mHeroCardRect = new Rect(0, 0, cardSize,
								HeroCardView.getDesiredHeight(mContext, mHeroDefaultCardSize));
						cardRect = mHeroCardRect;
						break;
					}
					case FEATURED_COLLECTIONS: {
						int cardSize = getCardSize(v, mFeaturedCollectionsCardsAcross);
						mFeaturedCollectionsCardRect = new Rect(0, 0, cardSize,
								CollectionCardView.getDesiredHeight(mContext, cardSize));
						cardRect = mFeaturedCollectionsCardRect;
						break;
					}
					case FEATURED_MAPPERS: {
						int cardSize = getCardSize(v, mFeaturedMappersCardsAcross);
						mFeaturedMappersCardRect = new Rect(0, 0, cardSize,
								UserCardView.getDesiredHeight(mContext, cardSize));
						cardRect = mFeaturedMappersCardRect;
						break;
					}
					case FEATURED_DEALS: {
						int cardSize = getCardSize(v, mFeaturedDealsCardsAcross);
						mFeaturedDealsCardRect = new Rect(0, 0, cardSize,
								DealCardView.getDesiredHeight(mContext, cardSize));
						cardRect = mFeaturedDealsCardRect;
						break;
					}
					default:
						return;
				}
				v.getLayoutParams().height = v.getPaddingTop() + cardRect.height() + v.getPaddingBottom();
				v.requestLayout();
			}
		}
	}

	/*
	 * ******************************************************************************
	 * Animator helper
	 * ******************************************************************************
	 */

	protected class AnimatorHelper {
		private AnimatorSet mAnimatorSet;

		public AnimatorHelper() {
			// Make an animator set using all progress bars
			ProgressBar[] progressBars = new ProgressBar[]{mHeroProgressBar,
					mFeaturedCollectionsProgressBar, mFeaturedMappersProgressBar,
					mFeaturedDealsProgressBar};
			ObjectAnimator[] progressBarAnimators = new ObjectAnimator[progressBars.length];
			for (int i = 0; i < progressBars.length; i++) {
				progressBarAnimators[i] = ObjectAnimator.ofPropertyValuesHolder(progressBars[i],
						PropertyValuesHolder.ofFloat("scaleX", 0.0f),
						PropertyValuesHolder.ofFloat("scaleY", 0.0f));
				progressBarAnimators[i].setDuration(getResources().getInteger(android.R.integer.config_longAnimTime));
				progressBarAnimators[i].setInterpolator(new AnticipateInterpolator(5.0f));
			}
			AnimatorSet progressBarAnimatorSet = new AnimatorSet();
			progressBarAnimatorSet.playTogether(progressBarAnimators);
			progressBarAnimatorSet.addListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					super.onAnimationEnd(animation);
					mHeroProgressBar.setVisibility(View.GONE);
					mHeroRecyclerView.setVisibility(View.VISIBLE);
					mFeaturedCollectionsProgressBar.setVisibility(View.GONE);
					mFeaturedCollectionsRecyclerView.setVisibility(View.VISIBLE);
					mFeaturedMappersProgressBar.setVisibility(View.GONE);
					mFeaturedMappersRecyclerView.setVisibility(View.VISIBLE);
					mFeaturedDealsProgressBar.setVisibility(View.GONE);
					mFeaturedDealsRecyclerView.setVisibility(View.VISIBLE);
				}
			});

			// Make a second animator set using all initial card views
			RecyclerView[] recyclerViews = new RecyclerView[]{mHeroRecyclerView,
					mFeaturedCollectionsRecyclerView, mFeaturedMappersRecyclerView,
					mFeaturedDealsRecyclerView};
			int size = 0;
			for (RecyclerView recyclerView : recyclerViews) {
				size += recyclerView.getChildCount();
			}
			ObjectAnimator[] cardViewAnimators = new ObjectAnimator[size];
			int index = 0;
			int duration = getResources().getInteger(android.R.integer.config_mediumAnimTime);
			int totalOffset = 0;
			for (RecyclerView recyclerView : recyclerViews) {
				int width = recyclerView.getWidth();
				int childCount = recyclerView.getChildCount();
				for (int i = 0; i < childCount; i++) {
					final CitymapsCardView cardView = (CitymapsCardView) recyclerView.getChildAt(i);
					float to = cardView.getX();
					float from = to + width;
					ObjectAnimator animator = ObjectAnimator.ofFloat(cardView, "x", from, to);
					animator.setDuration(duration);
					animator.setInterpolator(new OvershootInterpolator(0.75f));
					animator.setStartDelay(totalOffset);
					animator.addListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationStart(Animator animation) {
							super.onAnimationStart(animation);
							cardView.setVisibility(View.VISIBLE);
						}

						@Override
						public void onAnimationEnd(Animator animation) {
							super.onAnimationEnd(animation);
							cardView.setInInitialLayout(false);
						}
					});
					cardViewAnimators[index] = animator;
					index++;
					totalOffset += ANIMATOR_OFFSET;
				}
			}
			AnimatorSet cardViewAnimatorSet = new AnimatorSet();

			// TODO If cardViewAnimators.length is 0, this line crashes
			cardViewAnimatorSet.playTogether(cardViewAnimators);
			mAnimatorSet = new AnimatorSet();
			mAnimatorSet.playSequentially(progressBarAnimatorSet, cardViewAnimatorSet);
			mAnimatorSet.addListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					mInInitialLayout = false;
				}
			});
		}

		public void start() {
			mAnimatorSet.start();
		}
	}

	/*
	 * ******************************************************************************
	 * Data fragment
	 * ******************************************************************************
	 */

	public static class DataFragment extends Fragment {
		public static final String FRAGMENT_TAG = DataFragment.class.getName();

		private static final String ARG_MAP_LOCATION = "mapLocation";
		private static final String ARG_MAP_RADIUS = "mapRadius";
		private static final String ARG_MAP_ZOOM = "mapZoom";

		public static DataFragment newInstance(Intent intent) {
			DataFragment fragment = new DataFragment();
			Bundle args = new Bundle(3);
			args.putParcelable(ARG_MAP_LOCATION, IntentUtils.getMapLocation(intent));
			args.putFloat(ARG_MAP_RADIUS, IntentUtils.getMapRadius(intent, MapUtils.DEFAULT_SEARCH_RADIUS));
			args.putInt(ARG_MAP_ZOOM, IntentUtils.getMapZoom(intent, MapUtils.DEFAULT_SEARCH_ZOOM));
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
						"ExploreActivity.DataFragment must be attached to ExploreActivity");
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
			SearchResultsRequest heroRequest = SearchResultsRequest.newFeaturedHeroItemsRequest(getActivity(),
					mMapLocation, mMapZoom, mMapRadius, offset, limit,
					new Response.Listener<List<SearchResult>>() {
						@Override
						public void onResponse(List<SearchResult> response) {
							mHeroItems = response;
							mActivity.onHeroRequestResponse(mHeroItems);
						}
					},
					new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							if (LogEx.isLoggable(LogEx.ERROR)) {
								LogEx.e(error.getMessage(), error);
							}
							mHeroItems = null;
							mActivity.onHeroRequestResponse(null);
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
							mActivity.onFeaturedCollectionsRequestResponse(mFeaturedCollections);
						}
					},
					new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							if (LogEx.isLoggable(LogEx.ERROR)) {
								LogEx.e(error.getMessage(), error);
							}
							mFeaturedCollections = null;
							mActivity.onFeaturedCollectionsRequestResponse(null);
						}
					});

			UsersRequest featuredMappersRequest = UsersRequest.newFeaturedMappersRequest(getActivity(),
					mMapLocation, mMapRadius, offset, limit,
					new Response.Listener<List<User>>() {
						@Override
						public void onResponse(List<User> response) {
							mFeaturedMappers = response;
							mActivity.onFeaturedMappersRequestResponse(mFeaturedMappers);
						}
					},
					new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							if (LogEx.isLoggable(LogEx.ERROR)) {
								LogEx.e(error.getMessage(), error);
							}
							mFeaturedMappers = null;
							mActivity.onFeaturedMappersRequestResponse(null);
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
							mActivity.onFeaturedDealsRequestResponse(mFeaturedDeals);
						}
					},
					new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							if (LogEx.isLoggable(LogEx.ERROR)) {
								LogEx.e(error.getMessage(), error);
							}
							mFeaturedDeals = null;
							mActivity.onFeaturedDealsRequestResponse(null);
						}
					});

			RequestQueue queue = VolleyManager.getInstance(getActivity()).getRequestQueue();
			queue.add(heroRequest);
			queue.add(featuredCollectionsRequest);
			queue.add(featuredMappersRequest);
			queue.add(featuredDealsRequest);
		}
	}

	private static enum ExploreDataType {
		HERO,
		FEATURED_COLLECTIONS,
		FEATURED_MAPPERS,
		FEATURED_DEALS
	}
}
