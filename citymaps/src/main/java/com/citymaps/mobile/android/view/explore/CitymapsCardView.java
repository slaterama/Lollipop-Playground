package com.citymaps.mobile.android.view.explore;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;

public abstract class CitymapsCardView extends CardView {

	public CitymapsCardView(Context context) {
		super(context);
		init(context);
	}

	public CitymapsCardView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public CitymapsCardView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	protected void init(Context context) {

	}

	public abstract void setDefaultCardSize(int size);
}
