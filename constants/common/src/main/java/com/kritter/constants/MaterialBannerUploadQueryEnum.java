package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

public enum MaterialBannerUploadQueryEnum
{
	list_material_banner(0,"list_material_banner"),
	list_material_banner_by_pubincids(1,"list_material_banner_by_pubincids"),
	list_material_banner_by_pubincids_state(2,"list_material_banner_by_pubincids_state");

            private int code;
            private String name;
            private static Map<Integer, MaterialBannerUploadQueryEnum> map = new HashMap<Integer, MaterialBannerUploadQueryEnum>();
            static {
                for (MaterialBannerUploadQueryEnum val : MaterialBannerUploadQueryEnum.values()) {
                    map.put(val.code, val);
                }
            }

            
            private MaterialBannerUploadQueryEnum(int code,String name)
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
            
            public static MaterialBannerUploadQueryEnum getEnum(int i)
            {
                return map.get(i);
            }
}
