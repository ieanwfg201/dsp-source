package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

public enum AccountAPIEnum
{
            list_active_advertiser_by_demandtype(0,"list_active_advertiser_by_demandtype");

            private int code;
            private String name;
            private static Map<Integer, AccountAPIEnum> map = new HashMap<Integer, AccountAPIEnum>();
            static {
                for (AccountAPIEnum val : AccountAPIEnum.values()) {
                    map.put(val.code, val);
                }
            }

            
            private AccountAPIEnum(int code,String name)
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
            
            public static AccountAPIEnum getEnum(int i)
            {
                return map.get(i);
            }
}
