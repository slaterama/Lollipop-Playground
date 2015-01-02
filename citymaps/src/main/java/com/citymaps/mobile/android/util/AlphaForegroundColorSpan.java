package com.citymaps.mobile.android.util;

import android.graphics.Color;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.text.TextPaint;
import android.text.style.ForegroundColorSpan;

public class AlphaForegroundColorSpan extends ForegroundColorSpan {

	private float mAlpha;

	public AlphaForegroundColorSpan(int color) {
		super(color);
	}

	public AlphaForegroundColorSpan(Parcel in) {
		super(in);
		mAlpha = in.readFloat();
	}

	@Override
	public void updateDrawState(@NonNull TextPaint ds) {
		ds.setColor(getAlphaColor());
	}

	public float getAlpha() {
		return mAlpha;
	}

	public void setAlpha(float alpha) {
		mAlpha = alpha;

	}

	private int getAlphaColor() {
		int foregroundColor = getForegroundColor();
		return Color.argb((int) (mAlpha * 255), Color.red(foregroundColor), Color.green(foregroundColor), Color.blue(foregroundColor));
	}

	@Override
	public void writeToParcel(@NonNull Parcel out, int flags) {
		super.writeToParcel(out, flags);
		out.writeFloat(mAlpha);
	}
}
