package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

public enum SITE_PASSBACK_TYPE
{
    NONE((short)-1,"NONE"),
    DIRECT_PASSBACK((short)1,"Passback content is direct passback for publisher site"),
    SSP_WATERFALL_PASSBACK((short)2,"Passback content is ssp waterfall model for publisher site");

    @Getter
    private short code;
    @Getter
    private String description;
    
    private static Map<Short, SITE_PASSBACK_TYPE> map = new HashMap<Short, SITE_PASSBACK_TYPE>();
    static {
        for (SITE_PASSBACK_TYPE val : SITE_PASSBACK_TYPE.values()) {
            map.put(val.code, val);
        }
    }

    private SITE_PASSBACK_TYPE(short platform,String description)
    {
        this.code = platform;
        this.description = description;
    }
    
    public static SITE_PASSBACK_TYPE getEnum(short i)
    {
        return map.get(i);
    }
}
