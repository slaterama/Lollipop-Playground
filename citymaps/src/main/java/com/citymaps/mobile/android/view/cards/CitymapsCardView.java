package com.citymaps.mobile.android.view.cards;

import android.animation.AnimatorInflater;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.citymaps.mobile.android.R;
import com.citymaps.mobile.android.util.DrawableUtils;
import com.citymaps.mobile.android.util.LogEx;

public abstract class CitymapsCardView<D> extends CardView {

	protected static Drawable mMiniAvatarNoImageDrawable;

	protected static Drawable getMiniAvatarNoImageDrawable(Resources resources) {
		if (mMiniAvatarNoImageDrawable == null) {
			int size = resources.getDimensionPixelSize(R.dimen.mini_avatar_size);
			Bitmap bitmap = DrawableUtils.createBitmapWithBackgroundColor(resources, resources.getColor(R.color.default_image_background), size, size, R.drawable.ic_no_image_white_24dp);
			mMiniAvatarNoImageDrawable = DrawableUtils.createCircularBitmapDrawable(resources, bitmap);
		}
		return mMiniAvatarNoImageDrawable;
	}

	protected D mData;
	protected int mBaseSize;

	protected ImageView mImageView;
	protected ImageLoader.ImageContainer mImageContainer;

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

	public void init(Context context) {
		Resources resources = context.getResources();
		setCardElevation(resources.getDimensionPixelOffset(R.dimen.explore_card_default_elevation));
		setMaxCardElevation(resources.getDimensionPixelOffset(R.dimen.explore_card_max_elevation));
		setUseCompatPadding(resources.getBoolean(R.bool.explore_card_use_compat_padding));

		int[] attrs;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			attrs = new int[]{android.R.attr.selectableItemBackgroundBorderless};
		} else {
			attrs = new int[]{android.R.attr.selectableItemBackground};
		}
		TypedArray a = context.obtainStyledAttributes(attrs);
		Drawable selectableItemBackgroundBorderLess = a.getDrawable(0);
		a.recycle();

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			setStateListAnimator(AnimatorInflater.loadStateListAnimator(context, R.animator.elevation));
		}

		setForeground(selectableItemBackgroundBorderLess);
		setClickable(true);
	}

	public void setBaseSize(int size) {
		mBaseSize = size;
	}

	public D getData() {
		return mData;
	}

	public void setData(D data) {
		mData = data;
		if (mImageContainer != null) {
			mImageContainer.cancelRequest();
		}
		onBindData(data);
	}

	protected abstract void onBindData(D data);

	protected static class CardImageListener implements ImageLoader.ImageListener,
			Animation.AnimationListener {
		protected Context mContext;
		protected ImageView mImageView;
		protected Animation mAnimation;
		protected ImageState mImageState;

		public CardImageListener(Context context, ImageView imageView) {
			super();
			if (imageView == null) {
				throw new NullPointerException("imageView can not be null");
			}
			mContext = context;
			mImageView = imageView;
			mImageView.clearAnimation();
			mAnimation = AnimationUtils.loadAnimation(context, R.anim.grow_fade_in_center);
			mAnimation.setAnimationListener(this);
			clearImage();
		}

		@Override
		public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
			Bitmap bitmap = response.getBitmap();
			if (bitmap == null) {
				// Bitmap needs to be loaded
				setImageState(ImageState.LOADING);
			} else {
				setImage(bitmap, !isImmediate);
			}
		}

		@Override
		public void onErrorResponse(VolleyError error) {
			clearImage();
		}

		@Override
		public void onAnimationStart(Animation animation) {
			setImageState(ImageState.ANIMATING);
		}

		@Override
		public void onAnimationEnd(Animation animation) {
			setImageState(ImageState.FINISHED);
		}

		@Override
		public void onAnimationRepeat(Animation animation) {

		}

		public void setImage(Bitmap bitmap, boolean animate) {
			mImageView.setImageBitmap(bitmap);
			mImageView.setVisibility(View.VISIBLE);
			if (animate) {
				mImageView.startAnimation(mAnimation);
			} else {
				setImageState(ImageState.FINISHED);
			}
		}

		public void clearImage() {
			setImageState(ImageState.PENDING);
			mImageView.setVisibility(View.INVISIBLE);
			mImageView.setImageDrawable(null);
		}

		public ImageState getImageState() {
			return mImageState;
		}

		public void setImageState(ImageState state) {
			if (state != mImageState) {
				mImageState = state;
				onImageStateChange(state);
			}
		}

		public void onImageStateChange(ImageState state) {

		}

		protected static enum ImageState {
			PENDING,
			LOADING,
			LOADED,
			ANIMATING,
			FINISHED
		}
	}

	protected static class GradientCardImageListener extends CardImageListener {
		public GradientCardImageListener(Context context, ImageView imageView) {
			super(context, imageView);
		}

		@Override
		public void setImage(Bitmap bitmap, boolean animate) {
			Resources resources = mContext.getResources();
			Drawable[] layers = new Drawable[2];
			layers[0] = new BitmapDrawable(resources, bitmap);
			layers[1] = resources.getDrawable(R.drawable.card_image_gradient);
			LayerDrawable drawable = new LayerDrawable(layers);
			mImageView.setImageDrawable(drawable);
			mImageView.setVisibility(View.VISIBLE);
			if (animate) {
				mImageView.startAnimation(mAnimation);
			}
		}
	}
}
