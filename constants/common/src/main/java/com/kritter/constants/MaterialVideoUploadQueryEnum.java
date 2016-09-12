package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

public enum MaterialVideoUploadQueryEnum
{
	list_material_video(0,"list_material_video"),
	list_material_video_by_pubincids(1,"list_material_video_by_pubincids");

            private int code;
            private String name;
            private static Map<Integer, MaterialVideoUploadQueryEnum> map = new HashMap<Integer, MaterialVideoUploadQueryEnum>();
            static {
                for (MaterialVideoUploadQueryEnum val : MaterialVideoUploadQueryEnum.values()) {
                    map.put(val.code, val);
                }
            }

            
            private MaterialVideoUploadQueryEnum(int code,String name)
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
            
            public static MaterialVideoUploadQueryEnum getEnum(int i)
            {
                return map.get(i);
            }
}
