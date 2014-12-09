package com.citymaps.mobile.android.view.cards;

import android.animation.AnimatorInflater;
import android.animation.StateListAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import com.citymaps.mobile.android.R;

public abstract class CitymapsCardView<D> extends CardView {

	protected D mData;
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

		int[] attrs;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			attrs = new int[]{android.R.attr.selectableItemBackgroundBorderless};
		} else {
			attrs = new int[]{android.R.attr.selectableItemBackground};
		}
		TypedArray a = context.obtainStyledAttributes(attrs);
		Drawable selectableItemBackgroundBorderLess = a.getDrawable(0);
		a.recycle();

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			setStateListAnimator(AnimatorInflater.loadStateListAnimator(context, R.animator.elevation));
		}

		setForeground(selectableItemBackgroundBorderLess);
		setClickable(true);
	}

	public void setBaseSize(int size) {
		mBaseSize = size;
	}

	public D getData() {
		return mData;
	}

	public void setData(D data) {
		mData = data;
		onBindData(data);
	}

	protected abstract void onBindData(D data);
}
