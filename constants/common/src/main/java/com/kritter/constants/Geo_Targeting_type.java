package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

public enum Geo_Targeting_type
{

            COUNTRY_CARRIER(0,"COUNTRY_CARRIER"),
            IP(1,"IP"),
            ZIPCODE(2,"ZIPCODE");

            private int code;
            private String name;
            private static Map<Integer, Geo_Targeting_type> map = new HashMap<Integer, Geo_Targeting_type>();
            static {
                for (Geo_Targeting_type val : Geo_Targeting_type.values()) {
                    map.put(val.code, val);
                }
            }

            
            private Geo_Targeting_type(int code,String name)
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
            
            public static Geo_Targeting_type getEnum(int i)
            {
                return map.get(i);
            }
}
