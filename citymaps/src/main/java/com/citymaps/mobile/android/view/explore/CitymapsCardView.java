package com.citymaps.mobile.android.view.explore;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.android.volley.toolbox.ImageLoader;
import com.citymaps.mobile.android.R;
import com.citymaps.mobile.android.app.VolleyManager;
import com.citymaps.mobile.android.app.VolleyManager.CustomImageLoader;
import com.citymaps.mobile.android.util.LogEx;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public abstract class CitymapsCardView<D> extends CardView {

	protected CustomImageLoader mImageLoader;

	protected boolean mBindComplete = false;

	protected D mData;

	protected OnBindCompleteListener mOnBindCompleteListener;

	protected Set<ImageLoader.ImageContainer> mImageContainers;

	protected Set<ImageView> mPendingImageViews;

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
		mImageLoader = VolleyManager.getInstance(context).getImageLoader();
		mImageContainers = new HashSet<ImageLoader.ImageContainer>();
		mPendingImageViews = new HashSet<ImageView>();
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

	public void setOnBindCompleteListener(OnBindCompleteListener onBindCompleteListener) {
		mOnBindCompleteListener = onBindCompleteListener;
	}

	public boolean isBindComplete() {
		return mBindComplete;
	}

	public void notifyBindComplete() {
		mBindComplete = true;
		if (mOnBindCompleteListener != null) {
			mOnBindCompleteListener.onBindComplete(this);
		}
	}

	public abstract void setDefaultCardSize(int size);

	public void onBindData(D data, boolean animateImages) {
		resetView();
	}

	protected void resetView() {
		Iterator<ImageLoader.ImageContainer> iterator = mImageContainers.iterator();
		while (iterator.hasNext()) {
			ImageLoader.ImageContainer container = iterator.next();
			container.cancelRequest();
			iterator.remove();
		}
	}

	public static interface OnBindCompleteListener {
		public void onBindComplete(CitymapsCardView v);
	}
}
