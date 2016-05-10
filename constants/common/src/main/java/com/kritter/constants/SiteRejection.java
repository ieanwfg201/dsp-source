package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

public enum SiteRejection {

    Not_a_mobile_Site_or_App(1,"Not_a_mobile_Site_or_App"),
    Site_found_violating_TnC(2,"Site_found_violating_TnC"),
    Site_or_App_not_operational(3," Site_or_App_not_operational"),
    Insufficient_content_on_Site_or_App(4,"Insufficient_content_on_Site_or_App");

    private int code;
    private String name;
    private static Map<Integer, SiteRejection> map = new HashMap<Integer, SiteRejection>();
    static {
        for (SiteRejection val : SiteRejection.values()) {
            map.put(val.code, val);
        }
    }

    
    private SiteRejection(int code,String name)
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
    
    public static SiteRejection getEnum(int i)
    {
        return map.get(i);
    }
}
