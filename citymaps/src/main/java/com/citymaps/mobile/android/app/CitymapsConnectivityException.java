package com.citymaps.mobile.android.app;

/**
 * Thrown when an api call (i.e. HTTP request) is attempted when there is no network connection available.
 */
public class CitymapsConnectivityException extends CitymapsException {

	/**
	 * Constructs a new {@link CitymapsConnectivityException} that includes the current stack trace.
	 */
	public CitymapsConnectivityException() {
		super();
	}

	/**
	 * Constructs a new {@link CitymapsConnectivityException} with the current stack trace and
	 * the specified detail message.
	 */
	public CitymapsConnectivityException(String detailMessage) {
		super(detailMessage);
	}

	/**
	 * Constructs a new {@link CitymapsConnectivityException} with the current stack trace,
	 * the specified detail message and the specified cause.
	 */
	public CitymapsConnectivityException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	/**
	 * Constructs a new {@link CitymapsConnectivityException} with the current stack trace
	 * and the specified cause.
	 */
	public CitymapsConnectivityException(Throwable throwable) {
		super(throwable);
	}
}
