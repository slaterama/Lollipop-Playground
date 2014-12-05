package com.citymaps.mobile.android.util;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;

public class DrawableUtils {

	public static RoundedBitmapDrawable createCircularBitmapDrawable(Resources resources, Bitmap bitmap, int size) {
		RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(resources,
				bitmap.getWidth() == bitmap.getHeight()
						? bitmap
						: ThumbnailUtils.extractThumbnail(bitmap, size, size, ThumbnailUtils.OPTIONS_RECYCLE_INPUT));
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

	private DrawableUtils() {
	}
}
