package com.citymaps.mobile.android.view.cards;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.citymaps.mobile.android.R;
import com.citymaps.mobile.android.app.VolleyManager;
import com.citymaps.mobile.android.model.SearchResultCollection;
import com.citymaps.mobile.android.util.DrawableUtils;
import com.citymaps.mobile.android.util.LogEx;

import java.net.HttpURLConnection;

public class CollectionFixedHeightCardView extends CitymapsCardView<SearchResultCollection> {

	public static int getDesiredHeight(Context context, int size) {
		CollectionFixedHeightCardView cardView = new CollectionFixedHeightCardView(context);
		cardView.setBaseSize(size);
		cardView.measure(0, 0);
		return cardView.getMeasuredHeight();
	}

	private ViewGroup mMainContainerView;
	private ImageView mImageView;
	private TextView mNumMarkersView;
	private TextView mNameView;
	private TextView mDescriptionView;
	private ImageView mAvatarView;
	private TextView mAvatarUsernameView;

	public CollectionFixedHeightCardView(Context context) {
		super(context);
	}

	public CollectionFixedHeightCardView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CollectionFixedHeightCardView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	public void init(Context context) {
		super.init(context);
		View view = View.inflate(context, R.layout.card_collection_fixed_height, this);
		mMainContainerView = (ViewGroup) view.findViewById(R.id.card_main_container);
		mImageView = (ImageView) view.findViewById(R.id.card_image);
		mNumMarkersView = (TextView) view.findViewById(R.id.card_marker_count);
		mNameView = (TextView) view.findViewById(R.id.card_name);
		mDescriptionView = (TextView) view.findViewById(R.id.card_description);
		mAvatarView = (ImageView) view.findViewById(R.id.card_avatar);
		mAvatarUsernameView = (TextView) view.findViewById(R.id.card_avatar_username);
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

	public TextView getNumMarkersView() {
		return mNumMarkersView;
	}

	public TextView getNameView() {
		return mNameView;
	}

	public TextView getDescriptionView() {
		return mDescriptionView;
	}

	public ImageView getAvatarView() {
		return mAvatarView;
	}

	public TextView getAvatarUsernameView() {
		return mAvatarUsernameView;
	}

	@Override
	protected void onBindData(SearchResultCollection data) {
		mImageView.setImageResource(R.drawable.forrest_point);
		mNumMarkersView.setText(String.valueOf(data.getNumMarkers()));
		mNameView.setText(data.getName());
		mDescriptionView.setText(data.getDescription());
		mAvatarUsernameView.setText(data.getOwnerUsername());

		String avatarUrl = mData.getOwnerAvatar();
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
	}
}
