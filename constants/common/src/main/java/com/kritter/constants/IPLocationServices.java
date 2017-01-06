package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

public enum IPLocationServices {
	Unknown(-1,"Unknown"),
	ip2location(1,"ip2location"),
	Neustar_Quova(2,"Neustar (Quova)"),
	MaxMind(3,"MaxMind"),
	NetAquity_Digital_Element(4,"NetAquity (Digital Element)");
    
    private int code;
    private String name;
    private static Map<Integer, IPLocationServices> map = new HashMap<Integer, IPLocationServices>();
    static {
        for (IPLocationServices val : IPLocationServices.values()) {
            map.put(val.code, val);
        }
    }
    private IPLocationServices(int code,String name){
        this.code = code;
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    public int getCode(){
        return this.code;
    }
    
    public static IPLocationServices getEnum(int i){
        return map.get(i);
    }
}
