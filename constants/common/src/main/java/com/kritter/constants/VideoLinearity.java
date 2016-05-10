package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

public enum VideoLinearity {
    LINEAR_IN_STREAM(1,"Linear / In-Stream"),
    NON_LINEAR_OVERLAY(2,"Non-Linear / Overlay");
    
    private int code;
    private String name;
    private static Map<Integer, VideoLinearity> map = new HashMap<Integer, VideoLinearity>();
    static {
        for (VideoLinearity val : VideoLinearity.values()) {
            map.put(val.code, val);
        }
    }
    private VideoLinearity(int code,String name){
        this.code = code;
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    public int getCode(){
        return this.code;
    }
    
    public static VideoLinearity getEnum(int i){
        return map.get(i);
    }
}
