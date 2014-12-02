package com.citymaps.mobile.android.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
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
	private Date mCreatedAt;

	@SerializedName(UPDATED_AT)
	private Date mUpdatedAt;

	public Date getCreatedAt() {
		return mCreatedAt;
	}

	public void setCreatedAt(Date createdAt) {
		mCreatedAt = createdAt;
		setChanged();
		notifyObservers(CREATED_AT);
	}

	public Date getUpdatedAt() {
		return mUpdatedAt;
	}

	public void setUpdatedAt(Date updatedAt) {
		mUpdatedAt = updatedAt;
		setChanged();
		notifyObservers(UPDATED_AT);
	}
}
