package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

public enum ContentDeliveryMethods {
    
    Unknown(0,"Unknown"),
    Streaming(1,"Streaming"),
    Progressive(2,"Progressive");
    
    private int code;
    private String name;
    private static Map<Integer, ContentDeliveryMethods> map = new HashMap<Integer, ContentDeliveryMethods>();
    static {
        for (ContentDeliveryMethods val : ContentDeliveryMethods.values()) {
            map.put(val.code, val);
        }
    }
    private ContentDeliveryMethods(int code,String name){
        this.code = code;
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    public int getCode(){
        return this.code;
    }
    
    public static ContentDeliveryMethods getEnum(int i){
        return map.get(i);
    }
}
