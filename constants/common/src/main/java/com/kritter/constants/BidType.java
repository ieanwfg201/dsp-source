package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

public enum BidType {

    AUTO(0,"AUTO"),
    MANUAL(1,"MANUAL");
    
    private int code;
    private String name;
    private static Map<Integer, BidType> map = new HashMap<Integer, BidType>();
    static {
        for (BidType val : BidType.values()) {
            map.put(val.code, val);
        }
    }
    private BidType(int code,String name){
        this.code = code;
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    public int getCode(){
        return this.code;
    }
    
    public static BidType getEnum(int i){
        return map.get(i);
    }
}
