package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

public enum SITE_PASSBACK_CONTENT_TYPE
{
    NONE((short)-1,"NONE"),
    PASSBACK_URL((short)1,"Passback content is a URL"),
    PASSBACK_DIRECT_PAYLOAD((short)2,"Passback content is direct payload javascript/html etc.");

    @Getter
    private short code;
    @Getter
    private String description;
    
    private static Map<Short, SITE_PASSBACK_CONTENT_TYPE> map = new HashMap<Short, SITE_PASSBACK_CONTENT_TYPE>();
    static {
        for (SITE_PASSBACK_CONTENT_TYPE val : SITE_PASSBACK_CONTENT_TYPE.values()) {
            map.put(val.code, val);
        }
    }

    private SITE_PASSBACK_CONTENT_TYPE(short platform,String description)
    {
        this.code = platform;
        this.description = description;
    }
    
    public static SITE_PASSBACK_CONTENT_TYPE getEnum(short i)
    {
        return map.get(i);
    }
}
