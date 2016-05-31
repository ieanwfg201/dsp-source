package com.kritter.utils.common.url;

import java.util.HashMap;
import java.util.Map;

/**
 * This enum keeps length for each field that might be used to be added
 * to a URL for passing on information to some other system.
 */
public enum URLFieldLength
{
    /************************ Important.*****************************************/
     /* The new field must be added in the end to maintain the order.
      * position = code * previous_length
      */
     /***************************************************************************/
    KRITTER_USER_ID((short)0,36),
    EXTERNAL_SITE_ID((short)1,300);

    private short code;
    private int length;

    private static Map<Short, URLFieldLength> attributesMap = new HashMap<Short, URLFieldLength>();

    static
    {
        for (URLFieldLength urlFieldLength : URLFieldLength.values())
        {
            attributesMap.put(urlFieldLength.code, urlFieldLength);
        }
    }

    private URLFieldLength(short code,int length)
    {
        this.code = code;
        this.length = length;
    }

    public int getLength()
    {
        return this.length;
    }

    public short getCode()
    {
        return this.code;
    }

    public static URLFieldLength getEnum(int i)
    {
        return attributesMap.get(i);
    }
}