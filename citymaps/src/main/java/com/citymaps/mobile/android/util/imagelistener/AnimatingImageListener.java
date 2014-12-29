package com.citymaps.mobile.android.util.imagelistener;

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
import com.citymaps.mobile.android.util.LogEx;

public class AnimatingImageListener
		implements ImageLoader.ImageListener {

	protected Context mContext;
	protected ImageView mImageView;

	private boolean mAnimationSet = false;
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
			mAnimationSet = false;
		}
	}

	public Animation getAnimation() {
		if (!mAnimationSet) {
			mAnimation = (mAnimationResId == 0 ? null : AnimationUtils.loadAnimation(mContext, mAnimationResId));
			mAnimationSet = true;
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
		if (!isImmediate) {
			Animation animation = getAnimation();
			if (animation != null) {
				mImageView.setVisibility(View.VISIBLE);
				mImageView.startAnimation(animation);
			}
		}
	}

	@Override
	public void onErrorResponse(VolleyError error) {
		// TODO
		LogEx.e("There was a problem with this request", error);
	}

	protected Drawable getDrawable(Bitmap bitmap) {
		return (bitmap == null ? null : new BitmapDrawable(mContext.getResources(), bitmap));
	}
}