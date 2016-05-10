package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

public enum VideoMacros {
    ERRORCODE(1,"[ERRORCODE]","\\[ERRORCODE]"),
    CONTENTPLAYHEAD(2,"[CONTENTPLAYHEAD]","\\[CONTENTPLAYHEAD]"),
    CACHEBUSTING(3,"[CACHEBUSTING]","\\[[CACHEBUSTING]"),
    ASSETURI(4,"[ASSETURI]","\\[ASSETURI]");
    
    private int code;
    private String name;
    private String desc;
    private static Map<Integer, VideoMacros> map = new HashMap<Integer, VideoMacros>();
    static {
        for (VideoMacros val : VideoMacros.values()) {
            map.put(val.code, val);
        }
    }
    private VideoMacros(int code,String name, String desc){
        this.code = code;
        this.name = name;
        this.desc = desc;
    }

    public String getName(){
        return this.name;
    }
    
    public String getDesc(){
        return this.desc;
    }

    public int getCode(){
        return this.code;
    }
    
    public static VideoMacros getEnum(int i){
        return map.get(i);
    }
}
