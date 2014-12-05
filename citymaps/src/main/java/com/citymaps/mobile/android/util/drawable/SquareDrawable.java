package com.citymaps.mobile.android.util.drawable;

import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;

public class SquareDrawable extends Drawable {

	protected final Bitmap mBitmap;
	protected final int mIntrinsicSize;
	protected final float mBorderWidth;
	protected final RectF mRectF;
	protected final RectF mBorderRectF;
	protected final Paint mPaint;
	protected final Paint mBorderPaint;

	public SquareDrawable(Bitmap bitmap, int size, float borderWidth, int borderColor) {
		super();

		int min = Math.min(bitmap.getWidth(), bitmap.getHeight());
		mIntrinsicSize = (size > 0 ? size : min);

		mBitmap = ThumbnailUtils.extractThumbnail(bitmap, min, min);
		mBorderWidth = borderWidth;

		mRectF = new RectF();
		mBorderRectF = new RectF();
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setShader(new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));

		mBorderPaint = new Paint();
		mBorderPaint.setStyle(Paint.Style.STROKE);
		mBorderPaint.setAntiAlias(true);
		mBorderPaint.setDither(true);
		mBorderPaint.setStrokeWidth(borderWidth);
		mBorderPaint.setColor(borderColor);
	}

	public SquareDrawable(Bitmap bitmap, int size) {
		this(bitmap, size, 0.0f, Color.TRANSPARENT);
	}

	public SquareDrawable(Bitmap bitmap) {
		this(bitmap, Math.min(bitmap.getWidth(), bitmap.getHeight()), 0.0f, Color.TRANSPARENT);
	}

	@Override
	public void draw(Canvas canvas) {
		canvas.drawRect(mRectF, mPaint);
		if (hasBorder()) {
			canvas.drawRect(mBorderRectF, mBorderPaint);
		}
	}

	@Override
	public void setAlpha(int alpha) {
		if (mPaint.getAlpha() != alpha) {
			mPaint.setAlpha(alpha);
			invalidateSelf();
		}
	}

	@Override
	public void setColorFilter(ColorFilter filter) {
		mPaint.setColorFilter(filter);
	}

	@Override
	public int getOpacity() {
		return PixelFormat.TRANSLUCENT;
	}

	@Override
	protected void onBoundsChange(Rect bounds) {
		super.onBoundsChange(bounds);
		mRectF.set(bounds);
		mBorderRectF.set(bounds);
		float inset = mBorderWidth / 2.0f;
		mBorderRectF.inset(inset, inset);
	}

	@Override
	public int getIntrinsicWidth() {
		return mIntrinsicSize;
	}

	@Override
	public int getIntrinsicHeight() {
		return mIntrinsicSize;
	}

	public void setAntiAlias(boolean antiAlias) {
		mPaint.setAntiAlias(antiAlias);
		invalidateSelf();
	}

	@Override
	public void setFilterBitmap(boolean filter) {
		mPaint.setFilterBitmap(filter);
		invalidateSelf();
	}

	@Override
	public void setDither(boolean dither) {
		mPaint.setDither(dither);
		invalidateSelf();
	}

	public Bitmap getBitmap() {
		return mBitmap;
	}

	public boolean hasBorder() {
		return mBorderPaint.getStrokeWidth() > 0.0f && mBorderPaint.getColor() != Color.TRANSPARENT;
	}
}
