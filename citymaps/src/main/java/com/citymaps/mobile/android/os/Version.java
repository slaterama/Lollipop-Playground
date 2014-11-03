package com.citymaps.mobile.android.os;

import java.util.NoSuchElementException;
import java.util.StringTokenizer;

/**
 * Version identifier for capabilities such as bundles and packages.
 * <p/>
 * Version identifiers have four components.
 * <ol>
 * <li>Major version. A non-negative integer.</li>
 * <li>Minor version. A non-negative integer.</li>
 * <li>Revision. A non-negative integer.</li>
 * <li>Qualifier. A text string. See {@code Version(String)} for the format of
 * the qualifier string.</li>
 * </ol>
 */
public class Version {

	private static final String SEPARATOR = ".";

	public static final Version EMPTY_VERSION = new Version(0, 0, 0);

	private final int mMajor;
	private final int mMinor;
	private final int mRevision;
	private final String mQualifier;
	private transient String mVersionString /* default to null */;
	private transient int mHash /* default to 0 */;

	/**
	 * Creates a version identifier from the specified numerical components.
	 * <p/>
	 * The qualifier is set to the empty string.
	 *
	 * @param major    Major component of the version identifier.
	 * @param minor    Minor component of the version identifier.
	 * @param revision Revision component of the version identifier.
	 * @throws IllegalArgumentException If the numerical components are
	 *                                  negative.
	 */
	public Version(int major, int minor, int revision) {
		this(major, minor, revision, null);
	}

	/**
	 * Creates a version identifier from the specified components.
	 *
	 * @param major     Major component of the version identifier.
	 * @param minor     Minor component of the version identifier.
	 * @param revision  Revision component of the version identifier.
	 * @param qualifier Qualifier component of the version identifier. If
	 *                  {@code null} is specified, then the qualifier will be set to the
	 *                  empty string.
	 * @throws IllegalArgumentException If the numerical components are negative
	 *                                  or the qualifier string is invalid.
	 */
	public Version(int major, int minor, int revision, String qualifier) {
		if (qualifier == null) {
			qualifier = "";
		}

		mMajor = major;
		mMinor = minor;
		mRevision = revision;
		mQualifier = qualifier;
		validate();
	}

	/**
	 * Creates a version identifier from the specified string.
	 * <p/>
	 * Version string grammar:
	 * <p/>
	 * <pre>
	 * version ::= major('.'minor('.'revision('.'qualifier)?)?)?
	 * major ::= digit+
	 * minor ::= digit+
	 * revision ::= digit+
	 * qualifier ::= (alpha|digit|'_'|'-')+
	 * digit ::= [0..9]
	 * alpha ::= [a..zA..Z]
	 * </pre>
	 *
	 * @param version String representation of the version identifier. There
	 *                must be no whitespace in the argument.
	 * @throws IllegalArgumentException If {@code version} is improperly
	 *                                  formatted.
	 */
	public Version(String version) {
		int major = 0;
		int minor = 0;
		int revision = 0;
		String qualifier = "";

		try {
			StringTokenizer st = new StringTokenizer(version, SEPARATOR, true);
			major = parseInt(st.nextToken(), version);

			if (st.hasMoreTokens()) { // minor
				st.nextToken(); // consume delimiter
				minor = parseInt(st.nextToken(), version);

				if (st.hasMoreTokens()) { // revision
					st.nextToken(); // consume delimiter
					revision = parseInt(st.nextToken(), version);

					if (st.hasMoreTokens()) { // qualifier separator
						st.nextToken(); // consume delimiter
						qualifier = st.nextToken(""); // remaining string

						if (st.hasMoreTokens()) { // fail safe
							throw new IllegalArgumentException("invalid version \"" + version + "\": invalid format");
						}
					}
				}
			}
		} catch (NoSuchElementException e) {
			IllegalArgumentException iae = new IllegalArgumentException("invalid version \"" + version + "\": invalid format");
			iae.initCause(e);
			throw iae;
		}

		mMajor = major;
		mMinor = minor;
		mRevision = revision;
		mQualifier = qualifier;
		validate();
	}

