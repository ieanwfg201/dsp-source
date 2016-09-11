package com.kritter.constants;

import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@ToString
public enum FreqDuration {
    LIFE(1,"LIFE"),
    BYHOUR(2,"BYHOUR"),
    BYDAY(3,"BYDAY");

    private int code;
    private String name;
    private static Map<Integer, FreqDuration> map = new HashMap<Integer, FreqDuration>();
    static {
        for (FreqDuration val : FreqDuration.values()) {
            map.put(val.code, val);
        }
    }
    private FreqDuration(int code,String name){
        this.code = code;
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    public int getCode(){
        return this.code;
    }

    public static FreqDuration getEnum(int i){
        return map.get(i);
    }
}
