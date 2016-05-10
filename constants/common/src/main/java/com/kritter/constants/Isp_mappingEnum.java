package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

public enum Isp_mappingEnum
{
            get_mappings_by_country(0,"get_mappings_by_country"),
            mark_delete(1,"mark_delete"),
            get_mapped_isp_by_country(3,"get_mapped_isp_by_country"),
            get_rejected_isp_mapping_by_country(4,"get_rejected_isp_mapping_by_country");
            
            private int code;
            private String name;
            private static Map<Integer, Isp_mappingEnum> map = new HashMap<Integer, Isp_mappingEnum>();
            static {
                for (Isp_mappingEnum val : Isp_mappingEnum.values()) {
                    map.put(val.code, val);
                }
            }

            
            private Isp_mappingEnum(int code,String name)
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
            
            public static Isp_mappingEnum getEnum(int i)
            {
                return map.get(i);
            }
}
