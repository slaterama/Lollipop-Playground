package com.citymaps.mobile.android.view.cards;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import com.citymaps.mobile.android.R;
import com.citymaps.mobile.android.model.SearchResultPlace;
import com.citymaps.mobile.android.util.IntentUtils;

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
		inflate(context, R.layout.card_hero_place, this);
		super.init(context);
	}

	@Override
	public void onBindView(final SearchResultPlace data, boolean inInitialLayout) {
		super.onBindView(data, inInitialLayout);
	}

	@Override
	public void onClick(View v) {
		getContext().startActivity(IntentUtils.getPlaceIntent(mData.getBusinessId()));
	}
}
