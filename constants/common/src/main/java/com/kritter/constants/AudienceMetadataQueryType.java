package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

public enum AudienceMetadataQueryType
{
			list_all(1,"list_all");

            private int code;
            private String name;
            private static Map<Integer, AudienceMetadataQueryType> map = new HashMap<Integer, AudienceMetadataQueryType>();
            static {
                for (AudienceMetadataQueryType val : AudienceMetadataQueryType.values()) {
                    map.put(val.code, val);
                }
            }

            private AudienceMetadataQueryType(int code,String name)
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
            public static AudienceMetadataQueryType getEnum(int i)
            {
                return map.get(i);
            }
}
