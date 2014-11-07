package com.citymaps.mobile.android.exception;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;

public class CitymapsVolleyException extends VolleyError {

	public CitymapsVolleyException() {
		super();
	}

	public CitymapsVolleyException(NetworkResponse response) {
		super(response);
	}

	public CitymapsVolleyException(String exceptionMessage) {
		super(exceptionMessage);
	}

	public CitymapsVolleyException(String exceptionMessage, Throwable reason) {
		super(exceptionMessage, reason);
	}

	public CitymapsVolleyException(Throwable cause) {
		super(cause);
	}
}
