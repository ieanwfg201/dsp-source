package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

public enum NativeImageAssetType {

    Icon(1,"Icon image"),
    Logo(2,"Logo image for the brand/app."),
    Main(3,"Large image preview for the ad");

    private int code;
    private String name;
    private static Map<Integer, NativeImageAssetType> map = new HashMap<Integer, NativeImageAssetType>();
    static {
        for (NativeImageAssetType val : NativeImageAssetType.values()) {
            map.put(val.code, val);
        }
    }
    private NativeImageAssetType(int code,String name){
        this.code = code;
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    public int getCode(){
        return this.code;
    }
    
    public static NativeImageAssetType getEnum(int i){
        return map.get(i);
    }
}
