package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

public enum PayoutThreshold
{
	campaign_absolute_payout_threshold(0,"campaign_absolute_payout_threshold"),
	campaign_percentage_payout_threshold(1,"campaign_percentage_payout_threshold");

            private int code;
            private String name;
            private static Map<Integer, PayoutThreshold> map = new HashMap<Integer, PayoutThreshold>();
            static {
                for (PayoutThreshold val : PayoutThreshold.values()) {
                    map.put(val.code, val);
                }
            }

            
            private PayoutThreshold(int code,String name)
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
            
            public static PayoutThreshold getEnum(int i)
            {
                return map.get(i);
            }
}
