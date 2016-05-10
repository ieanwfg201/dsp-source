package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

public enum DataSource
{
            maxmind((short)1,"maxmind"),
            mobiclicks((short)2,"mobiclicks");

            private short code;
            private String name;
            private static Map<Short, DataSource> map = new HashMap<Short, DataSource>();
            static {
                for (DataSource val : DataSource.values()) {
                    map.put(val.code, val);
                }
            }

            private DataSource(short code,String name)
            {
                this.code = code;
                this.name = name;
            }

            public String getName()
            {
                return this.name;
            }

            public short getCode()
            {
                return this.code;
            }
            public static DataSource getEnum(short i)
            {
                return map.get(i);
            }
}
