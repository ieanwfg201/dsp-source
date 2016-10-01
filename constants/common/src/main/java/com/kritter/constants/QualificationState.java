package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

public enum QualificationState {
    
    add(1,"add"),
    update(2,"update"),
    delete(3,"delete");
    
    private int code;
    private String name;
    private static Map<Integer, QualificationState> map = new HashMap<Integer, QualificationState>();
    static {
        for (QualificationState val : QualificationState.values()) {
            map.put(val.code, val);
        }
    }
    private QualificationState(int code,String name){
        this.code = code;
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    public int getCode(){
        return this.code;
    }
    
    public static QualificationState getEnum(int i){
        return map.get(i);
    }
}
