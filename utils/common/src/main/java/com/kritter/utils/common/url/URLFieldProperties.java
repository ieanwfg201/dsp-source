package com.kritter.utils.common.url;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * This enum keeps properties for each field that might be used to be added
 * to a URL for passing on information to some other system.
 */
public class URLFieldProperties
{
    private int length;
    private URLFieldType urlFieldType;
    @Getter @Setter
    private Object fieldValue;

    public URLFieldProperties(int length,URLFieldType urlFieldType)
    {
        this.length = length;
        this.urlFieldType = urlFieldType;
    }

    public int getLength()
    {
        return this.length;
    }

    public URLFieldType getUrlFieldType()
    {
        return this.urlFieldType;
    }
}