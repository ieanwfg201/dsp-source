package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

public enum SupportedCurrencies {
    USD(840,"USD");
    private int code;
    private String name;
    private static Map<Integer, SupportedCurrencies> map = new HashMap<Integer, SupportedCurrencies>();
    static {
        for (SupportedCurrencies val : SupportedCurrencies.values()) {
            map.put(val.code, val);
        }
    }

    
    private SupportedCurrencies(int code,String name)
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
    
    public static SupportedCurrencies getEnum(int i)
    {
        return map.get(i);
    }

}
