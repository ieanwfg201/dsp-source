package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

public enum TEventType {
    video(1,"video"),
    videoerror(2,"videoerror"),
    passback(3,"passback");
    
    private int code;
    private String name;
    private static Map<Integer, TEventType> map = new HashMap<Integer, TEventType>();
    static {
        for (TEventType val : TEventType.values()) {
            map.put(val.code, val);
        }
    }
    private TEventType(int code,String name){
        this.code = code;
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    public int getCode(){
        return this.code;
    }
    
    public static TEventType getEnum(int i){
        return map.get(i);
    }
}
