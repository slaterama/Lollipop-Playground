package com.citymaps.mobile.android.app;

/**
 * Base class for all HTTP-related exceptions thrown by the Citymaps app.
 */
public class CitymapsHttpException extends CitymapsException {

	/**
	 * The result code returned by the Citymaps Api call.
	 */
	private int mCode;

	/**
	 * Constructs a new {@link CitymapsHttpException} that includes the current stack trace.
	 */
	public CitymapsHttpException() {
		super();
	}

	/**
	 * Constructs a new {@link CitymapsHttpException} that includes the current stack trace
	 * and the specified result code.
	 */
	public CitymapsHttpException(int code) {
		super();
		mCode = code;
	}

	/**
	 * Constructs a new {@link CitymapsHttpException} with the current stack trace and
	 * the specified detail message.
	 */
	public CitymapsHttpException(String detailMessage) {
		super(detailMessage);
	}

	/**
	 * Constructs a new {@link CitymapsHttpException} with the current stack trace,
	 * the specified result code and the specified detail message.
	 */
	public CitymapsHttpException(int code, String detailMessage) {
		super(detailMessage);
		mCode = code;
	}

	/**
	 * Constructs a new {@link CitymapsHttpException} with the current stack trace,
	 * the specified detail message and the specified cause.
	 */
	public CitymapsHttpException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	/**
	 * Constructs a new {@link CitymapsHttpException} with the current stack trace,
	 * the specified result code, the specified detail message and the specified cause.
	 */
	public CitymapsHttpException(int code, String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
		mCode = code;
	}

	/**
	 * Constructs a new {@link CitymapsHttpException} with the current stack trace
	 * and the specified cause.
	 */
	public CitymapsHttpException(Throwable throwable) {
		super(throwable);
	}

	/**
	 * Constructs a new {@link CitymapsHttpException} with the current stack trace,
	 * the specified result code and the specified cause.
	 */
	public CitymapsHttpException(int code, Throwable throwable) {
		super(throwable);
		mCode = code;
	}

	/**
	 * Returns the result code associated with this exception.
	 * @return The result code associated with this exception.
	 */
	public int getCode() {
		return mCode;
	}
}
