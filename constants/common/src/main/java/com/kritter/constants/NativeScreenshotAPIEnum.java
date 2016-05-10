package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

public enum NativeScreenshotAPIEnum
{
            get_native_screenshot_by_id(0,"get_native_screenshot_by_id"),
            list_native_screenshot_by_account(1,"list_native_screenshot_by_account"),
            get_native_screenshot_by_ids(2,"get_native_screenshot_by_ids");

            private int code;
            private String name;
            private static Map<Integer, NativeScreenshotAPIEnum> map = new HashMap<Integer, NativeScreenshotAPIEnum>();
            static {
                for (NativeScreenshotAPIEnum val : NativeScreenshotAPIEnum.values()) {
                    map.put(val.code, val);
                }
            }

            
            private NativeScreenshotAPIEnum(int code,String name)
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
            
            public static NativeScreenshotAPIEnum getEnum(int i)
            {
                return map.get(i);
            }
}
