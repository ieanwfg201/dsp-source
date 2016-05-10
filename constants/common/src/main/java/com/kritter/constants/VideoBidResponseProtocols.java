package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

public enum VideoBidResponseProtocols {

    //VAST_1_0(1,"VAST 1.0"),
    //VAST_2_0(2,"VAST 2.0"),
    //VAST_3_0(3,"VAST 3.0"),
    //VAST_1_0_WRAPPER(4,"VAST 1.0 Wrapper"),
    VAST_2_0_WRAPPER(5,"VAST 2.0 Wrapper"),
    VAST_3_0_WRAPPER(6,"VAST 3.0 Wrapper");;
    
    private int code;
    private String name;
    private static Map<Integer, VideoBidResponseProtocols> map = new HashMap<Integer, VideoBidResponseProtocols>();
    static {
        for (VideoBidResponseProtocols val : VideoBidResponseProtocols.values()) {
            map.put(val.code, val);
        }
    }
    private VideoBidResponseProtocols(int code,String name){
        this.code = code;
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    public int getCode(){
        return this.code;
    }
    
    public static VideoBidResponseProtocols getEnum(int i){
        return map.get(i);
    }
}
