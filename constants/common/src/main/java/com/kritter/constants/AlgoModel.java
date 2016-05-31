package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

public enum AlgoModel {
    BIDDER(1,"BIDDER");
    
    private int code;
    private String name;
    private static Map<Integer, AlgoModel> map = new HashMap<Integer, AlgoModel>();
    static {
        for (AlgoModel val : AlgoModel.values()) {
            map.put(val.code, val);
        }
    }
    private AlgoModel(int code,String name){
        this.code = code;
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    public int getCode(){
        return this.code;
    }
    
    public static AlgoModel getEnum(int i){
        return map.get(i);
    }
}
