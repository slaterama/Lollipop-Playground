package com.citymaps.mobile.android.view;

import android.animation.*;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Button;
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
import com.citymaps.mobile.android.util.CardSizeHelper;
import com.citymaps.mobile.android.util.IntentUtils;
import com.citymaps.mobile.android.util.LogEx;
import com.citymaps.mobile.android.util.MapUtils;
import com.citymaps.mobile.android.view.cards.*;
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

	private TextView mHeroNoItemsView;
	private TextView mFeaturedCollectionsNoItemsView;
	private TextView mFeaturedMappersNoItemsView;
	private TextView mFeaturedDealsNoItemsView;

	private RecyclerViewEx mHeroRecyclerView;
	private RecyclerViewEx mFeaturedCollectionsRecyclerView;
	private RecyclerViewEx mFeaturedMappersRecyclerView;
	private RecyclerViewEx mFeaturedDealsRecyclerView;

	private HeroAdapter mHeroAdapter;
	private FeaturedCollectionsAdapter mFeaturedCollectionsAdapter;
	private FeaturedMappersAdapter mFeaturedMappersAdapter;
	private FeaturedDealsAdapter mFeaturedDealsAdapter;

	private Set<CardType> mCardTypeSet;

	private DataFragment mDataFragment;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_explore);
		mCardSizeHelper = new CardSizeHelper(this, true);

		// Set up views

		mHeroLabelView = (TextView) findViewById(R.id.explore_hero_label);

		mHeroProgressBar = (ProgressBar) findViewById(R.id.explore_hero_progressbar);
		mFeaturedCollectionsProgressBar = (ProgressBar) findViewById(R.id.explore_featured_collections_progressbar);
		mFeaturedMappersProgressBar = (ProgressBar) findViewById(R.id.explore_featured_mappers_progressbar);
		mFeaturedDealsProgressBar = (ProgressBar) findViewById(R.id.explore_featured_deals_progressbar);

		mHeroNoItemsView = (TextView) findViewById(R.id.explore_hero_no_items);
		mFeaturedCollectionsNoItemsView = (TextView) findViewById(R.id.explore_featured_collections_no_items);
		mFeaturedMappersNoItemsView = (TextView) findViewById(R.id.explore_featured_mappers_no_items);
		mFeaturedDealsNoItemsView = (TextView) findViewById(R.id.explore_featured_deals_no_items);

		mHeroRecyclerView = (RecyclerViewEx) findViewById(R.id.explore_hero_recycler);
		mFeaturedCollectionsRecyclerView = (RecyclerViewEx) findViewById(R.id.explore_featured_collections_recycler);
		mFeaturedMappersRecyclerView = (RecyclerViewEx) findViewById(R.id.explore_featured_mappers_recycler);
		mFeaturedDealsRecyclerView = (RecyclerViewEx) findViewById(R.id.explore_featured_deals_recycler);

		mHeroRecyclerView.setTag(CardType.HERO);
		mFeaturedCollectionsRecyclerView.setTag(CardType.FEATURED_COLLECTIONS);
		mFeaturedMappersRecyclerView.setTag(CardType.FEATURED_MAPPERS);
		mFeaturedDealsRecyclerView.setTag(CardType.FEATURED_DEALS);

		mHeroRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
		mFeaturedCollectionsRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
		mFeaturedMappersRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
		mFeaturedDealsRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));

		mHeroRecyclerView.setAdapter(mHeroAdapter = new HeroAdapter(this));
		mFeaturedCollectionsRecyclerView.setAdapter(mFeaturedCollectionsAdapter = new FeaturedCollectionsAdapter(this));
		mFeaturedMappersRecyclerView.setAdapter(mFeaturedMappersAdapter = new FeaturedMappersAdapter(this));
		mFeaturedDealsRecyclerView.setAdapter(mFeaturedDealsAdapter = new FeaturedDealsAdapter(this));

		mHeroRecyclerView.setOnSizeChangedListener(mCardSizeHelper);
		mFeaturedCollectionsRecyclerView.setOnSizeChangedListener(mCardSizeHelper);
		mFeaturedMappersRecyclerView.setOnSizeChangedListener(mCardSizeHelper);
		mFeaturedDealsRecyclerView.setOnSizeChangedListener(mCardSizeHelper);

		Button heroViewAllButton = (Button) findViewById(R.id.explore_hero_view_all_button);
		Button featuredCollectionsViewAllButton = (Button) findViewById(R.id.explore_featured_collections_view_all_button);
		Button featuredMappersViewAllButton = (Button) findViewById(R.id.explore_featured_mappers_view_all_button);
		Button featuredDealsViewAllButton = (Button) findViewById(R.id.explore_featured_deals_view_all_button);

		heroViewAllButton.setTag(CardType.HERO);
		featuredCollectionsViewAllButton.setTag(CardType.FEATURED_COLLECTIONS);
		featuredMappersViewAllButton.setTag(CardType.FEATURED_MAPPERS);
		featuredDealsViewAllButton.setTag(CardType.FEATURED_DEALS);

		// Set up data fragment

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
			mCardTypeSet = new HashSet<CardType>(Arrays.asList(CardType.values()));
			mDataFragment = DataFragment.newInstance(getIntent());
			getSupportFragmentManager()
					.beginTransaction()
					.add(mDataFragment, DataFragment.FRAGMENT_TAG)
					.commit();
		} else {
			mDataFragment = (DataFragment) getSupportFragmentManager().getFragment(savedInstanceState, STATE_KEY_DATA_FRAGMENT);
			onHeroRequestResponse(mDataFragment.mHeroItems);
			if (mDataFragment.mHeroItems == null || mDataFragment.mHeroItems.size() == 0) {
				mHeroRecyclerView.setVisibility(View.INVISIBLE);
				mHeroNoItemsView.setVisibility(View.VISIBLE);
			}
			onFeaturedCollectionsRequestResponse(mDataFragment.mFeaturedCollections);
			if (mDataFragment.mFeaturedCollections == null || mDataFragment.mFeaturedCollections.size() == 0) {
				mFeaturedCollectionsRecyclerView.setVisibility(View.INVISIBLE);
				mFeaturedCollectionsNoItemsView.setVisibility(View.VISIBLE);
			}
			onFeaturedMappersRequestResponse(mDataFragment.mFeaturedMappers);
			if (mDataFragment.mFeaturedMappers == null || mDataFragment.mFeaturedMappers.size() == 0) {
				mFeaturedMappersRecyclerView.setVisibility(View.INVISIBLE);
				mFeaturedMappersNoItemsView.setVisibility(View.VISIBLE);
			}
			onFeaturedDealsRequestResponse(mDataFragment.mFeaturedDeals);
			if (mDataFragment.mFeaturedDeals == null || mDataFragment.mFeaturedDeals.size() == 0) {
				mFeaturedDealsRecyclerView.setVisibility(View.INVISIBLE);
				mFeaturedDealsNoItemsView.setVisibility(View.VISIBLE);
			}
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

	public void onViewAllClick(View v) {
		Intent intent = getIntent();
		Intent viewAllIntent = new Intent(this, ExploreViewAllActivity.class);
		IntentUtils.putCardType(viewAllIntent, (CardType) v.getTag());
		IntentUtils.putMapLocation(viewAllIntent, IntentUtils.getMapLocation(intent));
		IntentUtils.putMapRadius(viewAllIntent, IntentUtils.getMapRadius(intent, MapUtils.DEFAULT_SEARCH_RADIUS));
		IntentUtils.putMapZoom(viewAllIntent, IntentUtils.getMapZoom(intent, MapUtils.DEFAULT_SEARCH_ZOOM));
		this.startActivity(viewAllIntent);
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
		mHeroAdapter.setItems(response);
		updateHeroLabel();
	}

	private void onFeaturedCollectionsRequestResponse(List<SearchResultCollection> response) {
		mFeaturedCollectionsAdapter.setItems(response);
	}

	private void onFeaturedMappersRequestResponse(List<User> response) {
		mFeaturedMappersAdapter.setItems(response);
	}

	private void onFeaturedDealsRequestResponse(List<SearchResultPlace> response) {
		mFeaturedDealsAdapter.setItems(response);
	}

	private void onCardTypeLoaded(CardType type) {
		if (mCardTypeSet != null) {
			mCardTypeSet.remove(type);
			if (mCardTypeSet.size() == 0) {
				new AnimatorHelper().start();
			}
		}
	}

	private View.OnLayoutChangeListener mRecyclerView_OnLayoutChangeListener = new View.OnLayoutChangeListener() {
		@Override
		public void onLayoutChange(View v, int left, int top, int right, int bottom,
								   int oldLeft, int oldTop, int oldRight, int oldBottom) {
			if (v instanceof RecyclerView && ((RecyclerView) v).getChildCount() > 0) {
				onCardTypeLoaded((CardType) v.getTag());
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
		protected CardType mCardType;
		protected RecyclerView mRecyclerView;

		protected boolean mHasViewAllCard = true;

		public ExploreAdapter(Context context) {
			super();
			mContext = context;
			init(context);
		}

		protected void init(Context context) {
		}

		public List<D> getItems() {
			return mItems;
		}

		public void setItems(List<D> items) {
			mItems = items;
			if (mInInitialLayout) {
				if (items == null || items.size() == 0) {
					onCardTypeLoaded(mCardType);
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
			if (mItems == null || mItems.size() == 0) {
				return 0;
			} else {
				return mItems.size() + (mHasViewAllCard ? 1 : 0);
			}
		}

		@Override
		public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			if (viewType == VIEW_TYPE_VIEW_ALL) {
				ViewAllCardView cardView = (new ViewAllCardView(mContext));
				cardView.setIntent(getIntent());
				return new ExploreViewHolder(cardView);
			} else {
				return null;
			}
		}

		@Override
		public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
			// Configure the correct margin depending on position
			if (holder.itemView.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
				ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) holder.itemView.getLayoutParams();
				int margin = (int) (mCardSizeHelper.getCardPerceivedMargin() -
						2 * (holder.itemView instanceof CardView ? ((CardView) holder.itemView).getMaxCardElevation() : 0));
				MarginLayoutParamsCompat.setMarginEnd(mlp, position < getItemCount() - 1 ? margin : 0);
				holder.itemView.setLayoutParams(mlp);
			}
		}
	}

	private class HeroAdapter extends ExploreAdapter<SearchResult> {

		public HeroAdapter(Context context) {
			super(context);
		}

		@Override
		protected void init(Context context) {
			super.init(context);
			mCardType = CardType.HERO;
			mRecyclerView = mHeroRecyclerView;
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
		public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			ExploreCardView cardView;
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
				cardView = (ExploreCardView) holder.itemView;
			}
			cardView.setTag(CardType.HERO);
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
				((ViewAllCardView) holder.itemView).setData(CardType.HERO);
			}
		}
	}

	private class FeaturedCollectionsAdapter extends ExploreAdapter<SearchResultCollection> {

		public FeaturedCollectionsAdapter(Context context) {
			super(context);
		}

		@Override
		protected void init(Context context) {
			super.init(context);
			mCardType = CardType.FEATURED_COLLECTIONS;
			mRecyclerView = mFeaturedCollectionsRecyclerView;
		}

		@Override
		public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			ExploreCardView cardView;
			ViewHolder holder = super.onCreateViewHolder(parent, viewType);
			if (holder == null) {
				cardView = new CollectionCardView(mContext);
				holder = new ExploreViewHolder(cardView);
			} else {
				cardView = (ExploreCardView) holder.itemView;
			}
			cardView.setTag(CardType.FEATURED_COLLECTIONS);
			mCardSizeHelper.updateCardLayoutParams(cardView);
			return holder;
		}

		@Override
		public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
			super.onBindViewHolder(holder, position);
			if (holder.itemView instanceof CollectionCardView) {
				((CollectionCardView) holder.itemView).setData(mItems.get(position), mInInitialLayout);
			} else if (holder.itemView instanceof ViewAllCardView) {
				((ViewAllCardView) holder.itemView).setData(CardType.FEATURED_COLLECTIONS);
			}
		}
	}

	private class FeaturedMappersAdapter extends ExploreAdapter<User> {

		public FeaturedMappersAdapter(Context context) {
			super(context);
		}

		@Override
		protected void init(Context context) {
			super.init(context);
			mCardType = CardType.FEATURED_MAPPERS;
			mRecyclerView = mFeaturedMappersRecyclerView;
		}

		@Override
		public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			ExploreCardView cardView;
			ViewHolder holder = super.onCreateViewHolder(parent, viewType);
			if (holder == null) {
				cardView = new UserCardView(mContext);
				holder = new ExploreViewHolder(cardView);
			} else {
				cardView = (ExploreCardView) holder.itemView;
			}
			cardView.setTag(CardType.FEATURED_MAPPERS);
			mCardSizeHelper.updateCardLayoutParams(cardView);
			return holder;
		}

		@Override
		public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
			super.onBindViewHolder(holder, position);
			if (holder.itemView instanceof UserCardView) {
				((UserCardView) holder.itemView).setData(mItems.get(position), mInInitialLayout);
			} else if (holder.itemView instanceof ViewAllCardView) {
				((ViewAllCardView) holder.itemView).setData(CardType.FEATURED_MAPPERS);
			}
		}
	}

	private class FeaturedDealsAdapter extends ExploreAdapter<SearchResultPlace> {

		public FeaturedDealsAdapter(Context context) {
			super(context);
		}

		@Override
		protected void init(Context context) {
			super.init(context);
			mCardType = CardType.FEATURED_DEALS;
			mRecyclerView = mFeaturedDealsRecyclerView;
		}

		@Override
		public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			ExploreCardView cardView;
			ViewHolder holder = super.onCreateViewHolder(parent, viewType);
			if (holder == null) {
				cardView = new DealCardView(mContext);
				holder = new ExploreViewHolder(cardView);
			} else {
				cardView = (ExploreCardView) holder.itemView;
			}
			cardView.setTag(CardType.FEATURED_DEALS);
			mCardSizeHelper.updateCardLayoutParams(cardView);
			return holder;
		}

		@Override
		public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
			super.onBindViewHolder(holder, position);
			if (holder.itemView instanceof DealCardView) {
				((DealCardView) holder.itemView).setData(mItems.get(position), mInInitialLayout);
			} else if (holder.itemView instanceof ViewAllCardView) {
				((ViewAllCardView) holder.itemView).setData(CardType.FEATURED_DEALS);
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
			List<Animator> progressBarAnimators = new ArrayList<Animator>(progressBars.length);
			for (int i = 0; i < progressBars.length; i++) {
				ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(progressBars[i],
						PropertyValuesHolder.ofFloat("scaleX", 0.0f),
						PropertyValuesHolder.ofFloat("scaleY", 0.0f));
				animator.setDuration(getResources().getInteger(android.R.integer.config_longAnimTime));
				animator.setInterpolator(new AnticipateInterpolator(5.0f));
				progressBarAnimators.add(animator);
			}
			AnimatorSet progressBarAnimatorSet = new AnimatorSet();
			progressBarAnimatorSet.playTogether(progressBarAnimators);
			progressBarAnimatorSet.addListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					super.onAnimationEnd(animation);
					mHeroProgressBar.setVisibility(View.GONE);
					if (mHeroRecyclerView.getChildCount() == 0) {
						mHeroNoItemsView.setVisibility(View.VISIBLE);
					} else {
						mHeroRecyclerView.setVisibility(View.VISIBLE);
					}
					mFeaturedCollectionsProgressBar.setVisibility(View.GONE);
					if (mFeaturedCollectionsRecyclerView.getChildCount() == 0) {
						mFeaturedCollectionsNoItemsView.setVisibility(View.VISIBLE);
					} else {
						mFeaturedCollectionsRecyclerView.setVisibility(View.VISIBLE);
					}
					mFeaturedMappersProgressBar.setVisibility(View.GONE);
					if (mFeaturedMappersRecyclerView.getChildCount() == 0) {
						mFeaturedMappersNoItemsView.setVisibility(View.VISIBLE);
					} else {
						mFeaturedMappersRecyclerView.setVisibility(View.VISIBLE);
					}
					mFeaturedDealsProgressBar.setVisibility(View.GONE);
					if (mFeaturedDealsRecyclerView.getChildCount() == 0) {
						mFeaturedDealsNoItemsView.setVisibility(View.VISIBLE);
					} else {
						mFeaturedDealsRecyclerView.setVisibility(View.VISIBLE);
					}
				}
			});

			// Make a second animator set using all initial card views
			// and if necessary, a third set indicating data sets with no values
			RecyclerView[] recyclerViews = new RecyclerView[]{mHeroRecyclerView,
					mFeaturedCollectionsRecyclerView, mFeaturedMappersRecyclerView,
					mFeaturedDealsRecyclerView};
			List<Animator> noItemsAnimators = new ArrayList<Animator>();
			int size = 0;
			for (RecyclerView recyclerView : recyclerViews) {
				int childCount = recyclerView.getChildCount();
				if (childCount == 0) {
					Animator animator = AnimatorInflater.loadAnimator(ExploreActivity.this, R.animator.grow_fade_in_center);
					final View target;
					if (recyclerView == mFeaturedCollectionsRecyclerView) {
						target = findViewById(R.id.explore_featured_deals_no_items);
					} else if (recyclerView == mFeaturedMappersRecyclerView) {
						target = findViewById(R.id.explore_featured_mappers_no_items);
					} else if (recyclerView == mFeaturedDealsRecyclerView) {
						target = findViewById(R.id.explore_featured_collections_no_items);
					} else {
						target = findViewById(R.id.explore_hero_no_items);
					}
					animator.setTarget(target);
					noItemsAnimators.add(animator);
				}
				size += childCount;
			}
