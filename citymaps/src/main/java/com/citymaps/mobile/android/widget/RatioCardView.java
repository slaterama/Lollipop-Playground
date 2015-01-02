package com.citymaps.mobile.android.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.ViewGroup;
import android.view.WindowManager;
import com.citymaps.mobile.android.R;

public class RatioCardView extends CardView {

	private float mRatio;

	private boolean mBaseOnSmallestWidth = true;

	private DisplayMetrics mDisplayMetrics;

	private OnSizeChangedListener mOnSizeChangedListener;

	public RatioCardView(Context context) {
		super(context);
		init(context, null, 0);
	}

	public RatioCardView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs, 0);
	}

	public RatioCardView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context, attrs, defStyleAttr);
	}

	private void init(Context context, AttributeSet attrs, int defStyle) {
		// Load attributes
		final TypedArray a = getContext().obtainStyledAttributes(
				attrs, R.styleable.RatioCardView, defStyle, 0);
		setRatio(a.getFloat(R.styleable.RatioCardView_ratio, 0.0f));
		setBaseOnSmallestWidth(a.getBoolean(R.styleable.RatioCardView_baseOnSmallestWidth, false));
		a.recycle();

		WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = manager.getDefaultDisplay();
		mDisplayMetrics = new DisplayMetrics();
		display.getMetrics(mDisplayMetrics);
	}

	public int getPerceivedWidth() {
		int width = getWidth();
		if (width == 0) {
			return 0;
		} else {
			return width - (getUseCompatPadding() ? getPaddingLeft() + getPaddingRight() : 0);
		}
	}

	public int getPerceivedHeight() {
		int height = getHeight();
		if (height == 0) {
			return 0;
		} else {
			return height - (getUseCompatPadding() ? getPaddingTop() + getPaddingBottom() : 0);
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		// The following code is loosely based on the onMeasure method
		// in Android's ImageView widget, in order to base the measured dimensions
		// on a given ratio.

		if (mRatio >= 0.0000001) {
			final int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
			final int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
			final int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
			final int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

			// We are allowed to change the view's width
			boolean resizeWidth = widthSpecMode != MeasureSpec.EXACTLY;

			// We are allowed to change the view's height
			boolean resizeHeight = heightSpecMode != MeasureSpec.EXACTLY;

			if (resizeWidth || resizeHeight) {
				// If we get here, it means we want to resize to match the
				// desired aspect ratio, and we have the freedom to change at
				// least one dimension.

				int measuredWidth = getMeasuredWidth();
				int measuredHeight = getMeasuredHeight();

				// See what our actual aspect ratio is
				float actualAspect = (float) (measuredWidth) / (measuredHeight);

				if (Math.abs(actualAspect - mRatio) > 0.0000001) {

					boolean done = false;

					// Try adjusting width to be proportional to height
					if (resizeWidth) {
						int compatPadding = (getUseCompatPadding() ? getPaddingTop() + getPaddingBottom() : 0);
						int perceivedHeight = measuredHeight - compatPadding;
						int perceivedWidth = Math.max((int) (perceivedHeight * mRatio), getSuggestedMinimumWidth());
						int newWidth = perceivedWidth + compatPadding;
						if (widthSpecMode == MeasureSpec.AT_MOST && newWidth > widthSpecSize) {
							measuredWidth = widthSpecSize;
						} else {
							measuredWidth = newWidth;
							done = true;
						}
					}

					// Try adjusting height to be proportional to width
					if (!done && resizeHeight) {
						int widthToUse = measuredWidth;
						if (getLayoutParams().width == ViewGroup.LayoutParams.MATCH_PARENT && mBaseOnSmallestWidth
								&& mDisplayMetrics.widthPixels > mDisplayMetrics.heightPixels) {

							if (isInEditMode()) {
								widthToUse = (int) (widthToUse / 1.77777778f);
							} else if (mDisplayMetrics.heightPixels == 0) {
								widthToUse = measuredWidth;
							} else {
								float relativeWidth = measuredWidth * 1.0f / mDisplayMetrics.widthPixels;
								widthToUse = (int) (mDisplayMetrics.heightPixels * relativeWidth);
							}
						}

						int compatPadding = (getUseCompatPadding() ? getPaddingLeft() + getPaddingRight() : 0);
						int perceivedWidth = widthToUse - compatPadding;
						int perceivedHeight = Math.max((int) (perceivedWidth / mRatio), getSuggestedMinimumHeight());
						int newHeight = perceivedHeight + compatPadding;
						if (heightSpecMode == MeasureSpec.AT_MOST && newHeight > heightSpecSize) {
							measuredHeight = heightSpecSize;
						} else {
							measuredHeight = newHeight;
						}
					}

					setMeasuredDimension(measuredWidth, measuredHeight);
				}
			}
		}
	}
	/**
	 * Whether to base the ratio on the smallest screen width. If this flag is set,
	 * the control will attempt to set the height to what it would be in portrait orientation.
	 * Applies only to views with width set to {@code MATCH_PARENT} and height set to {@code WRAP_CONTENT}.
	 *
	 * @return Whether to base the ratio on the smallest screen width.
	 */
	public boolean isBaseOnSmallestWidth() {
		return mBaseOnSmallestWidth;
	}

	/**
	 * Sets whether to base the ratio on the smallest screen width. If this flag is set,
	 * the control will attempt to set the height to what it would be in portrait orientation.
	 * Applies only to views with width set to {@code MATCH_PARENT} and height set to {@code WRAP_CONTENT}.
	 */
	public void setBaseOnSmallestWidth(boolean baseOnSmallestWidth) {
		mBaseOnSmallestWidth = baseOnSmallestWidth;
		forceLayout();
	}

	/**
	 * Gets the ratio value.
	 *
	 * @return The ratio value.
	 */
	public float getRatio() {
		return mRatio;
	}

	/**
	 * Sets the view's example string attribute value. In the example view, this string
	 * is the text to draw.
	 *
	 * @param ratio The example string attribute value to use.
	 */
	public void setRatio(float ratio) {
		mRatio = Math.max(ratio, 0.0f);
		forceLayout();
	}

	public void setOnSizeChangedListener(OnSizeChangedListener onSizeChangedListener) {
		mOnSizeChangedListener = onSizeChangedListener;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		if (mOnSizeChangedListener != null) {
			mOnSizeChangedListener.onSizeChanged(this, w, h, oldw, oldh);
		}
	}
}
