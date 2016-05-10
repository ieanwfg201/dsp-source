package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

public enum SITE_PLATFORM
{
    /*WEB((short)1,"Web"),*/
    WAP((short)2,"Mobile Web"),
    APP((short)3,"Mobile Application"),
    NO_VALID_VALUE((short)-1,"");

    @Getter
    private short platform;
    @Getter
    private String description;
    
    private static Map<Short, SITE_PLATFORM> map = new HashMap<Short, SITE_PLATFORM>();
    static {
        for (SITE_PLATFORM val : SITE_PLATFORM.values()) {
            map.put(val.platform, val);
        }
    }

    private SITE_PLATFORM(short platform,String description)
    {
        this.platform = platform;
        this.description = description;
    }
    
    public static SITE_PLATFORM getEnum(short i)
    {
        return map.get(i);
    }
}
