package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

public enum ContentContext {

    Video(1,"Video (a video file or stream that is being watched by the user, including (Internet) television broadcasts)"),
    Game(2,"Game (an interactive software game that is being played by the user)"),
    Music(3,"Music (an audio file or stream that is being listened to by the user, including (Internet) radio broadcasts)"),
    Application(4,"Application (an interactive software application that is being used by the user)"),
    Text(5,"Text (a document that is primarily textual in nature that is being read or viewed by the user, including web page, eBook, or news article)"),
    Other(6,"Other (content type unknown or the user is consuming content which does not fit into one of the categories above)"),
    Unknown(7,"Unknown");
    
    private int code;
    private String name;
    private static Map<Integer, ContentContext> map = new HashMap<Integer, ContentContext>();
    static {
        for (ContentContext val : ContentContext.values()) {
            map.put(val.code, val);
        }
    }
    private ContentContext(int code,String name){
        this.code = code;
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    public int getCode(){
        return this.code;
    }
    
    public static ContentContext getEnum(int i){
        return map.get(i);
    }
}
