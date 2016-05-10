package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

public enum APIFrameworks {

    Unknown(0,"Unknown"),
    VPAID_1_0(1,"VPAID 1.0"),
    VPAID_2_0(2,"VPAID 2.0"),
    MRAID_1(3,"MRAID-1"),
    MRAID_2(4,"MRAID-2"),
    ORMMA(5,"ORMMA");
    
    private int code;
    private String name;
    private static Map<Integer, APIFrameworks> map = new HashMap<Integer, APIFrameworks>();
    static {
        for (APIFrameworks val : APIFrameworks.values()) {
            map.put(val.code, val);
        }
    }
    private APIFrameworks(int code,String name){
        this.code = code;
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    public int getCode(){
        return this.code;
    }
    
    public static APIFrameworks getEnum(int i){
        return map.get(i);
    }
}
