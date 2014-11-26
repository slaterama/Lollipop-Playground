package com.citymaps.mobile.android.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;
import com.citymaps.mobile.android.R;

/**
 * A Citymaps-themed Toast class.
 */
public class CitymapsToast extends Toast {

	/**
	 * Make a Citymaps toast that just contains a text view with the text from a resource.
	 * @param context The context to use. Usually your {@link android.app.Application} or {@link android.app.Activity} object.
	 * @param text The text to show. Can be formatted text.
	 * @param duration How long to display the message. Either {@link android.widget.Toast#LENGTH_SHORT} or {@link android.widget.Toast#LENGTH_LONG}
	 */
	@NonNull
	public static CitymapsToast makeText(@NonNull Context context, CharSequence text, int duration) {
		CitymapsToast toast = new CitymapsToast(context);
		toast.setText(text);
		toast.setDuration(duration);
		return toast;
	}

	/**
	 * Make a Citymaps toast that just contains a text view with the text from a resource.
	 * @param context The context to use. Usually your {@link android.app.Application} or {@link android.app.Activity} object.
	 * @param resId The resource id of the string resource to use. Can be formatted text.
	 * @param duration How long to display the message. Either {@link android.widget.Toast#LENGTH_SHORT} or {@link android.widget.Toast#LENGTH_LONG}
	 */
	@NonNull
	public static CitymapsToast makeText(@NonNull Context context, int resId, int duration) {
		return makeText(context, context.getString(resId), duration);
	}

	/**
	 * Construct an empty CitymapsToast object.
	 * @param context
	 */
	@SuppressLint("InflateParams")
	public CitymapsToast(Context context) {
		super(context);

		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.toast_citymaps, null);
		setGravity(Gravity.CENTER, 0, 0);
		setView(view);
	}
}
