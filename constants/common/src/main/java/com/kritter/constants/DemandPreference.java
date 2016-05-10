package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

public enum DemandPreference
{
            DIRECT(0,"DIRECT"),
            DIRECTthenMediation(1,"DIRECTthenMediation"),
            OnlyMediation(2,"OnlyMediation"),
            OnlyDSP(3,"OnlyDSP"),
            DirectThenDSP(4,"DirectThenDSP");

            private int code;
            private String name;
            private static Map<Integer, DemandPreference> map = new HashMap<Integer, DemandPreference>();
            static {
                for (DemandPreference val : DemandPreference.values()) {
                    map.put(val.code, val);
                }
            }

            private DemandPreference(int code,String name)
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
            public static DemandPreference getEnum(int i)
            {
                return map.get(i);
            }
}
