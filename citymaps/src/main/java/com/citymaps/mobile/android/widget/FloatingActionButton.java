/*
 * See http://www.bignerdranch.com/blog/floating-action-buttons-in-android-l/ for a non-support-friendly
 * (i.e. Android Lollipop only) version of creating a Floating Action Button. Need to provide backwards support.
 */

package com.citymaps.mobile.android.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.ImageButton;
import com.citymaps.mobile.android.R;
import com.citymaps.mobile.android.util.LogEx;

@TargetApi(Build.VERSION_CODES.L)
public class FloatingActionButton extends ImageButton {

	protected static int darkenColor(int color) {
		float[] hsv = new float[3];
		Color.colorToHSV(color, hsv);
		hsv[2] *= 0.8f;
		return Color.HSVToColor(hsv);
	}

	protected static Drawable createBackground(int color, int rippleColor) {
		final ShapeDrawable content = new ShapeDrawable(new OvalShape());
		content.getPaint().setColor(color);
		return content;
		//return new RippleDrawable(ColorStateList.valueOf(rippleColor), content, null);
	}

	protected Type mType;

	protected int mColor;

	protected int mRippleColor;

	protected int mMarginsSet;

	protected ViewOutlineProvider mOutlineProvider;

	public FloatingActionButton(Context context) {
		super(context);
		init(context);
	}

	public FloatingActionButton(Context context, AttributeSet attrs) {
		this(context, attrs, R.attr.floatingActionButtonStyle);
		init(context);
	}

	public FloatingActionButton(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initAttributes(context, attrs, defStyleAttr, 0);
		init(context);
	}

	@TargetApi(Build.VERSION_CODES.L)
	public FloatingActionButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		initAttributes(context, attrs, defStyleAttr, defStyleRes);
		init(context);
	}

	protected void initAttributes(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FloatingActionButton, defStyleAttr, defStyleRes);

		int typeInt = a.getInt(R.styleable.FloatingActionButton_fabType, Type.NORMAL.mValue);
		setType(Type.fromValue(typeInt));

		Rect padding = new Rect(getPaddingLeft(), getPaddingTop(), getPaddingRight(), getPaddingBottom());
		LogEx.d(String.format("padding=%s", padding));

		mColor = a.getColor(R.styleable.FloatingActionButton_android_color, Color.GRAY);
		mRippleColor = a.getColor(R.styleable.FloatingActionButton_rippleColor, darkenColor(mColor));
		setBackground(createBackground(mColor, mRippleColor));

		a.recycle();
	}

	protected void init(Context context) {
		mOutlineProvider = new FabOutlineProvider();

		setOutlineProvider(mOutlineProvider);
		setClipToOutline(true);
	}

	public Type getType() {
		return mType;
	}

	public void setType(Type type) {
		if (type == null) {
			type = Type.NORMAL;
		}
		if (type != mType) {
			mType = type;
			forceLayout();
		}
	}

	public int getColor() {
		return mColor;
	}

	public void setColor(int color) {
		if (color != mColor) {
			mColor = color;
			setBackground(createBackground(mColor, mRippleColor));
		}
	}

	public int getRippleColor() {
		return mRippleColor;
	}

	public void setRippleColor(int rippleColor) {
		if (rippleColor != mRippleColor) {
			mRippleColor = rippleColor;
			setBackground(createBackground(mColor, mRippleColor));
		}
	}

	/*
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
	}
	*/

	@Override
	public void setLayoutParams(ViewGroup.LayoutParams params) {
		super.setLayoutParams(params);
		// TODO deal with shadow, etc.
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		final int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
		final int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
		final int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
		final int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

		int desiredWidth;
		int desiredHeight;
		switch (mType) {
			case MINI:
				desiredWidth = desiredHeight = getResources().getDimensionPixelSize(R.dimen.mini_floating_action_button_size);
				break;
			case NORMAL:
			default:
				desiredWidth = desiredHeight = getResources().getDimensionPixelSize(R.dimen.floating_action_button_size);
		}

		int measuredWidth;
		int measuredHeight;
		switch (widthSpecMode) {
			case MeasureSpec.EXACTLY:
				measuredWidth = getMeasuredWidth();
				break;
			case MeasureSpec.AT_MOST:
				measuredWidth = Math.min(desiredWidth, widthSpecSize);
				break;
			case MeasureSpec.UNSPECIFIED:
			default:
				measuredWidth = desiredWidth;
		}

		switch (heightSpecMode) {
			case MeasureSpec.EXACTLY:
				measuredHeight = getMeasuredHeight();
				break;
			case MeasureSpec.AT_MOST:
				measuredHeight = Math.min(desiredHeight, heightSpecSize);
				break;
			case MeasureSpec.UNSPECIFIED:
			default:
				measuredHeight = desiredHeight;
		}

		setMeasuredDimension(measuredWidth, measuredHeight);
	}

	/*
	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
	}
	*/

	protected class FabOutlineProvider extends ViewOutlineProvider {
		@Override
		public void getOutline(View view, Outline outline) {
			int diameter = getResources().getDimensionPixelSize(R.dimen.floating_action_button_size);
			outline.setOval(0, 0, diameter, diameter);
		}
	}

	public static enum Type {
		NORMAL(0),
		MINI(1);

		private static SparseArray<Type> sTypeArray;

		private static Type fromValue(int value) {
			if (sTypeArray == null) {
				Type[] types = values();
				sTypeArray = new SparseArray<Type>(types.length);
				for (Type type : types) {
					sTypeArray.put(type.mValue, type);
				}
			}
			return sTypeArray.get(value);
		}

		private int mValue;

		private Type(int value) {
			mValue = value;
		}

		public int getValue() {
			return mValue;
		}
	}
}
