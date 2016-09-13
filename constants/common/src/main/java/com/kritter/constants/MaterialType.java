package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

public enum MaterialType
{
            ADPOSITION(0,"ADPOSITION"),
            BANNERUPLOAD(1,"BANNERUPLOAD"),
            VIDEOUPLOAD(2,"VIDEOUPLOAD");

            private int code;
            private String name;
            private static Map<Integer, MaterialType> map = new HashMap<Integer, MaterialType>();
            static {
                for (MaterialType val : MaterialType.values()) {
                    map.put(val.code, val);
                }
            }

            
            private MaterialType(int code,String name)
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
            
            public static MaterialType getEnum(int i)
            {
                return map.get(i);
            }
}
