package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

public enum Payment_type
{
            NothingSelected(0,"NothingSelected"),
            Paypal(1,"Paypal"),
            BankTransfer(2,"BankTransfer"),
            WireTransfer(3,"WireTransfer");

            private int code;
            private String name;
            private static Map<Integer, Payment_type> map = new HashMap<Integer, Payment_type>();
            static {
                for (Payment_type val : Payment_type.values()) {
                    map.put(val.code, val);
                }
            }

            
            private Payment_type(int code,String name)
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
            
            public static Payment_type getEnum(int i)
            {
                return map.get(i);
            }
}
