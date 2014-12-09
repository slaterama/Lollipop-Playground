package com.citymaps.mobile.android.view.cards;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.citymaps.mobile.android.R;
import com.citymaps.mobile.android.model.SearchResult;
import com.citymaps.mobile.android.util.DrawableUtils;

public class BestAroundCollectionFixedHeightCardView extends CitymapsCardView<SearchResult> {

	public static int getDesiredHeight(Context context, int size) {
		BestAroundCollectionFixedHeightCardView cardView = new BestAroundCollectionFixedHeightCardView(context);
		cardView.setBaseSize(size);
		cardView.measure(0, 0);
		return cardView.getMeasuredHeight();
	}

	private ImageView mImageView;
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
	protected void onBindData(SearchResult data) {
		mImageView.setImageResource(R.drawable.forrest_point);
		mNameView.setText(data.getName());

		// TODO TEMP
		mAvatarView.setImageDrawable(DrawableUtils.createCircularBitmapDrawable(
				getResources(), R.drawable.default_fb_avatar));


	}
}
