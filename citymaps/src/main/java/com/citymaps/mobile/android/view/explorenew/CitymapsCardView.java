package com.citymaps.mobile.android.view.explorenew;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import com.android.volley.toolbox.ImageLoader;
import com.citymaps.mobile.android.R;
import com.citymaps.mobile.android.app.VolleyManager;
import com.citymaps.mobile.android.app.VolleyManager.CustomImageLoader;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public abstract class CitymapsCardView<D> extends CardView {

	protected D mData;

	protected boolean mInInitialLayout;

	protected CustomImageLoader mImageLoader;

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
		mImageLoader = VolleyManager.getInstance(context).getImageLoader();
		mImageContainers = new HashSet<ImageLoader.ImageContainer>();
		Resources resources = context.getResources();
		setCardElevation(resources.getDimensionPixelOffset(R.dimen.explore_card_default_elevation));
		setMaxCardElevation(resources.getDimensionPixelOffset(R.dimen.explore_card_max_elevation));
		setUseCompatPadding(resources.getBoolean(R.bool.explore_card_use_compat_padding));
	}

	public D getData() {
		return mData;
	}

	public void setData(D data) {
		setData(data, false);
	}

	public void setData(D data, boolean inInitialLayout) {
		mData = data;
		mInInitialLayout = inInitialLayout;
		resetView();
		onBindView(data, inInitialLayout);
	}

	public abstract void setDefaultCardSize(int size);

	public abstract void onBindView(D data, boolean inInitialLayout);

	protected void resetView() {
		Iterator<ImageLoader.ImageContainer> iterator = mImageContainers.iterator();
		while (iterator.hasNext()) {
			ImageLoader.ImageContainer container = iterator.next();
			container.cancelRequest();
			iterator.remove();
		}
	}

	public void setPendingBitmaps() {
	}
}
