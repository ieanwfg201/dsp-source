package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

public enum ExpandableDirection {
	Left(1,"Left"),
	Right(2,"Right"),
	Up(3,"Up"),
	Down(4,"Down"),
	FullScreen(5,"Full Screen");
    
    private int code;
    private String name;
    private static Map<Integer, ExpandableDirection> map = new HashMap<Integer, ExpandableDirection>();
    static {
        for (ExpandableDirection val : ExpandableDirection.values()) {
            map.put(val.code, val);
        }
    }
    private ExpandableDirection(int code,String name){
        this.code = code;
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    public int getCode(){
        return this.code;
    }
    
    public static ExpandableDirection getEnum(int i){
        return map.get(i);
    }
}
