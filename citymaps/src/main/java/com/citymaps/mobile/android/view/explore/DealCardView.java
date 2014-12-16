package com.citymaps.mobile.android.view.explore;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.citymaps.mobile.android.R;
import com.citymaps.mobile.android.model.Deal;
import com.citymaps.mobile.android.model.SearchResultPlace;

public class DealCardView extends CitymapsCardView {

	public static int getDesiredHeight(Context context, int defaultCardSize) {
		DealCardView cardView = new DealCardView(context);
		cardView.setDefaultCardSize(defaultCardSize);
		cardView.measure(0, 0);
		return cardView.getMeasuredHeight();
	}

	private ViewGroup mMainContainerView;
	private ViewGroup mInfoContainerView;
	private ImageView mMainImageView;
	private TextView mNameView;
	private ImageView mAvatarView;
	private TextView mPlaceNameView;

	private SearchResultPlace mData;

	public DealCardView(Context context) {
		super(context);
	}

	public DealCardView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public DealCardView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	protected void inflateView(Context context) {
		View.inflate(context, R.layout.card_deal_new, this);
	}

	@Override
	protected void init(Context context) {
		super.init(context);
		mMainContainerView = (ViewGroup) findViewById(R.id.card_main_container);
		mInfoContainerView = (ViewGroup) findViewById(R.id.card_info_container);
		mMainImageView = (ImageView) findViewById(R.id.card_image);
		mNameView = (TextView) findViewById(R.id.card_name);
		mAvatarView = (ImageView) findViewById(R.id.card_avatar);
		mPlaceNameView = (TextView) findViewById(R.id.card_place_name);
	}

	@Override
	public void setDefaultCardSize(int defaultCardSize) {
		mMainContainerView.getLayoutParams().width = defaultCardSize;
		mMainContainerView.requestLayout();
	}

	public void bindData(SearchResultPlace data) {
		mData = data;
		mPlaceNameView.setText(data.getName());
		Deal[] deals = data.getDeals();
		if (deals != null && deals.length > 0) {
			bindData(deals[0]);
		}
	}

	protected void bindData(Deal deal) {
		mNameView.setText(deal.getLabel());

		// TODO Image stuff
	}
}
