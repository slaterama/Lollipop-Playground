package com.citymaps.mobile.android.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.util.SparseArray;
import com.citymaps.mobile.android.R;
import com.citymaps.mobile.android.util.LogEx;

public class FloatingActionButton extends CardView {

	protected Type mType;

	public FloatingActionButton(Context context) {
		super(context);
		initialize(context, null, 0);
	}

	public FloatingActionButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize(context, attrs, R.attr.floatingActionButtonStyle);
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public FloatingActionButton(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initialize(context, attrs, defStyleAttr);
	}

	protected void initialize(Context context, AttributeSet attrs, int defStyleAttr) {
		Resources resources = getResources();
		final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FloatingActionButton, defStyleAttr, 0);
		Type type = Type.fromValue(a.getInt(R.styleable.FloatingActionButton_fabType, 0), Type.NORMAL);
		setType(type);
		Drawable foreground = a.getDrawable(R.styleable.FloatingActionButton_android_foreground);
		setForeground(foreground);
		int color = a.getColor(R.styleable.FloatingActionButton_fabBackgroundColor,
				resources.getColor(R.color.fab_light_background));
		setFabBackgroundColor(color);
		float elevation = a.getDimension(R.styleable.FloatingActionButton_fabElevation,
				resources.getDimensionPixelOffset(R.dimen.fab_default_elevation));
		setFabElevation(elevation);
		float maxElevation = a.getDimension(R.styleable.FloatingActionButton_fabMaxElevation,
				resources.getDimensionPixelOffset(R.dimen.fab_default_elevation));
		setMaxFabElevation(maxElevation);
		boolean useCompatPadding = a.getBoolean(R.styleable.FloatingActionButton_fabUseCompatPadding, false);
		setUseCompatPadding(useCompatPadding);
		int defaultPadding = a.getDimensionPixelSize(R.styleable.FloatingActionButton_contentPadding, 0);
		int paddingLeft = a.getDimensionPixelSize(R.styleable.FloatingActionButton_contentPaddingLeft, defaultPadding);
		int paddingTop = a.getDimensionPixelSize(R.styleable.FloatingActionButton_contentPaddingTop, defaultPadding);
		int paddingRight = a.getDimensionPixelSize(R.styleable.FloatingActionButton_contentPaddingRight, defaultPadding);
		int paddingBottom = a.getDimensionPixelSize(R.styleable.FloatingActionButton_contentPaddingBottom, defaultPadding);
		setContentPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
		a.recycle();

		super.setPreventCornerOverlap(false);
	}

	public Type getType() {
		return mType;
	}

	public void setType(Type type) {
		mType = type;
		requestLayout();
	}

	public void setFabBackgroundColor(int color) {
		setCardBackgroundColor(color);
	}

	/*
	Not applicable
	@Override
	public void setPadding(int left, int top, int right, int bottom) {
		super.setPadding(left, top, right, bottom);
	}

	@Override
	public void setPaddingRelative(int start, int top, int end, int bottom) {
		super.setPaddingRelative(start, top, end, bottom);
	}
	*/

	/*
	Works just like CardView
	@Override
	public boolean getUseCompatPadding() {
		return super.getUseCompatPadding();
	}

	@Override
	public void setUseCompatPadding(boolean useCompatPadding) {
		super.setUseCompatPadding(useCompatPadding);
	}

	@Override
	public void setContentPadding(int left, int top, int right, int bottom) {
		super.setContentPadding(left, top, right, bottom);
	}
	 */

	/*
	Works just like CardView
	@Override
	public int getContentPaddingLeft() {
		return super.getContentPaddingLeft();
	}

	@Override
	public int getContentPaddingRight() {
		return super.getContentPaddingRight();
	}

	@Override
	public int getContentPaddingTop() {
		return super.getContentPaddingTop();
	}

	@Override
	public int getContentPaddingBottom() {
		return super.getContentPaddingBottom();
	}
	*/

	@Override
	public void setRadius(float radius) {
		// super.setRadius(radius);
	}

	/*
	Works just like CardView
	@Override
	public float getRadius() {
		return super.getRadius();
	}

	@Override
	public void setShadowPadding(int left, int top, int right, int bottom) {
		super.setShadowPadding(left, top, right, bottom);
	}
	*/

	/* Rename to fabXXX() */
	/*
	@Override
	public void setCardElevation(float radius) {
		super.setCardElevation(radius);
	}

	@Override
	public float getCardElevation() {
		return super.getCardElevation();
	}

	@Override
	public void setMaxCardElevation(float radius) {
		super.setMaxCardElevation(radius);
	}

	@Override
	public float getMaxCardElevation() {
		return super.getMaxCardElevation();
	}
	*/
	/* End Rename to fabXXX() */

	public void setFabElevation(float elevation) {
		super.setCardElevation(elevation);
	}

	public float getFabElevation() {
		return super.getCardElevation();
	}

	public void setMaxFabElevation(float elevation) {
		super.setMaxCardElevation(elevation);
	}

	public float getMaxFabElevation() {
		return super.getMaxCardElevation();
	}

	/*
	Not sure if I want to prevent these or not
	@Override
	public boolean getPreventCornerOverlap() {
		return super.getPreventCornerOverlap();
	}
	*/

	@Override
	public void setPreventCornerOverlap(boolean preventCornerOverlap) {
		// super.setPreventCornerOverlap(preventCornerOverlap);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);
		int measuredWidth;
		int measuredHeight;
		switch (widthMode) {
			case MeasureSpec.EXACTLY:
				measuredWidth = widthSize;
				break;
			case MeasureSpec.AT_MOST:
				measuredWidth = Math.min(getPaddingLeft() + mType.getSize(getContext()) + getPaddingRight(), widthSize);
				break;
			case MeasureSpec.UNSPECIFIED:
			default:
				measuredWidth = getPaddingLeft() + mType.getSize(getContext()) + getPaddingRight();
		}
		switch (heightMode) {
			case MeasureSpec.EXACTLY:
				measuredHeight = heightSize;
				break;
			case MeasureSpec.AT_MOST:
				measuredHeight = Math.min(getPaddingTop() + mType.getSize(getContext()) + getPaddingBottom(), heightSize);
				break;
			case MeasureSpec.UNSPECIFIED:
			default:
				measuredHeight = getPaddingTop() + mType.getSize(getContext()) + getPaddingBottom();
		}
		setMeasuredDimension(measuredWidth, measuredHeight);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		int width = w - getPaddingLeft() - getPaddingRight();
		int height = h - getPaddingTop() - getPaddingBottom();
		float radius = Math.min(width, height) / 2.0f;
		super.setRadius(radius);
	}

	public static enum Type {
		NORMAL(0, R.dimen.fab_size),
		MINI(1, R.dimen.mini_fab_size);

		private static SparseArray<Type> sTypeArray;

		public static Type fromValue(int value, Type defaultType) {
			if (sTypeArray == null) {
				Type[] types = values();
				sTypeArray = new SparseArray<Type>(types.length);
				for (Type type : types) {
					sTypeArray.put(type.mValue, type);
				}
			}
			return sTypeArray.get(value, defaultType);
		}

		public static Type fromValue(int value) {
			return fromValue(value, null);
		}

		private int mValue;
		private int mSizeResId;

		private Type(int value, int sizeResId) {
			mValue = value;
			mSizeResId = sizeResId;
		}

		public int getValue() {
			return mValue;
		}

		public int getSizeResId() {
			return mSizeResId;
		}

		public int getSize(Context context) {
			return context.getResources().getDimensionPixelSize(mSizeResId);
		}
	}
}
