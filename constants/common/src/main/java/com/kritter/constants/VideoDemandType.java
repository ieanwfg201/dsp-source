package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

public enum VideoDemandType {
	
    VastTagUrl(1,"VastTagUrl"),
    DirectVideo(2,"DirectVideo");
    
    private int code;
    private String name;
    private static Map<Integer, VideoDemandType> map = new HashMap<Integer, VideoDemandType>();
    static {
        for (VideoDemandType val : VideoDemandType.values()) {
            map.put(val.code, val);
        }
    }
    private VideoDemandType(int code,String name){
        this.code = code;
        this.name = name;
    }

    public String getName(){
        return this.name;
    }
    
    public int getCode(){
        return this.code;
    }
    
    public static VideoDemandType getEnum(int i){
        return map.get(i);
    }
}
