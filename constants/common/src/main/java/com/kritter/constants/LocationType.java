package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

public enum LocationType {

    GPS_Location_Services(1,"GPS/Location Services"),
    IP_Address(2,"IP Address"),
    User_provided(3,"User provided (e.g., registration data)");
    
    private int code;
    private String name;
    private static Map<Integer, LocationType> map = new HashMap<Integer, LocationType>();
    static {
        for (LocationType val : LocationType.values()) {
            map.put(val.code, val);
        }
    }
    private LocationType(int code,String name){
        this.code = code;
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    public int getCode(){
        return this.code;
    }
    
    public static LocationType getEnum(int i){
        return map.get(i);
    }
}
