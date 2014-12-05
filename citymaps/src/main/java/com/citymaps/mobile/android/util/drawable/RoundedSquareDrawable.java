package com.citymaps.mobile.android.util.drawable;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class RoundedSquareDrawable extends SquareDrawable {

	// TODO: Pass radius in constructor OR collapse SquareDrawable, CircularDrawable AND RoundedSquareDrawable into one

	public RoundedSquareDrawable(Bitmap bitmap, int size, float borderWidth, int borderColor) {
		super(bitmap, size, borderWidth, borderColor);
	}

	public RoundedSquareDrawable(Bitmap bitmap, int size) {
		super(bitmap, size);
	}

	public RoundedSquareDrawable(Bitmap bitmap) {
		super(bitmap);
	}

	@Override
	public void draw(Canvas canvas) {
		float radius = 10.0f;
		canvas.drawRoundRect(mRectF, radius, radius, mPaint);
		if (hasBorder()) {
			float borderRadius = radius - mBorderWidth / 2.0f;
			canvas.drawRoundRect(mBorderRectF, borderRadius, borderRadius, mBorderPaint);
		}
	}
}
