package com.citymaps.mobile.android.view.cards;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import com.citymaps.mobile.android.R;
import com.citymaps.mobile.android.model.SearchResultCollection;
import com.citymaps.mobile.android.util.IntentUtils;

public class CollectionHeroCardView extends HeroCardView<SearchResultCollection> {

	public CollectionHeroCardView(Context context) {
		super(context);
	}

	public CollectionHeroCardView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CollectionHeroCardView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	protected void init(Context context) {
		inflate(context, R.layout.card_hero_collection_new, this);
		super.init(context);
	}

	@Override
	public void onBindView(final SearchResultCollection data, boolean inInitialLayout) {
		super.onBindView(data, inInitialLayout);
	}

	@Override
	public void onClick(View v) {
		getContext().startActivity(IntentUtils.getCollectionIntent(mData.getMapId()));
	}
}
