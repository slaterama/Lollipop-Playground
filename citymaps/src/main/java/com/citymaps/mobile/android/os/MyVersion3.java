package com.citymaps.mobile.android.os;

import android.text.TextUtils;
import com.citymaps.mobile.android.util.LogEx;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyVersion3 {

	private static final String[] TEST_STRINGS = new String[]{
			"1.0.0",
			"1.0.0.0001",
			"2.4.6-dev",
			"1.0-rel",
			"10.0.1-beta1.0",
			"v1.0.0",
			"1.0.0.1.1",
			"cm4.005.13",
			"1.0.0-dev-build-4",
			"1.1b.14a_marker-test",
			"v1.0.0-",
			"v1.0.0-test"
	};

	public static void testMyVersion3() {
		for (int i = 0; i < TEST_STRINGS.length; i++) {
			try {
				MyVersion3 version = new MyVersion3(TEST_STRINGS[i]);
				LogEx.d(String.format("String=%s, version=%s", TEST_STRINGS[i], version));
			} catch (IllegalArgumentException e) {
				LogEx.d(String.format("String=%s, message=%s", TEST_STRINGS[i], e.getMessage()));
			}
		}

//		MyVersion3 version = new MyVersion3(1, 0, 0);
//		LogEx.d(String.format("version=%s", version));

//		version = new MyVersion3(12, 5, 1, "dev");
//		LogEx.d(String.format("version=%s", version));
	}

	private static final String OPTIONAL_PREFIX_REGEX = "([A-Za-z_-]+)?";

	private static final String MANDATORY_VERSION_CATEGORY = "(\\d+)(\\w+)?";

	private static final String OPTIONAL_VERSION_CATEGORY = "(?:\\.(\\d+)(\\w+)?)?";

	private static final String OPTIONAL_QUALIFIER = "(?:-([\\w\\.-]+))?";

	private static final String VERSION_REGEX = String.format("^%s%s%s%s%s%s$",
			OPTIONAL_PREFIX_REGEX, MANDATORY_VERSION_CATEGORY, OPTIONAL_VERSION_CATEGORY,
			OPTIONAL_VERSION_CATEGORY, OPTIONAL_VERSION_CATEGORY, OPTIONAL_QUALIFIER);

	private static final Pattern VERSION_PATTERN = Pattern.compile(VERSION_REGEX);

	private static final int PREFIX_GROUP = 1;

	private static final int MAJOR_VALUE_GROUP = 2;

	private static final int MAJOR_QUALIFIER_GROUP = 3;

	private static final int MINOR_VALUE_GROUP = 4;

	private static final int MINOR_QUALIFIER_GROUP = 5;

	private static final int RELEASE_VALUE_GROUP = 6;

	private static final int RELEASE_QUALIFIER_GROUP = 7;

	private static final int BUILD_VALUE_GROUP = 8;

	private static final int BUILD_QUALIFIER_GROUP = 9;

	private static final int QUALIFIER_GROUP = 10;

	private String mVersionString;

	private String mPrefix;

	private Category mMajor;

	private Category mMinor;

	private Category mRelease;

	private Category mBuild;
	
	private String mQualifier;

	private String mToString;

	private MyVersion3(String versionString) {
		Matcher matcher = VERSION_PATTERN.matcher(versionString);
		if (matcher.find()) {
			mPrefix = matcher.group(PREFIX_GROUP);
			mMajor = Category.newInstance(matcher.group(MAJOR_VALUE_GROUP), matcher.group(MAJOR_QUALIFIER_GROUP));
			mMinor = Category.newInstance(matcher.group(MINOR_VALUE_GROUP), matcher.group(MINOR_QUALIFIER_GROUP));
			mRelease = Category.newInstance(matcher.group(RELEASE_VALUE_GROUP), matcher.group(RELEASE_QUALIFIER_GROUP));
			mBuild = Category.newInstance(matcher.group(BUILD_VALUE_GROUP), matcher.group(BUILD_QUALIFIER_GROUP));
			mQualifier = matcher.group(QUALIFIER_GROUP);
			mVersionString = versionString;
		} else {
			throw new IllegalArgumentException(String.format("versionString '%s' is not a valid version string", versionString));
		}
	}

	private static class Category {
		public static Category newInstance(String valueString, String qualifier) {
			return (valueString == null ? null : new Category(valueString, qualifier));
		}

		private int mValueInt;
		private String mQualifier;

		private String mToString;

		private Category(String value, String qualifier) {
			mValueInt = Integer.parseInt(value);
			mQualifier = qualifier;
		}

		@Override
		public String toString() {
			if (mToString == null) {
				mToString = toString0();
			}
			return mToString;
		}

		public String toString0() {
			ToStringBuilder builder = new ToStringBuilder(this)
					.append("mValueInt", mValueInt);
			if (!TextUtils.isEmpty(mQualifier)) {
				builder.append("mQualifier", mQualifier);
			}
			return builder.toString();
		}
	}

	@Override
	public String toString() {
		if (mToString == null) {
			mToString = toString0();
		}
		return mToString;
	}

	public String toString0() {
		ToStringBuilder builder = new ToStringBuilder(this).append("mVersionString", mVersionString);
		if (mPrefix != null) {
			builder.append("mPrefix", mPrefix);
		}
		if (mMajor != null) {
			builder.append("mMajor", mMajor);
		}
		if (mMinor != null) {
			builder.append("mMinor", mMinor);
		}
		if (mRelease != null) {
			builder.append("mRelease", mRelease);
		}
		if (mBuild != null) {
			builder.append("mBuild", mBuild);
		}
		if (mQualifier != null) {
			builder.append("mQualifier", mQualifier);
		}
		return builder.toString();
	}
}
