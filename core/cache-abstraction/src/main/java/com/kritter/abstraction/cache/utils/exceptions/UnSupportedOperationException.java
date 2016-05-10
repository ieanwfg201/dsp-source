package com.kritter.abstraction.cache.utils.exceptions;

/**
 * Date: 7-June-2013<br></br>
 * Class: Invalid operation executed exception
 */
public class UnSupportedOperationException extends Exception 
{
    public UnSupportedOperationException()
    {
        super();
    }

    public UnSupportedOperationException(String message)
    {
        super(message);
    }

    public UnSupportedOperationException(String message, Throwable cause)
    {
        super(message,cause);
    }

    public UnSupportedOperationException(Throwable cause)
    {
        super(cause);
    }
}
