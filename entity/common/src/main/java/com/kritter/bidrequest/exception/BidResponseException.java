package com.kritter.bidrequest.exception;

/**
 * Created with IntelliJ IDEA.
 * User: chahar
 * Date: 30/10/13
 * Time: 12:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class BidResponseException extends Exception {

    private static final long serialVersionUID = 12923278273L;

    public BidResponseException(String errorMessageProvided) {
        super(errorMessageProvided);
    }

    public BidResponseException(String errorMessageProvided,
                               Throwable causeHandle) {
        super(errorMessageProvided, causeHandle);
    }

    public String getMessage() {
        return super.getMessage();
    }

    // other methods are inherited.
}