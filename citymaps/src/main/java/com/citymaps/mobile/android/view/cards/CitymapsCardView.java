package com.citymaps.mobile.android.view.cards;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import com.citymaps.mobile.android.R;

public abstract class CitymapsCardView extends CardView {

	protected int mBaseSize;

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

	public void init(Context context) {
		Resources resources = context.getResources();
		setCardElevation(resources.getDimensionPixelOffset(R.dimen.explore_card_default_elevation));
		setMaxCardElevation(resources.getDimensionPixelOffset(R.dimen.explore_card_max_elevation));
		setUseCompatPadding(resources.getBoolean(R.bool.explore_card_use_compat_padding));

		TypedArray a = context.obtainStyledAttributes(new int[]{android.R.attr.selectableItemBackground, android.R.attr.selectableItemBackgroundBorderless});
		Drawable selectableItemBackgroundBorderLess = a.getDrawable(0);
		a.recycle();

		setForeground(selectableItemBackgroundBorderLess);
		setClickable(true);
	}

	public void setBaseSize(int size) {
		mBaseSize = size;
	}
}
