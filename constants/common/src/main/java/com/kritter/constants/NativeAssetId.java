package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

public enum NativeAssetId {

    Title(1,"Title"),
    Icon(2,"Icon"),
    Screenshot(3,"Screenshot"),
    Desc(4,"Desc");
    
    private int code;
    private String name;
    private static Map<Integer, NativeAssetId> map = new HashMap<Integer, NativeAssetId>();
    static {
        for (NativeAssetId val : NativeAssetId.values()) {
            map.put(val.code, val);
        }
    }
    private NativeAssetId(int code,String name){
        this.code = code;
        this.name = name;
    }
    public String getName(){
        return this.name;
    }

    public int getCode(){
        return this.code;
    }
    
    public static NativeAssetId getEnum(int i){
        return map.get(i);
    }
}
