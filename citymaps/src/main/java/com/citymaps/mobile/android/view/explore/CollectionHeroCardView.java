package com.citymaps.mobile.android.view.explore;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.citymaps.mobile.android.R;
import com.citymaps.mobile.android.model.SearchResult;

public class CollectionHeroCardView extends HeroCardView {

	private ImageView mMainImageView;
	private TextView mNameView;

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
		View.inflate(context, R.layout.card_hero_collection_new, this);
		mMainImageView = (ImageView) findViewById(R.id.card_image);
		mNameView = (TextView) findViewById(R.id.card_name);
		super.init(context);
	}

	@Override
	public void bindData(SearchResult data) {
		super.bindData(data);
		mNameView.setText(data.getName());
	}
}
