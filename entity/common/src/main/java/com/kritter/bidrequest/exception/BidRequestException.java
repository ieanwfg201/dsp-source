package com.kritter.bidrequest.exception;

/**
 * This exception class can be used for any bid request related workflow
 * exceptions.
 */

public class BidRequestException extends Exception {

	private static final long serialVersionUID = 12987878273L;

	public BidRequestException(String errorMessageProvided) {
		super(errorMessageProvided);
	}

	public BidRequestException(String errorMessageProvided,
			Throwable causeHandle) {
		super(errorMessageProvided, causeHandle);
	}

	public String getMessage() {
		return super.getMessage();
	}

	// other methods are inherited.
}
