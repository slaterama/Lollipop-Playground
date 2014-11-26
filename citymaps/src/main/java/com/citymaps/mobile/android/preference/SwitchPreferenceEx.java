package com.citymaps.mobile.android.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.preference.SwitchPreference;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
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

	private ImageView mSecondaryIconView;

	private int mMinLines;

	private int mLines;

	private int mMaxLines;

	private boolean mSingleLine = true;

	/**
	 * Construct a new CitymapsSwitchPreference with the given style options.
	 *
	 * @param context The Context that will style this preference.
	 */
	public SwitchPreferenceEx(Context context) {
		super(context);
		init(context, null, 0);
	}

	/**
	 * Construct a new CitymapsSwitchPreference with the given style options.
	 *
	 * @param context The Context that will style this preference.
	 * @param attrs   Style attributes that differ from the default.
	 */
	public SwitchPreferenceEx(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs, 0); //R.attr.switchPreferenceExStyle);
	}

	/**
	 * Construct a new CitymapsSwitchPreference with the given style options.
	 *
	 * @param context  The Context that will style this preference.
	 * @param attrs    Style attributes that differ from the default.
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
		final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.SwitchPreferenceEx, defStyle, 0);
		setCheckOnClick(a.getBoolean(R.styleable.SwitchPreferenceEx_checkOnClick, true));
		setSecondaryIcon(a.getResourceId(R.styleable.SwitchPreferenceEx_secondaryIcon, 0));
		setMaxLines(a.getInt(R.styleable.SwitchPreferenceEx_android_maxLines, -1));
		setLines(a.getInt(R.styleable.SwitchPreferenceEx_android_lines, -1));
		setMinLines(a.getInt(R.styleable.SwitchPreferenceEx_android_minLines, -1));
		setSingleLine(a.getBoolean(R.styleable.SwitchPreferenceEx_android_singleLine, true));
		a.recycle();
	}

	public boolean isCheckOnClick() {
		return mCheckOnClick;
	}

	public void setCheckOnClick(boolean checkOnClick) {
		mCheckOnClick = checkOnClick;

		notifyChanged();
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
	 * @param secondaryIconResId The icon as a resource ID.
	 * @see #setSecondaryIcon(Drawable)
	 */
	public void setSecondaryIcon(int secondaryIconResId) {
		mSecondaryIconResId = secondaryIconResId;

		Drawable secondaryIcon = secondaryIconResId < 1 ? null : getContext().getResources().getDrawable(secondaryIconResId);
		setSecondaryIcon(secondaryIcon);
	}

	public int getMinLines() {
		return mMinLines;
	}

	public void setMinLines(int minLines) {
		mMinLines = minLines;
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

	@Override
	protected View onCreateView(ViewGroup parent) {
		View view = super.onCreateView(parent);
		if (view instanceof ViewGroup) {
			mSecondaryIconView = new ImageView(getContext());
			mSecondaryIconView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
			mSecondaryIconView.setVisibility(View.GONE);
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
					ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
			View widgetFrame = view.findViewById(android.R.id.widget_frame);
			int index = ((ViewGroup) view).indexOfChild(widgetFrame);
			if (index < 0) {
				((ViewGroup) view).addView(mSecondaryIconView, layoutParams);
			} else {
				((ViewGroup) view).addView(mSecondaryIconView, index, layoutParams);
			}
		}
		return view;
	}

	@Override
	protected void onBindView(@NonNull View view) {
		super.onBindView(view);

		TextView textView = (TextView) view.findViewById(android.R.id.title);
		if (textView != null) {
			textView.setMaxLines(mMaxLines);
			textView.setLines(mLines);
			textView.setMinLines(mMinLines);
			textView.setSingleLine(mSingleLine);
		}

		mSecondaryIconView.setImageDrawable(mSecondaryIcon);
		mSecondaryIconView.setVisibility(mSecondaryIcon == null ? View.GONE : View.VISIBLE);

		Switch switchWidget = getSwitchWidget(view);
		if (switchWidget != null) {
			switchWidget.setClickable(!mCheckOnClick);
		}
	}

	@Override
	protected void onClick() {
		if (mCheckOnClick) {
			super.onClick();
		}
	}

	private Switch getSwitchWidget(View view) {
		Switch switchWidget = null;
		LinearLayout widgetFrame = (LinearLayout) view.findViewById(android.R.id.widget_frame);
		if (widgetFrame != null) {
			int count = widgetFrame.getChildCount();
			for (int i = 0; i < count; i++) {
				View child = widgetFrame.getChildAt(i);
				if (child instanceof Switch) {
					int id = child.getId();
					String resourceName = getContext().getResources().getResourceName(id);
					if (TextUtils.equals(resourceName, "android:id/switchWidget")) {
						switchWidget = (Switch) child;
						break;
					}
				}
			}
		}
		return switchWidget;
	}
}
