package com.citymaps.mobile.android.util;

import android.util.Patterns;
import com.citymaps.mobile.android.BuildConfig;

import java.util.regex.Pattern;

public class CitymapsPatterns {

	public static final Pattern PASSWORD = Pattern.compile(
			String.format("^.{%d,%d}$", BuildConfig.PASSWORD_MIN_LENGTH, BuildConfig.PASSWORD_MAX_LENGTH),
			Pattern.UNICODE_CASE);

	public static final Pattern USERNAME = Pattern.compile(
			String.format("^[\\p{L}\\p{N}][\\p{L}\\p{N}\\-_\\.]{%d,%d}$",
					Math.max(BuildConfig.USERNAME_MIN_LENGTH - 1, 0),
					Math.max(BuildConfig.USERNAME_MAX_LENGTH - 1, 0)),
			Pattern.UNICODE_CASE);

	public static final Pattern NAME = Pattern.compile(
			String.format("^[\\p{L}][\\p{L}\\-_ ]{%d,%d}$",
					Math.max(BuildConfig.NAME_MIN_LENGTH - 1, 0),
					Math.max(BuildConfig.NAME_MAX_LENGTH - 1, 0)),
			Pattern.UNICODE_CASE);

	private CitymapsPatterns() {
	}
}
