package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

public enum NativeIconAPIEnum
{
            get_native_icon_by_id(0,"get_native_icon_by_id"),
            list_native_icon_by_account(1,"list_native_icon_by_account"),
            get_native_icon_by_ids(2,"get_native_icon_by_ids");

            private int code;
            private String name;
            private static Map<Integer, NativeIconAPIEnum> map = new HashMap<Integer, NativeIconAPIEnum>();
            static {
                for (NativeIconAPIEnum val : NativeIconAPIEnum.values()) {
                    map.put(val.code, val);
                }
            }

            
            private NativeIconAPIEnum(int code,String name)
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
            
            public static NativeIconAPIEnum getEnum(int i)
            {
                return map.get(i);
            }
}
