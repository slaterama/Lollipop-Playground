//
//  FileName.java
//  Citymaps
//
//  Created by Scott Slater on 10/31/14.
//  Copyright (c) 2014 CityMaps. All rights reserved.
//

package com.citymaps.mobile.android.os;

public class Build {

	public static class VERSION {
		/**
		 * The user-visible version string.  E.g., "1.0" or "3.4b5".
		 */
		public static final String RELEASE = "";

		/**
		 * The user-visible SDK version of the framework; its possible
		 * values are defined in {@link VERSION_CODES}.
		 */
		public static final int SDK_INT = VERSION_CODES.GOTHAM;

		/**
		 * The current development codename, or the string "REL" if this is
		 * a release build.
		 */
		public static final String CODENAME = "GOTHAM";

		/**
		 * The SDK version to use when accessing resources.
		 * Use the current SDK version code.  If we are a development build,
		 * also allow the previous SDK version + 1.
		 * @hide
		 */
		public static final int RESOURCES_SDK_INT = SDK_INT
				+ ("REL".equals(CODENAME) ? 0 : 1);
	}

	public static class VERSION_CODES {
		/**
		 * Magic version number for a current development build, which has
		 * not yet turned into an official release.
		 */
		public static final int CUR_DEVELOPMENT = 10000;

		/**
		 * December 2013: The original, first, version of Citymaps for Android.
		 */
		public static final int CORE = 1;

		/**
		 * June 2014: The first "official" release of Citymaps for Android.
		 */
		public static final int BIG_PAPI = 2;

		/**
		 * August 2014: Not a real release per se but incorporated many changes
		 * that led to a brand-new look and feel.
		 */
		public static final int EXPLORE = 3;

		/**
		 * December 2014: The first major reworking of Citymaps for Android.
		 */
		public static final int GOTHAM = 4;
	}
}
