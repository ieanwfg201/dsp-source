package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

public enum ReportingTableType
{
            FIRSTLEVEL(1,"FIRSTLEVEL"),
            CHANNEL(2,"CHANNEL"),
            ADPOSITION(3,"ADPOSITION");
            private int code;
            private String name;
            private static Map<Integer, ReportingTableType> map = new HashMap<Integer, ReportingTableType>();
            static {
                for (ReportingTableType val : ReportingTableType.values()) {
                    map.put(val.code, val);
                }
            }

            
            private ReportingTableType(int code,String name)
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
            
            public static ReportingTableType getEnum(int i)
            {
                return map.get(i);
            }
}
