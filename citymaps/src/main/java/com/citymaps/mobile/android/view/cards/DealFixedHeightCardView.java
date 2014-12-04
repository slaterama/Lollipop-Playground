package com.citymaps.mobile.android.view.cards;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import com.citymaps.mobile.android.R;

public class DealFixedHeightCardView extends CitymapsCardView {

	private TextView mNameView;

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
		mNameView = (TextView) view.findViewById(R.id.card_deal_fixed_height_name);
	}

	@Override
	protected void onSetBaseCardWidth(int width) {

	}

	public TextView getNameView() {
		return mNameView;
	}
}
