package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

public enum NativeScreenShotImageSize {

    I600x313(1,"600*313"),
    I1200x627(2,"1200*627"),
    I250x300(3,"250*300"),
    I300x250(4,"300*250"),
    I728x90(5,"728*90"),
    I568x320(6,"568*320"),
    I1136x640(7,"1136*640"),
    I1280x720(8,"1280*720"),
    I320x50(9,"320*50"),
    I320x480(10,"320*480"),
    I640x960(11,"640*960"),
    I800x1200(12,"800*1200"),
    I480x320(13,"480*320"),
    I960x640(14,"960*640"),
    I1200x800(15,"1200*800"),
    I320x568(16,"320*568"),
    I640x1136(17,"640*1136"),
    I720x1280(18,"720*1280");
    
    private int code;
    private String name;
    private static Map<Integer, NativeScreenShotImageSize> map = new HashMap<Integer, NativeScreenShotImageSize>();
    static {
        for (NativeScreenShotImageSize val : NativeScreenShotImageSize.values()) {
            map.put(val.code, val);
        }
    }
    private NativeScreenShotImageSize(int code,String name){
        this.code = code;
        this.name = name;
    }

    public String getName(){
        return this.name;
    }
    public String getDesc(){
        return this.name;
    }

    public int getCode(){
        return this.code;
    }
    
    public static NativeScreenShotImageSize getEnum(int i){
        return map.get(i);
    }
}