//			final List<ExploreCardView> cardViews = new ArrayList<ExploreCardView>(size);
			List<Animator> cardViewAnimators = new ArrayList<Animator>(size);
			int duration = getResources().getInteger(android.R.integer.config_mediumAnimTime);
			int totalOffset = 0;
			for (RecyclerView recyclerView : recyclerViews) {
				int width = recyclerView.getWidth();
				int childCount = recyclerView.getChildCount();
				for (int i = 0; i < childCount; i++) {
					final ExploreCardView cardView = (ExploreCardView) recyclerView.getChildAt(i);
//					cardViews.add(cardView);
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
					cardViewAnimators.add(animator);
					totalOffset += ANIMATOR_OFFSET;
				}
			}

			List<Animator> animatorSets = new ArrayList<Animator>();
			animatorSets.add(progressBarAnimatorSet);
			if (noItemsAnimators.size() > 0) {
				AnimatorSet set = new AnimatorSet();
				set.playTogether(noItemsAnimators);
				animatorSets.add(set);
			}
			if (cardViewAnimators.size() > 0) {
				AnimatorSet set = new AnimatorSet();
				set.playTogether(cardViewAnimators);
				animatorSets.add(set);
			}
			mAnimatorSet = new AnimatorSet();
			mAnimatorSet.playSequentially(animatorSets);
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
}
