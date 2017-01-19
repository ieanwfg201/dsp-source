package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

public enum FeedTypes {
	Unknown(-1,"Unknown"),
	MusicService(1,"MusicService"),
	FM_AM_Broadcast(2,"FM_AM_Broadcast"),
	Podcast(3,"Podcast");
    
    private int code;
    private String name;
    private static Map<Integer, FeedTypes> map = new HashMap<Integer, FeedTypes>();
    static {
        for (FeedTypes val : FeedTypes.values()) {
            map.put(val.code, val);
        }
    }
    private FeedTypes(int code,String name){
        this.code = code;
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    public int getCode(){
        return this.code;
    }
    
    public static FeedTypes getEnum(int i){
        return map.get(i);
    }
}
