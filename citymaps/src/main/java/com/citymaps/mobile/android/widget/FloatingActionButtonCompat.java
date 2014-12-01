package com.citymaps.mobile.android.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.drawable.*;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.support.annotation.IntDef;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.ImageButton;
import com.citymaps.mobile.android.R;
import com.citymaps.mobile.android.util.LogEx;

public class FloatingActionButtonCompat extends ImageButton {

	protected static int defaultPressedColor(int color) {
		float[] hsv = new float[3];
		Color.colorToHSV(color, hsv);
		hsv[2] *= 0.9f;
		return Color.HSVToColor(hsv);
	}

	protected static int defaultRippleColor(int color) {
		float[] hsv = new float[3];
		Color.colorToHSV(color, hsv);
		hsv[2] *= 0.5f;
		return Color.HSVToColor(hsv);
	}

	@IntDef({TYPE_NORMAL, TYPE_MINI})
	public static @interface Type {
	}

	public static final int TYPE_NORMAL = 0;
	public static final int TYPE_MINI = 1;

	protected FloatingActionButtonHelper mHelper;

	protected int mType;

	protected int mColor;

	protected int mPressedColor;

	protected int mRippleColor;

	public FloatingActionButtonCompat(Context context) {
		super(context);
		init(context);
		postInit();
	}

	public FloatingActionButtonCompat(Context context, AttributeSet attrs) {
		this(context, attrs, R.attr.floatingActionButtonCompatStyle);
	}

	public FloatingActionButtonCompat(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
		initAttributes(context, attrs, defStyleAttr, 0);
		postInit();
	}

	@TargetApi(Build.VERSION_CODES.L)
	public FloatingActionButtonCompat(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		init(context);
		initAttributes(context, attrs, defStyleAttr, defStyleRes);
		postInit();
	}

