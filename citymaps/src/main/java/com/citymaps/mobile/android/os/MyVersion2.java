package com.citymaps.mobile.android.os;

import com.citymaps.mobile.android.util.LogEx;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class MyVersion2 {

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
			"1.1b.14a_marker-test"
	};

	public static void testMyVersion2() {
		for (int i = 0; i < TEST_STRINGS.length; i++) {
			try {
				MyVersion2 version = new MyVersion2(TEST_STRINGS[i]);
				LogEx.d(String.format("String=%s, version=%s", TEST_STRINGS[i], version));
			} catch (IllegalArgumentException e) {
				LogEx.d(String.format("String=%s, message=%s", TEST_STRINGS[i], e.getMessage()));
			}
		}

		MyVersion2 version = new MyVersion2(1, 0, 0);
		LogEx.d(String.format("version=%s", version));

		version = new MyVersion2(12, 5, 1, "dev");
		LogEx.d(String.format("version=%s", version));
	}

	private static final String PREFIX_REGEX = "[^A-Za-z_].*";

	private static final String VALUE_REGEX = "[^\\d]*$";

	private static final String HYPHEN_REGEX = "-";

	private static final String DOT_REGEX = "\\.";

	private String mVersionString = null;

	private String mPrefix = null;

	private Category mMajor = null;

	private Category mMinor = null;

	private Category mRelease = null;

	private Category mBuild = null;

	private String mQualifier = null;

	private MyVersion2(Category major, Category minor, Category release, Category build, String qualifier) {
		mMajor = major;
		mMinor = minor;
		mRelease = release;
		mBuild = build;
		if (qualifier != null && !qualifier.matches("^\\w*$")) {
			throw new IllegalArgumentException();
		}
		mQualifier = qualifier;
	}

	public MyVersion2(int major, int minor, int release, int build, String qualifier) {
		this(new Category(major), new Category(minor), new Category(release), new Category(build), qualifier);
	}

	public MyVersion2(int major, int minor, int release, int build) {
		this(new Category(major), new Category(minor), new Category(release), new Category(build), null);
	}

	public MyVersion2(int major, int minor, int release, String qualifier) {
		this(new Category(major), new Category(minor), new Category(release), null, qualifier);
	}

	public MyVersion2(int major, int minor, int release) {
		this(new Category(major), new Category(minor), new Category(release), null, null);
	}

	public MyVersion2(int major, int minor, String qualifier) {
		this(new Category(major), new Category(minor), null, null, qualifier);
	}

	public MyVersion2(int major, int minor) {
		this(new Category(major), new Category(minor), null, null, null);
	}

	public MyVersion2(int major, String qualifier) {
		this(new Category(major), null, null, null, qualifier);
	}

	public MyVersion2(int major) {
		this(new Category(major), null, null, null, null);
	}

	public MyVersion2(String versionString) {
		if (versionString == null) {
			throw new IllegalArgumentException();
		}

		mPrefix = versionString.replaceFirst(PREFIX_REGEX, "");
		String[] substrings = versionString.substring(mPrefix.length()).split(HYPHEN_REGEX, 2);
		if (substrings.length == 2) {
			mQualifier = substrings[1];
		}

		substrings = substrings[0].split(DOT_REGEX);
		if (substrings.length > 4) {
			throw new IllegalArgumentException(
					"Version number strings cannot have more than four categories (major, minor, release, build)");
		}

		for (String substring : substrings) {
			Category category = new Category(substring);
			if (mMajor == null) {
				mMajor = category;
			} else if (mMinor == null) {
				mMinor = category;
			} else if (mRelease == null) {
				mRelease = category;
			} else if (mBuild == null) {
				mBuild = category;
			}
		}

		mVersionString = versionString;
	}

	@Override
	public String toString() {
		if (mVersionString == null) {
			StringBuilder builder = new StringBuilder();
			if (mPrefix != null) {
				builder.append(mPrefix);
			}
			builder.append(mMajor);
			if (mMinor != null) {
				builder.append(".").append(mMinor);
				if (mRelease != null) {
					builder.append(".").append(mRelease);
					if (mBuild != null) {
						builder.append(".").append(mBuild);
					}
				}
			}
			if (mQualifier != null) {
				builder.append("-").append(mQualifier);
			}
			mVersionString = builder.toString();
		}
		return new ToStringBuilder(this).append(mVersionString).toString();
	}

	private static class Category {

		public String mString = null;

		public int mValue = 0;

		public int mWidth = 0;

		public String mQualifier = null;

		public Category(int value) {
			mValue = value;
			mWidth = 1;
		}

		public Category(String categoryString) {
			try {
				String valueString = categoryString.replaceFirst(VALUE_REGEX, "");
				mValue = Integer.parseInt(valueString);
				mWidth = valueString.length();
				mQualifier = categoryString.substring(mWidth);
				mString = categoryString;
			} catch (IllegalArgumentException e) {
				throw new IllegalArgumentException("Invalid version number category");
			}
		}

		@Override
		public String toString() {
			if (mString == null) {
				StringBuilder builder = new StringBuilder();
				String format = "%0" + String.valueOf(mWidth) + "d";
				builder.append(String.format(format, mValue));
				if (mQualifier != null) {
					builder.append(mQualifier);
				}
				mString = builder.toString();
			}
			return mString;
		}
	}
}
