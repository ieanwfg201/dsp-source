package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

public enum VASTNonLinearTrackingEventTypes {

    creativeView(1,"creativeView"),
    start(2,"start"),
    firstQuartile(3,"firstQuartile"),
    midpoint(4,"midpoint"),
    thirdQuartile(5,"thirdQuartile"),
    complete(6,"complete"),
    mute(7,"mute"),
    unmute(8,"unmute"),
    pause(9,"pause"),
    rewind(10,"rewind"),
    resume(11,"resume"),
    fullscreen(12,"fullscreen"),
    exitFullscreen(13,"exitFullscreen"),
    expand(14,"expand"),
    collapse(15,"collapse"),
    acceptInvitation(16,"acceptInvitation"),
    close(17,"close"),
    progress(18,"progress");
    
    private int code;
    private String name;
    private static Map<Integer, VASTNonLinearTrackingEventTypes> map = new HashMap<Integer, VASTNonLinearTrackingEventTypes>();
    static {
        for (VASTNonLinearTrackingEventTypes val : VASTNonLinearTrackingEventTypes.values()) {
            map.put(val.code, val);
        }
    }
    private VASTNonLinearTrackingEventTypes(int code,String name){
        this.code = code;
        this.name = name;
    }

    public String getName(){
        return this.name;
    }
    
    public int getCode(){
        return this.code;
    }
    
    public static VASTNonLinearTrackingEventTypes getEnum(int i){
        return map.get(i);
    }
}
