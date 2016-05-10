package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

public enum IddefinitionType
{

            ID(1,"ID"),
            GUID(2,"GUID");

            private int code;
            private String name;
            private static Map<Integer, IddefinitionType> map = new HashMap<Integer, IddefinitionType>();
            static {
                for (IddefinitionType val : IddefinitionType.values()) {
                    map.put(val.code, val);
                }
            }

            
            private IddefinitionType(int code,String name)
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
            
            public static IddefinitionType getEnum(int i)
            {
                return map.get(i);
            }
}
