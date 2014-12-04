package com.citymaps.mobile.android.view.cards;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.citymaps.mobile.android.R;
import com.citymaps.mobile.android.util.LogEx;

public class BestAroundCollectionFixedHeightCardView extends CitymapsCardView {

	public static int getDesiredHeight(Context context, int size) {
		BestAroundCollectionFixedHeightCardView cardView = new BestAroundCollectionFixedHeightCardView(context);

		int height;
		cardView.measure(0, 0);
		height = cardView.getMeasuredHeight();

		LogEx.d(String.format("Before setting height: width=%d, height=%d", cardView.getMeasuredWidth(), height));

		cardView.setBaseSize(size);
		cardView.measure(0, 0);
		height = cardView.getMeasuredHeight();

		LogEx.d(String.format("Before setting height: width=%d, height=%d", cardView.getMeasuredWidth(), height));

		return height;
	}

	private TextView mNameView;
	private ImageView mImageView;
	private ViewGroup mInfoContainerView;

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
		mImageView = (ImageView) view.findViewById(R.id.card_best_around_collection_fixed_height_image);
		mInfoContainerView = (ViewGroup) view.findViewById(R.id.card_best_around_collection_fixed_height_info_container);
		mNameView = (TextView) view.findViewById(R.id.card_best_around_collection_fixed_height_name);
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
}
