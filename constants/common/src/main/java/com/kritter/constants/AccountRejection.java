package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

public enum AccountRejection {

    Account_found_violating_TnC(1,"Account_found_violating_TnC"),
    Account_earlier_caught_in_fraudulent_activity(2,"Account_earlier_caught_in_fraudulent_activity");

    private int code;
    private String name;
    private static Map<Integer, AccountRejection> map = new HashMap<Integer, AccountRejection>();
    static {
        for (AccountRejection val : AccountRejection.values()) {
            map.put(val.code, val);
        }
    }

    
    private AccountRejection(int code,String name)
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
    
    public static AccountRejection getEnum(int i)
    {
        return map.get(i);
    }
}
