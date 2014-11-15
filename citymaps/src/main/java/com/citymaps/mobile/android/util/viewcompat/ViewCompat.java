package com.citymaps.mobile.android.util.viewcompat;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.view.ViewParent;

import java.lang.reflect.Field;

/**
 * Class that allows for determining whether a view is attached to a window in pre-KitKat builds.
 * Though logic is accessed via static methods, this singleton implements the "Initialization-on-demand holder idiom"
 * for singletons in Java as described here: http://en.wikipedia.org/wiki/Initialization-on-demand_holder_idiom
 */
public abstract class ViewCompat {
	/**
	 * Retrieves a singleton instance of ViewCompat.
	 * @return ViewCompat instance.
	 */
	private static ViewCompat getInstance() {
		return LazyHolder.INSTANCE;
	}

	/**
	 * Static class that creates the appropriate instance based on Android build version.
	 */
	private static class LazyHolder {
		private static ViewCompat createInstance() {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
				return new ViewCompatKitKat();
			} else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
				return new ViewCompatJellyBean();
			} else {
				return new ViewCompatBase();
			}
		}

		private static final ViewCompat INSTANCE = createInstance();
	}

	/**
	 * Returns the {@link Activity} in which a view currently resides. It is conceivable that a view
	 * was created using a context other than an Activity (for example, an Application or Service object).
	 * It is also possible, though rare, that a view can be removed from a ViewGroup in one Activity and
	 * placed into a ViewGroup in a completely different Activity.
	 * @param view A view whose current Activity you want
	 * @return The context in which the view currently resides.
	 */
	public static Activity getCurrentActivity(View view) {
		Context context = getCurrentContext(view);
		return (context != null && context instanceof Activity ? (Activity) context : null);
	}

	/**
	 * Returns the {@link Context} in which a view currently resides. It is conceivable that a view
	 * was created using a context other than an Activity (for example, an Application or Service object).
	 * It is also possible, though rare, that a view can be removed from a ViewGroup in one Activity and
	 * placed into a ViewGroup in a completely different Activity.
	 * @param view A view whose current context you want
	 * @return The context in which the view currently resides.
	 */
	public static Context getCurrentContext(View view) {
		Context context = null;
		if (view != null) {
			View rootView = view.getRootView();
			context = (rootView == null ? null : rootView.getContext());
		}
		return context;
	}

	public static int getHierarchyHeight(View view) {
		int depth = 0;
		if (view != null) {
			depth++;
			ViewParent parent = view.getParent();
			while (parent != null) {
				depth++;
				parent = parent.getParent();
			}
		}
		return depth;
	}

	public static int getMinimumHeight(View view) {
		return getInstance().getMinimumHeightImpl(view);
	}

	public static int getMinimumWidth(View view) {
		return getInstance().getMinimumWidthImpl(view);
	}

	/**
	 * Returns true if this view is currently attached to a window.
	 */
	public static boolean isAttachedToWindow(View view) {
		return getInstance().isAttachedToWindowImpl(view);
	}

	/**
	 * Set the background to a given Drawable, or remove the background. If the background has padding,
	 * this View's padding is set to the background's padding. However, when a background is removed,
	 * this View's padding isn't touched. If setting the padding is desired, please use
	 * {@link View#setPadding(int, int, int, int)}.
	 * @param view The view whose background you want to set.
	 * @param background The Drawable to use as the background, or null to remove the background.
	 */
	public static void setBackground(View view, Drawable background) {
		getInstance().setBackgroundImpl(view, background);
	}

	protected ViewCompat() {
	}

	public abstract int getMinimumHeightImpl(View view);

	public abstract int getMinimumWidthImpl(View view);

	/**
	 * Returns true if this view is currently attached to a window.
	 */
	public abstract boolean isAttachedToWindowImpl(View view);

	/**
	 * Set the background to a given Drawable, or remove the background. If the background has padding,
	 * this View's padding is set to the background's padding. However, when a background is removed,
	 * this View's padding isn't touched. If setting the padding is desired, please use
	 * {@link View#setPadding(int, int, int, int)}.
	 * @param view The view whose background you want to set.
	 * @param background The Drawable to use as the background, or null to remove the background.
	 */
	public abstract void setBackgroundImpl(View view, Drawable background);
}
