package com.citymaps.mobile.android.view.explore;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.citymaps.mobile.android.R;
import com.citymaps.mobile.android.model.SearchResultCollection;

public class CollectionCardView extends CitymapsCardView {

	public static int getDesiredHeight(Context context, int defaultCardSize) {
		CollectionCardView cardView = new CollectionCardView(context);
		cardView.setDefaultCardSize(defaultCardSize);
		cardView.measure(0, 0);
		return cardView.getMeasuredHeight();
	}

	private ViewGroup mMainContainerView;
	private ViewGroup mInfoContainerView;
	private ImageView mMainImageView;
	private TextView mNumMarkersView;
	private TextView mNameView;
	private TextView mDescriptionView;
	private ImageView mAvatarView;
	private TextView mUsernameView;

	private SearchResultCollection mData;

	public CollectionCardView(Context context) {
		super(context);
	}

	public CollectionCardView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CollectionCardView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	protected void inflateView(Context context) {
		View.inflate(context, R.layout.card_collection_new, this);
	}

	@Override
	protected void init(Context context) {
		super.init(context);
		mMainContainerView = (ViewGroup) findViewById(R.id.card_main_container);
		mInfoContainerView = (ViewGroup) findViewById(R.id.card_info_container);
		mMainImageView = (ImageView) findViewById(R.id.card_image);
		mNumMarkersView = (TextView) findViewById(R.id.card_marker_count);
		mNameView = (TextView) findViewById(R.id.card_name);
		mDescriptionView = (TextView) findViewById(R.id.card_description);
		mAvatarView = (ImageView) findViewById(R.id.card_avatar);
		mUsernameView = (TextView) findViewById(R.id.card_username);
	}

	@Override
	public void setDefaultCardSize(int defaultCardSize) {
		mMainContainerView.getLayoutParams().width = defaultCardSize;
		mMainContainerView.requestLayout();
	}

	public void bindData(SearchResultCollection data) {
		mData = data;
		mNumMarkersView.setText(String.valueOf(data.getNumMarkers()));
		mNameView.setText(data.getName());
		mDescriptionView.setText(data.getDescription());
		mUsernameView.setText(data.getOwnerUsername());

		// TODO Image stuff
	}
}
