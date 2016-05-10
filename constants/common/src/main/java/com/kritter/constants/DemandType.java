package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

public enum DemandType
{
            DIRECT(0,"DIRECT"),
            API(1,"API"),
            DSP(2,"DSP");

            private int code;
            private String name;
            private static Map<Integer, DemandType> map = new HashMap<Integer, DemandType>();
            static {
                for (DemandType val : DemandType.values()) {
                    map.put(val.code, val);
                }
            }

            private DemandType(int code,String name)
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
            public static DemandType getEnum(int i)
            {
                return map.get(i);
            }
}
