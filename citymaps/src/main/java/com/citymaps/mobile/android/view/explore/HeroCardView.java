package com.citymaps.mobile.android.view.explore;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.citymaps.mobile.android.R;
import com.citymaps.mobile.android.app.VolleyManager;
import com.citymaps.mobile.android.model.FoursquarePhoto;
import com.citymaps.mobile.android.model.SearchResult;
import com.citymaps.mobile.android.model.request.FoursquarePhotosRequest;
import com.citymaps.mobile.android.util.LogEx;
import com.citymaps.mobile.android.util.imagelistener.GradientAnimatingImageListener;

import java.util.*;

public abstract class HeroCardView<D extends SearchResult> extends CitymapsCardView<D> {

	public static int getDesiredHeight(Context context, int defaultCardSize) {
		// Use PlaceHeroCardView since HeroCardView is abstract
		HeroCardView cardView = new PlaceHeroCardView(context);
		cardView.setDefaultCardSize(defaultCardSize);
		cardView.measure(0, 0);
		return cardView.getMeasuredHeight();
	}

	protected ImageView mMainImageView;

	protected ViewGroup mInfoContainerView;

	protected TextView mNameView;

	protected Set<ImageView> mPendingImageViews;

	public HeroCardView(Context context) {
		super(context);
	}

	public HeroCardView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public HeroCardView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	protected void init(Context context) {
		super.init(context);
		mMainImageView = (ImageView) findViewById(R.id.card_image);
		mInfoContainerView = (ViewGroup) findViewById(R.id.card_info_container);
		mNameView = (TextView) findViewById(R.id.card_name);
		mPendingImageViews = new HashSet<ImageView>();
	}

	@Override
	public void setDefaultCardSize(int size) {
		mInfoContainerView.getLayoutParams().height = size;
		mInfoContainerView.requestLayout();
	}

	@Override
	public void onBindData(final D data, boolean animateImages) {
		Iterator<ImageLoader.ImageContainer> iterator = mImageContainers.iterator();
		while (iterator.hasNext()) {
			ImageLoader.ImageContainer container = iterator.next();
			container.cancelRequest();
			iterator.remove();
		}

		mPendingImageViews.addAll(Arrays.asList(mMainImageView));

		mNameView.setText(data.getName());

		final ImageLoader loader = VolleyManager.getInstance(getContext()).getImageLoader();
		final String foursquarePhotoUrl = data.getFoursquarePhotoUrl();
		if (TextUtils.isEmpty(foursquarePhotoUrl)) {
			String foursquareId = data.getFoursquareId();
			FoursquarePhotosRequest request = FoursquarePhotosRequest.getFoursquarePhotosRequest(getContext(),
					foursquareId, 1,
					new Response.Listener<List<FoursquarePhoto>>() {
						@Override
						public void onResponse(List<FoursquarePhoto> response) {
							if (response == null || response.size() == 0) {
								// TODO Default image/
							} else {
								FoursquarePhoto photo = response.get(0);
								String foursquarePhotoUrl = photo.getPhotoUrl();
								data.setFoursquarePhotoUrl(foursquarePhotoUrl);
								mImageContainers.add(loader.get(foursquarePhotoUrl,
										new MainImageListener(getContext(), mMainImageView)));
							}
						}
					},
					new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							if (LogEx.isLoggable(LogEx.ERROR)) {
								LogEx.e(error.getMessage(), error);
							}
						}
					});
			VolleyManager.getInstance(getContext()).getRequestQueue().add(request);
		} else {
			mImageContainers.add(loader.get(foursquarePhotoUrl,
					new MainImageListener(getContext(), mMainImageView)));
		}
	}

	protected void onLoadComplete(ImageView imageView) {
		int sizeBeforeRemove = mPendingImageViews.size();
		mPendingImageViews.remove(imageView);
		if (mOnLoadCompleteListener != null && sizeBeforeRemove == 1 && mPendingImageViews.size() == 0) {
			mOnLoadCompleteListener.onLoadComplete(this);
		}
	}

	protected class MainImageListener extends GradientAnimatingImageListener {
		public MainImageListener(Context context, ImageView imageView) {
			super(context, imageView);
		}

		@Override
		public void onLoadComplete() {
			HeroCardView.this.onLoadComplete(getImageView());
		}
	}
}
