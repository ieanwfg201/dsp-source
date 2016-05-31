package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

public enum ReqLoggingEnum
{
            get_all_req_logging_entities(0,"get_all_req_logging_entities"),
            get_specific_req_logging_entities(1,"get_specific_req_logging_entities");
            
            private int code;
            private String name;
            private static Map<Integer, ReqLoggingEnum> map = new HashMap<Integer, ReqLoggingEnum>();
            static {
                for (ReqLoggingEnum val : ReqLoggingEnum.values()) {
                    map.put(val.code, val);
                }
            }
            
            private ReqLoggingEnum(int code,String name)
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
            
            public static ReqLoggingEnum getEnum(int i)
            {
                return map.get(i);
            }
}
