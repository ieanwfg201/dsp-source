package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is used for defining account user types.
 */
public enum AccountUserType
{
    ADMIN(1,"Admin Super User having complete acess to demand and supply");

    private int id;
    private String name;

    private static Map<Integer, AccountUserType> map = new HashMap<Integer, AccountUserType>();

    static
    {
        for (AccountUserType val : AccountUserType.values())
        {
            map.put(val.id, val);
        }
    }

    private AccountUserType(int id,String name)
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

    public static AccountUserType getEnum(int i)
    {
        return map.get(i);
    }
}
