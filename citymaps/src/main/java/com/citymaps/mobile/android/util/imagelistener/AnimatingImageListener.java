package com.citymaps.mobile.android.util.imagelistener;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.citymaps.mobile.android.R;

public class AnimatingImageListener
		implements ImageLoader.ImageListener, Animation.AnimationListener {

	protected static final int DEFAULT_ANIMATION_RES_ID = R.anim.grow_fade_in_center;

	protected Context mContext;
	protected ImageView mImageView;
	protected Animation mAnimation;

	public AnimatingImageListener(Context context, ImageView imageView, int animationResId) {
		mContext = context;
		mImageView = imageView;
		if (animationResId != 0) {
			mAnimation = AnimationUtils.loadAnimation(context, animationResId);
			mAnimation.setAnimationListener(this);
		}
	}

	public AnimatingImageListener(Context context, ImageView imageView, boolean animateImage) {
		this(context, imageView, animateImage ? DEFAULT_ANIMATION_RES_ID : 0);
	}

	public AnimatingImageListener(Context context, ImageView imageView) {
		this(context, imageView, DEFAULT_ANIMATION_RES_ID);
	}

	public ImageView getImageView() {
		return mImageView;
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

	@Override
	public void onErrorResponse(VolleyError error) {

	}

	@Override
	public void onAnimationStart(Animation animation) {

	}

	@Override
	public void onAnimationEnd(Animation animation) {
		onImageLoadComplete();
	}

	@Override
	public void onAnimationRepeat(Animation animation) {

	}

	public void setBitmap(Bitmap bitmap, boolean isImmediate) {
		onSetBitmap(bitmap);
		mImageView.setVisibility(View.VISIBLE);
		if (!isImmediate && mAnimation != null) {
			mImageView.startAnimation(mAnimation);
		} else {
			onImageLoadComplete();
		}
	}

	protected void onSetBitmap(Bitmap bitmap) {
		mImageView.setImageBitmap(bitmap);
	}

	public void onImageLoadComplete() {

	}
}