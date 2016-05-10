package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

public enum VASTLinearTrackingEventTypes {

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
    acceptInvitationLinear(16,"acceptInvitationLinear"),
    closeLinear(17,"closeLinear"),
    skip(18,"skip"),
    progress(19,"progress");
    
    private int code;
    private String name;
    private static Map<Integer, VASTLinearTrackingEventTypes> map = new HashMap<Integer, VASTLinearTrackingEventTypes>();
    static {
        for (VASTLinearTrackingEventTypes val : VASTLinearTrackingEventTypes.values()) {
            map.put(val.code, val);
        }
    }
    private VASTLinearTrackingEventTypes(int code,String name){
        this.code = code;
        this.name = name;
    }

    public String getName(){
        return this.name;
    }
    
    public int getCode(){
        return this.code;
    }
    
    public static VASTLinearTrackingEventTypes getEnum(int i){
        return map.get(i);
    }
}
