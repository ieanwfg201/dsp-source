package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

public enum PMPAPIEnum
{
    none(0,"none"),
    start_multiple_deal(1,"start_multiple_deal"),
    pause_multiple_deal(3,"pause_multiple_site");

    private int code;
    private String name;
    private static Map<Integer, PMPAPIEnum> map = new HashMap<Integer, PMPAPIEnum>();
    static {
        for (PMPAPIEnum val : PMPAPIEnum.values()) {
            map.put(val.code, val);
        }
    }


    private PMPAPIEnum(int code,String name)
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

    public static PMPAPIEnum getEnum(int i)
    {
        return map.get(i);
    }
}
