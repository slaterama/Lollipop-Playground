package com.citymaps.mobile.android.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
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
import com.citymaps.mobile.android.view.cards.CardType;
import com.citymaps.mobile.android.widget.RatioCardView;

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

	protected RecyclerView mRecyclerView;

	protected RecyclerView.Adapter mAdapter;

	protected DataFragment mDataFragment;

	protected ActionBar mActionBar;
	protected float mActionBarInitialElevation;

	protected Drawable mActionBarBackgroundDrawable;
	protected View mActionBarView;
	protected TextView mActionBarTitleView;
	protected AlphaForegroundColorSpan mAlphaForegroundColorSpan;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mActionBar = ((ActionBarActivity) activity).getSupportActionBar();
			mActionBarInitialElevation = mActionBar.getElevation();
		} catch (ClassCastException e) {
			throw new ClassCastException(
					"ExploreViewAllFragment must be attached to an Activity of type ActionBarActivity");
		}

		mActionBarView = ActivityUtils.getActionBarView(activity);
		mActionBarTitleView = ActivityUtils.getActionBarTitleView(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mActionBar.setElevation(0.0f);
//		mActionBarView.setAlpha(0.0f);
		mActionBarBackgroundDrawable = getResources().getDrawable(R.drawable.ab_background);
		mActionBarBackgroundDrawable.setAlpha(0);
		mActionBar.setBackgroundDrawable(mActionBarBackgroundDrawable);
		mActionBarTitleView.setAlpha(0.0f);

		Bundle args = getArguments();
		if (args != null) {
			mCardType = (CardType) args.getSerializable(ARG_CARD_TYPE);
			mLocation = args.getParcelable(ARG_MAP_LOCATION);
			mRadius = args.getFloat(ARG_MAP_RADIUS);
			mZoom = args.getInt(ARG_MAP_ZOOM);
		}

		if (savedInstanceState != null) {
			mDataFragment = (DataFragment) getFragmentManager().getFragment(savedInstanceState, STATE_KEY_DATA_FRAGMENT);
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
		mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
		mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(getSpanCount(), StaggeredGridLayoutManager.VERTICAL));
		mRecyclerView.setAdapter(mAdapter = newAdapter(getActivity()));
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
						LogEx.d(String.format("size=%d", size));
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

	protected abstract class ExploreViewAllAdapter<D> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
		protected static final int HEADER_VIEW_TYPE = -1;

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
			return (mItems == null ? 0 : mItems.size()) + 1;
		}

		@Override
		public int getItemViewType(int position) {
			return (position == 0 ? HEADER_VIEW_TYPE : 0);
		}

		@Override
		public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			if (viewType == HEADER_VIEW_TYPE) {
				LinearLayout header = (LinearLayout) mInflater.inflate(R.layout.explore_view_all_header, parent, false);
				StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) header.getLayoutParams();
				layoutParams.setFullSpan(true);
				header.setLayoutParams(layoutParams);
				return new HeaderViewHolder(header);
			} else {
				return null;
			}
		}
	}

	public static class HeaderViewHolder extends ViewHolder {
		public LinearLayout mLinearLayout;
		public RatioCardView mHeaderBackground;
		public TextView mTitleView1;
		public TextView mTitleView2;
		public Button mFilterButton;

		public HeaderViewHolder(LinearLayout itemView) {
			super(itemView);
			mLinearLayout = itemView;
			mHeaderBackground = (RatioCardView) itemView.findViewById(R.id.explore_view_all_header_ratiocardview);
			mTitleView1 = (TextView) itemView.findViewById(R.id.explore_view_all_title1);
			mTitleView2 = (TextView) itemView.findViewById(R.id.explore_view_all_title2);
			mFilterButton = (Button) itemView.findViewById(R.id.explore_view_all_header_filter_button);

			ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) mHeaderBackground.getLayoutParams();
			int paddingLeft = mHeaderBackground.getPaddingLeft();
			int paddingTop = mHeaderBackground.getPaddingTop();
			int paddingRight = mHeaderBackground.getPaddingRight();
			LogEx.d(String.format("lp=%s, paddingLeft=%d, paddingTop=%d, paddingRight=%d", lp, paddingLeft, paddingTop, paddingRight));
			lp.setMargins(-paddingLeft, -paddingTop, -paddingRight, lp.bottomMargin);
		}
	}

	public static class ExploreViewAllViewHolder extends ViewHolder {
		public ExploreViewAllViewHolder(View itemView) {
			super(itemView);
		}
	}

	public static class ExploreViewAllHeroFragment extends ExploreViewAllFragment<SearchResult> {

		@Override
		protected ExploreViewAllAdapter newAdapter(Context context) {
			return new HeroAdapter(context);
		}

		@Override
		protected int getTitleResId() {
			return R.string.explore_best_around_me;
		}

		protected class HeroAdapter extends ExploreViewAllAdapter<SearchResult> {
			public HeroAdapter(Context context) {
				super(context);
			}

			@Override
			protected void init(Context context) {
				super.init(context);
			}

			@Override
			public int getItemViewType(int position) {
				if (position == 0) {
					return HEADER_VIEW_TYPE;
				} else {
					SearchResult searchResult = mItems.get(position);
					return searchResult.getType().value();
				}
			}

			@Override
			public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
				return super.onCreateViewHolder(parent, viewType);
			}

			@Override
			public void onBindViewHolder(ViewHolder holder, int position) {
				if (holder instanceof HeaderViewHolder) {
					HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
					headerViewHolder.mTitleView1.setVisibility(View.GONE);
					headerViewHolder.mTitleView2.setText(R.string.view_all_header_hero_title2);
				}
			}
		}
	}

	public static class ExploreViewAllFeaturedCollectionsFragment extends ExploreViewAllFragment<SearchResultCollection> {

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

		protected class FeaturedCollectionsAdapter extends ExploreViewAllAdapter<SearchResultCollection> {
			public FeaturedCollectionsAdapter(Context context) {
				super(context);
			}

			@Override
			protected void init(Context context) {
				super.init(context);
			}

			@Override
			public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
				return super.onCreateViewHolder(parent, viewType);
			}

			@Override
			public void onBindViewHolder(ViewHolder holder, int position) {
				if (holder instanceof HeaderViewHolder) {
					HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
					headerViewHolder.mTitleView1.setText(R.string.view_all_header_featured_title1);
					headerViewHolder.mTitleView2.setText(R.string.view_all_header_featured_collections_title2);
				}
			}
		}
	}

	public static class ExploreViewAllFeaturedMappersFragment extends ExploreViewAllFragment<User> {

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

		protected class FeaturedMappersAdapter extends ExploreViewAllAdapter<User> {
			public FeaturedMappersAdapter(Context context) {
				super(context);
			}

			@Override
			protected void init(Context context) {
				super.init(context);
			}

			@Override
			public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
				return super.onCreateViewHolder(parent, viewType);
			}

			@Override
			public void onBindViewHolder(ViewHolder holder, int position) {
				if (holder instanceof HeaderViewHolder) {
					HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
					headerViewHolder.mTitleView1.setText(R.string.view_all_header_featured_title1);
					headerViewHolder.mTitleView2.setText(R.string.view_all_header_featured_mappers_title2);
				}
			}
		}
	}

	public static class ExploreViewAllFeaturedDealsFragment extends ExploreViewAllFragment<SearchResultPlace> {

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

		protected class FeaturedDealsAdapter extends ExploreViewAllAdapter<SearchResultPlace> {
			public FeaturedDealsAdapter(Context context) {
				super(context);
			}

			@Override
			protected void init(Context context) {
				super.init(context);
			}

			@Override
			public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
				return super.onCreateViewHolder(parent, viewType);
			}

			@Override
			public void onBindViewHolder(ViewHolder holder, int position) {
				if (holder instanceof HeaderViewHolder) {
					HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
					headerViewHolder.mTitleView1.setText(R.string.view_all_header_featured_title1);
					headerViewHolder.mTitleView2.setText(R.string.view_all_header_featured_deals_title2);
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
			SearchResultsRequest request = SearchResultsRequest.newFeaturedHeroItemsRequest(getActivity(),
					mMapLocation, mMapZoom, mMapRadius, mOffset, DEFAULT_LIMIT,
					new Response.Listener<List<SearchResult>>() {
						@Override
						public void onResponse(List<SearchResult> response) {
							int size = response.size();
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
			SearchResultsRequest request = SearchResultsRequest.newFeaturedCollectionsRequest(getActivity(),
					mMapLocation, mMapZoom, mMapRadius, mOffset, DEFAULT_LIMIT,
					new Response.Listener<List<SearchResult>>() {
						@Override
						public void onResponse(List<SearchResult> response) {
							int size = response.size();
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
			UsersRequest request = UsersRequest.newFeaturedMappersRequest(getActivity(),
					mMapLocation, mMapRadius, mOffset, DEFAULT_LIMIT,
					new Response.Listener<List<User>>() {
						@Override
						public void onResponse(List<User> response) {
							int size = response.size();
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
			SearchResultsRequest request = SearchResultsRequest.newFeaturedDealsRequest(getActivity(),
					mMapLocation, mMapZoom, mMapRadius, mOffset, DEFAULT_LIMIT,
					new Response.Listener<List<SearchResult>>() {
						@Override
						public void onResponse(List<SearchResult> response) {
							int size = response.size();
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
