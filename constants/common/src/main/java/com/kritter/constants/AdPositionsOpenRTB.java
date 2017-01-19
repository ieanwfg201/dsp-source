package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

public enum AdPositionsOpenRTB {

    Unknown(0,"Unknown"),
    AboveTheFold(1,"Above the Fold"),
    DEPRECATED(2,"DEPRECATED - May or may not be initially visible depending on screen size/resolution"),
    BelowTheFold(3,"Below the Fold"),
    Header(4,"Header"),
    Footer(5,"Footer"),
    Sidebar(6,"Sidebar"),
    FullScreen(3,"Full Screen");
    
    private int code;
    private String name;
    private static Map<Integer, AdPositionsOpenRTB> map = new HashMap<Integer, AdPositionsOpenRTB>();
    static {
        for (AdPositionsOpenRTB val : AdPositionsOpenRTB.values()) {
            map.put(val.code, val);
        }
    }
    private AdPositionsOpenRTB(int code,String name){
        this.code = code;
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    public int getCode(){
        return this.code;
    }
    
    public static AdPositionsOpenRTB getEnum(int i){
        return map.get(i);
    }
}
