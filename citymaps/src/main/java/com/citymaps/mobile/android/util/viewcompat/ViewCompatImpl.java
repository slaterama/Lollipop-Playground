package com.citymaps.mobile.android.util.viewcompat;

import android.graphics.drawable.Drawable;
import android.view.View;

public interface ViewCompatImpl {
	/**
	 * Returns true if this view is currently attached to a window.
	 */
	public boolean isAttachedToWindow(View view);

	/**
	 * Set the background to a given Drawable, or remove the background. If the background has padding,
	 * this View's padding is set to the background's padding. However, when a background is removed,
	 * this View's padding isn't touched. If setting the padding is desired, please use
	 * {@link View#setPadding(int, int, int, int)}.
	 * @param view The view whose background you want to set.
	 * @param background The Drawable to use as the background, or null to remove the background.
	 */
	public void setBackground(View view, Drawable background);
}
