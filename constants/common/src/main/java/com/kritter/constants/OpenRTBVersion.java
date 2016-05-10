package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

/**
 * This is the enum for open rtb versions we have in our system.
 */
public enum OpenRTBVersion
{

    VERSION_2_0(0,"openrtb-version-2.0"),
    VERSION_2_1(1,"openrtb-version-2.1"),
    VERSION_2_2(2,"openrtb-version-2.2"),
    VERSION_2_3(3,"openrtb-version-2.3");

    private int code;
    private String name;
    private static Map<Integer, OpenRTBVersion> map = new HashMap<Integer, OpenRTBVersion>();

    static
    {
        for (OpenRTBVersion val : OpenRTBVersion.values())
        {
            map.put(val.code, val);
        }
    }

    private OpenRTBVersion(int code,String name)
    {
        this.code = code;
        this.name = name;
    }

    public String getName()
    {
        return this.name;
    }

    public int getCode()
    {
        return this.code;
    }

    public static OpenRTBVersion getEnum(int i)
    {
        return map.get(i);
    }
}