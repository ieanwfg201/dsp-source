package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

public enum QualificationDefEnum
{
	select_qualification_byadvertisers(0,"select_qualification_byadvertisers"),
	delete_multiple_qualification(1,"delete_multiple_qualification"),
	select_qualification_byinternalids(2,"select_qualification_byinternalids"),
	select_by_name_adv(3,"select_by_name_adv");

            private int code;
            private String name;
            private static Map<Integer, QualificationDefEnum> map = new HashMap<Integer, QualificationDefEnum>();
            static {
                for (QualificationDefEnum val : QualificationDefEnum.values()) {
                    map.put(val.code, val);
                }
            }

            
            private QualificationDefEnum(int code,String name)
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
            
            public static QualificationDefEnum getEnum(int i)
            {
                return map.get(i);
            }
}
