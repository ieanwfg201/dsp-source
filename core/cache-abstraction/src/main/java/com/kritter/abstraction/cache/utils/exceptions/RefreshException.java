package com.kritter.abstraction.cache.utils.exceptions;

/**
 * Date: 6-June-2013<br></br>
 * Class: Exception specializing in Refresh errors
 */
public class RefreshException extends Exception
{
    public RefreshException()
    {
        super();
    }

    public RefreshException(String message)
    {
        super(message);
    }

    public RefreshException(String message, Throwable cause)
    {
        super(message,cause);
    }

    public RefreshException(Throwable cause)
    {
        super(cause);
    }
}
