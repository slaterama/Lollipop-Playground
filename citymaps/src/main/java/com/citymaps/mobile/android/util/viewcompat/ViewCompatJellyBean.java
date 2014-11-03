package com.citymaps.mobile.android.util.viewcompat;

import android.annotation.TargetApi;
import android.graphics.drawable.Drawable;
import android.view.View;

@TargetApi(16)
public class ViewCompatJellyBean extends ViewCompatBase {

	@Override
	public void setBackground(View view, Drawable background) {
		view.setBackground(background);
	}
}
