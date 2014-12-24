package com.citymaps.mobile.android.view.explorenew;

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

import java.util.ArrayList;
import java.util.List;

public class ExploreActivity extends TrackedActionBarActivity {

	private static final String STATE_KEY_HELPER_FRAGMENT = "helperFragment";

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

	private HelperFragment mHelperFragment;

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

		mHeroRecyclerView.setTag(RecyclerViewType.HERO);
		mFeaturedCollectionsRecyclerView.setTag(RecyclerViewType.FEATURED_COLLECTIONS);
		mFeaturedMappersRecyclerView.setTag(RecyclerViewType.FEATURED_MAPPERS);
		mFeaturedDealsRecyclerView.setTag(RecyclerViewType.FEATURED_DEALS);

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
			mHelperFragment = HelperFragment.newInstance(getIntent());
			getSupportFragmentManager()
					.beginTransaction()
					.add(mHelperFragment, HelperFragment.FRAGMENT_TAG)
					.commit();
		} else {
			mHelperFragment = (HelperFragment) getSupportFragmentManager().getFragment(savedInstanceState, STATE_KEY_HELPER_FRAGMENT);
			onHeroRequestResponse(mHelperFragment.mHeroItems);
			onFeaturedCollectionsRequestResponse(mHelperFragment.mFeaturedCollections);
			onFeaturedMappersRequestResponse(mHelperFragment.mFeaturedMappers);
			onFeaturedDealsRequestResponse(mHelperFragment.mFeaturedDeals);
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

	private void onHeroRequestResponse(List<SearchResult> response) {
		if (mHeroAdapter == null) {
			mHeroAdapter = new HeroAdapter(this, response);
			mHeroRecyclerView.setAdapter(mHeroAdapter);
		} else {
			mHeroAdapter.setItems(response);
		}
	}

	private void onFeaturedCollectionsRequestResponse(List<SearchResultCollection> response) {
		if (mFeaturedCollectionsAdapter == null) {
			mFeaturedCollectionsAdapter = new FeaturedCollectionsAdapter(this, response);
			mFeaturedCollectionsRecyclerView.setAdapter(mFeaturedCollectionsAdapter);
		} else {
			mFeaturedCollectionsAdapter.setItems(response);
		}
	}

	private void onFeaturedMappersRequestResponse(List<User> response) {
		if (mFeaturedMappersAdapter == null) {
			mFeaturedMappersAdapter = new FeaturedMappersAdapter(this, response);
			mFeaturedMappersRecyclerView.setAdapter(mFeaturedMappersAdapter);
		} else {
			mFeaturedMappersAdapter.setItems(response);
		}
	}

	private void onFeaturedDealsRequestResponse(List<SearchResultPlace> response) {
		if (mFeaturedDealsAdapter == null) {
			mFeaturedDealsAdapter = new FeaturedDealsAdapter(this, response);
			mFeaturedDealsRecyclerView.setAdapter(mFeaturedDealsAdapter);
		} else {
			mFeaturedDealsAdapter.setItems(response);
		}
	}

	/*
	 * ********************************************************************************
	 * RecyclerView adapters
	 * ********************************************************************************
	 */

	private abstract class ExploreAdapter<D> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
		protected static final int VIEW_TYPE_VIEW_ALL = Integer.MIN_VALUE;

		protected Context mContext;
		protected List<D> mItems;

		protected boolean mHasViewAllCard = true;

		public ExploreAdapter(Context context, List<D> items) {
			super();
			mContext = context;
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
			ViewHolder holder = super.onCreateViewHolder(parent, viewType);
			if (holder == null) {
				CitymapsCardView cardView;
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
				cardView.setTag(RecyclerViewType.HERO);
				cardView.setDefaultCardSize(mCardSizeHelper.mHeroDefaultCardSize);
				int actualCardWidth = mCardSizeHelper.mHeroCardRect.width() +
						(mCardSizeHelper.mUseCompatPadding ? 2 * mCardSizeHelper.mCardMaxElevation : 0);
				cardView.setLayoutParams(new RecyclerView.LayoutParams(actualCardWidth, mCardSizeHelper.mHeroCardRect.height()));
				holder = new ExploreViewHolder(cardView);
			}
			return holder;
		}

		@Override
		public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
			boolean animateImages = false; // TODO (mAnimationHelper != null);
			super.onBindViewHolder(holder, position);
			if (holder.itemView instanceof CollectionHeroCardView) {
				((CollectionHeroCardView) holder.itemView).setData((SearchResultCollection) mItems.get(position));
			} else if (holder.itemView instanceof PlaceHeroCardView) {
				((PlaceHeroCardView) holder.itemView).setData((SearchResultPlace) mItems.get(position));
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
		public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			CitymapsCardView cardView;
			if (viewType == VIEW_TYPE_VIEW_ALL) {
				cardView = new ViewAllCardView(mContext);
			} else {
				cardView = new CollectionCardView(mContext);
			}
			cardView.setTag(RecyclerViewType.FEATURED_COLLECTIONS);
			mCardSizeHelper.updateCardLayoutParams(cardView);
			return new ExploreViewHolder(cardView);
		}

		@Override
		public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
			boolean animateImages = false; // TODO (mAnimationHelper != null);
			super.onBindViewHolder(holder, position);
			if (holder.itemView instanceof CollectionCardView) {
				((CollectionCardView) holder.itemView).setData(mItems.get(position));
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
		public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			CitymapsCardView cardView;
			if (viewType == VIEW_TYPE_VIEW_ALL) {
				cardView = new ViewAllCardView(mContext);
			} else {
				cardView = new UserCardView(mContext);
			}
			cardView.setTag(RecyclerViewType.FEATURED_MAPPERS);
			mCardSizeHelper.updateCardLayoutParams(cardView);
			return new ExploreViewHolder(cardView);
		}

		@Override
		public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
			super.onBindViewHolder(holder, position);
			if (holder.itemView instanceof UserCardView) {
				((UserCardView) holder.itemView).setData(mItems.get(position));
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
		public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			CitymapsCardView cardView;
			if (viewType == VIEW_TYPE_VIEW_ALL) {
				cardView = new ViewAllCardView(mContext);
			} else {
				cardView = new DealCardView(mContext);
			}
			cardView.setTag(RecyclerViewType.FEATURED_DEALS);
			mCardSizeHelper.updateCardLayoutParams(cardView);
			return new ExploreViewHolder(cardView);
		}

		@Override
		public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
			super.onBindViewHolder(holder, position);
			if (holder.itemView instanceof DealCardView) {
				((DealCardView) holder.itemView).setData(mItems.get(position));
			} else if (holder.itemView instanceof ViewAllCardView) {
				((ViewAllCardView) holder.itemView).setData(R.string.card_view_all_featured_deals);
			}
		}
	}

	private class ExploreViewHolder extends ViewHolder {
		public ExploreViewHolder(View itemView) {
			super(itemView);
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
			RecyclerViewType type = (RecyclerViewType) cardView.getTag();
			switch (type) {
				case HERO:
					rect = mFeaturedDealsCardRect;
					break;
				case FEATURED_COLLECTIONS:
					rect = mFeaturedCollectionsCardRect;
					break;
				case FEATURED_MAPPERS:
					rect = mFeaturedMappersCardRect;
					break;
				case FEATURED_DEALS:
					rect = mFeaturedDealsCardRect;
					break;
				default:
					return;
			}
			int perceivedWidth = rect.width();
			int actualCardWidth = perceivedWidth + (mUseCompatPadding ? 2 * mCardMaxElevation : 0);
			cardView.setDefaultCardSize(perceivedWidth);
			cardView.setLayoutParams(new RecyclerView.LayoutParams(actualCardWidth, rect.height()));
		}

		@Override
		public void onSizeChanged(View v, int w, int h, int oldw, int oldh) {
			if (w != oldw) {
				final Rect cardRect;
				RecyclerViewType type = (RecyclerViewType) v.getTag();
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
	 * Helper fragment
	 * ******************************************************************************
	 */

	public static class HelperFragment extends Fragment {
		public static final String FRAGMENT_TAG = HelperFragment.class.getName();

		private static final String ARG_MAP_LOCATION = "mapLocation";
		private static final String ARG_MAP_RADIUS = "mapRadius";
		private static final String ARG_MAP_ZOOM = "mapZoom";

		public static HelperFragment newInstance(Intent intent) {
			HelperFragment fragment = new HelperFragment();
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

	private static enum RecyclerViewType {
		HERO,
		FEATURED_COLLECTIONS,
		FEATURED_MAPPERS,
		FEATURED_DEALS
	}
}
