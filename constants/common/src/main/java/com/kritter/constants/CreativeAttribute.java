package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

public enum CreativeAttribute {
    EXTRA(17,"Banner Only"),
    Native(51,"Native");

    private int code;
    private String name;
    private static Map<Integer, CreativeAttribute> map = new HashMap<Integer, CreativeAttribute>();
    static {
        for (CreativeAttribute val : CreativeAttribute.values()) {
            map.put(val.code, val);
        }
    }
    private CreativeAttribute(int code,String name){
        this.code = code;
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    public int getCode(){
        return this.code;
    }
    
    public static CreativeAttribute getEnum(int i){
        return map.get(i);
    }
}
