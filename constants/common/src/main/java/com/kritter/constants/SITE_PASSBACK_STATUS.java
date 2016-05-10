package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

public enum SITE_PASSBACK_STATUS
{
    PASSBACK_NOT_DONE((short)0,"Passback content not sent or was missing for Direct publisher site"),
    PASSBACK_DONE((short)1,"Passback content sent to Direct publisher site");

    @Getter
    private short code;
    @Getter
    private String description;
    
    private static Map<Short, SITE_PASSBACK_STATUS> map = new HashMap<Short, SITE_PASSBACK_STATUS>();
    static {
        for (SITE_PASSBACK_STATUS val : SITE_PASSBACK_STATUS.values()) {
            map.put(val.code, val);
        }
    }

    private SITE_PASSBACK_STATUS(short platform,String description)
    {
        this.code = platform;
        this.description = description;
    }
    
    public static SITE_PASSBACK_STATUS getEnum(short i)
    {
        return map.get(i);
    }
}
