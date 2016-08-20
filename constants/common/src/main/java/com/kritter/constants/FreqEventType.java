package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

public enum FreqEventType {
    CLK(1,"CLK"),
    IMP(2,"IMP");
    
    private int code;
    private String name;
    private static Map<Integer, FreqEventType> map = new HashMap<Integer, FreqEventType>();
    static {
        for (FreqEventType val : FreqEventType.values()) {
            map.put(val.code, val);
        }
    }
    private FreqEventType(int code,String name){
        this.code = code;
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    public int getCode(){
        return this.code;
    }
    
    public static FreqEventType getEnum(int i){
        return map.get(i);
    }
}
