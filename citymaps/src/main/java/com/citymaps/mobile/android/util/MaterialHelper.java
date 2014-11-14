package com.citymaps.mobile.android.util;

import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;

public class MaterialHelper {

	public static void constrainWidthToShorterDimension(final Window window, View view) {
		view.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
			@Override
			public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
				View decorView = window.getDecorView();
				int diff = decorView.getWidth() - (right - left);
				int newWidth = Math.min(decorView.getWidth(), decorView.getHeight()) - diff;
				LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) v.getLayoutParams();
				lp.width = newWidth;
				v.requestLayout();
				v.removeOnLayoutChangeListener(this);
			}
		});
	}

	private MaterialHelper() {
	}
}
