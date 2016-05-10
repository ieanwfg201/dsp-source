package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

public enum SupplySourceEnum
{

            NETWORK((short)1,"Only Network"),
            EXCHANGE((short)2,"Only Exchange"),
            EXCHANGE_NETWORK((short)3,"Either exchange or network");

            private short code;
            private String name;
            private static Map<Short, SupplySourceEnum> map = new HashMap<Short, SupplySourceEnum>();
            static {
                for (SupplySourceEnum val : SupplySourceEnum.values()) {
                    map.put(val.code, val);
                }
            }

            
            private SupplySourceEnum(short code,String name)
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
            
            public static SupplySourceEnum getEnum(short i)
            {
                return map.get(i);
            }
}
