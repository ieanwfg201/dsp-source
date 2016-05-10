package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

public enum QAGMediaRatings {

    AllAudiences(1,"All Audiences"),
    EveryoneOver12(2,"Everyone Over 12"),
    MatureAudiences(3,"Mature Audiences");
    
    private int code;
    private String name;
    private static Map<Integer, QAGMediaRatings> map = new HashMap<Integer, QAGMediaRatings>();
    static {
        for (QAGMediaRatings val : QAGMediaRatings.values()) {
            map.put(val.code, val);
        }
    }
    private QAGMediaRatings(int code,String name){
        this.code = code;
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    public int getCode(){
        return this.code;
    }
    
    public static QAGMediaRatings getEnum(int i){
        return map.get(i);
    }
}
