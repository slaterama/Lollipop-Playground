package com.citymaps.mobile.android.view.explore;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import com.citymaps.mobile.android.R;
import com.citymaps.mobile.android.model.SearchResult;

public abstract class HeroCardView extends CitymapsCardView {

	public static int getDesiredHeight(Context context, int defaultCardSize) {
		// Use PlaceHeroCardView since HeroCardView is abstract
		HeroCardView cardView = new PlaceHeroCardView(context);
		cardView.setDefaultCardSize(defaultCardSize);
		cardView.measure(0, 0);
		return cardView.getMeasuredHeight();
	}

	private ViewGroup mInfoContainerView;

	protected SearchResult mData;

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
		mInfoContainerView = (ViewGroup) findViewById(R.id.card_info_container);
	}

	@Override
	public void setDefaultCardSize(int size) {
		mInfoContainerView.getLayoutParams().height = size;
		mInfoContainerView.requestLayout();
	}

	public void bindData(SearchResult data) {
		mData = data;
	}
}
