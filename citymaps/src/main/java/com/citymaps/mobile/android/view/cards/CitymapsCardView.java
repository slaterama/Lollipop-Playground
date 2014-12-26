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

public abstract class CitymapsCardView<D> extends CardView {

	protected static final int BITMAP_KEY_MAIN = 0;
	protected static final int BITMAP_KEY_AVATAR = 1;

	protected final Object LOCK = new Object();

	protected D mData;

	protected boolean mInInitialLayout;

	protected CustomImageLoader mImageLoader;

	protected Set<ImageLoader.ImageContainer> mImageContainers;

	protected Map<Integer, Bitmap> mPendingBitmaps;

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

	@SuppressLint("UseSparseArrays")
	protected void init(Context context) {
		mImageLoader = VolleyManager.getInstance(context).getImageLoader();
		mImageContainers = new HashSet<ImageLoader.ImageContainer>();
		mPendingBitmaps = new HashMap<Integer, Bitmap>();
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

	public void setInInitialLayout(boolean inInitialLayout) {
		if (inInitialLayout != mInInitialLayout) {
			mInInitialLayout = inInitialLayout;
			if (!inInitialLayout) {
				synchronized (LOCK) {
					Set<Integer> keySet = mPendingBitmaps.keySet();
					Iterator<Integer> iterator = keySet.iterator();
					while (iterator.hasNext()) {
						int key = iterator.next();
						restorePendingBitmap(key, mPendingBitmaps.get(key));
						iterator.remove();
					}
				}
			}
		}
	}

	protected abstract void restorePendingBitmap(int key, Bitmap bitmap);

	protected class CardViewImageListener extends AnimatingImageListener {
		protected int mKey;

		public CardViewImageListener(Context context, ImageView imageView, int key) {
			super(context, imageView);
			mKey = key;
		}

		@Override
		public void setBitmap(Bitmap bitmap, boolean isImmediate) {
			if (isImmediate || !mInInitialLayout) {
				super.setBitmap(bitmap, isImmediate);
			} else {
				synchronized (LOCK) {
					mPendingBitmaps.put(mKey, bitmap);
				}
			}
		}
	}
}
