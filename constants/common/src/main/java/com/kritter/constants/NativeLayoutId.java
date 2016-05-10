package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

public enum NativeLayoutId {

    Content_Wall(1,"Content_Wall"),
    App_Wall(2,"App_Wall"),
    News_Feed(3,"News_Feed"),
    Chat_List(4,"Chat_List"),
    Carousel(5,"Carousel"),
    Content_Stream(6,"Content_Stream"),
    Grid_adjoining_the_content(7,"Grid_adjoining_the_content");
    
    private int code;
    private String name;
    private static Map<Integer, NativeLayoutId> map = new HashMap<Integer, NativeLayoutId>();
    static {
        for (NativeLayoutId val : NativeLayoutId.values()) {
            map.put(val.code, val);
        }
    }
    private NativeLayoutId(int code,String name){
        this.code = code;
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    public int getCode(){
        return this.code;
    }
    
    public static NativeLayoutId getEnum(int i){
        return map.get(i);
    }
}
