package com.citymaps.mobile.android.view;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MarginLayoutParamsCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.citymaps.mobile.android.R;
import com.citymaps.mobile.android.app.VolleyManager;
import com.citymaps.mobile.android.map.ParcelableLonLat;
import com.citymaps.mobile.android.model.SearchResult;
import com.citymaps.mobile.android.model.SearchResultCollection;
import com.citymaps.mobile.android.model.SearchResultPlace;
import com.citymaps.mobile.android.model.User;
import com.citymaps.mobile.android.model.request.SearchResultsRequest;
import com.citymaps.mobile.android.model.request.UsersRequest;
import com.citymaps.mobile.android.util.*;
import com.citymaps.mobile.android.view.cards.*;
import com.citymaps.mobile.android.widget.OnSizeChangedListener;
import com.citymaps.mobile.android.widget.RatioCardView;
import com.citymaps.mobile.android.widget.RecyclerViewEx;

import java.util.ArrayList;
import java.util.List;

public abstract class ExploreViewAllFragment<D> extends Fragment {

	private static final String STATE_KEY_DATA_FRAGMENT = "dataFragment";

	private static final int REQUEST_CODE_DATA = 0;

	private static final int RESULT_ERROR = Activity.RESULT_FIRST_USER;

	private static final String ARG_CARD_TYPE = "cardType";
	private static final String ARG_MAP_LOCATION = "mapLocation";
	private static final String ARG_MAP_RADIUS = "mapRadius";
	private static final String ARG_MAP_ZOOM = "mapZoom";

	public static ExploreViewAllFragment newInstance(Intent intent) {
		final ExploreViewAllFragment fragment;
		CardType cardType = IntentUtils.getCardType(intent);
		switch (cardType) {
			case FEATURED_COLLECTIONS:
				fragment = new ExploreViewAllFeaturedCollectionsFragment();
				break;
			case FEATURED_MAPPERS:
				fragment = new ExploreViewAllFeaturedMappersFragment();
				break;
			case FEATURED_DEALS:
				fragment = new ExploreViewAllFeaturedDealsFragment();
				break;
			case HERO:
			default:
				fragment = new ExploreViewAllHeroFragment();
		}
		Bundle args = new Bundle(4);
		args.putSerializable(ARG_CARD_TYPE, cardType);
		args.putParcelable(ARG_MAP_LOCATION, IntentUtils.getMapLocation(intent));
		args.putFloat(ARG_MAP_RADIUS, IntentUtils.getMapRadius(intent, MapUtils.DEFAULT_SEARCH_RADIUS));
		args.putInt(ARG_MAP_ZOOM, IntentUtils.getMapZoom(intent, MapUtils.DEFAULT_SEARCH_ZOOM));
		fragment.setArguments(args);
		return fragment;
	}

	protected CardType mCardType;
	protected ParcelableLonLat mLocation;
	protected float mRadius;
	protected int mZoom;

	protected RecyclerViewEx mRecyclerView;
	protected RatioCardView mHeaderView;
	protected TextView mHeaderTitleView1;
	protected TextView mHeaderTitleView2;

	protected ExploreViewAllAdapter mAdapter;

	protected DataFragment mDataFragment;

	protected CardSizeHelper mCardSizeHelper;

	protected ActionBarActivity mActivity;
	protected ActionBar mActionBar;
	protected View mActionBarView;
	protected float mActionBarInitialElevation;
	protected Drawable mActionBarBackgroundDrawable;
	protected TextView mActionBarTitleView;

	protected int mActionBarHeight;
	protected int mHeaderHeight;

