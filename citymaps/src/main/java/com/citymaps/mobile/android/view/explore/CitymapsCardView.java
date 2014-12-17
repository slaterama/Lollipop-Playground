package com.citymaps.mobile.android.view.explore;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import com.android.volley.toolbox.ImageLoader;
import com.citymaps.mobile.android.R;

import java.util.HashSet;
import java.util.Set;

public abstract class CitymapsCardView<D> extends CardView {

	private boolean mLoadComplete = false;

	private D mData;

	private OnLoadCompleteListener mOnLoadCompleteListener;

	protected Set<ImageLoader.ImageContainer> mImageContainers;

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

	protected void init(Context context) {
		mImageContainers = new HashSet<ImageLoader.ImageContainer>();
		Resources resources = context.getResources();
		setCardElevation(resources.getDimensionPixelOffset(R.dimen.explore_card_default_elevation));
		setMaxCardElevation(resources.getDimensionPixelOffset(R.dimen.explore_card_max_elevation));
		setUseCompatPadding(resources.getBoolean(R.bool.explore_card_use_compat_padding));
	}

	public D getData() {
		return mData;
	}

	public void setData(D data, boolean animateImages) {
		mData = data;
		onBindData(data, animateImages);
	}

	public void setOnLoadCompleteListener(OnLoadCompleteListener onLoadCompleteListener) {
		mOnLoadCompleteListener = onLoadCompleteListener;
	}

	public boolean isLoadComplete() {
		return mLoadComplete;
	}

	public abstract void setDefaultCardSize(int size);

	public abstract void onBindData(D data, boolean animateImages);

	public static interface OnLoadCompleteListener {
		public void onLoadComplete(CitymapsCardView v);
	}
}
