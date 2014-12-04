package com.citymaps.mobile.android.view.cards;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import com.citymaps.mobile.android.R;

public class BestAroundCollectionFixedHeightCardView extends CitymapsCardView {

	private TextView mNameView;

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
		mNameView = (TextView) view.findViewById(R.id.card_best_around_collection_fixed_height_name);
	}

	@Override
	protected void onSetBaseCardWidth(int width) {

	}

	public TextView getNameView() {
		return mNameView;
	}
}
