package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

public enum MidpValue
{
    MIDP_1((short)1,"midp1"),
    MIDP_2((short)2,"midp2"),
    ALL((short)3,"ALL");

    private short code;
    private String description;

    private static Map<Short, MidpValue> map = new HashMap<Short, MidpValue>();
    static
    {
        for (MidpValue val : MidpValue.values())
        {
            map.put(val.code, val);
        }
    }

    private MidpValue(short code,String description)
    {
        this.code = code;
        this.description = description;
    }

    public static MidpValue fetchMipValue(Short code)
    {
        if(code == 1)
            return MIDP_1;
        if(code == 2)
            return MIDP_2;
        if(code == 3)
            return ALL;

        return null;
    }

    public String getDescription()
    {
        return this.description;
    }

    public short getCode()
    {
        return this.code;
    }
}
