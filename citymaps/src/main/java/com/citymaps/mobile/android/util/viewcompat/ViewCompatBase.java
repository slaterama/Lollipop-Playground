package com.citymaps.mobile.android.util.viewcompat;

import android.annotation.TargetApi;
import android.graphics.drawable.Drawable;
import android.view.View;

public class ViewCompatBase implements ViewCompatImpl {
	@Override
	public boolean isAttachedToWindow(View view) {
		boolean attached = false;
		if (view != null) {
			View rootView = view.getRootView();
			attached = (rootView != null && rootView.getParent() != null);
		}
		return attached;
	}

	@TargetApi(15)
	@Override
	public void setBackground(View view, Drawable background) {
		view.setBackgroundDrawable(background);
	}
}
