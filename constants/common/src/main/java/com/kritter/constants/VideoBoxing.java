package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

public enum VideoBoxing {

    Unknown(-1,"Unknown"),
    BoxingNotAllowed(0,"BoxingNotAllowed"),
    BoxingAllowed(1,"BoxingAllowed");
    
    private int code;
    private String name;
    private static Map<Integer, VideoBoxing> map = new HashMap<Integer, VideoBoxing>();
    static {
        for (VideoBoxing val : VideoBoxing.values()) {
            map.put(val.code, val);
        }
    }
    private VideoBoxing(int code,String name){
        this.code = code;
        this.name = name;
    }

    public String getName(){
        return this.name;
    }
    
    public int getCode(){
        return this.code;
    }
    
    public static VideoBoxing getEnum(int i){
        return map.get(i);
    }
}
