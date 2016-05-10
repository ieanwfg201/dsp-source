package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

public enum SupplySourceTypeEnum
{

            APP_WAP((short)1,"APP and WAP"),
            WAP((short)2,"WAP"),
            APP((short)3,"APP");

            private short code;
            private String name;
            private static Map<Short, SupplySourceTypeEnum> map = new HashMap<Short, SupplySourceTypeEnum>();
            static {
                for (SupplySourceTypeEnum val : SupplySourceTypeEnum.values()) {
                    map.put(val.code, val);
                }
            }

            
            private SupplySourceTypeEnum(short code,String name)
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
            
            public static SupplySourceTypeEnum getEnum(short i)
            {
                return map.get(i);
            }
}
