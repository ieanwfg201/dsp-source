package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

public enum Protocol
{
            HTTP(1,"HTTP"),
            HTTPS(2,"HTTPS"),
            HTTP_HTTPS(3,"HTTP_HTTPS");

            private int code;
            private String name;
            private static Map<Integer, Protocol> map = new HashMap<Integer, Protocol>();
            static {
                for (Protocol val : Protocol.values()) {
                    map.put(val.code, val);
                }
            }

            private Protocol(int code,String name)
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
            public static Protocol getEnum(int i)
            {
                return map.get(i);
            }
}
