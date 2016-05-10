package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

public enum CreativeContainerAPIEnum
{
            get_creative_container(0,"get_creative_container"),
            list_creative_container_by_account(1,"list_creative_container_by_account"),
            list_creative_container_by_status(2,"list_creative_container_by_status"),
            update_status(3,"update_status"),
            update_multiple_status(4,"update_multiple_status");

            private int code;
            private String name;
            private static Map<Integer, CreativeContainerAPIEnum> map = new HashMap<Integer, CreativeContainerAPIEnum>();
            static {
                for (CreativeContainerAPIEnum val : CreativeContainerAPIEnum.values()) {
                    map.put(val.code, val);
                }
            }

            
            private CreativeContainerAPIEnum(int code,String name)
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
            
            public static CreativeContainerAPIEnum getEnum(int i)
            {
                return map.get(i);
            }
}
