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
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.citymaps.mobile.android.R;
import com.citymaps.mobile.android.util.DrawableUtils;

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

	protected class CardImageListener implements ImageLoader.ImageListener {
		protected ImageView mImageView;

		public CardImageListener(ImageView imageView) {
			super();
			mImageView = imageView;
			mImageView.clearAnimation();
			onNoImage();
		}

		@Override
		public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
			if (mImageView != null && response.getBitmap() != null) {
				mImageView.setImageBitmap(response.getBitmap());
				if (!isImmediate) {
					mImageView.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.grow_fade_in_center));
				}
				mImageView.setVisibility(View.VISIBLE);
			}
		}

		@Override
		public void onErrorResponse(VolleyError error) {
			onNoImage();
		}

		public void onNoImage() {
			if (mImageView != null) {
				mImageView.setVisibility(View.INVISIBLE);
				mImageView.setImageDrawable(null);
			}
		}
	}

	protected class GradientCardImageListener extends CardImageListener {
		public GradientCardImageListener(ImageView imageView) {
			super(imageView);
		}

		@Override
		public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
			if (mImageView != null && response.getBitmap() != null) {
				Drawable[] layers = new Drawable[2];
				layers[0] = new BitmapDrawable(getResources(), response.getBitmap());
				layers[1] = getResources().getDrawable(R.drawable.card_image_gradient);
				LayerDrawable drawable = new LayerDrawable(layers);
				mImageView.setImageDrawable(drawable);
				if (!isImmediate) {
					mImageView.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.grow_fade_in_center));
				}
				mImageView.setVisibility(View.VISIBLE);
			}
		}
	}
}
