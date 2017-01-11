package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

public enum VideoInfoAPIEnum
{
            get_video_info_by_id(0,"get_video_info_by_id"),
            list_video_info_by_account(1,"list_video_info_by_account"),
            get_video_info_by_ids(2,"get_video_info_by_ids"),
			get_video_info_by_pub(3,"get_video_info_by_pub");
	
            private int code;
            private String name;
            private static Map<Integer, VideoInfoAPIEnum> map = new HashMap<Integer, VideoInfoAPIEnum>();
            static {
                for (VideoInfoAPIEnum val : VideoInfoAPIEnum.values()) {
                    map.put(val.code, val);
                }
            }

            
            private VideoInfoAPIEnum(int code,String name)
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
            
            public static VideoInfoAPIEnum getEnum(int i)
            {
                return map.get(i);
            }
}
