package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

public enum AudienceDefinitionQueryType
{
			list_by_type(1,"list_by_type"),
			list_by_type_tier(2,"list_by_type_tier"),
			list_by_ids(3,"list_by_ids"),
			list_by_parentids(4,"list_by_parentids"),
			list_cat_by_tier(5,"list_cat_by_tier");

            private int code;
            private String name;
            private static Map<Integer, AudienceDefinitionQueryType> map = new HashMap<Integer, AudienceDefinitionQueryType>();
            static {
                for (AudienceDefinitionQueryType val : AudienceDefinitionQueryType.values()) {
                    map.put(val.code, val);
                }
            }

            private AudienceDefinitionQueryType(int code,String name)
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
            public static AudienceDefinitionQueryType getEnum(int i)
            {
                return map.get(i);
            }
}
