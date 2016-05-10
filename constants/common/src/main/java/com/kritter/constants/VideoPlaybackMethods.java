package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

public enum VideoPlaybackMethods {
    Unknown(0,"Unknown"),
    Auto_Play_Sound_On(1,"Auto-Play Sound On"),
    Auto_Play_Sound_Off(2,"Auto-Play Sound Off"),
    Click_to_Play(3,"Click-to-Play"),
    Mouse_Over(4,"Mouse-Over");
    
    private int code;
    private String name;
    private static Map<Integer, VideoPlaybackMethods> map = new HashMap<Integer, VideoPlaybackMethods>();
    static {
        for (VideoPlaybackMethods val : VideoPlaybackMethods.values()) {
            map.put(val.code, val);
        }
    }
    private VideoPlaybackMethods(int code,String name){
        this.code = code;
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    public int getCode(){
        return this.code;
    }
    
    public static VideoPlaybackMethods getEnum(int i){
        return map.get(i);
    }
}
