package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

public enum AudienceTypeEnum
{

            MMA_TAG(1,"MMA Tag"),
            AUDIENCE_PACKAGE(2,"Audience Package");

            private int code;
            private String name;
            private static Map<Integer, AudienceTypeEnum> map = new HashMap<Integer, AudienceTypeEnum>();
            static {
                for (AudienceTypeEnum val : AudienceTypeEnum.values()) {
                    map.put(val.code, val);
                }
            }

            private AudienceTypeEnum(int code, String name)
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
            
            public static AudienceTypeEnum getEnum(int i)
            {
                return map.get(i);
            }
}
