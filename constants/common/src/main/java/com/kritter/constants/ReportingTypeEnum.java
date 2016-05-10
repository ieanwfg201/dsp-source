package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

public enum ReportingTypeEnum
{
            SUPPLY(1,"SUPPLY"),
            DEMAND(2,"DEMAND"),
            NETWORK(3,"NETWORK");
            private int code;
            private String name;
            private static Map<Integer, ReportingTypeEnum> map = new HashMap<Integer, ReportingTypeEnum>();
            static {
                for (ReportingTypeEnum val : ReportingTypeEnum.values()) {
                    map.put(val.code, val);
                }
            }

            
            private ReportingTypeEnum(int code,String name)
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
            
            public static ReportingTypeEnum getEnum(int i)
            {
                return map.get(i);
            }
}
