package com.kritter.constants;

import lombok.Getter;
import java.util.HashMap;
import java.util.Map;

public enum RICHMEDIA_TYPE
{
    RICHMEDIA_INTERSTITIAL(1,"Richmedia type is interstitial or javascript payload etc."),
    NONE(0,"RICHMEDIA type is unknown");

    @Getter
    private int code;
    @Getter
    private String description;

    private static Map<Integer, RICHMEDIA_TYPE> map = new HashMap<Integer, RICHMEDIA_TYPE>();
    static {
        for (RICHMEDIA_TYPE val : RICHMEDIA_TYPE.values()) {
            map.put(val.code, val);
        }
    }

    private RICHMEDIA_TYPE(int platform,String description)
    {
        this.code = platform;
        this.description = description;
    }

    public static RICHMEDIA_TYPE getEnum(int i)
    {
        return map.get(i);
    }
}