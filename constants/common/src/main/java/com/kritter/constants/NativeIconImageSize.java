package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

public enum NativeIconImageSize {

    I75x75(1,"75*75"),
    I80x80(1,"80*80"),
    I150x150(2,"150*150"),
    I300x300(3,"300*300");
    
    private int code;
    private String name;
    private static Map<Integer, NativeIconImageSize> map = new HashMap<Integer, NativeIconImageSize>();
    static {
        for (NativeIconImageSize val : NativeIconImageSize.values()) {
            map.put(val.code, val);
        }
    }
    private NativeIconImageSize(int code,String name){
        this.code = code;
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    public int getCode(){
        return this.code;
    }
    
    public static NativeIconImageSize getEnum(int i){
        return map.get(i);
    }
}
