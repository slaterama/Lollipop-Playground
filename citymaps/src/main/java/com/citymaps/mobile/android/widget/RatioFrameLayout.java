package com.citymaps.mobile.android.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import com.citymaps.mobile.android.R;

/**
 * TODO: document your custom view class.
 */
public class RatioFrameLayout extends FrameLayout {

	private float mRatio;

	public RatioFrameLayout(Context context) {
		super(context);
		init(null, 0);
	}

	public RatioFrameLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs, 0);
	}

	public RatioFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(attrs, defStyleAttr);
	}

	/*
	public RatioFrameLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		init(attrs, defStyleAttr);
	}
	*/

	private void init(AttributeSet attrs, int defStyle) {
		// Load attributes
		final TypedArray a = getContext().obtainStyledAttributes(
				attrs, R.styleable.RatioFrameLayout, defStyle, 0);
		setRatio(a.getFloat(R.styleable.RatioFrameLayout_ratio, 0.0f));
		a.recycle();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if (mRatio > 0.0f) {
			int widthMode = MeasureSpec.getMode(widthMeasureSpec);
			int widthSize = MeasureSpec.getSize(widthMeasureSpec);
			int heightMode = MeasureSpec.getMode(heightMeasureSpec);
			int heightSize = MeasureSpec.getSize(heightMeasureSpec);

			// Update widthMeasureSpec if appropriate
			if (heightMode == MeasureSpec.EXACTLY) {
				int desiredWidth = (int) (heightSize * mRatio);
				if (widthMode == MeasureSpec.UNSPECIFIED) {
					widthMeasureSpec = MeasureSpec.makeMeasureSpec(desiredWidth, MeasureSpec.EXACTLY);
				} else if (widthMode == MeasureSpec.AT_MOST) {
					widthMeasureSpec = MeasureSpec.makeMeasureSpec(Math.min(desiredWidth, heightSize), MeasureSpec.EXACTLY);
				}
			}

			// Update heightMeasureSpec if appropriate
			if (widthMode == MeasureSpec.EXACTLY || widthMode == MeasureSpec.AT_MOST) {
				int desiredHeight = (int) (widthSize / mRatio);
				if (heightMode == MeasureSpec.UNSPECIFIED) {
					heightMeasureSpec = MeasureSpec.makeMeasureSpec(desiredHeight, MeasureSpec.EXACTLY);
				} else if (heightMode == MeasureSpec.AT_MOST) {
					heightMeasureSpec = MeasureSpec.makeMeasureSpec(Math.min(widthSize, desiredHeight), MeasureSpec.EXACTLY);
				}
			}
		}
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
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
}
