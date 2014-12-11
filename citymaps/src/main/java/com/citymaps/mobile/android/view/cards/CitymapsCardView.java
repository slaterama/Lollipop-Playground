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
import com.citymaps.mobile.android.util.GraphicsUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class CitymapsCardView<D> extends CardView {

	protected static final String KEY_MAIN_IMAGE = "mainImage";

	protected static Drawable mMiniAvatarNoImageDrawable;

	protected static Drawable getMiniAvatarNoImageDrawable(Resources resources) {
		if (mMiniAvatarNoImageDrawable == null) {
			int size = resources.getDimensionPixelSize(R.dimen.mini_avatar_size);
			Bitmap bitmap = GraphicsUtils.createBitmapWithBackgroundColor(resources, resources.getColor(R.color.default_image_background), size, size, R.drawable.ic_no_image_white_24dp);
			mMiniAvatarNoImageDrawable = GraphicsUtils.createCircularBitmapDrawable(resources, bitmap);
		}
		return mMiniAvatarNoImageDrawable;
	}

	protected D mData;
	protected int mBaseSize;

	protected ImageView mImageView;
	protected Map<String, ImageLoader.ImageContainer> mImageContainerMap;

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
		mImageContainerMap = new HashMap<String, ImageLoader.ImageContainer>();

		Resources resources = context.getResources();
		setCardElevation(resources.getDimensionPixelOffset(R.dimen.explore_card_default_elevation));
		setMaxCardElevation(resources.getDimensionPixelOffset(R.dimen.explore_card_max_elevation));
		setUseCompatPadding(resources.getBoolean(R.bool.explore_card_use_compat_padding));

		int[] attrs;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			attrs = new int[]{android.R.attr.selectableItemBackgroundBorderless};
			setStateListAnimator(AnimatorInflater.loadStateListAnimator(context, R.animator.elevation));
		} else {
			attrs = new int[]{android.R.attr.selectableItemBackground};
		}
		TypedArray a = context.obtainStyledAttributes(attrs);
		Drawable selectableItemBackgroundBorderLess = a.getDrawable(0);
		a.recycle();

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
		Collection<ImageLoader.ImageContainer> containers = mImageContainerMap.values();
		for (ImageLoader.ImageContainer container : containers) {
			container.cancelRequest();
		}
		onBindData(data);
	}

	protected abstract void onBindData(D data);

	protected static class CardImageListener implements ImageLoader.ImageListener, Animation.AnimationListener {
		protected Context mContext;
		protected ImageView mImageView;
		protected Animation mAnimation;

		public CardImageListener(Context context) {
			super();
			mContext = context;
			mAnimation = onCreateAnimation();
			mAnimation.setAnimationListener(this);
		}

		public CardImageListener setView(ImageView imageView) {
			if (imageView == null) {
				throw new NullPointerException("imageView can not be null");
			}
			mImageView = imageView;
			clearImage();
			return this;
		}

		@Override
		public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
			Bitmap bitmap = response.getBitmap();
			if (bitmap == null) {
				// Bitmap needs to be loaded
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
		}

		@Override
		public void onAnimationEnd(Animation animation) {
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
		}

		protected Animation onCreateAnimation() {
			return AnimationUtils.loadAnimation(mContext, R.anim.grow_fade_in_center);
		}

		protected void setImage(Bitmap bitmap, boolean animate) {
			if (mImageView == null) {
				throw new IllegalStateException("You must call setView() before calling this method");
			}
			mImageView.setImageBitmap(bitmap);
			mImageView.setVisibility(View.VISIBLE);
			if (animate && mAnimation != null) {
				startAnimation();
			}
		}

		protected void clearImage() {
			if (mImageView == null) {
				throw new IllegalStateException("You must call setView() before calling this method");
			}
			mImageView.clearAnimation();
			mImageView.setVisibility(View.INVISIBLE);
			mImageView.setImageDrawable(null);
		}

		protected void startAnimation() {
			if (mImageView == null) {
				throw new IllegalStateException("You must call setView() before calling this method");
			}
			if (mAnimation != null) {
				mImageView.startAnimation(mAnimation);
			}
		}
	}

	protected static class GradientCardImageListener extends CardImageListener {
		public GradientCardImageListener(Context context) {
			super(context);
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
