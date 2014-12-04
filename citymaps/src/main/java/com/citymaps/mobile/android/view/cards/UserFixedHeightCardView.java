package com.citymaps.mobile.android.view.cards;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import com.citymaps.mobile.android.R;

public class UserFixedHeightCardView extends CitymapsCardView {

	private TextView mNameView;

	public UserFixedHeightCardView(Context context) {
		super(context);
	}

	public UserFixedHeightCardView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public UserFixedHeightCardView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	public void init(Context context) {
		super.init(context);
		View view = View.inflate(context, R.layout.card_user_fixed_height, this);
		mNameView = (TextView) view.findViewById(R.id.card_user_fixed_height_name);
	}

	@Override
	public void setBaseSize(int size) {
		super.setBaseSize(size);
	}

	public TextView getNameView() {
		return mNameView;
	}
}
