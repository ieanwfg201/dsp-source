package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

public enum AdxBasedExchangesMetadataQueryEnum
{
	get_all(0,"get_all"),
	get_by_internalids(1,"get_by_internalids"),
	get_by_pubincids(2,"get_by_pubincids");

            private int code;
            private String name;
            private static Map<Integer, AdxBasedExchangesMetadataQueryEnum> map = new HashMap<Integer, AdxBasedExchangesMetadataQueryEnum>();
            static {
                for (AdxBasedExchangesMetadataQueryEnum val : AdxBasedExchangesMetadataQueryEnum.values()) {
                    map.put(val.code, val);
                }
            }

            
            private AdxBasedExchangesMetadataQueryEnum(int code,String name)
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
            
            public static AdxBasedExchangesMetadataQueryEnum getEnum(int i)
            {
                return map.get(i);
            }
}
