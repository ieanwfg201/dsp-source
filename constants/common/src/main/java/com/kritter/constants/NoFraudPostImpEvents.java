package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

public enum NoFraudPostImpEvents {


    csc(1,"csc"),
    win(2,"win"),
    clk(3,"clk");
    
    private int code;
    private String name;
    private static Map<Integer, NoFraudPostImpEvents> map = new HashMap<Integer, NoFraudPostImpEvents>();
    static {
        for (NoFraudPostImpEvents val : NoFraudPostImpEvents.values()) {
            map.put(val.code, val);
        }
    }
    private NoFraudPostImpEvents(int code,String name){
        this.code = code;
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    public int getCode(){
        return this.code;
    }
    
    public static NoFraudPostImpEvents getEnum(int i){
        return map.get(i);
    }

}
