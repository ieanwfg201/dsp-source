package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

public enum LatLonRadiusUnit {
    MILES(0,"MILES"),
    KM(1,"KM");
    
    private int code;
    private String name;
    private static Map<Integer, LatLonRadiusUnit> map = new HashMap<Integer, LatLonRadiusUnit>();
    static {
        for (LatLonRadiusUnit val : LatLonRadiusUnit.values()) {
            map.put(val.code, val);
        }
    }
    private LatLonRadiusUnit(int code,String name){
        this.code = code;
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    public int getCode(){
        return this.code;
    }
    
    public static LatLonRadiusUnit getEnum(int i){
        return map.get(i);
    }
}
