package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

public enum APP_STORE_ID
{
    NONE(0,""),
    APPLE(1,"App Store"),
    BLACKBERRY(2,"BlackBerry World"),
    GOOGLE_PLAY(3,"Google Play"),
    NOKIA_STORE(4,"Nokia Store"),
    WINDOWS_PHONE_STORE(5,"Windows Phone Store"),
    WINDOWS_STORE(6,"Windows Store");

    @Getter
    private int appStoreId;
    @Getter
    private String description;
    
    private static Map<Integer, APP_STORE_ID> map = new HashMap<Integer, APP_STORE_ID>();
    static {
        for (APP_STORE_ID val : APP_STORE_ID.values()) {
            map.put(val.appStoreId, val);
        }
    }


    private APP_STORE_ID(int appStoreId,String description)
    {
        this.appStoreId = appStoreId;
        this.description = description;
    }
    public static APP_STORE_ID getEnum(int i)
    {
        return map.get(i);
    }

}
