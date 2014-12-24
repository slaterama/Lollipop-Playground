package com.citymaps.mobile.android.view.explorenew;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.widget.ImageView;
import com.citymaps.mobile.android.R;

public class GradientAnimatingImageListener extends AnimatingImageListener {
	protected int mGradientResId = R.drawable.card_image_gradient;

	protected Drawable mGradientDrawable;

	public GradientAnimatingImageListener(Context context, ImageView imageView) {
		super(context, imageView);
	}

	public void setGradientResId(int gradientResId) {
		if (gradientResId != mGradientResId) {
			mGradientResId = gradientResId;
			mGradientDrawable = null;
		}
	}

	public Drawable getGradientDrawable() {
		if (mGradientDrawable == null && mGradientResId != 0) {
			mGradientDrawable = mContext.getResources().getDrawable(mGradientResId);
		}
		return mGradientDrawable;
	}

	@Override
	protected Drawable getDrawable(Bitmap bitmap) {
		Drawable drawable = null;
		if (bitmap != null) {
			drawable = new BitmapDrawable(mContext.getResources(), bitmap);
			Drawable gradientDrawable = getGradientDrawable();
			if (gradientDrawable != null) {
				drawable = new LayerDrawable(new Drawable[]{drawable, gradientDrawable});
			}
		}
		return drawable;
	}
}