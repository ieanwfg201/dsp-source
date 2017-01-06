package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

public enum AudienceMetadata
{
			/**
			 * Same as that present in audience_metadata table
			 */
			Gender(1,"Gender"),
			AgeRange(2,"AgeRange"),
			AudienceCategory(3," AudienceCategory");

            private int code;
            private String name;
            private static Map<Integer, AudienceMetadata> map = new HashMap<Integer, AudienceMetadata>();
            static {
                for (AudienceMetadata val : AudienceMetadata.values()) {
                    map.put(val.code, val);
                }
            }

            private AudienceMetadata(int code,String name)
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
            public static AudienceMetadata getEnum(int i)
            {
                return map.get(i);
            }
}
