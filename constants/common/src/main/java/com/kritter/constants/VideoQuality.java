package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

public enum VideoQuality {

    Unknown(0,"Unknown"),
    ProfessionallyProduced(1,"Professionally Produced"),
    Prosumer(2,"Prosumer"),
    UserGenerated(3,"User Generated (UGC)");
    
    private int code;
    private String name;
    private static Map<Integer, VideoQuality> map = new HashMap<Integer, VideoQuality>();
    static {
        for (VideoQuality val : VideoQuality.values()) {
            map.put(val.code, val);
        }
    }
    private VideoQuality(int code,String name){
        this.code = code;
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    public int getCode(){
        return this.code;
    }
    
    public static VideoQuality getEnum(int i){
        return map.get(i);
    }
}
