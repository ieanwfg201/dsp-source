package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is used in real time scenario from ad request to bid request
 * conversion, where site.isnative and site.isvideo or site.isbanner
 * functionality is overwritten by getting one of these values from
 * ad request of the direct publisher.
 */
public enum BidRequestImpressionType
{
    BANNER(1,"banner"),
    NATIVE(2,"native"),
    VIDEO(3,"video");

    private int code;
    private String name;
    private static Map<Integer, BidRequestImpressionType> map = new HashMap<Integer, BidRequestImpressionType>();
    static {
        for (BidRequestImpressionType val : BidRequestImpressionType.values()) {
            map.put(val.code, val);
        }
    }
    private BidRequestImpressionType(int code,String name){
        this.code = code;
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    public int getCode(){
        return this.code;
    }

    public static BidRequestImpressionType getEnum(int i){
        return map.get(i);
    }

}
