package com.citymaps.mobile.android.view.cards;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import com.citymaps.mobile.android.R;
import com.citymaps.mobile.android.util.IntentUtils;
import com.citymaps.mobile.android.util.MapUtils;
import com.citymaps.mobile.android.view.ExploreViewAllActivity;

public class ViewAllCardView extends ExploreCardView<CardType> {

	private TextView mLabelView;

	private Intent mIntent;

	private CardType mCardType;

	public ViewAllCardView(Context context) {
		super(context);
	}

	public ViewAllCardView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ViewAllCardView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	protected void init(Context context) {
		inflate(context, R.layout.card_view_all, this);
		setCardBackgroundColor(getResources().getColor(R.color.color_primary));
		mLabelView = (TextView) findViewById(R.id.card_view_all_label);
		super.init(context);
	}

	public Intent getIntent() {
		return mIntent;
	}

	public void setIntent(Intent intent) {
		mIntent = intent;
	}

	public CardType getCardType() {
		return mCardType;
	}

	public void setCardType(CardType cardType) {
		mCardType = cardType;
	}

	@Override
	public void setDefaultCardSize(int size) {
		// No action
	}

	@Override
	public void onBindView(CardType data, boolean inInitialLayout) {
		mCardType = data;
		final int resId;
		switch (data) {
			case FEATURED_COLLECTIONS:
				resId = R.string.card_view_all_featured_collections;
				break;
			case FEATURED_MAPPERS:
				resId = R.string.card_view_all_featured_mappers;
				break;
			case FEATURED_DEALS:
				resId = R.string.card_view_all_featured_deals;
				break;
			case HERO:
			default:
				resId = R.string.card_view_all_hero;
		}
		mLabelView.setText(resId);
	}

	@Override
	public void onClick(View v) {
		Context context = getContext();
		Intent intent = new Intent(context, ExploreViewAllActivity.class);
		IntentUtils.putCardType(intent, mCardType);
		IntentUtils.putMapLocation(intent, IntentUtils.getMapLocation(mIntent));
		IntentUtils.putMapRadius(intent, IntentUtils.getMapRadius(mIntent, MapUtils.DEFAULT_SEARCH_RADIUS));
		IntentUtils.putMapZoom(intent, IntentUtils.getMapZoom(mIntent, MapUtils.DEFAULT_SEARCH_ZOOM));
		context.startActivity(intent);
	}
}
