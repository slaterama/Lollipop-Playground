package com.citymaps.mobile.android.view.cards;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import com.citymaps.mobile.android.R;

public class CollectionFixedHeightCardView extends CitymapsCardView {

	private TextView mNameView;

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
		mNameView = (TextView) view.findViewById(R.id.card_collection_fixed_height_name);
	}

	@Override
	public void setBaseSize(int size) {
		super.setBaseSize(size);
	}

	public TextView getNameView() {
		return mNameView;
	}
}