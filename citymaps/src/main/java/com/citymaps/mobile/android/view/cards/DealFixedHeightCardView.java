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
import com.citymaps.mobile.android.model.Deal;
import com.citymaps.mobile.android.model.FoursquarePhoto;
import com.citymaps.mobile.android.model.SearchResultPlace;
import com.citymaps.mobile.android.model.request.FoursquarePhotosRequest;
import com.citymaps.mobile.android.util.DrawableUtils;
import com.citymaps.mobile.android.util.LogEx;

import java.util.List;

public class DealFixedHeightCardView extends CitymapsCardView<SearchResultPlace> {

	public static int getDesiredHeight(Context context, int size) {
		DealFixedHeightCardView cardView = new DealFixedHeightCardView(context);
		cardView.setBaseSize(size);
		cardView.measure(0, 0);
		return cardView.getMeasuredHeight();
	}

	private ViewGroup mMainContainerView;
	private TextView mNameView;
//	private TextView mDescriptionView;
	private ImageView mAvatarView;
	private TextView mUsernameView;

	public DealFixedHeightCardView(Context context) {
		super(context);
	}

	public DealFixedHeightCardView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public DealFixedHeightCardView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	public void init(Context context) {
		super.init(context);
		View view = View.inflate(context, R.layout.card_deal_fixed_height, this);
		mMainContainerView = (ViewGroup) view.findViewById(R.id.card_main_container);
		mImageView = (ImageView) view.findViewById(R.id.card_image);
		mNameView = (TextView) view.findViewById(R.id.card_name);
//		mDescriptionView = (TextView) view.findViewById(R.id.card_description);
		mAvatarView = (ImageView) view.findViewById(R.id.card_avatar);
		mUsernameView = (TextView) view.findViewById(R.id.card_username);
	}

	@Override
	public void setBaseSize(int size) {
		super.setBaseSize(size);
		mMainContainerView.getLayoutParams().width = size;
	}

	public ViewGroup getMainContainerView() {
		return mMainContainerView;
	}

	public ImageView getImageView() {
		return mImageView;
	}

	public TextView getNameView() {
		return mNameView;
	}

	/*
	public TextView getDescriptionView() {
		return mDescriptionView;
	}
	*/

	public ImageView getAvatarView() {
		return mAvatarView;
	}

	public TextView getUsernameView() {
		return mUsernameView;
	}

	@Override
	protected void onBindData(final SearchResultPlace searchResult) {
		mUsernameView.setText(searchResult.getName());

		Deal[] deals = searchResult.getDeals();
		if (deals == null || deals.length < 1) {
			return;
		}

		Deal deal = deals[0];
		mNameView.setText(deal.getLabel());

		final ImageLoader loader = VolleyManager.getInstance(getContext()).getImageLoader();
		final String imageUrl = deal.getThumbnailImage(Deal.GrouponThumbnailSize.XLARGE);
		if (TextUtils.isEmpty(imageUrl)) {
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
									mImageContainer = loader.get(foursquarePhotoUtil, new CardImageListener(mImageView));
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
				mImageContainer = loader.get(foursquarePhotoUrl, new CardImageListener(mImageView));
			}
		} else {
			mImageContainer = loader.get(imageUrl, new CardImageListener(mImageView));
		}

		// TODO TEMP
		mAvatarView.setImageDrawable(DrawableUtils.createCircularBitmapDrawable(
				getResources(), R.drawable.default_fb_avatar));

		/*
		String avatarUrl = searchResult.getOwnerAvatar();
		if (TextUtils.isEmpty(avatarUrl)) {
			mAvatarView.setImageDrawable(DrawableUtils.createCircularBitmapDrawable(
					getResources(), R.drawable.default_user_avatar_mini));
		} else {
			final ImageLoader loader = VolleyManager.getInstance(getContext()).getImageLoader();
			loader.get(avatarUrl,
					new ImageLoader.ImageListener() {
						@Override
						public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
							Bitmap bitmap = response.getBitmap();
							if (bitmap == null) {
								mAvatarView.setImageDrawable(null);
							} else {
								mAvatarView.setImageDrawable(DrawableUtils.createCircularBitmapDrawable(
										getResources(), bitmap));
							}
						}

						@Override
						public void onErrorResponse(VolleyError error) {
							mAvatarView.setImageDrawable(getMiniAvatarNoImageDrawable(getResources()));
						}
					});
		}
		*/
	}
}
