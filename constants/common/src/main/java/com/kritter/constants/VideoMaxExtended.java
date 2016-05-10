package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

public enum VideoMaxExtended {

    Unknown(0,"Unknown"),
    EXTENSION_NOT_ALLOWED(1,"EXTENSION_NOT_ALLOWED"),
    EXTENSION_ALLOWED_WITH_NO_LIMIT(2,"EXTENSION_ALLOWED_WITH_NO_LIMIT"),
    EXTENSION_ALLOWED_WITH_LIMIT_IN_SEC(3,"EXTENSION_ALLOWED_WITH_LIMIT_IN_SEC");
    
    private int code;
    private String name;
    private static Map<Integer, VideoMaxExtended> map = new HashMap<Integer, VideoMaxExtended>();
    static {
        for (VideoMaxExtended val : VideoMaxExtended.values()) {
            map.put(val.code, val);
        }
    }
    private VideoMaxExtended(int code,String name){
        this.code = code;
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    public int getCode(){
        return this.code;
    }
    
    public static VideoMaxExtended getEnum(int i){
        return map.get(i);
    }
}