	/**
	 * Parse numeric component into an int.
	 *
	 * @param value   Numeric component
	 * @param version Complete version string for exception message, if any
	 * @return int value of numeric component
	 */
	private static int parseInt(String value, String version) {
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			IllegalArgumentException iae = new IllegalArgumentException("invalid version \"" + version + "\": non-numeric \"" + value + "\"");
			iae.initCause(e);
			throw iae;
		}
	}

	/**
	 * Called by the Version constructors to validate the version components.
	 *
	 * @throws IllegalArgumentException If the numerical components are negative
	 *                                  or the qualifier string is invalid.
	 */
	private void validate() {
		if (mMajor < 0) {
			throw new IllegalArgumentException("invalid version \"" + toString0() + "\": negative number \"" + mMajor + "\"");
		}
		if (mMinor < 0) {
			throw new IllegalArgumentException("invalid version \"" + toString0() + "\": negative number \"" + mMinor + "\"");
		}
		if (mRevision < 0) {
			throw new IllegalArgumentException("invalid version \"" + toString0() + "\": negative number \"" + mRevision + "\"");
		}
		for (char ch : mQualifier.toCharArray()) {
			if (('A' <= ch) && (ch <= 'Z')) {
				continue;
			}
			if (('a' <= ch) && (ch <= 'z')) {
				continue;
			}
			if (('0' <= ch) && (ch <= '9')) {
				continue;
			}
			if ((ch == '_') || (ch == '-')) {
				continue;
			}
			throw new IllegalArgumentException("invalid version \"" + toString0() + "\": invalid qualifier \"" + mQualifier + "\"");
		}
	}

	/**
	 * Parses a version identifier from the specified string.
	 * <p/>
	 * <p/>
	 * See {@code Version(String)} for the format of the version string.
	 *
	 * @param version String representation of the version identifier. Leading
	 *                and trailing whitespace will be ignored.
	 * @return A {@code Version} object representing the version identifier. If
	 * {@code version} is {@code null} or the empty string then
	 * {@code emptyVersion} will be returned.
	 * @throws IllegalArgumentException If {@code version} is improperly
	 *                                  formatted.
	 */
	public static Version parseVersion(String version) {
		if (version == null) {
			return EMPTY_VERSION;
		}

		version = version.trim();
		if (version.length() == 0) {
			return EMPTY_VERSION;
		}

		return new Version(version);
	}

	/**
	 * Returns the major component of this version identifier.
	 *
	 * @return The major component.
	 */
	public int getMajor() {
		return mMajor;
	}

	/**
	 * Returns the minor component of this version identifier.
	 *
	 * @return The minor component.
	 */
	public int getMinor() {
		return mMinor;
	}

	/**
	 * Returns the revision component of this version identifier.
	 *
	 * @return The revision component.
	 */
	public int getRevision() {
		return mRevision;
	}

	/**
	 * Returns the qualifier component of this version identifier.
	 *
	 * @return The qualifier component.
	 */
	public String getQualifier() {
		return mQualifier;
	}

	/**
	 * Returns the string representation of this version identifier.
	 * <p/>
	 * <p/>
	 * The format of the version string will be {@code major.minor.revision} if
	 * qualifier is the empty string or {@code major.minor.revision.qualifier}
	 * otherwise.
	 *
	 * @return The string representation of this version identifier.
	 */
	public String toString() {
		return toString0();
	}

	/**
	 * Internal toString behavior
	 *
	 * @return The string representation of this version identifier.
	 */
	String toString0() {
		if (mVersionString != null) {
			return mVersionString;
		}
		int q = mQualifier.length();
		StringBuilder result = new StringBuilder(20 + q);
		result.append(mMajor);
		result.append(SEPARATOR);
		result.append(mMinor);
		result.append(SEPARATOR);
		result.append(mRevision);
		if (q > 0) {
			result.append(SEPARATOR);
			result.append(mQualifier);
		}
		return mVersionString = result.toString();
	}

	/**
	 * Returns a hash code value for the object.
	 *
	 * @return An integer which is a hash code value for this object.
	 */
	public int hashCode() {
		if (mHash != 0) {
			return mHash;
		}
		int h = 31 * 17;
		h = 31 * h + mMajor;
		h = 31 * h + mMinor;
		h = 31 * h + mRevision;
		h = 31 * h + mQualifier.hashCode();
		return mHash = h;
	}

	/**
	 * Compares this {@code Version} object to another object.
	 * <p/>
	 * <p/>
	 * A version is considered to be <b>equal to </b> another version if the
	 * major, minor and revision components are equal and the qualifier component
	 * is equal (using {@code String.equals}).
	 *
	 * @param object The {@code Version} object to be compared.
	 * @return {@code true} if {@code object} is a {@code Version} and is equal
	 * to this object; {@code false} otherwise.
	 */
	public boolean equals(Object object) {
		if (object == this) { // quicktest
			return true;
		}

		if (!(object instanceof Version)) {
			return false;
		}

		Version other = (Version) object;
		return (mMajor == other.mMajor) && (mMinor == other.mMinor) && (mRevision == other.mRevision) && mQualifier.equals(other.mQualifier);
	}

	/**
	 * Compares this {@code Version} object to another {@code Version}.
	 * <p/>
	 * <p/>
	 * A version is considered to be <b>less than</b> another version if its
	 * major component is less than the other version's major component, or the
	 * major components are equal and its minor component is less than the other
	 * version's minor component, or the major and minor components are equal
	 * and its revision component is less than the other version's revision component,
	 * or the major, minor and revision components are equal and it's qualifier
	 * component is less than the other version's qualifier component (using
	 * {@code String.compareTo}).
	 * <p/>
	 * <p/>
	 * A version is considered to be <b>equal to</b> another version if the
	 * major, minor and revision components are equal and the qualifier component
	 * is equal (using {@code String.compareTo}).
	 *
	 * @param other The {@code Version} object to be compared.
	 * @return A negative integer, zero, or a positive integer if this version
	 * is less than, equal to, or greater than the specified
	 * {@code Version} object.
	 * @throws ClassCastException If the specified object is not a
	 *                            {@code Version} object.
	 */
	public int compareTo(Version other) {
		if (other == this) { // quicktest
			return 0;
		}

		int result = mMajor - other.mMajor;
		if (result != 0) {
			return result;
		}

		result = mMinor - other.mMinor;
		if (result != 0) {
			return result;
		}

		result = mRevision - other.mRevision;
		if (result != 0) {
			return result;
		}

		return mQualifier.compareTo(other.mQualifier);
	}
}
