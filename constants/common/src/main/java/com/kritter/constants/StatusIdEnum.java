package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

public enum StatusIdEnum
{

            Active(1,"Active"),
            Paused(2,"Paused"),
            Expired(3,"Expired"),
            Rejected(4,"Rejected"),
            Pending(5,"Pending"),
            Experimental(6,"Experimental"),
            Sandbox(7,"Sandbox"),
            DEBUG(8,"DEBUG"),
            Approved(9,"Approved"),
            CAMP_DAILY_BUDGET_OVER(10,"CAMP_DAILY_BUDGET_OVER"),
            CAMP_TOTAL_BUDGET_OVER(11,"CAMP_TOTAL_BUDGET_OVER"),
            ACCOUNT_BALANCE_OVER(12,"ACCOUNT_BALANCE_OVER"),
            PAYOUT_THRESHOLD(13,"PAYOUT_THRESHOLD");

            private int code;
            private String name;
            private static Map<Integer, StatusIdEnum> map = new HashMap<Integer, StatusIdEnum>();
            static {
                for (StatusIdEnum val : StatusIdEnum.values()) {
                    map.put(val.code, val);
                }
            }

            
            private StatusIdEnum(int code,String name)
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
            
            public static StatusIdEnum getEnum(int i)
            {
                return map.get(i);
            }
}
