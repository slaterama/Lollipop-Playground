package com.citymaps.mobile.android.view.cards;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.citymaps.mobile.android.R;
import com.citymaps.mobile.android.app.VolleyManager;
import com.citymaps.mobile.android.model.FoursquarePhoto;
import com.citymaps.mobile.android.model.SearchResultCollection;
import com.citymaps.mobile.android.model.request.FoursquarePhotosRequest;
import com.citymaps.mobile.android.util.DrawableUtils;
import com.citymaps.mobile.android.util.LogEx;

import java.util.List;

public class BestAroundCollectionFixedHeightCardView extends CitymapsCardView<SearchResultCollection> {

	public static int getDesiredHeight(Context context, int size) {
		BestAroundCollectionFixedHeightCardView cardView = new BestAroundCollectionFixedHeightCardView(context);
		cardView.setBaseSize(size);
		cardView.measure(0, 0);
		return cardView.getMeasuredHeight();
	}

	private ViewGroup mInfoContainerView;
	private TextView mNameView;
	private ImageView mAvatarView;

	public BestAroundCollectionFixedHeightCardView(Context context) {
		super(context);
	}

	public BestAroundCollectionFixedHeightCardView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public BestAroundCollectionFixedHeightCardView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	public void init(Context context) {
		super.init(context);
		View view = View.inflate(context, R.layout.card_best_around_collection_fixed_height, this);
		mImageView = (ImageView) view.findViewById(R.id.card_image);
		mInfoContainerView = (ViewGroup) view.findViewById(R.id.card_best_around_collection_fixed_height_info_container);
		mNameView = (TextView) view.findViewById(R.id.card_name);
		mAvatarView = (ImageView) view.findViewById(R.id.card_avatar);
	}

	@Override
	public void setBaseSize(int size) {
		super.setBaseSize(size);
		mInfoContainerView.getLayoutParams().height = size;
	}

	public ImageView getImageView() {
		return mImageView;
	}

	public ViewGroup getInfoContainerView() {
		return mInfoContainerView;
	}

	public TextView getNameView() {
		return mNameView;
	}

	public ImageView getAvatarView() {
		return mAvatarView;
	}

	@Override
	protected void onBindData(final SearchResultCollection searchResult) {
		mNameView.setText(searchResult.getName());

		// TODO TEMP
		mAvatarView.setImageDrawable(DrawableUtils.createCircularBitmapDrawable(
				getResources(), R.drawable.default_fb_avatar));

		final ImageLoader loader = VolleyManager.getInstance(getContext()).getImageLoader();
		final String coverImageUrl = searchResult.getCoverImageUrl();
		if (TextUtils.isEmpty(coverImageUrl)) {
			final String foursquarePhotoUrl = searchResult.getFoursquarePhotoUrl();
			if (TextUtils.isEmpty(foursquarePhotoUrl)) {
				mImageView.setImageDrawable(null);

				String foursquareId = searchResult.getFoursquareId();
				FoursquarePhotosRequest request = FoursquarePhotosRequest.getFoursquarePhotosRequest(getContext(), foursquareId, 1,
						new Response.Listener<List<FoursquarePhoto>>() {
							@Override
							public void onResponse(List<FoursquarePhoto> response) {
								if (response != null && response.size() > 0) {
									FoursquarePhoto photo = response.get(0);
									String foursquarePhotoUtil = photo.getPhotoUrl();
									searchResult.setFoursquarePhotoUrl(foursquarePhotoUrl);
									mImageContainer = loader.get(foursquarePhotoUtil, new CardImageListener(getContext(), mImageView));
								}
							}
						},
						new Response.ErrorListener() {
							@Override
							public void onErrorResponse(VolleyError error) {
								LogEx.d();
							}
						});
				VolleyManager.getInstance(getContext()).getRequestQueue().add(request);
			} else {
				mImageContainer = loader.get(foursquarePhotoUrl, new CardImageListener(getContext(), mImageView));
			}
		} else {
			mImageContainer = loader.get(coverImageUrl, new CardImageListener(getContext(), mImageView));
		}
	}
}
