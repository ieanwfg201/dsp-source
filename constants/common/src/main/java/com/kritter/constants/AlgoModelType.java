package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

public enum AlgoModelType {
    GETALPHA(1,"GETALPHA");
    
    private int code;
    private String name;
    private static Map<Integer, AlgoModelType> map = new HashMap<Integer, AlgoModelType>();
    static {
        for (AlgoModelType val : AlgoModelType.values()) {
            map.put(val.code, val);
        }
    }
    private AlgoModelType(int code,String name){
        this.code = code;
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    public int getCode(){
        return this.code;
    }
    
    public static AlgoModelType getEnum(int i){
        return map.get(i);
    }
}
