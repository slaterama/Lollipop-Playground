package com.citymaps.mobile.android.preference;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.preference.SwitchPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.citymaps.mobile.android.R;
import com.citymaps.mobile.android.util.LogEx;

/**
 * A {@link android.preference.SwitchPreference} that doesn't automatically change its value when clicked.
 * This allows, for example, a dialog to be presented to the user prior to updating the underlying
 * preference value.
 */
public class SwitchPreferenceEx extends SwitchPreference {

	private boolean mCheckOnClick;

	private Drawable mSecondaryIcon;

	private int mSecondaryIconResId;

	private ImageView mSecondaryIcondView;

	private int mLines = 1;

	private int mMaxLines;

	private boolean mSingleLine = true;

	/**
	 * Construct a new CitymapsSwitchPreference with the given style options.
	 * @param context The Context that will style this preference.
	 */
	public SwitchPreferenceEx(Context context) {
		super(context);
		init(context, null, 0);
	}

	/**
	 * Construct a new CitymapsSwitchPreference with the given style options.
	 * @param context The Context that will style this preference.
	 * @param attrs Style attributes that differ from the default.
	 */
	public SwitchPreferenceEx(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs, R.attr.switchPreferenceExStyle);
	}

	/**
	 * Construct a new CitymapsSwitchPreference with the given style options.
	 * @param context The Context that will style this preference.
	 * @param attrs Style attributes that differ from the default.
	 * @param defStyle Theme attribute defining the default style options.
	 */
	public SwitchPreferenceEx(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs, defStyle);
	}

	/*
	public SwitchPreferenceEx(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
	}
	*/

	private void init(Context context, AttributeSet attrs, int defStyle) {
		// Load attributes
		/*
		 * NOTE I wish I could override & extend preference styles (i.e. @android:style/Preference.SwitchPreference,
		 * @android:style/Preference.Holo.SwitchPreference etc.) but android doesn't expose those styles for overriding.
		 * So all custom appearances/behaviors will have to be done via code.
		 */
		/*
		final TypedArray a = getContext().obtainStyledAttributes(
				attrs, R.styleable.SwitchPreferenceEx, defStyle, 0);
		setCheckOnClick(a.getBoolean(R.styleable.SwitchPreferenceEx_checkOnClick, true));
		setSecondaryIcon(a.getResourceId(R.styleable.SwitchPreferenceEx_secondaryIcon, 0));
		setLines(a.getInt(R.styleable.SwitchPreferenceEx_android_lines, 1));
		setMaxLines(a.getInt(R.styleable.SwitchPreferenceEx_android_maxLines, 1));
		setSingleLine(a.getBoolean(R.styleable.SwitchPreferenceEx_android_singleLine, true));
		a.recycle();
		*/
	}

	public boolean isCheckOnClick() {
		return mCheckOnClick;
	}

	public void setCheckOnClick(boolean checkOnClick) {
		mCheckOnClick = checkOnClick;
	}

	public Drawable getSecondaryIcon() {
		return mSecondaryIcon;
	}

	/**
	 * Sets the secondary icon for this Preference with a Drawable.
	 * This icon will be placed into the ID
	 * {@link R.id#secondary_icon} within the View created by
	 * {@link #onCreateView(android.view.ViewGroup)}.
	 *
	 * @param secondaryIcon The optional secondary icon for this Preference.
	 */
	public void setSecondaryIcon(Drawable secondaryIcon) {
		if ((secondaryIcon == null && mSecondaryIcon != null) || (secondaryIcon != null && mSecondaryIcon != secondaryIcon)) {
			mSecondaryIcon = secondaryIcon;

			notifyChanged();
		}
	}

	/**
	 * Sets the secondary icon for this Preference with a resource ID.
	 *
	 * @see #setSecondaryIcon(Drawable)
	 * @param secondaryIconResId The icon as a resource ID.
	 */
	public void setSecondaryIcon(int secondaryIconResId) {
		mSecondaryIconResId = secondaryIconResId;
		setIcon(getContext().getResources().getDrawable(secondaryIconResId));
	}

	public int getLines() {
		return mLines;
	}

	public void setLines(int lines) {
		mLines = lines;

		notifyChanged();
	}

	public int getMaxLines() {
		return mMaxLines;
	}

	public void setMaxLines(int maxLines) {
		mMaxLines = maxLines;

		notifyChanged();
	}

	public boolean isSingleLine() {
		return mSingleLine;
	}

	public void setSingleLine(boolean singleLine) {
		mSingleLine = singleLine;

		notifyChanged();
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	@Override
	protected void onBindView(View view) {
		super.onBindView(view);

		TextView textView = (TextView) view.findViewById(android.R.id.title);
		if (textView != null) {
			int maxLines = textView.getMaxLines();
			LogEx.d(String.format("maxLines=%d", maxLines));
			textView.setLines(mLines);
			textView.setMaxLines(mMaxLines);
			textView.setSingleLine(mSingleLine);
		}
	}

	@Override
	protected void onClick() {
		if (mCheckOnClick) {
			super.onClick();
		}
	}
}
