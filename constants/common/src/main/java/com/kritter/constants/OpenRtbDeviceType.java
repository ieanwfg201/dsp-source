package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

public enum OpenRtbDeviceType
{
    Mobile_Tablet(1,"Mobile/Tablet"),
    Personal_Computer(2,"Personal Computer"),
    Connected_TV(3,"Connected TV"),
    Phone(4,"Phone"),
    Tablet(5,"Tablet"),
    Connected_Device(6,"Connected Device"),
    SetTop_Box(7,"Set Top Box");

    private int code;
    private String name;
    private static Map<Integer, OpenRtbDeviceType> map = new HashMap<Integer, OpenRtbDeviceType>();
    static {
        for (OpenRtbDeviceType val : OpenRtbDeviceType.values()) {
            map.put(val.code, val);
        }
    }

    private OpenRtbDeviceType(int code,String name)
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
    public static OpenRtbDeviceType getEnum(int i)
    {
        return map.get(i);
    }
}
