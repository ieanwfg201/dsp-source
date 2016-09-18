package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

public enum ExtClickType {

    INEXCRESPONSE(1,"INEXCRESPONSE"),
    INPOSTIMP(2,"INPOSTIMP");
    
    private int code;
    private String name;
    private static Map<Integer, ExtClickType> map = new HashMap<Integer, ExtClickType>();
    static {
        for (ExtClickType val : ExtClickType.values()) {
            map.put(val.code, val);
        }
    }
    private ExtClickType(int code,String name){
        this.code = code;
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    public int getCode(){
        return this.code;
    }
    
    public static ExtClickType getEnum(int i){
        return map.get(i);
    }
}
