package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

public enum IOStatus
{
            NEW(0,"new"),
            REJECTED(1,"Rejected"),
            Approved(3,"Approved and added to account");

            private int code;
            private String name;
            private static Map<Integer, IOStatus> map = new HashMap<Integer, IOStatus>();
            static {
                for (IOStatus val : IOStatus.values()) {
                    map.put(val.code, val);
                }
            }

            
            private IOStatus(int code,String name)
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
            
            public static IOStatus getEnum(int i)
            {
                return map.get(i);
            }
}
