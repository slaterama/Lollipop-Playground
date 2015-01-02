package com.citymaps.mobile.android.util;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ActivityUtils {

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

	public static ViewGroup getActionBarView(Activity activity) {
		ViewGroup actionBarView = null;
		View decorView = activity.getWindow().getDecorView();
		String[] packageNames = new String[]{activity.getPackageName(), ANDROID_DEF_PACKAGE};
		for (String packageName : packageNames) {
			int resId = activity.getResources().getIdentifier(ACTION_BAR_IDENTIFIER_NAME, ID_DEF_TYPE, activity.getPackageName());
			actionBarView = (ViewGroup) decorView.findViewById(resId);
			if (actionBarView != null) {
				break;
			}
		}
		return actionBarView;
	}

	public static TextView getActionBarTitleView(Activity activity) {
		TextView actionBarTitleView = null;
		View decorView = activity.getWindow().getDecorView();
		String[] packageNames = new String[]{activity.getPackageName(), ANDROID_DEF_PACKAGE};
		for (String packageName : packageNames) {
			int resId = activity.getResources().getIdentifier(ACTION_BAR_TITLE_IDENTIFIER_NAME, ID_DEF_TYPE, activity.getPackageName());
			actionBarTitleView = (TextView) decorView.findViewById(resId);
			if (actionBarTitleView != null) {
				break;
			}
		}
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

	private ActivityUtils() {
	}
}
