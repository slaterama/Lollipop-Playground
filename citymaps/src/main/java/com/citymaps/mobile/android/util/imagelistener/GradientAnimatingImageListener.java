package com.citymaps.mobile.android.util.imagelistener;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.widget.ImageView;
import com.citymaps.mobile.android.R;

public class GradientAnimatingImageListener extends AnimatingImageListener {
	protected static final int DEFAULT_GRADIENT_RES_ID = R.drawable.card_image_gradient;

	protected Drawable mGradientDrawable;

	public GradientAnimatingImageListener(Context context, ImageView imageView, int animationResId, int gradientResId) {
		super(context, imageView, animationResId);
		if (gradientResId != 0) {
			mGradientDrawable = context.getResources().getDrawable(gradientResId);
		}
	}

	public GradientAnimatingImageListener(Context context, ImageView imageView, int animationResId) {
		this(context, imageView, animationResId, DEFAULT_GRADIENT_RES_ID);
	}

	public GradientAnimatingImageListener(Context context, ImageView imageView, boolean animateImage) {
		this(context, imageView, animateImage ? DEFAULT_ANIMATION_RES_ID : 0);
	}

	public GradientAnimatingImageListener(Context context, ImageView imageView) {
		this(context, imageView, DEFAULT_ANIMATION_RES_ID, DEFAULT_GRADIENT_RES_ID);
	}

	@Override
	protected void onSetBitmap(Bitmap bitmap) {
		if (mGradientDrawable == null) {
			super.onSetBitmap(bitmap);
		} else {
			Resources resources = mContext.getResources();
			Drawable[] layers = new Drawable[2];
			layers[0] = new BitmapDrawable(resources, bitmap);
			layers[1] = resources.getDrawable(R.drawable.card_image_gradient);
			LayerDrawable drawable = new LayerDrawable(layers);
			mImageView.setImageDrawable(drawable);
		}
	}
}