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
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.citymaps.mobile.android.R;
import com.citymaps.mobile.android.app.VolleyManager;
import com.citymaps.mobile.android.model.FoursquarePhoto;
import com.citymaps.mobile.android.model.SearchResultCollection;
import com.citymaps.mobile.android.model.request.FoursquarePhotosRequest;
import com.citymaps.mobile.android.util.GraphicsUtils;
import com.citymaps.mobile.android.util.LogEx;

import java.util.List;

public class BestAroundCollectionCardView extends CitymapsCardView<SearchResultCollection> {

	public static int getDesiredHeight(Context context, int size) {
		BestAroundCollectionCardView cardView = new BestAroundCollectionCardView(context);
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

	public BestAroundCollectionCardView(Context context) {
		super(context);
	}

	public BestAroundCollectionCardView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public BestAroundCollectionCardView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	public void init(Context context) {
		super.init(context);
		View view = View.inflate(context, R.layout.card_best_around_collection_fixed_height, this);
		mMainImageView = (ImageView) view.findViewById(R.id.card_image);
		mInfoContainerView = (ViewGroup) view.findViewById(R.id.card_best_around_collection_fixed_height_info_container);
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
	protected void onBindData(final SearchResultCollection searchResult) {
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

		final VolleyManager.CustomImageLoader loader = VolleyManager.getInstance(getContext()).getImageLoader();
		final String coverImageUrl = searchResult.getCoverImageUrl();
		if (TextUtils.isEmpty(coverImageUrl)) {
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
									String foursquarePhotoUtil = photo.getPhotoUrl();
									searchResult.setFoursquarePhotoUrl(foursquarePhotoUrl);
									mMainImageContainer = loader.get(foursquarePhotoUtil,
											new CardImageListener(getContext()).setView(mMainImageView));
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
				mMainImageContainer = loader.get(foursquarePhotoUrl, new CardImageListener(getContext()).setView(mMainImageView));
			}
		} else {
			mMainImageContainer = loader.get(coverImageUrl, new CardImageListener(getContext()).setView(mMainImageView));
		}
	}
}
