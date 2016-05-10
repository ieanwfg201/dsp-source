package com.kritter.constants;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public enum HygieneCategory
{
    FAMILY_SAFE((short)1,"Family Safe"),
    MATURE((short)2,"Mature"),
    PERFORMANCE((short)3,"Performance"),
    PREMIUM((short)4,"Premium");

    private short code;
    private String description;
    private static Map<Short, HygieneCategory> map = new HashMap<Short, HygieneCategory>();
    static
    {
        for (HygieneCategory val : HygieneCategory.values())
        {
            map.put(val.code, val);
        }
    }

    private HygieneCategory(short code,String description)
    {
        this.code = code;
        this.description = description;
    }

    public String getDescription()
    {
        return this.description;
    }

    public short getCode()
    {
        return this.code;
    }



    public static HygieneCategory fetchHygieneCategoryInstance(Short code)
    {
        if(code == 1)
            return FAMILY_SAFE;
        if(code == 2)
            return MATURE;
        if(code == 3)
            return PERFORMANCE;
        if(code == 4)
            return PREMIUM;

        return null;
    }

    public static Set<HygieneCategory> fetchHygieneCategoryInstance(Short[] codes)
    {
        Set<HygieneCategory> result = new HashSet<HygieneCategory>();

        for(Short code: codes)
        {
            if(code == 1)
                result.add(FAMILY_SAFE);
            else if(code == 2)
                result.add(MATURE);
            else if(code == 3)
                result.add(PERFORMANCE);
            else if(code == 4)
                result.add(PREMIUM);
        }

        return result;
    }

    public static HygieneCategory getEnum(short i)
    {
        return map.get(i);
    }
}
