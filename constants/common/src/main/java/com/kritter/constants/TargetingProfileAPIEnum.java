package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

public enum TargetingProfileAPIEnum
{
            list_active_targeting_profile_by_account(0,"list_active_targeting_profile_by_account"),
            get_targeting_profile(2,"get_targeting_profile");

            private int code;
            private String name;
            private static Map<Integer, TargetingProfileAPIEnum> map = new HashMap<Integer, TargetingProfileAPIEnum>();
            static {
                for (TargetingProfileAPIEnum val : TargetingProfileAPIEnum.values()) {
                    map.put(val.code, val);
                }
            }

            
            private TargetingProfileAPIEnum(int code,String name)
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
            
            public static TargetingProfileAPIEnum getEnum(int i)
            {
                return map.get(i);
            }
}
