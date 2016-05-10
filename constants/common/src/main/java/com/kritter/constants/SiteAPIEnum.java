package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

public enum SiteAPIEnum
{
            none(0,"none"),
            approve_multiple_site(1,"approve_multiple_site"),
            reject_multiple_site(2,"reject_multiple_site"),
            pause_multiple_site(3,"pause_multiple_site"),
            change_site_payout(4,"change_site_payout");
            

            private int code;
            private String name;
            private static Map<Integer, SiteAPIEnum> map = new HashMap<Integer, SiteAPIEnum>();
            static {
                for (SiteAPIEnum val : SiteAPIEnum.values()) {
                    map.put(val.code, val);
                }
            }

            
            private SiteAPIEnum(int code,String name)
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
            
            public static SiteAPIEnum getEnum(int i)
            {
                return map.get(i);
            }
}
