package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

public enum ReportingDIMTypeEnum
{
            LIMITED(1,"LIMITED"),
            EXHAUSTIVE(2,"EXHAUSTIVE");
            private int code;
            private String name;
            private static Map<Integer, ReportingDIMTypeEnum> map = new HashMap<Integer, ReportingDIMTypeEnum>();
            static {
                for (ReportingDIMTypeEnum val : ReportingDIMTypeEnum.values()) {
                    map.put(val.code, val);
                }
            }

            
            private ReportingDIMTypeEnum(int code,String name)
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
            
            public static ReportingDIMTypeEnum getEnum(int i)
            {
                return map.get(i);
            }
}
