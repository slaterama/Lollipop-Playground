package com.citymaps.mobile.android.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.media.ThumbnailUtils;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;

public class GraphicsUtils {

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

	public static Bitmap createBlurredBitmap(Context context, Bitmap in, int width, int height) {
		Bitmap thumb = ThumbnailUtils.extractThumbnail(in, width, height);
		Bitmap out = Bitmap.createBitmap(thumb.getWidth(), thumb.getHeight(), Bitmap.Config.ARGB_8888);
		RenderScript rs = RenderScript.create(context.getApplicationContext());
		ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
		Allocation allocationIn = Allocation.createFromBitmap(rs, thumb);
		Allocation allocationOut = Allocation.createFromBitmap(rs, out);
		blurScript.setRadius(25.0f);
		blurScript.setInput(allocationIn);
		blurScript.forEach(allocationOut);
		allocationOut.copyTo(out);
		in.recycle();
		rs.destroy();
		return out;
	}

	private GraphicsUtils() {
	}
}
