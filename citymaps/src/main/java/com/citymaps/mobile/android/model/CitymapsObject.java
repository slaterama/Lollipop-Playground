package com.citymaps.mobile.android.model;

import android.content.Context;
import android.util.SparseArray;
import com.citymaps.mobile.android.R;

/**
 * An object maintained by the Citymaps application. Every object has a type, an ID, and a name.
 * Type is read-only and is provided by the various classes that implement this interface.
 */
public interface CitymapsObject {

	/**
	 * Gets the type of this object.
	 * @return The type of this object.
	 */
	public ObjectType getType();

	/**
	 * Gets the ID of this object.
	 * @return The ID of this object.
	 */
	public String getId();

	/**
	 * Sets the ID of this object.
	 * @param id The new ID.
	 */
	public void setId(String id);

	/**
	 * Returns the name of this object.
	 * @return The name of this object.
	 */
	public String getName();

	/**
	 * Sets the name of this object.
	 * @param name The new name.
	 */
	public void setName(String name);

	/*
	 * Enums
	 */

	/**
	 * Defines an enumeration for the various types of objects used in the Citymaps app.
	 */
	public static enum ObjectType {
		/**
		 * Indicates an unknown object type.
		 */
		UNKNOWN(0, R.plurals.cmobj_unknown, R.plurals.cmobj_unknown_capital),

		/**
		 * Indicates an object that represents a business.
		 */
		BUSINESS(1, R.plurals.cmobj_business, R.plurals.cmobj_business_capital),

		/**
		 * Indicates an object that represents a collection.
		 */
		COLLECTION(2, R.plurals.cmobj_collection, R.plurals.cmobj_collection_capital),

		/**
		 * Indicates an object that represents a user.
		 */
		USER(3, R.plurals.cmobj_user, R.plurals.cmobj_user_capital),

		/**
		 * Indicates an object that represents a tip.
		 */
		TIP(4, R.plurals.cmobj_tip, R.plurals.cmobj_tip_capital),

		/**
		 * Indicates an object that represents a marker.
		 */
		MARKER(5, R.plurals.cmobj_marker, R.plurals.cmobj_marker_capital),

		/**
		 * Indicates an object that represents a region.
		 */
		REGION(6, R.plurals.cmobj_region, R.plurals.cmobj_region_capital),

		/**
		 * Indicates an object that represents a place of interest.
		 */
		POI(12, R.plurals.cmobj_poi, R.plurals.cmobj_poi_capital),

		/**
		 * Indicates an object that represents a geocoded address.
		 */
		GEOCODED_ADDRESS(13, R.plurals.cmobj_geocoded_address, R.plurals.cmobj_geocoded_address_capital),

		/**
		 * Indicates an object that represents a country.
		 */
		COUNTRY(61, R.plurals.cmobj_country, R.plurals.cmobj_country_capital),

		/**
		 * Indicates an object that represents a state.
		 */
		STATE(62, R.plurals.cmobj_state, R.plurals.cmobj_state_capital),

		/**
		 * Indicates an object that represents a city.
		 */
		CITY(63, R.plurals.cmobj_city, R.plurals.cmobj_city_capital),

		/**
		 * Indicates an object that represents a neighborhood.
		 */
		NEIGHBORHOOD(64, R.plurals.cmobj_neighborhood, R.plurals.cmobj_neighborhood_capital);

		/**
		 * A lookup array used to map integer values with their corresponding enum constants.
		 */
		private static SparseArray<ObjectType> sLookup;

		/**
		 * Returns the enum constant of this type with the specified value.
		 * @param value The integer value of the enum constant to be returned.
		 * @return The enum constant with the specified value.
		 */
		public static ObjectType valueOf(int value) {
			if (sLookup == null) {
				sLookup = new SparseArray<ObjectType>(values().length);
				for (ObjectType type : values()) {
					sLookup.put(type.value(), type);
				}
			}

			return sLookup.get(value, UNKNOWN);
		}

		/**
		 * The integer value associated with this enum constant.
		 */
		private int mValue;

		/**
		 * The plural resource id associated with this enum constant.
		 */

		private int mPluralResId;

		/**
		 * The plural resource id associated with this enum constant when the result should be capitalized.
		 */

		private int mCapitalPluralResId;

		/**
		 * Creates an instance of this enum constant with the given value and resource id.
		 * @param value The integer value associated with this enum constant.
		 * @param pluralResId The plural resource id associated with this enum constant.
		 */
		private ObjectType(int value, int pluralResId, int capitalPluralResId) {
			mValue = value;
			mPluralResId = pluralResId;
			mCapitalPluralResId = capitalPluralResId;
		}

		/**
		 * @return The integer value associated with this enum constant.
		 */
		public int value() {
			return mValue;
		}

		/**
		 * Returns the (lowercase) string representing a single instance of this CitymapsObject. Note that the string
		 * is selected based solely on grammatical necessity, and that such rules differ between languages.
		 * Do not assume you know which string will be returned for a given quantity.
		 * @param context The context you wish to use to get the quantity string.
		 * @return The string data associated with the resource, stripped of styled text information.
		 */
		public String getString(Context context) {
			return getQuantityString(context, 1, false);
		}

		/**
		 * Returns the string representing a single instance of this CitymapsObject. Note that the string
		 * is selected based solely on grammatical necessity, and that such rules differ between languages.
		 * Do not assume you know which string will be returned for a given quantity.
		 * @param context The context you wish to use to get the quantity string.
		 * @param capitalize Whether the resulting string should be returned in language-appropriate word-capitalized form
		 *                   (for example, "Places of Interest").
		 * @return The string data associated with the resource, stripped of styled text information.
		 */
		public String getString(Context context, boolean capitalize) {
			return getQuantityString(context, 1, capitalize);
		}

		/**
		 * Returns the string necessary for grammatically correct pluralization of this CitymapsObject for the given quantity.
		 * Note that the string is selected based solely on grammatical necessity, and that such rules differ between languages.
		 * Do not assume you know which string will be returned for a given quantity.
		 * @param context The context you wish to use to get the quantity string.
		 * @param capitalize Whether the resulting string should be returned in language-appropriate word-capitalized form
		 *                   (for example, "Places of Interest").
		 * @param quantity The number used to get the correct string for the current language's plural rules.
		 * @return The string data associated with the resource, stripped of styled text information.
		 */
		public String getQuantityString(Context context, int quantity, boolean capitalize) {
			int resId = (capitalize ? mCapitalPluralResId : mPluralResId);
			return context.getResources().getQuantityString(resId, quantity);
		}

		/**
		 * Returns the string necessary for grammatically correct pluralization of this CitymapsObject for the given quantity,
		 * using the given arguments. Note that the string is selected based solely on grammatical necessity, and that such rules
		 * differ between languages. Do not assume you know which string will be returned for a given quantity.
		 * @param context The context you wish to use to get the quantity string.
		 * @param quantity The number used to get the correct string for the current language's plural rules.
		 * @param capitalize Whether the resulting string should be returned in language-appropriate word-capitalized form
		 *                   (for example, "Places of Interest").
		 * @param formatArgs The format arguments that will be used for substitution.
		 * @return The string data associated with the resource, stripped of styled text information.
		 */
		public String getQuantityString(Context context, int quantity, boolean capitalize, Object... formatArgs) {
			int resId = (capitalize ? mCapitalPluralResId : mPluralResId);
			return context.getResources().getQuantityString(resId, quantity, formatArgs);
		}
	}
}
