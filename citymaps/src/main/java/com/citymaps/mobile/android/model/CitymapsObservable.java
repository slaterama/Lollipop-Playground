package com.citymaps.mobile.android.model;

import com.google.gson.annotations.SerializedName;
import org.joda.time.DateTime;

import java.util.Observable;

/**
 * An object maintained by the Citymaps application. Every object has a type, an id, and a name. Type is read-only and is provided
 * by the various classes that implement this interface. Name is also read-only but may be configured as writable by the
 * implementing class. Id is writable and can be set via the {@link #setId(String) setName} method.
 */
public abstract class CitymapsObservable extends Observable
		implements CitymapsObject {

	public static final String CREATED_AT = "created_at";
	public static final String UPDATED_AT = "updated_at";

	@SerializedName(CREATED_AT)
	private DateTime mCreatedAt;

	@SerializedName(UPDATED_AT)
	private DateTime mUpdatedAt;

	public DateTime getCreatedAt() {
		return mCreatedAt;
	}

	public void setCreatedAt(DateTime createdAt) {
		mCreatedAt = createdAt;
		setChanged();
		notifyObservers(CREATED_AT);
	}

	public DateTime getUpdatedAt() {
		return mUpdatedAt;
	}

	public void setUpdatedAt(DateTime updatedAt) {
		mUpdatedAt = updatedAt;
		setChanged();
		notifyObservers(UPDATED_AT);
	}
}
