package com.citymaps.mobile.android.view.cards;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.android.volley.toolbox.ImageLoader;
import com.citymaps.mobile.android.R;
import com.citymaps.mobile.android.app.VolleyManager;
import com.citymaps.mobile.android.app.VolleyManager.CustomImageLoader;
import com.citymaps.mobile.android.util.imagelistener.AnimatingImageListener;

import java.util.*;

public abstract class ExploreCardView<D> extends CardView {

	protected final Object LOCK = new Object();

	protected D mData;

	protected boolean mInInitialLayout;

	protected CustomImageLoader mImageLoader;

	protected Set<ImageLoader.ImageContainer> mImageContainers;

	protected Map<ImageView, Bitmap> mPendingBitmaps;

	public ExploreCardView(Context context) {
		super(context);
		init(context);
	}

	public ExploreCardView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public ExploreCardView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	@SuppressLint("UseSparseArrays")
	protected void init(Context context) {
		mImageLoader = VolleyManager.getInstance(context).getImageLoader();
		mImageContainers = new HashSet<ImageLoader.ImageContainer>();
		mPendingBitmaps = new HashMap<ImageView, Bitmap>();
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

	public void onSetPendingBitmap(ImageView imageView, Bitmap bitmap) {
		new AnimatingImageListener(getContext(), imageView).setBitmap(bitmap, false);
	}

	protected void resetView() {
		Iterator<ImageLoader.ImageContainer> iterator = mImageContainers.iterator();
		while (iterator.hasNext()) {
			ImageLoader.ImageContainer container = iterator.next();
			container.cancelRequest();
			iterator.remove();
		}
	}

	public void setInInitialLayout(boolean inInitialLayout) {
		if (inInitialLayout != mInInitialLayout) {
			mInInitialLayout = inInitialLayout;
			if (!inInitialLayout) {
				synchronized (LOCK) {
					Set<ImageView> keySet = mPendingBitmaps.keySet();
					Iterator<ImageView> iterator = keySet.iterator();
					while (iterator.hasNext()) {
						ImageView imageView = iterator.next();
						Bitmap bitmap = mPendingBitmaps.get(imageView);
						onSetPendingBitmap(imageView, bitmap);
						iterator.remove();
					}
				}
			}
		}
	}

	protected class CardViewImageListener extends AnimatingImageListener {
		public CardViewImageListener(Context context, ImageView imageView) {
			super(context, imageView);
		}

		@Override
		public void setBitmap(Bitmap bitmap, boolean isImmediate) {
			if (mInInitialLayout && !isImmediate) {
				synchronized (LOCK) {
					mPendingBitmaps.put(mImageView, bitmap);
				}
			} else {
				super.setBitmap(bitmap, isImmediate);
			}
		}
	}
}
