package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

public enum IddefinitionEnum
{

            NONE(1,"NONE"),
            GET_PUB(2,"GET_PUB"),
            GET_SITE(3,"GET_SITE"),
            GET_EXT_SITE(4,"GET_EXT_SITE"),
            GET_ADV(5,"GET_ADV"),
            GET_CAMPAIGN(6,"GET_CAMPAIGN"),
            GET_AD(7,"GET_AD"),
            GET_CREATIVE(8,"GET_CREATIVE"),
            GET_TP(9,"GET_TP"),
            GET_OS(10,"GET_OS"),
            GET_BRAND(11,"GET_BRAND"),
            GET_MODEL(12,"GET_MODEL"),
            GET_BROWSER(13,"GET_BROWSER");

            private int code;
            private String name;
            private static Map<Integer, IddefinitionEnum> map = new HashMap<Integer, IddefinitionEnum>();
            static {
                for (IddefinitionEnum val : IddefinitionEnum.values()) {
                    map.put(val.code, val);
                }
            }

            
            private IddefinitionEnum(int code,String name)
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
            
            public static IddefinitionEnum getEnum(int i)
            {
                return map.get(i);
            }
}
