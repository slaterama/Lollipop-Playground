package com.citymaps.mobile.android.view.explore;

import android.content.Context;
import android.util.AttributeSet;

public abstract class HeroCardView extends CitymapsCardView {

	public HeroCardView(Context context) {
		super(context);
	}

	public HeroCardView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public HeroCardView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	protected void init(Context context) {
		super.init(context);
	}

	@Override
	public void setDefaultCardSize(int size) {

	}
}
