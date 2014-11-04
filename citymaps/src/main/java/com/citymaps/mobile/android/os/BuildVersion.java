package com.citymaps.mobile.android.os;

import android.support.annotation.NonNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

// TODO Better class JavaDoc description

/**
 * A class that encapsulates build version information.
 */
public class BuildVersion
		implements Comparable<BuildVersion> {
	/**
	 * A regular expression used to capture a version prefix.
	 */
	private static final String OPTIONAL_PREFIX_REGEX = "([A-Za-z_-]+)?";

	/**
	 * A regular expression used to capture a (major) version category.
	 */
	private static final String CATEGORY_REGEX = "(\\d+)(\\w+)?";

	/**
	 * A regular expression used to capture an optional version category (i.e. minor, release, or build).
	 */
	private static final String OPTIONAL_CATEGORY_REGEX = String.format("(?:\\.%s)?", CATEGORY_REGEX);

	/**
	 * A regular expression used to capture a version qualifier.
	 */
	private static final String QUALIFIER_REGEX = "[\\w\\.-]+";

	/**
	 * A regular expression used to capture a version qualifier.
	 */
	private static final String OPTIONAL_QUALIFIER_REGEX = String.format("(?:-(%s))?", QUALIFIER_REGEX);

	/**
	 * A {@link Pattern} used to parse a version string.
	 */
	private static final Pattern VERSION_PATTERN = Pattern.compile(String.format("^%s%s%s%s%s%s$",
			OPTIONAL_PREFIX_REGEX, CATEGORY_REGEX,
			OPTIONAL_CATEGORY_REGEX, OPTIONAL_CATEGORY_REGEX, OPTIONAL_CATEGORY_REGEX,
			OPTIONAL_QUALIFIER_REGEX));

	/**
	 * A {@link Pattern} used to parse a version category string.
	 */
	private static final Pattern CATEGORY_PATTERN = Pattern.compile(String.format("^%s$", CATEGORY_REGEX));

	/**
	 * A {@link Pattern} used to validate a qualifier string.
	 */
	private static final Pattern QUALIFIER_PATTERN = Pattern.compile(String.format("^%s$", QUALIFIER_REGEX));

	/**
	 * A group number used to access the prefix in a version string.
	 */
	private static final int PREFIX_GROUP = 1;

	/**
	 * A group number used to access the major value in a version string.
	 */
	private static final int MAJOR_VALUE_GROUP = 2;

	/**
	 * A group number used to access the major qualifier in a version string.
	 */
	private static final int MAJOR_QUALIFIER_GROUP = 3;

	/**
	 * A group number used to access the minor value in a version string.
	 */
	private static final int MINOR_VALUE_GROUP = 4;

	/**
	 * A group number used to access the minor qualifier in a version string.
	 */
	private static final int MINOR_QUALIFIER_GROUP = 5;

	/**
	 * A group number used to access the release value in a version string.
	 */
	private static final int RELEASE_VALUE_GROUP = 6;

	/**
	 * A group number used to access the release qualifier in a version string.
	 */
	private static final int RELEASE_QUALIFIER_GROUP = 7;

	/**
	 * A group number used to access the build value in a version string.
	 */
	private static final int BUILD_VALUE_GROUP = 8;

	/**
	 * A group number used to access the build qualifier in a version string.
	 */
	private static final int BUILD_QUALIFIER_GROUP = 9;

	/**
	 * A group number used to access the qualifier in a version string.
	 */
	private static final int QUALIFIER_GROUP = 10;

	/**
	 * A group number used to access the value in a category version string.
	 */
	private static final int CATEGORY_VALUE_GROUP = 1;

	/**
	 * A group number used to access the qualifier in a category version string.
	 */
	private static final int CATEGORY_QUALIFIER_GROUP = 2;

	/**
	 * Parses the specified string as a signed decimal integer value, returning 0
	 * if any runtime exceptions are caught.
	 * @param string The string representation of an integer value.
	 * @return The primitive integer value represented by {@code string}.
	 */
	private static int safeParseInt(String string) {
		try {
			return Integer.parseInt(string);
		} catch (RuntimeException e) {
			return 0;
		}
	}

	/**
	 * Compares two {@code int} values.
	 * @return 0 if lhs = rhs, less than 0 if lhs < rhs, and greater than 0 if lhs > rhs.
	 */
	private static int intCompare(int lhs, int rhs) {
		return (lhs < rhs) ? -1 : ((lhs == rhs) ? 0 : 1);
	}

	/**
	 * Compares two {@code String} values.
	 * @return 0 if lhs = rhs, less than 0 if lhs < rhs, and greater than 0 if lhs > rhs.
	 */
	private static int stringCompare(String lhs, String rhs) {
		if (lhs == null) {
			return (rhs == null ? 0 : -1);
		} else {
			return (rhs == null ? 1 : lhs.compareTo(rhs));
		}
	}

	/**
	 * The prefix of this version, if any.
	 */
	private final String mPrefix;

	/**
	 * A string representing the numeric portion of the major category of this version.
	 */
	private final String mMajorValue;

	/**
	 * A string representing the major category qualifier of this version, if any.
	 */
	private final String mMajorQualifier;

	/**
	 * A string representing the numeric portion of the minor category of this version, if any.
	 */
	private final String mMinorValue;

	/**
	 * A string representing the minor category qualifier of this version, if any.
	 */
	private final String mMinorQualifier;

	/**
	 * A string representing the numeric portion of the release category of this version, if any.
	 */
	private final String mReleaseValue;

	/**
	 * A string representing the release category qualifier of this version, if any.
	 */
	private final String mReleaseQualifier;

	/**
	 * A string representing the numeric portion of the build category of this version, if any.
	 */
	private final String mBuildValue;

	/**
	 * A string representing the build category qualifier of this version, if any.
	 */
	private final String mBuildQualifier;

	/**
	 * A string representing any additional qualifier of this version.
	 */
	private final String mQualifier;

	/**
	 * A string representation of this version.
	 */
	private final String mToString;

	/**
	 * Constructs a new {@code Version} with the specified version string.
	 * @param versionString The string representation of the version.
	 */
	public BuildVersion(String versionString) {
		if (versionString == null) {
			throw new IllegalArgumentException("Version string can not be null");
		}
		Matcher matcher = VERSION_PATTERN.matcher(versionString);
		if (matcher.find()) {
			mPrefix = matcher.group(PREFIX_GROUP);
			mMajorValue = matcher.group(MAJOR_VALUE_GROUP);
			mMajorQualifier = matcher.group(MAJOR_QUALIFIER_GROUP);
			mMinorValue = matcher.group(MINOR_VALUE_GROUP);
			mMinorQualifier = matcher.group(MINOR_QUALIFIER_GROUP);
			mReleaseValue = matcher.group(RELEASE_VALUE_GROUP);
			mReleaseQualifier = matcher.group(RELEASE_QUALIFIER_GROUP);
			mBuildValue = matcher.group(BUILD_VALUE_GROUP);
			mBuildQualifier = matcher.group(BUILD_QUALIFIER_GROUP);
			mQualifier = matcher.group(QUALIFIER_GROUP);
			mToString = versionString;
		} else {
			throw new IllegalArgumentException(String.format("'%s' is not a valid version string", versionString));
		}
	}

	/**
	 * Constructs a new {@code Version} with the specified version category values.
	 * @param major A string representing the major category.
	 * @param minor A string representing the minor category.
	 * @param release A string representing the release category.
	 * @param build A string representing the build category.
	 * @param qualifier Any additional qualifier to append to this version.
	 */
	public BuildVersion(String major, String minor, String release, String build, String qualifier) {
		mPrefix = null;
		if (major == null) {
			throw new IllegalArgumentException("Major category can not be null");
		} else {
			Matcher matcher = CATEGORY_PATTERN.matcher(major);
			if (matcher.find()) {
				mMajorValue = matcher.group(CATEGORY_VALUE_GROUP);
				mMajorQualifier = matcher.group(CATEGORY_QUALIFIER_GROUP);
			} else {
				throw new IllegalArgumentException(String.format("'%s' is not a valid major category", major));
			}
		}
		if (minor == null) {
			mMinorValue = null;
			mMinorQualifier = null;
		} else {
			Matcher matcher = CATEGORY_PATTERN.matcher(minor);
			if (matcher.find()) {
				mMinorValue = matcher.group(CATEGORY_VALUE_GROUP);
				mMinorQualifier = matcher.group(CATEGORY_QUALIFIER_GROUP);
			} else {
				throw new IllegalArgumentException(String.format("'%s' is not a valid minor category", minor));
			}
		}
		if (release == null) {
			mReleaseValue = null;
			mReleaseQualifier = null;
		} else {
			Matcher matcher = CATEGORY_PATTERN.matcher(release);
			if (matcher.find()) {
				mReleaseValue = matcher.group(CATEGORY_VALUE_GROUP);
				mReleaseQualifier = matcher.group(CATEGORY_QUALIFIER_GROUP);
			} else {
				throw new IllegalArgumentException(String.format("'%s' is not a valid release category", release));
			}
		}
		if (build == null) {
			mBuildValue = null;
			mBuildQualifier = null;
		} else {
			Matcher matcher = CATEGORY_PATTERN.matcher(build);
			if (matcher.find()) {
				mBuildValue = matcher.group(CATEGORY_VALUE_GROUP);
				mBuildQualifier = matcher.group(CATEGORY_QUALIFIER_GROUP);
			} else {
				throw new IllegalArgumentException(String.format("'%s' is not a valid build category", build));
			}
		}
		if (qualifier == null) {
			mQualifier = null;
		} else {
			Matcher matcher = QUALIFIER_PATTERN.matcher(qualifier);
			if (matcher.find()) {
				mQualifier = qualifier;
			} else {
				throw new IllegalArgumentException(String.format("'%s' is not a valid qualifier", qualifier));
			}
		}
		mToString = toString0();
	}
	
	public BuildVersion(int major, int minor, int release, int build, String qualifier) {
		this(String.valueOf(major), String.valueOf(minor), String.valueOf(release), String.valueOf(build), qualifier);
	}

	public BuildVersion(int major, int minor, int release, String qualifier) {
		this(String.valueOf(major), String.valueOf(minor), String.valueOf(release), null, qualifier);
	}

	public BuildVersion(int major, int minor, String qualifier) {
		this(String.valueOf(major), String.valueOf(minor), null, null, qualifier);
	}

	public BuildVersion(int major, String qualifier) {
		this(String.valueOf(major), null, null, null, qualifier);
	}

	/**
	 * Returns the prefix of this {@code Version}.
	 * @return The prefix.
	 */
	public String getPrefix() {
		return mPrefix;
	}

	/**
	 * Returns the major value of this {@code Version}.
	 * @return The major value.
	 */
	public String getMajorValue() {
		return mMajorValue;
	}

	/**
	 * Returns the major value of this {@code Version} as an int.
	 * @return The major value.
	 */
	public int getMajorInt() {
		return safeParseInt(mMajorValue);
	}

	/**
	 * Returns the major qualifier of this {@code Version}, if any.
	 * @return The major qualifier.
	 */
	public String getMajorQualifier() {
		return mMajorQualifier;
	}

	/**
	 * Returns the minor value of this {@code Version}.
	 * @return The minor value.
	 */
	public String getMinorValue() {
		return mMinorValue;
	}

	/**
	 * Returns the major value of this {@code Version} as an int.
	 * @return The major value.
	 */
	public int getMinorInt() {
		return safeParseInt(mMinorValue);
	}

	/**
	 * Returns the minor qualifier of this {@code Version}, if any.
	 * @return The minor qualifier.
	 */
	public String getMinorQualifier() {
		return mMinorQualifier;
	}

	/**
	 * Returns the release value of this {@code Version}.
	 * @return The release value.
	 */
	public String getReleaseValue() {
		return mReleaseValue;
	}

	/**
	 * Returns the release value of this {@code Version} as an int.
	 * @return The release value.
	 */
	public int getReleaseInt() {
		return safeParseInt(mReleaseValue);
	}

	/**
	 * Returns the release qualifier of this {@code Version}, if any.
	 * @return The release qualifier.
	 */
	public String getReleaseQualifier() {
		return mReleaseQualifier;
	}

	/**
	 * Returns the build value of this {@code Version}.
	 * @return The build value.
	 */
	public String getBuildValue() {
		return mBuildValue;
	}

	/**
	 * Returns the build value of this {@code Version} as an int.
	 * @return The build value.
	 */
	public int getBuildInt() {
		return safeParseInt(mBuildValue);
	}

	/**
	 * Returns the build qualifier of this {@code Version}, if any.
	 * @return The build qualifier.
	 */
	public String getBuildQualifier() {
		return mBuildQualifier;
	}

	/**
	 * Returns the qualifier associated with this {@code Version}, if any.
	 * @return The version qualifier.
	 */
	public String getQualifier() {
		return mQualifier;
	}

	/**
	 * Constructs a string representation of this {@code Version}.
	 * @return A concise, human-readable description of this {@code Version}.
	 */
	private String toString0() {
		StringBuilder builder = new StringBuilder();
		if (mPrefix != null) {
			builder.append(mPrefix);
		}
		if (mMajorValue != null) {
			builder.append(mMajorValue);
			if (mMajorQualifier != null) {
				builder.append(mMajorQualifier);
			}
		}
		if (mMinorValue != null) {
			builder.append(".").append(mMinorValue);
			if (mMinorQualifier != null) {
				builder.append(mMinorQualifier);
			}
		}
		if (mReleaseValue != null) {
			builder.append(".").append(mReleaseValue);
			if (mReleaseQualifier != null) {
				builder.append(mReleaseQualifier);
			}
		}
		if (mBuildValue != null) {
			builder.append(".").append(mBuildValue);
			if (mBuildQualifier != null) {
				builder.append(mBuildQualifier);
			}
		}
		if (mQualifier != null) {
			builder.append("-").append(mQualifier);
		}
		return builder.toString();
	}

	/**
	 * Compares this {@code Version} to the specified {@code Version} to determine their relative order.
	 * @param another The {@code Version} to compare to this instance.
	 * @return A negative integer if this instance is less than {@code another}; a positive integer if this instance is
	 * greater than {@code another}; 0 if this instance has the same order as {@code another}.
	 */
	@Override
	public int compareTo(@NonNull BuildVersion another) {
		int compareTo = intCompare(getMajorInt(), another.getMajorInt());
		if (compareTo != 0) {
			return compareTo;
		}
		compareTo = stringCompare(mMajorQualifier, another.mMajorQualifier);
		if (compareTo != 0) {
			return compareTo;
		}
		compareTo = intCompare(getMinorInt(), another.getMinorInt());
		if (compareTo != 0) {
			return compareTo;
		}
		compareTo = stringCompare(mMinorQualifier, another.mMinorQualifier);
		if (compareTo != 0) {
			return compareTo;
		}
		compareTo = intCompare(getReleaseInt(), another.getReleaseInt());
		if (compareTo != 0) {
			return compareTo;
		}
		compareTo = stringCompare(mReleaseQualifier, another.mReleaseQualifier);
		if (compareTo != 0) {
			return compareTo;
		}
		compareTo = intCompare(getBuildInt(), another.getBuildInt());
		if (compareTo != 0) {
			return compareTo;
		}
		compareTo = stringCompare(mBuildQualifier, another.mBuildQualifier);
		if (compareTo != 0) {
			return compareTo;
		}
		return stringCompare(mQualifier, another.mQualifier);
	}

	@Override
	public String toString() {
		return mToString;
	}
}
