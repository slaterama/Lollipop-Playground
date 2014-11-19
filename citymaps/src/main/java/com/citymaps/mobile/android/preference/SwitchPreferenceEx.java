package com.citymaps.mobile.android.preference;

import android.content.Context;
import android.preference.SwitchPreference;
import android.util.AttributeSet;

/**
 * A {@link android.preference.SwitchPreference} that doesn't automatically change its value when clicked.
 * This allows, for example, a dialog to be presented to the user prior to updating the underlying
 * preference value.
 */
public class SwitchPreferenceEx extends SwitchPreference {

	/**
	 * Construct a new CitymapsSwitchPreference with the given style options.
	 * @param context The Context that will style this preference.
	 * @param attrs Style attributes that differ from the default.
	 * @param defStyle Theme attribute defining the default style options.
	 */
	public SwitchPreferenceEx(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	/**
	 * Construct a new CitymapsSwitchPreference with the given style options.
	 * @param context The Context that will style this preference.
	 * @param attrs Style attributes that differ from the default.
	 */
	public SwitchPreferenceEx(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * Construct a new CitymapsSwitchPreference with the given style options.
	 * @param context The Context that will style this preference.
	 */
	public SwitchPreferenceEx(Context context) {
		super(context);
	}

	@Override
	protected void onClick() {
		// Don't change the value on click
	}
}
