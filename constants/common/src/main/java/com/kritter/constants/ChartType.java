package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

public enum ChartType
{
            TABLE(0,"TABLE"),
            PIE(1,"PIE"),
            BAR(2,"BAR"),
            LINES(3,"LINES");

            private int code;
            private String name;
            private static Map<Integer, ChartType> map = new HashMap<Integer, ChartType>();
            static {
                for (ChartType val : ChartType.values()) {
                    map.put(val.code, val);
                }
            }

            private ChartType(int code,String name)
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
            public static ChartType getEnum(int i)
            {
                return map.get(i);
            }
}
