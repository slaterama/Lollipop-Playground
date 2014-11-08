package com.citymaps.mobile.android.util.viewcompat;

import android.annotation.TargetApi;
import android.graphics.drawable.Drawable;
import android.view.View;

@TargetApi(16)
public class ViewCompatJellyBean extends ViewCompat {
	@Override
	public boolean isAttachedToWindowImpl(View view) {
		boolean attached = false;
		if (view != null) {
			View rootView = view.getRootView();
			attached = (rootView != null && rootView.getParent() != null);
		}
		return attached;
	}

	@Override
	public void setBackgroundImpl(View view, Drawable background) {
		view.setBackground(background);
	}
}
