package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

public enum SSPEnum {

    COUNTRY_ALL(-997,"COUNTRY_ALL"),
    INSERT_ID(997,"997"),
    READ_TIMEOUT(200,"200");

    private int code;
    private String name;
    private static Map<Integer, SSPEnum> map = new HashMap<Integer, SSPEnum>();
    static {
        for (SSPEnum val : SSPEnum.values()) {
            map.put(val.code, val);
        }
    }
    private SSPEnum(int code,String name){
        this.code = code;
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    public int getCode(){
        return this.code;
    }
    
    public static SSPEnum getEnum(int i){
        return map.get(i);
    }
}
