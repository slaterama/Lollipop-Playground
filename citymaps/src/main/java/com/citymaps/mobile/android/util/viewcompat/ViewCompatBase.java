package com.citymaps.mobile.android.util.viewcompat;

import android.annotation.TargetApi;
import android.graphics.drawable.Drawable;
import android.view.View;

import java.lang.reflect.Field;

public class ViewCompatBase extends ViewCompat {
	@Override
	public int getMinimumHeightImpl(View view) {
		try {
			Field field = view.getClass().getDeclaredField("mMinHeight");
			field.setAccessible(true);
			return field.getInt(view);
		} catch (NoSuchFieldException e) {
			return 0;
		} catch (IllegalAccessException e) {
			return 0;
		}
	}

	@Override
	public int getMinimumWidthImpl(View view) {
		try {
			Field field = view.getClass().getDeclaredField("mMinWidth");
			field.setAccessible(true);
			return field.getInt(view);
		} catch (NoSuchFieldException e) {
			return 0;
		} catch (IllegalAccessException e) {
			return 0;
		}
	}

	@Override
	public boolean isAttachedToWindowImpl(View view) {
		boolean attached = false;
		if (view != null) {
			View rootView = view.getRootView();
			attached = (rootView != null && rootView.getParent() != null);
		}
		return attached;
	}

	@TargetApi(15)
	@Override
	public void setBackgroundImpl(View view, Drawable background) {
		view.setBackgroundDrawable(background);
	}
}
