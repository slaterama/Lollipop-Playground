package com.citymaps.mobile.android.app;

/**
 * <p>An ExceptionWrapper object wraps a CitymapsException object.</p>
 * <p>Note that in this implementation, any call to getData will always result in a CitymapsException being thrown.</p>
 * @param <D> The type of the data this wrapper is wrapping.
 */
public class CitymapsExceptionWrapper<D> implements Wrapper<D> {

	private CitymapsException mException;

	public CitymapsExceptionWrapper(CitymapsException exception) {
		mException = exception;
	}

	/**
	 * Throws the Throwable being wrapped by this wrapper.
	 */
	@Override
	public D getData() throws CitymapsException {
		throw mException;
	}
}
