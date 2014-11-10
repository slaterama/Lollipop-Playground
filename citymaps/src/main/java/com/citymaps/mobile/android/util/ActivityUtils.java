package com.citymaps.mobile.android.util;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Build;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ActivityUtils {

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

	private ActivityUtils() {
	}
}
