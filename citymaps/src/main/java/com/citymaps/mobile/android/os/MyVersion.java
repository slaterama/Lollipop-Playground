package com.citymaps.mobile.android.os;

import android.text.TextUtils;

public class MyVersion
		implements Comparable<MyVersion> {

	// TODO Should "build" be constrained to at least 1 ?
	// TODO Should we allow any characters other than digit / a-z / hyphen / underscore ? A: No

	/*
		String[] test = new String[]{
			"1.0.0",
			"1.0.0.0001",
			"2.4.6-dev",
			"1.0-rel",
			"10.0.1-beta1.0",
			"v1.0.0",
			"1.0.0.1.1", <-- invalid
			"1.0.0-dev-build-4",
			"1.1b.14a_marker-test"
		};
	 */

//	private static final String PART_FORMAT = "\\d+\\w*";
//	private static final String FORMAT = "^v?\\d+\\w*(\\.\\d+\\w*){0,3}(-[\\w-]*)$";
	private static final String TRY_PARSE_EXP = "[^\\d]+.*";

	private static final String DOT_SEPARATOR = "\\.";
	private static final String HYPHEN_SEPARATOR = "-";

	private String mVersionString = null;
	private VersionPart mMajor = null;
	private VersionPart mMinor = null;
	private VersionPart mRelease = null;
	private VersionPart mBuild = null;
	private String mQualifier = null;

	public static MyVersion parseString(String versionString) {
		return new MyVersion(versionString);
	}

	private static int tryParse(String string) {
		String[] strings = string.split(TRY_PARSE_EXP);
		if (strings.length == 0) {
			return -1;
		} else {
			try {
				return Integer.parseInt(strings[0]);
			} catch (NumberFormatException e) {
				return -1;
			}
		}
	}

	public MyVersion() {
	}

	public MyVersion(String versionString) {
		if (versionString != null) {
			// Allow leading "v"
			if (versionString.toLowerCase().startsWith("v")) {
				versionString = versionString.substring(1);
			}

			String[] parts = versionString.split(HYPHEN_SEPARATOR, 2);
			if (parts.length > 1) {
				mQualifier = parts[1];
			}

			parts = parts[0].split(DOT_SEPARATOR);
			if (parts.length > 4) {
				throw new IllegalArgumentException();
			}

			for (int i = 0; i < parts.length; i++) {
				VersionPart versionPart = new VersionPart(parts[i]);
				if (mMajor == null) {
					mMajor = versionPart;
				} else if (mMinor == null) {
					mMinor = versionPart;
				} else if (mRelease == null) {
					mRelease = versionPart;
				} else if (mBuild == null) {
					mBuild = versionPart;
				}
			}

			mVersionString = versionString;
		}
	}

	private MyVersion(VersionPart major, VersionPart minor, VersionPart release,
					  VersionPart build, String qualifier) {
		mMajor = major;
		mMinor = minor;
		mRelease = release;
		mBuild = build;
		mQualifier = qualifier;
		mVersionString = toString();
	}

	public MyVersion(int major, int minor, int release, int build, String qualifier) {
		this(new VersionPart(major), new VersionPart(minor), new VersionPart(release),
				new VersionPart(build), qualifier);
	}

	public MyVersion(int major, int minor, int release, int build) {
		this(major, minor, release, build, null);
	}

	public MyVersion(int major, int minor, int release, String qualifier) {
		this(new VersionPart(major), new VersionPart(minor), new VersionPart(release), null, qualifier);
	}

	public MyVersion(int major, int minor, int release) {
		this(major, minor, release, null);
	}

	public MyVersion(int major, int minor, String qualifier) {
		this(new VersionPart(major), new VersionPart(minor), null, null, qualifier);
	}

	public MyVersion(int major, int minor) {
		this(major, minor, null);
	}

	public MyVersion(int major, String qualifier) {
		this(new VersionPart(major), null, null, null, qualifier);
	}

	public MyVersion(int major) {
		this(major, null);
	}

	@Override
	public int compareTo(MyVersion myVersion) {
		return 0;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	@Override
	public boolean equals(Object o) {
		return super.equals(o);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public String toString() {
		return super.toString(); // TODO
	}

	private static class VersionPart {
		int mValue;
		String mQualifier;

		public VersionPart(int value, String qualifier) {
			mValue = value;
			mQualifier = qualifier;
		}

		public VersionPart(int value) {
			this(value, null);
		}

		public VersionPart(String string) {
			if (string == null) {
				throw new IllegalArgumentException();
			}

			String numericString = string.replaceAll(TRY_PARSE_EXP, "");
			if (TextUtils.isEmpty(numericString)) {
				throw new IllegalArgumentException();
			}

			mValue = Integer.parseInt(numericString);
			mQualifier = string.substring(numericString.length());
		}

		@Override
		public String toString() {
			return super.toString();
		}
	}
}
