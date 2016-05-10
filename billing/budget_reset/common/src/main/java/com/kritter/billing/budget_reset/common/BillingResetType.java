package com.kritter.billing.budget_reset.common;

import java.util.HashMap;
import java.util.Map;

public enum BillingResetType
{

            UPDATEALL(1,"UPDATEALL");

            private int code;
            private String name;
            private static Map<Integer, BillingResetType> map = new HashMap<Integer, BillingResetType>();
            static {
                for (BillingResetType val : BillingResetType.values()) {
                    map.put(val.code, val);
                }
            }

            
            private BillingResetType(int code,String name)
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
            
            public static BillingResetType getEnum(int i)
            {
                return map.get(i);
            }
}

