package com.citymaps.mobile.android.view.explorenew;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import com.citymaps.mobile.android.R;
import com.citymaps.mobile.android.model.SearchResultPlace;

public class PlaceHeroCardView extends HeroCardView<SearchResultPlace> {

	public PlaceHeroCardView(Context context) {
		super(context);
	}

	public PlaceHeroCardView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PlaceHeroCardView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	protected void init(Context context) {
		View.inflate(context, R.layout.card_hero_place_new, this);
		super.init(context);
	}

	@Override
	public void onBindView(final SearchResultPlace data) {
		super.onBindView(data);
	}
}
