package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

public enum OpenRtbConnectionType
{
    Unknown(0,"Unknown"),
    Ethernet(1,"Ethernet"),
    WIFI(2,"WIFI"),
    CellularNetwork_UnknownGeneration(3,"Cellular Network – Unknown Generation"),
    CellularNetwork_2G(4,"Cellular Network – 2G"),
    CellularNetwork_3G(5,"Cellular Network – 3G"),
    CellularNetwork_4G(6,"Cellular Network – 4G");
    
    private int code;
    private String name;
    private static Map<Integer, OpenRtbConnectionType> map = new HashMap<Integer, OpenRtbConnectionType>();
    static {
        for (OpenRtbConnectionType val : OpenRtbConnectionType.values()) {
            map.put(val.code, val);
        }
    }

    private OpenRtbConnectionType(int code,String name)
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
    public static OpenRtbConnectionType getEnum(int i)
    {
        return map.get(i);
    }
}
