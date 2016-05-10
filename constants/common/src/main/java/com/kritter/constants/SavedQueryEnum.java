package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

public enum SavedQueryEnum
{
            list_saved_query_entity_by_account_guids(0,"list_saved_query_entity_by_account_guids"),
            delete_saved_query(2,"delete_saved_query"),
            list_saved_query_entity_by_entity_id(3,"list_saved_query_entity_by_entity_id");
            private int code;
            private String name;
            private static Map<Integer, SavedQueryEnum> map = new HashMap<Integer, SavedQueryEnum>();
            static {
                for (SavedQueryEnum val : SavedQueryEnum.values()) {
                    map.put(val.code, val);
                }
            }

            
            private SavedQueryEnum(int code,String name)
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
            
            public static SavedQueryEnum getEnum(int i)
            {
                return map.get(i);
            }
}
