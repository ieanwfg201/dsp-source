package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

public enum MaterialAdvInfoUploadQueryEnum
{
	list_material_advinfo(0,"list_material_advinfo"),
	list_material_advinfo_by_pubincids(1,"list_material_advinfo_by_pubincids");

            private int code;
            private String name;
            private static Map<Integer, MaterialAdvInfoUploadQueryEnum> map = new HashMap<Integer, MaterialAdvInfoUploadQueryEnum>();
            static {
                for (MaterialAdvInfoUploadQueryEnum val : MaterialAdvInfoUploadQueryEnum.values()) {
                    map.put(val.code, val);
                }
            }

            
            private MaterialAdvInfoUploadQueryEnum(int code,String name)
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
            
            public static MaterialAdvInfoUploadQueryEnum getEnum(int i)
            {
                return map.get(i);
            }
}
