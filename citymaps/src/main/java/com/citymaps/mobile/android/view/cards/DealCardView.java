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
import com.citymaps.mobile.android.R;
import com.citymaps.mobile.android.app.VolleyManager;
import com.citymaps.mobile.android.model.Deal;
import com.citymaps.mobile.android.model.FoursquarePhoto;
import com.citymaps.mobile.android.model.SearchResultPlace;
import com.citymaps.mobile.android.model.request.FoursquarePhotosRequest;
import com.citymaps.mobile.android.util.IntentUtils;
import com.citymaps.mobile.android.util.LogEx;

import java.util.List;

public class DealCardView extends ExploreCardView<SearchResultPlace> {

	public static int getDesiredHeight(Context context, int defaultCardSize) {
		DealCardView cardView = new DealCardView(context);
		cardView.setDefaultCardSize(defaultCardSize);
		cardView.measure(0, 0);
		return cardView.getMeasuredHeight();
	}

	private ViewGroup mMainContainerView;
	private ViewGroup mInfoContainerView;
	private ImageView mMainImageView;
	private TextView mNameView;
	private ImageView mAvatarView;
	private TextView mPlaceNameView;

	public DealCardView(Context context) {
		super(context);
	}

	public DealCardView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public DealCardView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	protected void init(Context context) {
		inflate(context, R.layout.card_deal_new, this);
		mMainContainerView = (ViewGroup) findViewById(R.id.card_main_container);
		mInfoContainerView = (ViewGroup) findViewById(R.id.card_info_container);
		mMainImageView = (ImageView) findViewById(R.id.card_image);
		mNameView = (TextView) findViewById(R.id.card_name);
		mAvatarView = (ImageView) findViewById(R.id.card_avatar);
		mPlaceNameView = (TextView) findViewById(R.id.card_place_name);
		super.init(context);
	}

	@Override
	public void setDefaultCardSize(int defaultCardSize) {
		mMainContainerView.getLayoutParams().width = defaultCardSize;
		mMainContainerView.requestLayout();
	}

	@Override
	public void onBindView(final SearchResultPlace data, boolean inInitialLayout) {
		mPlaceNameView.setText(data.getName());

		final boolean useAvatarImageAsMainImage;

		Deal[] deals = data.getDeals();
		if (deals != null && deals.length > 0) {
			Deal deal = deals[0];
			mNameView.setText(deal.getLabel());

			final String imageUrl = deal.getThumbnailImage(Deal.GrouponThumbnailSize.XLARGE);
			if (TextUtils.isEmpty(imageUrl)) {
				useAvatarImageAsMainImage = true;
			} else {
				useAvatarImageAsMainImage = false;
				mImageContainers.add(mImageLoader.get(imageUrl,
						new CardViewImageListener(getContext(), mMainImageView)));
			}
		} else {
			useAvatarImageAsMainImage = true;
		}

		String foursquarePhotoUrl = data.getFoursquarePhotoUrl();
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
								String avatarImageUrl;
								if (response == null || response.size() == 0) {
									avatarImageUrl = data.getLogoImageUrl(getContext());
								} else {
									FoursquarePhoto photo = response.get(0);
									avatarImageUrl = photo.getPhotoUrl();
									data.setFoursquarePhotoUrl(avatarImageUrl);
								}
								int size = getResources().getDimensionPixelSize(R.dimen.avatar_size);
								mImageContainers.add(mImageLoader.get(avatarImageUrl,
										new CardViewImageListener(getContext(), mAvatarView), size, size, VolleyManager.OPTION_CIRCLE));
								if (useAvatarImageAsMainImage) {
									mImageContainers.add(mImageLoader.get(avatarImageUrl,
											new CardViewImageListener(getContext(), mMainImageView)));
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
			int size = getResources().getDimensionPixelSize(R.dimen.avatar_size);
			mImageContainers.add(mImageLoader.get(foursquarePhotoUrl,
					new CardViewImageListener(getContext(), mAvatarView), size, size, VolleyManager.OPTION_CIRCLE));
			if (useAvatarImageAsMainImage) {
				mImageContainers.add(mImageLoader.get(foursquarePhotoUrl,
						new CardViewImageListener(getContext(), mMainImageView)));
			}
		}
	}

	@Override
	protected void resetView() {
		super.resetView();
		mMainImageView.setImageDrawable(null);
		mAvatarView.setImageDrawable(null);
	}

	@Override
	public void onClick(View v) {
		getContext().startActivity(IntentUtils.getPlaceIntent(mData.getBusinessId()));
	}
}
