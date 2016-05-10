package com.kritter.abstraction.cache.utils.exceptions;

/**
 * Date: 6-June-2013<br></br>
 * Class: Exception specializing in Initialization errors
 */
public class InitializationException extends Exception
{
    public InitializationException()
    {
        super();
    }

    public InitializationException(String message)
    {
        super(message);
    }

    public InitializationException(String message, Throwable cause)
    {
        super(message,cause);
    }

    public InitializationException(Throwable cause)
    {
        super(cause);
    }
}
