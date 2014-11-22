package com.citymaps.mobile.android.model;

import java.util.Observable;

/**
 * An object maintained by the Citymaps application. Every object has a type, an id, and a name. Type is read-only and is provided
 * by the various classes that implement this interface. Name is also read-only but may be configured as writable by the
 * implementing class. Id is writable and can be set via the {@link #setId(String) setName} method.
 */
public abstract class CitymapsObservable extends Observable
		implements CitymapsObject {

}
