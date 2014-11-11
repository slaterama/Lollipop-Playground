package com.citymaps.mobile.android.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.Button;
import com.citymaps.mobile.android.R;

public class CenteredDrawableButton extends Button {

	public CenteredDrawableButton(Context context) {
		super(context);
	}

	public CenteredDrawableButton(Context context, AttributeSet attrs) {
		this(context, attrs, R.attr.centeredDrawableButtonStyle);
	}

	public CenteredDrawableButton(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	/*
	public CenteredDrawableButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
	}
	*/

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		Drawable[] drawables = getCompoundDrawables();
		if (drawables != null) {
			Drawable drawableLeft = drawables[0];
			Drawable drawableRight = drawables[2];
			if (drawableLeft != null || drawableRight != null) {
				float textWidth = getPaint().measureText(getText().toString());
				int drawablePadding = getCompoundDrawablePadding();
				int drawableWidth = 0;
				if (drawableLeft != null)
					drawableWidth = drawableLeft.getIntrinsicWidth();
				else /* drawableRight != null */ {
					drawableWidth = drawableRight.getIntrinsicWidth();
				}
				float bodyWidth = textWidth + drawableWidth + drawablePadding;
				int padding = Math.max((int) ((w - bodyWidth) / 2.0f), 0);
				setPadding(padding, getPaddingTop(), padding, getPaddingBottom());
			}
		}
	}
}
