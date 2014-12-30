package com.citymaps.mobile.android.view.cards;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
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

import java.util.List;

public abstract class HeroCardView<D extends SearchResult> extends ExploreCardView<D> {

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

	protected MainImageListener mMainImageListener;

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
		mMainImageListener = new MainImageListener(getContext(), mMainImageView);
	}

	@Override
	public void setDefaultCardSize(int size) {
		mInfoContainerView.getLayoutParams().height = size;
		mInfoContainerView.requestLayout();
	}

	@Override
	public void onBindView(final D data, boolean inInitialLayout) {
		mNameView.setText(data.getName());

		final String foursquarePhotoUrl = data.getFoursquarePhotoUrl();
		if (TextUtils.isEmpty(foursquarePhotoUrl)) {
			String foursquareId = data.getFoursquareId();
			if (TextUtils.isEmpty(foursquareId)) {
				// TODO TODO TODO
				if (LogEx.isLoggable(LogEx.WARN)) {
					LogEx.w(String.format("'%s': empty foursquare id", data.getName()));
				}
			} else {
				FoursquarePhotosRequest request = FoursquarePhotosRequest.getFoursquarePhotosRequest(getContext(),
						foursquareId, 1,
						new Response.Listener<List<FoursquarePhoto>>() {
							@Override
							public void onResponse(List<FoursquarePhoto> response) {
								if (response == null || response.size() == 0) {
									// TODO Default image?
								} else {
									FoursquarePhoto photo = response.get(0);
									String foursquarePhotoUrl = photo.getPhotoUrl();
									data.setFoursquarePhotoUrl(foursquarePhotoUrl);

									mImageContainers.add(mImageLoader.get(foursquarePhotoUrl, mMainImageListener));
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
			}
		} else {
			mImageContainers.add(mImageLoader.get(foursquarePhotoUrl, mMainImageListener));
		}
	}

	@Override
	protected void resetView() {
		super.resetView();
		mMainImageView.setImageDrawable(null);
	}

	@Override
	public void onSetPendingBitmap(ImageView imageView, Bitmap bitmap) {
		if (imageView == mMainImageView) {
			mMainImageListener.setBitmap(bitmap, false); //new MainImageListener(getContext(), imageView).setBitmap(bitmap, false);
		} else {
			super.onSetPendingBitmap(imageView, bitmap);
		}
	}

	protected class MainImageListener extends CardViewImageListener {
		Drawable mGradientDrawable;

		public MainImageListener(Context context, ImageView imageView) {
			super(context, imageView);
			mGradientDrawable = mContext.getResources().getDrawable(R.drawable.card_image_gradient);
		}

		@Override
		protected Drawable getDrawable(Bitmap bitmap) {
			return new LayerDrawable(new Drawable[]{
					new BitmapDrawable(mContext.getResources(), bitmap),
					mGradientDrawable});
		}
	}
}
