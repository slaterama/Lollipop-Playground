package com.citymaps.mobile.android.view;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.MarginLayoutParamsCompat;
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

		boolean animateOnInitialLoad = (savedInstanceState == null);
		mAdapters = new LinkedHashMap<RecyclerType, Adapter>(length);
		mAdapters.put(RecyclerType.BEST_AROUND, new BestAroundAdapter(animateOnInitialLoad));
		mAdapters.put(RecyclerType.FEATURED_COLLECTIONS, new FeaturedCollectionsAdapter(animateOnInitialLoad));
		mAdapters.put(RecyclerType.FEATURED_MAPPERS, new FeaturedMappersAdapter(animateOnInitialLoad));
		mAdapters.put(RecyclerType.FEATURED_DEALS, new FeaturedDealsAdapter(animateOnInitialLoad));
		for (RecyclerType type : RecyclerType.values()) {
			mRecyclerViews.get(type).setAdapter(mAdapters.get(type));
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
			if (v == mRecyclerViews.get(RecyclerType.BEST_AROUND)) {
				mBestAroundDefaultCardWidth = getCardWidth(v, mDefaultCardsAcross);
				mBestAroundCardWidth = getCardWidth(v, mBestAroundCardsAcross);
				int desiredHeight = BestAroundCollectionCardView.getDesiredHeight(this, mBestAroundDefaultCardWidth);
				v.getLayoutParams().height = desiredHeight + v.getPaddingTop() + v.getPaddingBottom();
				v.setLayoutParams(v.getLayoutParams());
			} else if (v == mRecyclerViews.get(RecyclerType.FEATURED_COLLECTIONS)) {
				mFeaturedCollectionCardWidth = getCardWidth(v, mFeaturedCollectionsCardsAcross);
				int desiredHeight = CollectionCardView.getDesiredHeight(this, mFeaturedCollectionCardWidth);
				v.getLayoutParams().height = desiredHeight + v.getPaddingTop() + v.getPaddingBottom();
				v.setLayoutParams(v.getLayoutParams());
			} else if (v == mRecyclerViews.get(RecyclerType.FEATURED_MAPPERS)) {
				mFeaturedMapperCardWidth = getCardWidth(v, mFeaturedMappersCardsAcross);
				int desiredHeight = UserCardView.getDesiredHeight(this, mFeaturedMapperCardWidth);
				v.getLayoutParams().height = desiredHeight + v.getPaddingTop() + v.getPaddingBottom();
				v.setLayoutParams(v.getLayoutParams());
			} else if (v == mRecyclerViews.get(RecyclerType.FEATURED_DEALS)) {
				mFeaturedDealCardWidth = getCardWidth(v, mFeaturedDealsCardsAcross);
				int desiredHeight = DealCardView.getDesiredHeight(this, mFeaturedDealCardWidth);
				v.getLayoutParams().height = desiredHeight + v.getPaddingTop() + v.getPaddingBottom();
				v.setLayoutParams(v.getLayoutParams());
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
		LogEx.d();
		if (isImmediate) {
			((BestAroundAdapter) mAdapters.get(RecyclerType.BEST_AROUND)).setData(mHelperFragment.mBestAround);
			((FeaturedCollectionsAdapter) mAdapters.get(RecyclerType.FEATURED_COLLECTIONS)).setData(mHelperFragment.mFeaturedCollections);
			((FeaturedMappersAdapter) mAdapters.get(RecyclerType.FEATURED_MAPPERS)).setData(mHelperFragment.mFeaturedMappers);
			((FeaturedDealsAdapter) mAdapters.get(RecyclerType.FEATURED_DEALS)).setData(mHelperFragment.mFeaturedDeals);
		} else {
			new AsyncTask<RecyclerType, RecyclerType, Void>() {
				@Override
				protected Void doInBackground(RecyclerType... params) {
					for (RecyclerType type : params) {
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
				protected void onProgressUpdate(RecyclerType... values) {
					RecyclerType type = values[0];
					switch (type) {
						case BEST_AROUND:
							((BestAroundAdapter) mAdapters.get(RecyclerType.BEST_AROUND)).setData(mHelperFragment.mBestAround);
							break;
						case FEATURED_COLLECTIONS:
							((FeaturedCollectionsAdapter) mAdapters.get(RecyclerType.FEATURED_COLLECTIONS)).setData(mHelperFragment.mFeaturedCollections);
							break;
						case FEATURED_MAPPERS:
							((FeaturedMappersAdapter) mAdapters.get(RecyclerType.FEATURED_MAPPERS)).setData(mHelperFragment.mFeaturedMappers);
							break;
						case FEATURED_DEALS:
							((FeaturedDealsAdapter) mAdapters.get(RecyclerType.FEATURED_DEALS)).setData(mHelperFragment.mFeaturedDeals);
							break;
					}
				}
			}.execute(RecyclerType.BEST_AROUND, RecyclerType.FEATURED_COLLECTIONS, RecyclerType.FEATURED_MAPPERS, RecyclerType.FEATURED_DEALS);
		}
	}

	protected abstract class ExploreAdapter<D> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
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
		public void onBindViewHolder(ViewHolder holder, int position) {
			if (!mHolders.contains(holder)) {
				Animation animation = AnimationUtils.loadAnimation(ExploreActivity.this, R.anim.overshoot_in_right);
				animation.setStartOffset(position * 100);
				holder.itemView.startAnimation(animation);
				mHolders.add(holder);
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
		public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
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
		public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
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
		public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
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

	private static enum RecyclerType {
		BEST_AROUND,
		FEATURED_COLLECTIONS,
		FEATURED_MAPPERS,
		FEATURED_DEALS
	}
}
