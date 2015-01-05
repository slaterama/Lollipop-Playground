package com.citymaps.mobile.android.util;

import android.app.Activity;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.citymaps.mobile.android.R;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ActivityUtils {

	private static final String ACTION_BAR_CONTAINER_IDENTIFIER_NAME = "action_bar_container";
	private static final String ACTION_BAR_IDENTIFIER_NAME = "action_bar";
	private static final String ACTION_BAR_TITLE_IDENTIFIER_NAME = "action_bar_title";
	private static final String ID_DEF_TYPE = "id";
	private static final String ANDROID_DEF_PACKAGE = "android";

	public static boolean isStarted(Activity activity) {
		boolean isStarted = false;
		if (activity != null) {
			try {
				Field field = Activity.class.getField("isStopped");
				if (!field.isAccessible()) {
					field.setAccessible(true);
				}
				Object result = field.get(activity);
				isStarted = (result instanceof Boolean && !((Boolean) result));
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return isStarted;
	}

	public static boolean isResumed(Activity activity) {
		boolean isResumed = false;
		if (activity != null) {
			try {
				Method method = Activity.class.getMethod("isResumed");
				if (!method.isAccessible()) {
					method.setAccessible(true);
				}
				Object result = method.invoke(activity);
				isResumed = (result instanceof Boolean && (Boolean) result);
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		return isResumed;
	}

	public static View getView(Activity activity, String name) {
		View decorView = activity.getWindow().getDecorView();
		String[] packageNames = new String[]{activity.getPackageName(), ANDROID_DEF_PACKAGE};
		for (String packageName : packageNames) {
			int resId = activity.getResources().getIdentifier(name, ID_DEF_TYPE, packageName);
			View view = decorView.findViewById(resId);
			if (view != null) {
				return view;
			}
		}
		return null;
	}

	public static ViewGroup getActionBarContainerView(Activity activity) {
		return (ViewGroup) getView(activity, ACTION_BAR_CONTAINER_IDENTIFIER_NAME);
	}

	public static ViewGroup getActionBarView(Activity activity) {
		return (ViewGroup) getView(activity, ACTION_BAR_IDENTIFIER_NAME);
	}

	public static TextView getActionBarTitleView(Activity activity) {
		TextView actionBarTitleView = (TextView) getView(activity, ACTION_BAR_TITLE_IDENTIFIER_NAME);
		if (actionBarTitleView == null) {
			ViewGroup actionBarView = getActionBarView(activity);
			if (actionBarView != null) {
				int childCount = actionBarView.getChildCount();
				for (int i = 0; i < childCount; i++) {
					View child = actionBarView.getChildAt(i);
					if (child instanceof TextView) {
						actionBarTitleView = (TextView) child;
						break;
					}
				}
			}
		}
		return actionBarTitleView;
	}

	public static int getActionBarHeight(Activity activity) {
		int actionBarHeight = 0;
		TypedValue tv = new TypedValue();
		Resources.Theme theme = activity.getTheme();
		if (theme.resolveAttribute(R.attr.actionBarSize, tv, true) ||
				theme.resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
			actionBarHeight = TypedValue.complexToDimensionPixelOffset(tv.data, activity.getResources().getDisplayMetrics());
		}
		LogEx.d(String.format("actionBarHeight=%d", actionBarHeight));
		return actionBarHeight;
	}

	private ActivityUtils() {
	}
}
