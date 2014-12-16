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
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.citymaps.mobile.android.R;
import com.citymaps.mobile.android.app.VolleyManager;
import com.citymaps.mobile.android.model.FoursquarePhoto;
import com.citymaps.mobile.android.model.SearchResultPlace;
import com.citymaps.mobile.android.model.request.FoursquarePhotosRequest;
import com.citymaps.mobile.android.util.GraphicsUtils;
import com.citymaps.mobile.android.util.LogEx;

import java.util.List;

public class BestAroundPlaceCardView extends CitymapsCardView<SearchResultPlace> {

	public static int getDesiredHeight(Context context, int size) {
		BestAroundPlaceCardView cardView = new BestAroundPlaceCardView(context);
		cardView.setBaseSize(size);
		cardView.measure(0, 0);
		return cardView.getMeasuredHeight();
	}

	private ViewGroup mInfoContainerView;
	private TextView mNameView;
	private ImageView mMainImageView;
	private ImageView mAvatarView;

	private ImageContainer mMainImageContainer;
	private ImageContainer mAvatarImageContainer;

	public BestAroundPlaceCardView(Context context) {
		super(context);
	}

	public BestAroundPlaceCardView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public BestAroundPlaceCardView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	public void init(Context context) {
		super.init(context);
		View view = View.inflate(context, R.layout.card_hero_place, this);
		mMainImageView = (ImageView) view.findViewById(R.id.card_image);
		mInfoContainerView = (ViewGroup) view.findViewById(R.id.card_best_around_place_fixed_height_info_container);
		mNameView = (TextView) view.findViewById(R.id.card_name);
		mAvatarView = (ImageView) view.findViewById(R.id.card_avatar);
	}

	@Override
	public void setBaseSize(int size) {
		super.setBaseSize(size);
		mInfoContainerView.getLayoutParams().height = size;
	}

	public ImageView getMainImageView() {
		return mMainImageView;
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
	protected void onBindData(final SearchResultPlace searchResult) {
		if (mMainImageContainer != null) {
			mMainImageContainer.cancelRequest();
		}

		if (mAvatarImageContainer != null) {
			mAvatarImageContainer.cancelRequest();
		}

		mNameView.setText(searchResult.getName());

		// TODO TEMP
		mAvatarView.setImageDrawable(GraphicsUtils.createCircularBitmapDrawable(
				getResources(), R.drawable.default_fb_avatar));

		final ImageLoader loader = VolleyManager.getInstance(getContext()).getImageLoader();
		final String foursquarePhotoUrl = searchResult.getFoursquarePhotoUrl();
		if (TextUtils.isEmpty(foursquarePhotoUrl)) {
			mMainImageView.setImageDrawable(null);

			String foursquareId = searchResult.getFoursquareId();
			FoursquarePhotosRequest request = FoursquarePhotosRequest.getFoursquarePhotosRequest(getContext(), foursquareId, 1,
					new Response.Listener<List<FoursquarePhoto>>() {
						@Override
						public void onResponse(List<FoursquarePhoto> response) {
							if (response != null && response.size() > 0) {
								FoursquarePhoto photo = response.get(0);
								String foursquarePhotoUrl = photo.getPhotoUrl();
								searchResult.setFoursquarePhotoUrl(foursquarePhotoUrl);
								mMainImageContainer = loader.get(foursquarePhotoUrl,
										new GradientCardImageListener(getContext()).setView(mMainImageView));
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
			mMainImageContainer = loader.get(foursquarePhotoUrl,
					new GradientCardImageListener(getContext()).setView(mMainImageView));
		}
	}
}
