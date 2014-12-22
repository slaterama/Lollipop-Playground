package com.citymaps.mobile.android.view.explore;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.citymaps.mobile.android.R;
import com.citymaps.mobile.android.app.VolleyManager;
import com.citymaps.mobile.android.model.Collection;
import com.citymaps.mobile.android.model.FoursquarePhoto;
import com.citymaps.mobile.android.model.SearchResultCollection;
import com.citymaps.mobile.android.model.request.FoursquarePhotosRequest;
import com.citymaps.mobile.android.util.GraphicsUtils;
import com.citymaps.mobile.android.util.LogEx;
import com.citymaps.mobile.android.util.imagelistener.AnimatingImageListener;

import java.util.Arrays;
import java.util.List;

public class CollectionCardView extends CitymapsCardView<SearchResultCollection> {

	public static int getDesiredHeight(Context context, int defaultCardSize) {
		CollectionCardView cardView = new CollectionCardView(context);
		cardView.setDefaultCardSize(defaultCardSize);
		cardView.measure(0, 0);
		return cardView.getMeasuredHeight();
	}

	private ViewGroup mMainContainerView;
	private ViewGroup mInfoContainerView;
	private ImageView mMainImageView;
	private TextView mNumMarkersView;
	private TextView mNameView;
	private TextView mDescriptionView;
	private ImageView mAvatarView;
	private TextView mUsernameView;

	public CollectionCardView(Context context) {
		super(context);
	}

	public CollectionCardView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CollectionCardView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	protected void init(Context context) {
		View.inflate(context, R.layout.card_collection_new, this);
		mMainContainerView = (ViewGroup) findViewById(R.id.card_main_container);
		mInfoContainerView = (ViewGroup) findViewById(R.id.card_info_container);
		mMainImageView = (ImageView) findViewById(R.id.card_image);
		mNumMarkersView = (TextView) findViewById(R.id.card_marker_count);
		mNameView = (TextView) findViewById(R.id.card_name);
		mDescriptionView = (TextView) findViewById(R.id.card_description);
		mAvatarView = (ImageView) findViewById(R.id.card_avatar);
		mUsernameView = (TextView) findViewById(R.id.card_username);
		super.init(context);
	}

	@Override
	public void setDefaultCardSize(int defaultCardSize) {
		mMainContainerView.getLayoutParams().width = defaultCardSize;
		mMainContainerView.requestLayout();
	}

	@Override
	public void onBindData(final SearchResultCollection data, final boolean animateImages) {
		super.onBindData(data, animateImages);

		mNumMarkersView.setText(String.valueOf(data.getNumMarkers()));
		mNameView.setText(data.getName());
		mDescriptionView.setText(data.getDescription());
		mUsernameView.setText(data.getOwnerUsername());

		mPendingImageViews.addAll(Arrays.asList(mMainImageView, mAvatarView));

		final String foursquarePhotoUrl = data.getFoursquarePhotoUrl();
		if (TextUtils.isEmpty(foursquarePhotoUrl)) {
			String foursquareId = data.getFoursquareId();
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
								mImageContainers.add(mImageLoader.get(foursquarePhotoUrl,
										new ImageListener(getContext(), mMainImageView, animateImages)));
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
			mImageContainers.add(mImageLoader.get(foursquarePhotoUrl,
					new ImageListener(getContext(), mMainImageView, animateImages)));
		}

		String avatarUrl = data.getOwnerAvatar();
		if (TextUtils.isEmpty(avatarUrl)) {
			// TODO Cache this like blurred image in UserCardView main image
			mAvatarView.setImageDrawable(GraphicsUtils.createCircularBitmapDrawable(
					getResources(), R.drawable.default_user_avatar_mini));
		} else {
			int size = getResources().getDimensionPixelSize(R.dimen.avatar_size);
			mImageContainers.add(mImageLoader.get(avatarUrl, new ImageListener(getContext(), mAvatarView, animateImages),
					size, size, VolleyManager.OPTION_CIRCLE));
		}
	}

	@Override
	protected void resetView() {
		super.resetView();
		mMainImageView.setImageDrawable(null);
		mAvatarView.setImageDrawable(null);
	}

	protected class ImageListener extends AnimatingImageListener {
		public ImageListener(Context context, ImageView imageView, boolean animateImage) {
			super(context, imageView, animateImage);
		}

		@Override
		public void onImageLoadComplete() {
			super.onImageLoadComplete();
			mPendingImageViews.remove(getImageView());
			if (mPendingImageViews.size() == 0) {
				notifyBindComplete();
			}
		}
	}
}
