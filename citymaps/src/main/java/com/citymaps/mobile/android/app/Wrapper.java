package com.citymaps.mobile.android.app;

import com.citymaps.mobile.android.exception.CitymapsException;

/**
 * A Wrapper object wraps either a data object or a CitymapsException.
 * @param <D> The type of the data this wrapper is wrapping.
 */
public interface Wrapper<D> {

	/**
	 * Returns this wrapper's data.
	 * @return The data wrapped by this wrapper.
	 */
	public D getData() throws CitymapsException;
}
