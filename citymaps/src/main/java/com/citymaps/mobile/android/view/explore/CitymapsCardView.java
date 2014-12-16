package com.citymaps.mobile.android.view.explore;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import com.citymaps.mobile.android.R;

public abstract class CitymapsCardView extends CardView {

	public CitymapsCardView(Context context) {
		super(context);
		preInit(context);
		inflateView(context);
		init(context);
	}

	public CitymapsCardView(Context context, AttributeSet attrs) {
		super(context, attrs);
		preInit(context);
		inflateView(context);
		init(context);
	}

	public CitymapsCardView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		preInit(context);
		inflateView(context);
		init(context);
	}

	protected void preInit(Context context) {
		Resources resources = context.getResources();
		setCardElevation(resources.getDimensionPixelOffset(R.dimen.explore_card_default_elevation));
		setMaxCardElevation(resources.getDimensionPixelOffset(R.dimen.explore_card_max_elevation));
		setUseCompatPadding(resources.getBoolean(R.bool.explore_card_use_compat_padding));
	}

	protected abstract void inflateView(Context context);

	protected void init(Context context) {
	}

	public abstract void setDefaultCardSize(int size);
}
