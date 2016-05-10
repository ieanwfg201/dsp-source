package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

/**
 * This class keeps constant values for parent account type,
 * used in defining business entities that own our software.
 */
public enum ParentAccountType
{
    AD_NETWORK(1,"Ad Network as business entity"),
    ATD(2,"Agency Trading Desk as business entity"),
    PTD(3,"Publisher Trading Desk as business entity"),
    DEFAULT(4,"Default, where business entity not defined.");

    private int id;
    private String name;

    private static Map<Integer, ParentAccountType> map = new HashMap<Integer, ParentAccountType>();

    static
    {
        for (ParentAccountType val : ParentAccountType.values())
        {
            map.put(val.id, val);
        }
    }

    private ParentAccountType(int id,String name)
    {
        this.id = id;
        this.name = name;
    }

    public String getName()
    {
        return this.name;
    }

    public int getId()
    {
        return this.id;
    }

    public static ParentAccountType getEnum(int i)
    {
        return map.get(i);
    }
}