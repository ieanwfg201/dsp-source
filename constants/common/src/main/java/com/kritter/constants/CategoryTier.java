package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

public enum CategoryTier
{
            TIER1(1,"TIER1"),
            TIER2(2,"TIER2");

            private int code;
            private String name;
            private static Map<Integer, CategoryTier> map = new HashMap<Integer, CategoryTier>();
            static {
                for (CategoryTier val : CategoryTier.values()) {
                    map.put(val.code, val);
                }
            }

            private CategoryTier(int code,String name)
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
            public static CategoryTier getEnum(int i)
            {
                return map.get(i);
            }
}
