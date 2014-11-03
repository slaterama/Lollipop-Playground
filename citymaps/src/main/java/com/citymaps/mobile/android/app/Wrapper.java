package com.citymaps.mobile.android.app;

/**
 * A Wrapper object wraps either a data object or a Throwable.
 * @param <D> The type of the data this wrapper is wrapping.
 * @param <T> The type of the Throwable this wrapper is wrapping.
 */
public interface Wrapper<D, T extends Throwable> {

	/**
	 * Returns this wrapper's data.
	 * @return The data wrapped by this wrapper.
	 * @throws T The Throwable wrapped by this wrapper.
	 */
	public D getData() throws T;
}
