package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

public enum NoBidReason
{
    Unknown_Error(0,"Unknown Error"),
    Technical_Error(1,"Technical Error"),
    Invalid_Request(2,"Invalid Request"),
    Known_Web_Spider(3,"Known Web Spider"),
    Suspected_Non_Human_Traffic(4,"Suspected Non-Human Traffic"),
    Cloud_DataCenter_ProxyIP(5,"Cloud, Data center, or Proxy IP"),
    Unsupported_Device(6,"Unsupported Device"),
    Blocked_Publisher_Site(7,"Blocked Publisher or Site"),
    Unmatched_User(8,"Unmatched User");
    
    private int code;
    private String name;
    private static Map<Integer, NoBidReason> map = new HashMap<Integer, NoBidReason>();
    static {
        for (NoBidReason val : NoBidReason.values()) {
            map.put(val.code, val);
        }
    }

    private NoBidReason(int code,String name)
    {
        this.code = code;
        this.name = name;
    }

    public String getName()
    {
        return this.name;
    }

    public int getCode()
    {
        return this.code;
    }
    public static NoBidReason getEnum(int i)
    {
        return map.get(i);
    }
}
