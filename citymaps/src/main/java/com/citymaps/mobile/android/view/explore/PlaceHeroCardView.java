package com.citymaps.mobile.android.view.explore;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.citymaps.mobile.android.R;
import com.citymaps.mobile.android.model.SearchResultPlace;

public class PlaceHeroCardView extends HeroCardView<SearchResultPlace> {

	private ImageView mMainImageView;
	private TextView mNameView;

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
		mMainImageView = (ImageView) findViewById(R.id.card_image);
		mNameView = (TextView) findViewById(R.id.card_name);
		super.init(context);
	}

	@Override
	public void onBindData(SearchResultPlace data, boolean animateImages) {
		mNameView.setText(data.getName());
	}
}
