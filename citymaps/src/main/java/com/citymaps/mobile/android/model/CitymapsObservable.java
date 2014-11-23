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

	@SerializedName("created_at")
	private Date mCreatedAt;

	@SerializedName("updated_at")
	private Date mUpdatedAt;

	public Date getCreatedAt() {
		return mCreatedAt;
	}

	public void setCreatedAt(Date createdAt) {
		mCreatedAt = createdAt;
	}

	public Date getUpdatedAt() {
		return mUpdatedAt;
	}

	public void setUpdatedAt(Date updatedAt) {
		mUpdatedAt = updatedAt;
	}
}
