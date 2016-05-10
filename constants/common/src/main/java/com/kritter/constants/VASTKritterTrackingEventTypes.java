package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

public enum VASTKritterTrackingEventTypes {

    firstQuartile(3,"firstQuartile"),
    midpoint(4,"midpoint"),
    thirdQuartile(5,"thirdQuartile"),
    complete(6,"complete");
    
    private int code;
    private String name;
    private static Map<Integer, VASTKritterTrackingEventTypes> map = new HashMap<Integer, VASTKritterTrackingEventTypes>();
    static {
        for (VASTKritterTrackingEventTypes val : VASTKritterTrackingEventTypes.values()) {
            map.put(val.code, val);
        }
    }
    private VASTKritterTrackingEventTypes(int code,String name){
        this.code = code;
        this.name = name;
    }

    public String getName(){
        return this.name;
    }
    
    public int getCode(){
        return this.code;
    }
    
    public static VASTKritterTrackingEventTypes getEnum(int i){
        return map.get(i);
    }
}
