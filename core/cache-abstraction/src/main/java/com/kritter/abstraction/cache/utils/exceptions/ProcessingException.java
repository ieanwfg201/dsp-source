package com.kritter.abstraction.cache.utils.exceptions;

/**
 * Date: 6-June-2013<br></br>
 * Class: Any processing error
 */
public class ProcessingException extends Exception
{
    public ProcessingException()
    {
        super();
    }

    public ProcessingException(String message)
    {
        super(message);
    }

    public ProcessingException(String message, Throwable cause)
    {
        super(message,cause);
    }

    public ProcessingException(Throwable cause)
    {
        super(cause);
    }
}
