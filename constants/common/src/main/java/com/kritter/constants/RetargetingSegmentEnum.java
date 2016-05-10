package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

public enum RetargetingSegmentEnum {

    get_retargeting_segments_by_ids(1,"get_retargeting_segments_by_ids"),
    get_retargeting_segments_by_accounts(2,"get_retargeting_segments_by_accounts");

    private int code;
    private String name;
    private static Map<Integer, RetargetingSegmentEnum> map = new HashMap<Integer, RetargetingSegmentEnum>();
    static {
        for (RetargetingSegmentEnum val : RetargetingSegmentEnum.values()) {
            map.put(val.code, val);
        }
    }
    private RetargetingSegmentEnum(int code,String name){
        this.code = code;
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    public int getCode(){
        return this.code;
    }
    
    public static RetargetingSegmentEnum getEnum(int i){
        return map.get(i);
    }
}
