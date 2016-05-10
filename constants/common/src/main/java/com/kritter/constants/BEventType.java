package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

public enum BEventType {
    cscwin(1,"cscwin");
    
    private int code;
    private String name;
    private static Map<Integer, BEventType> map = new HashMap<Integer, BEventType>();
    static {
        for (BEventType val : BEventType.values()) {
            map.put(val.code, val);
        }
    }
    private BEventType(int code,String name){
        this.code = code;
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    public int getCode(){
        return this.code;
    }
    
    public static BEventType getEnum(int i){
        return map.get(i);
    }
}
