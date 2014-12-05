package com.citymaps.mobile.android.util.drawable;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class CircularDrawable extends SquareDrawable {

	public CircularDrawable(Bitmap bitmap, int size, float borderWidth, int borderColor) {
		super(bitmap, size, borderWidth, borderColor);
	}

	public CircularDrawable(Bitmap bitmap, int size) {
		super(bitmap, size);
	}

	public CircularDrawable(Bitmap bitmap) {
		super(bitmap);
	}

	@Override
	public void draw(Canvas canvas) {
		canvas.drawOval(mRectF, mPaint);
		if (hasBorder()) {
			canvas.drawOval(mBorderRectF, mBorderPaint);
		}
	}
}
