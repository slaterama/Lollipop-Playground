package com.citymaps.mobile.android.view.explorenew;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.citymaps.mobile.android.R;

public class AnimatingImageListener
		implements ImageLoader.ImageListener {

	protected Context mContext;
	protected ImageView mImageView;

	protected int mAnimationResId = R.anim.grow_fade_in_center;
	private Animation mAnimation;

	public AnimatingImageListener(Context context, ImageView imageView) {
		mContext = context;
		mImageView = imageView;
	}

	public ImageView getImageView() {
		return mImageView;
	}

	public void setAnimationResId(int animationResId) {
		if (animationResId != mAnimationResId) {
			mAnimationResId = animationResId;
			mAnimation = null;
		}
	}

	public Animation getAnimation() {
		if (mAnimation == null && mAnimationResId != 0) {
			mAnimation = AnimationUtils.loadAnimation(mContext, mAnimationResId);
		}
		return mAnimation;
	}

	@Override
	public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
		Bitmap bitmap = response.getBitmap();
		if (bitmap == null) {
			mImageView.setVisibility(View.INVISIBLE);
		} else {
			setBitmap(bitmap, isImmediate);
		}
	}

	public void setBitmap(Bitmap bitmap, boolean isImmediate) {
		mImageView.setImageDrawable(getDrawable(bitmap));
		mImageView.setVisibility(View.VISIBLE);
		Animation animation = getAnimation();
		if (!isImmediate && animation != null) {
			mImageView.startAnimation(animation);
		}
	}

	@Override
	public void onErrorResponse(VolleyError error) {
		// TODO
	}

	protected Drawable getDrawable(Bitmap bitmap) {
		return (bitmap == null ? null : new BitmapDrawable(mContext.getResources(), bitmap));
	}
}