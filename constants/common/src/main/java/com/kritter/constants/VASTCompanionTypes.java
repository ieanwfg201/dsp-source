package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

public enum VASTCompanionTypes {
    
    Unknown(0,"Unknown"),
    StaticResource(1,"Static Resource"),
    HTMLResource(2,"HTML Resource"),
    iframeResource(3,"iframe Resource");
    
    private int code;
    private String name;
    private static Map<Integer, VASTCompanionTypes> map = new HashMap<Integer, VASTCompanionTypes>();
    static {
        for (VASTCompanionTypes val : VASTCompanionTypes.values()) {
            map.put(val.code, val);
        }
    }
    private VASTCompanionTypes(int code,String name){
        this.code = code;
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    public int getCode(){
        return this.code;
    }
    
    public static VASTCompanionTypes getEnum(int i){
        return map.get(i);
    }
}
