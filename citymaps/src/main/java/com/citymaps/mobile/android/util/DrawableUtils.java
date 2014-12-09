package com.citymaps.mobile.android.util;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.media.ThumbnailUtils;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;

public class DrawableUtils {

	public static RoundedBitmapDrawable createCircularBitmapDrawable(Resources resources, Bitmap bitmap, int size) {
		RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(resources,
				bitmap.getWidth() == bitmap.getHeight()
						? bitmap
						: ThumbnailUtils.extractThumbnail(bitmap, size, size));
		drawable.setAntiAlias(true);
		drawable.setCornerRadius(size / 2.0f);
		return drawable;
	}

	public static RoundedBitmapDrawable createCircularBitmapDrawable(Resources resources, Bitmap bitmap) {
		int size = Math.min(bitmap.getWidth(), bitmap.getHeight());
		return createCircularBitmapDrawable(resources, bitmap, size);
	}

	public static RoundedBitmapDrawable createCircularBitmapDrawable(Resources resources, int resId, int size) {
		Bitmap bitmap = BitmapFactory.decodeResource(resources, resId);
		return createCircularBitmapDrawable(resources, bitmap, size);
	}

	public static RoundedBitmapDrawable createCircularBitmapDrawable(Resources resources, int resId) {
		Bitmap bitmap = BitmapFactory.decodeResource(resources, resId);
		int size = Math.min(bitmap.getWidth(), bitmap.getHeight());
		return createCircularBitmapDrawable(resources, bitmap, size);
	}

	public static Bitmap createBitmapWithBackgroundColor(Resources resources, int backgroundColor, int width, int height, int resId) {
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		Paint paint = new Paint();
		paint.setColor(backgroundColor);
		paint.setStyle(Paint.Style.FILL);
		canvas.drawPaint(paint);

		Bitmap resourceBitmap = BitmapFactory.decodeResource(resources, resId);
		canvas.drawBitmap(resourceBitmap, (width - resourceBitmap.getWidth()) / 2.0f, (height - resourceBitmap.getHeight()) / 2.0f, null);
		return bitmap;
	}

	private DrawableUtils() {
	}
}
