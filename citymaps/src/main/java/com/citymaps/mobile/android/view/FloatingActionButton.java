/*
 * See http://www.bignerdranch.com/blog/floating-action-buttons-in-android-l/ for a non-support-friendly
 * (i.e. Android Lollipop only) version of creating a Floating Action Button. Need to provide backwards support.
 */

package com.citymaps.mobile.android.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ImageButton;

public class FloatingActionButton extends ImageButton {

	public FloatingActionButton(Context context) {
		super(context);
		init(context);
	}

	public FloatingActionButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public FloatingActionButton(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	/*
	public FloatingActionButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		init(context);
	}
	*/

	protected void init(Context context) {
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

	}
}
