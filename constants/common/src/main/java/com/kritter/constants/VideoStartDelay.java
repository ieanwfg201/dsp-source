package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

public enum VideoStartDelay {

    Unknown(0,"-11","Unknown"),
    MidRoll(1,"> 0","Mid-Roll (value indicates start delay in second)"),
    PreRoll(2,"0", "Pre-Roll"),
    GenericMidRoll(3,"-1", "Generic Mid-Roll"),
    GenericPostRoll(4,"-2", "Generic Post-Roll");
    
    private int code;
    private String name;
    private String desc;
    private static Map<Integer, VideoStartDelay> map = new HashMap<Integer, VideoStartDelay>();
    static {
        for (VideoStartDelay val : VideoStartDelay.values()) {
            map.put(val.code, val);
        }
    }
    private VideoStartDelay(int code,String name, String desc){
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
    
    public static VideoStartDelay getEnum(int i){
        return map.get(i);
    }
}
