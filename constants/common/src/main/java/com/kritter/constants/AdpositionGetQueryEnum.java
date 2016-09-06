package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

public enum AdpositionGetQueryEnum
{
	list_adposition_get(0,"list_adposition_get");

            private int code;
            private String name;
            private static Map<Integer, AdpositionGetQueryEnum> map = new HashMap<Integer, AdpositionGetQueryEnum>();
            static {
                for (AdpositionGetQueryEnum val : AdpositionGetQueryEnum.values()) {
                    map.put(val.code, val);
                }
            }

            
            private AdpositionGetQueryEnum(int code,String name)
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
            
            public static AdpositionGetQueryEnum getEnum(int i)
            {
                return map.get(i);
            }
}
