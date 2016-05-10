package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

public enum DeviceType
{
    DESKTOP((short)1,"DESKTOP"),
    MOBILE((short)2,"MOBILE"),
    UNKNOWN((short)0,"UNKNOWN");

    private short code;
    private String name;
    private static Map<Short, DeviceType> map = new HashMap<Short, DeviceType>();
    static {
        for (DeviceType val : DeviceType.values()) {
            map.put(val.code, val);
        }
    }

    private DeviceType(short code,String name)
    {
        this.code = code;
        this.name = name;
    }

    public String getName()
    {
        return this.name;
    }

    public short getCode()
    {
        return this.code;
    }
    public static DeviceType getEnum(int i)
    {
        return map.get(i);
    }
}
