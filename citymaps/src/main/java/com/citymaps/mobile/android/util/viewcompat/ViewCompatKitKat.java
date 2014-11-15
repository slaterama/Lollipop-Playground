package com.citymaps.mobile.android.util.viewcompat;

import android.annotation.TargetApi;
import android.graphics.drawable.Drawable;
import android.view.View;

@TargetApi(19)
public class ViewCompatKitKat extends ViewCompat {
	@Override
	public int getMinimumHeightImpl(View view) {
		return view.getMinimumHeight();
	}

	@Override
	public int getMinimumWidthImpl(View view) {
		return view.getMinimumWidth();
	}

	@Override
	public boolean isAttachedToWindowImpl(View view) {
		return view.isAttachedToWindow();
	}

	@Override
	public void setBackgroundImpl(View view, Drawable background) {
		view.setBackground(background);
	}
}
