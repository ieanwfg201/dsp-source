package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

public enum PayoutThresholdListEnum
{
	default_payout_threshold(0,"default_payout_threshold"),
	campaign_payout_threshold(1,"campaign_payout_threshold");

            private int code;
            private String name;
            private static Map<Integer, PayoutThresholdListEnum> map = new HashMap<Integer, PayoutThresholdListEnum>();
            static {
                for (PayoutThresholdListEnum val : PayoutThresholdListEnum.values()) {
                    map.put(val.code, val);
                }
            }

            
            private PayoutThresholdListEnum(int code,String name)
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
            
            public static PayoutThresholdListEnum getEnum(int i)
            {
                return map.get(i);
            }
}
