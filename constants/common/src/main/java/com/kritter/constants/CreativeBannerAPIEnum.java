package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

public enum CreativeBannerAPIEnum
{
            get_creative_banner(0,"get_creative_banner"),
            list_creative_banner_by_account(1,"list_creative_banner_by_account"),
            get_creative_banner_by_ids(2,"get_creative_banner_by_ids");

            private int code;
            private String name;
            private static Map<Integer, CreativeBannerAPIEnum> map = new HashMap<Integer, CreativeBannerAPIEnum>();
            static {
                for (CreativeBannerAPIEnum val : CreativeBannerAPIEnum.values()) {
                    map.put(val.code, val);
                }
            }

            
            private CreativeBannerAPIEnum(int code,String name)
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
            
            public static CreativeBannerAPIEnum getEnum(int i)
            {
                return map.get(i);
            }
}
