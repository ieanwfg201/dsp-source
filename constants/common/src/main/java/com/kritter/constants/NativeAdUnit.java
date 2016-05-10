package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

public enum NativeAdUnit {

    Paid_Search_Units(1,"Paid_Search_Units"),
    Recommendation_Widgets(2,"Recommendation_Widgets"),
    Promoted_Listings(3,"Promoted_Listings"),
    InAdwithNativeElementUnits(4,"InAdwithNativeElementUnits"),
    Custom_CantBeContained(5,"Custom_CantBeContained");

    
    private int code;
    private String name;
    private static Map<Integer, NativeAdUnit> map = new HashMap<Integer, NativeAdUnit>();
    static {
        for (NativeAdUnit val : NativeAdUnit.values()) {
            map.put(val.code, val);
        }
    }
    private NativeAdUnit(int code,String name){
        this.code = code;
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    public int getCode(){
        return this.code;
    }
    
    public static NativeAdUnit getEnum(int i){
        return map.get(i);
    }
}