	protected boolean mActionBarShowing = false;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mActivity = ((ActionBarActivity) activity);
		} catch (ClassCastException e) {
			throw new ClassCastException(
					"ExploreViewAllFragment must be attached to an Activity of type ActionBarActivity");
		}

		mCardSizeHelper = new CardSizeHelper(activity, false);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle args = getArguments();
		if (args != null) {
			mCardType = (CardType) args.getSerializable(ARG_CARD_TYPE);
			mLocation = args.getParcelable(ARG_MAP_LOCATION);
			mRadius = args.getFloat(ARG_MAP_RADIUS);
			mZoom = args.getInt(ARG_MAP_ZOOM);
		}

		mAdapter = newAdapter(mActivity);

		if (savedInstanceState != null) {
			mDataFragment = (DataFragment) getFragmentManager().getFragment(savedInstanceState, STATE_KEY_DATA_FRAGMENT);
			onDataReturned(-1);
		}
		if (mDataFragment == null) {
			mDataFragment = DataFragment.newInstance(args);
			getFragmentManager()
					.beginTransaction()
					.add(mDataFragment, DataFragment.FRAGMENT_TAG)
					.commit();
		}
		mDataFragment.setTargetFragment(this, REQUEST_CODE_DATA);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_explore_view_all, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mRecyclerView = (RecyclerViewEx) view.findViewById(R.id.recyclerview);
		mRecyclerView.setTag(mCardType);

		StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(getSpanCount(), StaggeredGridLayoutManager.VERTICAL);
		layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
		mRecyclerView.setLayoutManager(layoutManager);
		mRecyclerView.setAdapter(mAdapter);
		mRecyclerView.setOnSizeChangedListener(mCardSizeHelper);
		mRecyclerView.setOnScrollListener(mOnScrollListener);

		mHeaderView = (RatioCardView) view.findViewById(R.id.explore_view_all_header_card);
		mHeaderTitleView1 = (TextView) view.findViewById(R.id.explore_view_all_title1);
		mHeaderTitleView2 = (TextView) view.findViewById(R.id.explore_view_all_title2);

		mHeaderView.setOnSizeChangedListener(new OnSizeChangedListener() {
			@Override
			public void onSizeChanged(View v, int w, int h, int oldw, int oldh) {
				mHeaderHeight = mHeaderView.getPerceivedHeight();
			}
		});

		ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) mHeaderView.getLayoutParams();
		lp.setMargins(-mHeaderView.getPaddingLeft(), -mHeaderView.getPaddingTop(),
				-mHeaderView.getPaddingRight(), lp.bottomMargin);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// Set up for transparent ActionBar logic
		mActionBar = mActivity.getSupportActionBar();
		mActionBarHeight = ActivityUtils.getActionBarHeight(mActivity);
		mActionBarInitialElevation = mActionBar.getElevation();
		mActionBar.setElevation(0.0f);
		mActionBarBackgroundDrawable = getResources().getDrawable(R.drawable.ab_background);
		mActionBarBackgroundDrawable.setAlpha(0);
		mActionBar.setBackgroundDrawable(mActionBarBackgroundDrawable);
		mActionBarView = ActivityUtils.getActionBarView(mActivity);
		mActionBarTitleView = ActivityUtils.getActionBarTitleView(mActivity);
		mActionBarTitleView.setAlpha(0.0f);
		mActionBarShowing = false;

		if (mHeaderView != null) {
			mHeaderView.setCardElevation(mActionBarInitialElevation);
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		getFragmentManager().putFragment(outState, STATE_KEY_DATA_FRAGMENT, mDataFragment);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case REQUEST_CODE_DATA:
				switch (resultCode) {
					case RESULT_ERROR:
						LogEx.d("An error occurred");
						break;
					case Activity.RESULT_OK:
					default:
						int size = IntentUtils.getSize(data, 0);
						onDataReturned(size);
				}
				break;
			default:
				super.onActivityResult(requestCode, resultCode, data);
		}
	}

	protected abstract int getTitleResId();

	protected int getSpanCount() {
		return (int) ResourcesUtils.getFloat(getResources(), R.dimen.explore_default_cards_across, 2.0f);
	}

	protected abstract ExploreViewAllAdapter newAdapter(Context context);

	protected abstract void onDataReturned(int size);

	private HeaderViewHolder getHeaderViewHolder() {
		if (mRecyclerView.getChildCount() > 0) {
			View child = mRecyclerView.getChildAt(0);
			Object tag = child.getTag();
			if (tag instanceof HeaderViewHolder) {
				return (HeaderViewHolder) tag;
			}
		}
		return null;
	}

	protected void showActionBar() {
		if (!mActionBarShowing) {
			ObjectAnimator animator = ObjectAnimator.ofFloat(mActionBarTitleView, "alpha", 0.0f, 1.0f);
			animator.setDuration(getResources().getInteger(android.R.integer.config_shortAnimTime));
			animator.start();
			mActionBarShowing = true;
		}
	}

	protected void hideActionBar() {
		if (mActionBarShowing) {
			ObjectAnimator animator = ObjectAnimator.ofFloat(mActionBarTitleView, "alpha", 1.0f, 0.0f);
			animator.setDuration(getResources().getInteger(android.R.integer.config_shortAnimTime));
			animator.start();
			mActionBarShowing = false;
		}
	}

	protected RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener() {
		@Override
		public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
			super.onScrolled(recyclerView, dx, dy);
			int target = mActionBarHeight - mHeaderHeight;
			View child = recyclerView.getChildAt(0);
			if (child != null) {
				int position = recyclerView.getChildPosition(child);
				if (position == 0) {
					int top = child.getTop();
					float y = -mHeaderView.getPaddingTop() + Math.max(top, target);
					mHeaderView.setY(y);
					if (y < target) {
						if (!mActionBarShowing) {
							showActionBar();
						}
					} else {
						if (mActionBarShowing) {
							hideActionBar();
						}
					}
				}
			}
		}
	};

	protected abstract class ExploreViewAllAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
		protected static final int HEADER_VIEW_TYPE = -1;

		protected static final int HEADER_VIEW_COUNT = 1;

		protected Context mContext;
		protected LayoutInflater mInflater;
		protected List<D> mItems;

		public ExploreViewAllAdapter(Context context) {
			super();
			mContext = context;
			mInflater = LayoutInflater.from(context);
			init(context);
		}

		protected void init(Context context) {
		}

		public List<D> getItems() {
			return mItems;
		}

		public void setItems(List<D> items) {
			mItems = items;
			notifyDataSetChanged();
		}

		@Override
		public int getItemCount() {
			return (mItems == null ? 0 : mItems.size()) + HEADER_VIEW_COUNT;
		}

		@Override
		public int getItemViewType(int position) {
			return (position == 0 ? HEADER_VIEW_TYPE : 0);
		}

		@Override
		public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			if (viewType == HEADER_VIEW_TYPE) {
				View headerView = mInflater.inflate(R.layout.explore_view_all_header, parent, false);
				StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) headerView.getLayoutParams();
				layoutParams.setFullSpan(true);
				headerView.setLayoutParams(layoutParams);
				HeaderViewHolder holder = new HeaderViewHolder(headerView, mActionBarInitialElevation);
				headerView.setTag(holder);
				return holder;
			} else {
				return null;
			}
		}

		@Override
		public void onBindViewHolder(ViewHolder holder, int position) {
			if (!(holder instanceof HeaderViewHolder)) {
				// Configure the correct margin depending on position
				if (holder.itemView.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
					ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) holder.itemView.getLayoutParams();
					int margin = (int) (mCardSizeHelper.getCardPerceivedMargin() -
							2 * (holder.itemView instanceof CardView ? ((CardView) holder.itemView).getMaxCardElevation() : 0));
					MarginLayoutParamsCompat.setMarginEnd(mlp, position < getItemCount() - 1 ? margin : 0);
					holder.itemView.setLayoutParams(mlp);
				}

				if (position >= getItemCount() - 1) {
					if (mDataFragment.mHasMore) {
						mDataFragment.retrieveNext();
					}
				}
			}
		}
	}

	public static class HeaderViewHolder extends ViewHolder {
		public LinearLayout mLinearLayout;
		public RatioCardView mHeaderBackground;
		public Button mFilterButton;

		public HeaderViewHolder(View itemView, float elevation) {
			super(itemView);
			mLinearLayout = (LinearLayout) itemView.findViewById(R.id.explore_view_all_header);
			mHeaderBackground = (RatioCardView) itemView.findViewById(R.id.explore_view_all_header_card);
			mHeaderBackground.setCardElevation(elevation);
			mFilterButton = (Button) itemView.findViewById(R.id.explore_view_all_header_filter_button);
			ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) mHeaderBackground.getLayoutParams();
			lp.setMargins(-mHeaderBackground.getPaddingLeft(), -mHeaderBackground.getPaddingTop(),
					-mHeaderBackground.getPaddingRight(), lp.bottomMargin);
		}
	}

	public static class ExploreViewAllViewHolder extends ViewHolder {
		public ExploreViewAllViewHolder(View itemView) {
			super(itemView);
		}
	}

	public static class ExploreViewAllHeroFragment extends ExploreViewAllFragment<SearchResult> {
		@Override
		public void onViewCreated(View view, Bundle savedInstanceState) {
			super.onViewCreated(view, savedInstanceState);
			mHeaderTitleView1.setVisibility(View.GONE);
			mHeaderTitleView2.setText(R.string.view_all_header_hero_title2);
		}

		@Override
		protected ExploreViewAllAdapter newAdapter(Context context) {
			return new HeroAdapter(context);
		}

		@Override
		protected int getTitleResId() {
			return R.string.explore_best_around_me;
		}

		@Override
		protected void onDataReturned(int size) {
			mAdapter.setItems(((HeroDataFragment) mDataFragment).mData);
		}

		protected class HeroAdapter extends ExploreViewAllAdapter {
			public HeroAdapter(Context context) {
				super(context);
			}

			@Override
			protected void init(Context context) {
				super.init(context);
			}

			@Override
			public int getItemViewType(int position) {
				// TODO
				if (position == 0) {
					return HEADER_VIEW_TYPE;
				} else {
					SearchResult searchResult = mItems.get(position - HEADER_VIEW_COUNT);
					return searchResult.getType().value();
				}
			}

			@Override
			public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
				// TODO
				return super.onCreateViewHolder(parent, viewType);
			}

			@Override
			public void onBindViewHolder(ViewHolder holder, int position) {
				// TODO
				super.onBindViewHolder(holder, position);
			}
		}
	}

	public static class ExploreViewAllFeaturedCollectionsFragment extends ExploreViewAllFragment<SearchResultCollection> {
		@Override
		public void onViewCreated(View view, Bundle savedInstanceState) {
			super.onViewCreated(view, savedInstanceState);
			mHeaderTitleView1.setText(R.string.view_all_header_featured_title1);
			mHeaderTitleView2.setText(R.string.view_all_header_featured_collections_title2);
		}

		@Override
		protected int getSpanCount() {
			return (int) ResourcesUtils.getFloat(getResources(), R.dimen.explore_featured_collections_cards_across, 2.0f);
		}

		@Override
		protected int getTitleResId() {
			return R.string.explore_featured_collections;
		}

		@Override
		protected ExploreViewAllAdapter newAdapter(Context context) {
			return new FeaturedCollectionsAdapter(context);
		}

		@Override
		protected void onDataReturned(int size) {
			mAdapter.setItems(((FeaturedCollectionsDataFragment) mDataFragment).mData);
		}

		protected class FeaturedCollectionsAdapter extends ExploreViewAllAdapter {
			public FeaturedCollectionsAdapter(Context context) {
				super(context);
			}

			@Override
			protected void init(Context context) {
				super.init(context);
			}

			@Override
			public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
				ViewHolder holder;
				if (viewType == HEADER_VIEW_TYPE) {
					holder = super.onCreateViewHolder(parent, viewType);
				} else {
					ExploreCardView cardView = new CollectionCardView(mContext);
					cardView.setVariableHeight(true);
					cardView.setTag(CardType.FEATURED_COLLECTIONS);
					holder = new ExploreViewAllViewHolder(cardView);
					mCardSizeHelper.updateCardLayoutParams(cardView);
				}
				return holder;
			}

			@Override
			public void onBindViewHolder(ViewHolder holder, int position) {
				super.onBindViewHolder(holder, position);
				if (holder instanceof HeaderViewHolder) {

				} else {
					((CollectionCardView) holder.itemView).setData(mItems.get(position - HEADER_VIEW_COUNT));
				}
			}
		}
	}

	public static class ExploreViewAllFeaturedMappersFragment extends ExploreViewAllFragment<User> {
		@Override
		public void onViewCreated(View view, Bundle savedInstanceState) {
			super.onViewCreated(view, savedInstanceState);
			mHeaderTitleView1.setText(R.string.view_all_header_featured_title1);
			mHeaderTitleView2.setText(R.string.view_all_header_featured_mappers_title2);
		}

		@Override
		protected int getSpanCount() {
			return (int) ResourcesUtils.getFloat(getResources(), R.dimen.explore_featured_mappers_cards_across, 2.0f);
		}

		@Override
		protected int getTitleResId() {
			return R.string.explore_featured_mappers;
		}

		@Override
		protected ExploreViewAllAdapter newAdapter(Context context) {
			return new FeaturedMappersAdapter(context);
		}

		@Override
		protected void onDataReturned(int size) {
			mAdapter.setItems(((FeaturedMappersDataFragment) mDataFragment).mData);
		}

		protected class FeaturedMappersAdapter extends ExploreViewAllAdapter {
			public FeaturedMappersAdapter(Context context) {
				super(context);
			}

			@Override
			protected void init(Context context) {
				super.init(context);
			}

			@Override
			public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
				ViewHolder holder;
				if (viewType == HEADER_VIEW_TYPE) {
					holder = super.onCreateViewHolder(parent, viewType);
				} else {
					ExploreCardView cardView = new UserCardView(mContext);
					cardView.setVariableHeight(true);
					cardView.setTag(CardType.FEATURED_MAPPERS);
					holder = new ExploreViewAllViewHolder(cardView);
					mCardSizeHelper.updateCardLayoutParams(cardView);
				}
				return holder;
			}

			@Override
			public void onBindViewHolder(ViewHolder holder, int position) {
				super.onBindViewHolder(holder, position);
				if (holder instanceof HeaderViewHolder) {

				} else {
					((UserCardView) holder.itemView).setData(mItems.get(position - HEADER_VIEW_COUNT));
				}
			}
		}
	}

	public static class ExploreViewAllFeaturedDealsFragment extends ExploreViewAllFragment<SearchResultPlace> {
		@Override
		public void onViewCreated(View view, Bundle savedInstanceState) {
			super.onViewCreated(view, savedInstanceState);
			mHeaderTitleView1.setText(R.string.view_all_header_featured_title1);
			mHeaderTitleView2.setText(R.string.view_all_header_featured_deals_title2);
		}

		@Override
		protected int getSpanCount() {
			return (int) ResourcesUtils.getFloat(getResources(), R.dimen.explore_featured_deals_cards_across, 2.0f);
		}

		@Override
		protected int getTitleResId() {
			return R.string.explore_featured_deals;
		}

		@Override
		protected ExploreViewAllAdapter newAdapter(Context context) {
			return new FeaturedDealsAdapter(context);
		}

		@Override
		protected void onDataReturned(int size) {
			mAdapter.setItems(((FeaturedDealsDataFragment) mDataFragment).mData);
		}

		protected class FeaturedDealsAdapter extends ExploreViewAllAdapter {
			public FeaturedDealsAdapter(Context context) {
				super(context);
			}

			@Override
			protected void init(Context context) {
				super.init(context);
			}

			@Override
			public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
				ViewHolder holder;
				if (viewType == HEADER_VIEW_TYPE) {
					holder = super.onCreateViewHolder(parent, viewType);
				} else {
					ExploreCardView cardView = new DealCardView(mContext);
					cardView.setVariableHeight(true);
					cardView.setTag(CardType.FEATURED_DEALS);
					holder = new ExploreViewAllViewHolder(cardView);
					mCardSizeHelper.updateCardLayoutParams(cardView);
				}
				return holder;
			}

			@Override
			public void onBindViewHolder(ViewHolder holder, int position) {
				super.onBindViewHolder(holder, position);
				if (holder instanceof HeaderViewHolder) {

				} else {
					((DealCardView) holder.itemView).setData(mItems.get(position - HEADER_VIEW_COUNT));
				}
			}
		}
	}

	/*
	 * ******************************************************************************
	 * Data fragment
	 * ******************************************************************************
	 */

	public abstract static class DataFragment<D> extends Fragment {
		public static final String FRAGMENT_TAG = DataFragment.class.getName();

		public static final int DEFAULT_LIMIT = 20;

		public static DataFragment newInstance(Bundle args) {
			final DataFragment fragment;
			CardType cardType = (CardType) args.getSerializable(ARG_CARD_TYPE);
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
			fragment.setArguments(args);
			return fragment;
		}

		//protected ExploreViewAllActivity mActivity;

		protected ParcelableLonLat mMapLocation;
		protected float mMapRadius;
		protected int mMapZoom;

		protected int mOffset = 0;
		protected List<D> mData;
		protected boolean mHasMore;

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			/*
			try {
				mActivity = (ExploreViewAllActivity) activity;
			} catch (ClassCastException e) {
				throw new ClassCastException(
						"ExploreViewAllActivity.DataFragment must be attached to ExploreViewAllActivity");
			}
			*/
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

			mData = new ArrayList<D>();
			retrieveNext();
		}

		public abstract void retrieveNext();
	}

	/*
	 * ******************************************************************************
	 * Concrete data fragment classes
	 * ******************************************************************************
	 */

	public static class HeroDataFragment extends DataFragment<SearchResult> {
		@Override
		public void retrieveNext() {
			final int limit = DEFAULT_LIMIT;
			SearchResultsRequest request = SearchResultsRequest.newFeaturedHeroItemsRequest(getActivity(),
					mMapLocation, mMapZoom, mMapRadius, mOffset, limit,
					new Response.Listener<List<SearchResult>>() {
						@Override
						public void onResponse(List<SearchResult> response) {
							int size = response.size();
							mHasMore = (size == limit);
							mOffset += size;
							mData.addAll(response);
							Intent data = new Intent();
							IntentUtils.putSize(data, size);
							getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, data);
						}
					},
					new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							if (LogEx.isLoggable(LogEx.ERROR)) {
								LogEx.e(error.getMessage(), error);
							}
							getTargetFragment().onActivityResult(getTargetRequestCode(), RESULT_ERROR, null);
						}
					});
			VolleyManager.getInstance(getActivity()).getRequestQueue().add(request);
		}
	}

	public static class FeaturedCollectionsDataFragment extends DataFragment<SearchResultCollection> {
		@Override
		public void retrieveNext() {
			final int limit = DEFAULT_LIMIT;
			SearchResultsRequest request = SearchResultsRequest.newFeaturedCollectionsRequest(getActivity(),
					mMapLocation, mMapZoom, mMapRadius, mOffset, limit,
					new Response.Listener<List<SearchResult>>() {
						@Override
						public void onResponse(List<SearchResult> response) {
							int size = response.size();
							mHasMore = (size == limit);
							mOffset += size;
							for (SearchResult searchResult : response) {
								if (searchResult instanceof SearchResultCollection) {
									mData.add((SearchResultCollection) searchResult);
								}
							}
							Intent data = new Intent();
							IntentUtils.putSize(data, size);
							getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, data);
						}
					},
					new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							if (LogEx.isLoggable(LogEx.ERROR)) {
								LogEx.e(error.getMessage(), error);
							}
							getTargetFragment().onActivityResult(getTargetRequestCode(), RESULT_ERROR, null);
						}
					});
			VolleyManager.getInstance(getActivity()).getRequestQueue().add(request);
		}
	}

	public static class FeaturedMappersDataFragment extends DataFragment<User> {
		@Override
		public void retrieveNext() {
			final int limit = DEFAULT_LIMIT;
			UsersRequest request = UsersRequest.newFeaturedMappersRequest(getActivity(),
					mMapLocation, mMapRadius, mOffset, limit,
					new Response.Listener<List<User>>() {
						@Override
						public void onResponse(List<User> response) {
							int size = response.size();
							mHasMore = (size == limit);
							mOffset += size;
							mData.addAll(response);
							Intent data = new Intent();
							IntentUtils.putSize(data, size);
							getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, data);
						}
					},
					new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							if (LogEx.isLoggable(LogEx.ERROR)) {
								LogEx.e(error.getMessage(), error);
							}
							getTargetFragment().onActivityResult(getTargetRequestCode(), RESULT_ERROR, null);
						}
					});
			VolleyManager.getInstance(getActivity()).getRequestQueue().add(request);
		}
	}

	public static class FeaturedDealsDataFragment extends DataFragment<SearchResultPlace> {
		@Override
		public void retrieveNext() {
			final int limit = DEFAULT_LIMIT;
			SearchResultsRequest request = SearchResultsRequest.newFeaturedDealsRequest(getActivity(),
					mMapLocation, mMapZoom, mMapRadius, mOffset, limit,
					new Response.Listener<List<SearchResult>>() {
						@Override
						public void onResponse(List<SearchResult> response) {
							int size = response.size();
							mHasMore = (size == limit);
							mOffset += size;
							for (SearchResult searchResult : response) {
								if (searchResult instanceof SearchResultPlace) {
									mData.add((SearchResultPlace) searchResult);
								}
							}
							Intent data = new Intent();
							IntentUtils.putSize(data, size);
							getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, data);
						}
					},
					new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							if (LogEx.isLoggable(LogEx.ERROR)) {
								LogEx.e(error.getMessage(), error);
							}
							getTargetFragment().onActivityResult(getTargetRequestCode(), RESULT_ERROR, null);
						}
					});
			VolleyManager.getInstance(getActivity()).getRequestQueue().add(request);
		}
	}
}
