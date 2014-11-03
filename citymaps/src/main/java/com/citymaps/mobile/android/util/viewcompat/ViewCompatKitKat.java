package com.citymaps.mobile.android.util.viewcompat;

import android.annotation.TargetApi;
import android.view.View;

@TargetApi(19)
public class ViewCompatKitKat extends ViewCompatJellyBean {

	@Override
	public boolean isAttachedToWindow(View view) {
		return view.isAttachedToWindow();
	}
}