	protected void init(Context context) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			mHelper = new FloatingActionButtonHelperLollipop();
		} else {
			mHelper = new FloatingActionButtonHelperBase();
		}
	}

	protected void initAttributes(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FloatingActionButton, defStyleAttr, defStyleRes);

		mType = a.getInt(R.styleable.FloatingActionButtonCompat_fabType, TYPE_NORMAL);

		mColor = a.getColor(R.styleable.FloatingActionButtonCompat_android_color, Color.GRAY);

		mPressedColor = a.getColor(R.styleable.FloatingActionButtonCompat_rippleColor, defaultPressedColor(mColor));

		mRippleColor = a.getColor(R.styleable.FloatingActionButtonCompat_rippleColor, defaultRippleColor(mColor));

		a.recycle();
	}

	protected void postInit() {
		mHelper.updateBackgroundDrawable();
	}

	public int getType() {
		return mType;
	}

	public void setType(@Type int type) {
		mType = type;
		forceLayout();
	}

	public int getColor() {
		return mColor;
	}

	public void setColor(int color) {
		mColor = color;
		forceLayout();
	}

	public int getPressedColor() {
		return mPressedColor;
	}

	public void setPressedColor(int pressedColor) {
		mPressedColor = pressedColor;
		forceLayout();
	}

	public int getRippleColor() {
		return mRippleColor;
	}

	public void setRippleColor(int rippleColor) {
		mRippleColor = rippleColor;
		forceLayout();
	}

	@Override
	public void setLayoutParams(ViewGroup.LayoutParams params) {
		mHelper.setLayoutParams(params);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		mHelper.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		mHelper.onLayout(changed, left, top, right, bottom);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		mHelper.onSizeChanged(w, h, oldw, oldh);
	}

	protected class FloatingActionButtonHelperBase implements FloatingActionButtonHelper {

		protected int mShadowOffset;

		public FloatingActionButtonHelperBase() {
			mShadowOffset = getResources().getDimensionPixelOffset(R.dimen.compat_shadow_offset);
		}

		@Override
		public void setLayoutParams(ViewGroup.LayoutParams params) {
			if (params instanceof ViewGroup.MarginLayoutParams) {
				ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) params;
				marginLayoutParams.leftMargin -= mShadowOffset;
				marginLayoutParams.topMargin -= mShadowOffset;
				marginLayoutParams.rightMargin -= mShadowOffset;
				marginLayoutParams.bottomMargin -= mShadowOffset;
				FloatingActionButtonCompat.super.setLayoutParams(marginLayoutParams);
			} else {
				FloatingActionButtonCompat.super.setLayoutParams(params);
			}
		}

		@Override
		public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			final int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
			final int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
			final int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
			final int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

			int desiredWidth;
			int desiredHeight;
			switch (mType) {
				case TYPE_MINI:
					desiredWidth = desiredHeight = getResources().getDimensionPixelSize(R.dimen.mini_floating_action_button_size)
							+ 2 * mShadowOffset;
					break;
				case TYPE_NORMAL:
				default:
					desiredWidth = desiredHeight = getResources().getDimensionPixelSize(R.dimen.floating_action_button_size)
							+ 2 * mShadowOffset;
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

		@Override
		public void onSizeChanged(int w, int h, int oldw, int oldh) {
			// TODO Save the gradientDrawable as an instance variable

			if (w == 0 || h == 0) {
				return;
			}

			Drawable background = getBackground();
			if (background instanceof LayerDrawable) {
				LayerDrawable layerDrawable = (LayerDrawable) background;
				Drawable layer = layerDrawable.getDrawable(0);
				if (layer instanceof GradientDrawable) {
					GradientDrawable gradientDrawable = (GradientDrawable) layer;
					gradientDrawable.setGradientRadius((w - 2.0f * mShadowOffset + 10) / 2.0f);
				}
			}
		}

		@Override
		public void onLayout(boolean changed, int left, int top, int right, int bottom) {

		}

		@Override
		public void updateBackgroundDrawable() {
			StateListDrawable stateListDrawable = new StateListDrawable();

			ShapeDrawable pressedDrawable = new ShapeDrawable(new OvalShape());
			pressedDrawable.getPaint().setColor(mPressedColor);

			ShapeDrawable defaultDrawable = new ShapeDrawable(new OvalShape());
			defaultDrawable.getPaint().setColor(mColor);

			stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, pressedDrawable);
			stateListDrawable.addState(new int[]{}, defaultDrawable);

			int[] colors = new int[] {
				Color.parseColor("#80000000"),
				Color.parseColor("#00000000")
			};
			GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colors);
			gradientDrawable.setGradientType(GradientDrawable.RADIAL_GRADIENT);
			gradientDrawable.setGradientCenter(0.5f, 0.55f);

			LayerDrawable layerDrawable = new LayerDrawable(new Drawable[] {gradientDrawable, stateListDrawable});
			layerDrawable.setLayerInset(1, mShadowOffset, mShadowOffset, mShadowOffset, mShadowOffset);

			setBackground(layerDrawable);
		}
	}

	@TargetApi(Build.VERSION_CODES.L)
	protected class FloatingActionButtonHelperLollipop implements FloatingActionButtonHelper {

		protected ViewOutlineProvider mOutlineProvider;

		public FloatingActionButtonHelperLollipop() {
			mOutlineProvider = new FabOutlineProvider();
			setOutlineProvider(mOutlineProvider);
			setClipToOutline(true);
		}

		@Override
		public void setLayoutParams(ViewGroup.LayoutParams params) {
			LogEx.d(String.format("params=%s", params));
			FloatingActionButtonCompat.super.setLayoutParams(params);
		}

		@Override
		public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			final int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
			final int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
			final int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
			final int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

			int desiredWidth;
			int desiredHeight;
			switch (mType) {
				case TYPE_MINI:
					desiredWidth = desiredHeight = getResources().getDimensionPixelSize(R.dimen.mini_floating_action_button_size);
					break;
				case TYPE_NORMAL:
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

		@Override
		public void onSizeChanged(int w, int h, int oldw, int oldh) {

		}

		@Override
		public void onLayout(boolean changed, int left, int top, int right, int bottom) {

		}

		@Override
		public void updateBackgroundDrawable() {
			StateListDrawable stateListDrawable = new StateListDrawable();

			ShapeDrawable pressedDrawable = new ShapeDrawable(new OvalShape());
			pressedDrawable.getPaint().setColor(mPressedColor);

			ShapeDrawable defaultDrawable = new ShapeDrawable(new OvalShape());
			defaultDrawable.getPaint().setColor(mColor);

			stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, pressedDrawable);
			stateListDrawable.addState(new int[]{}, defaultDrawable);

			ColorStateList colorStateList = new ColorStateList(new int[][]{{}}, new int[] {mRippleColor});
			RippleDrawable rippleDrawable = new RippleDrawable(colorStateList, stateListDrawable, null);

			setBackground(rippleDrawable);
		}

		public class FabOutlineProvider extends ViewOutlineProvider {
			@Override
			public void getOutline(View view, Outline outline) {
				outline.setOval(0, 0, getWidth(), getHeight());
			}
		}
	}

	public static interface FloatingActionButtonHelper {
		public void setLayoutParams(ViewGroup.LayoutParams params);

		public void onMeasure(int widthMeasureSpec, int heightMeasureSpec);

		public void onLayout(boolean changed, int left, int top, int right, int bottom);

		public void onSizeChanged(int w, int h, int oldw, int oldh);

		public void updateBackgroundDrawable();
	}

}
