package com.kritter.kumbaya.libraries.ext_site_app_merger.entity;

import java.util.HashMap;
import java.util.Map;


public enum DBActivityEnum {
    INSERT((short)1,"INSERT"),
    UPDATE((short)2,"UPDATE"),
    DONOTHING((short)3,"DONOTHING");
    private short code;
    private String name;
    private static Map<Short, DBActivityEnum> map = new HashMap<Short, DBActivityEnum>();
    static {
        for (DBActivityEnum val : DBActivityEnum.values()) {
            map.put(val.code, val);
        }
    }

    
    private DBActivityEnum(short code,String name)
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
    
    public static DBActivityEnum getEnum(short i)
    {
        return map.get(i);
    }

}
