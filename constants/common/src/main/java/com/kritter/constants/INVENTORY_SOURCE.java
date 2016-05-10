package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

public enum INVENTORY_SOURCE
{
            DIRECT_PUBLISHER((short)1,"direct-publisher"),
            RTB_EXCHANGE((short)2,"rtb-exchange"),
            SSP((short)3,"ssp"),
            DCP((short)4,"dcp"),
            AGGREGATOR((short)5,"aggregator");

            private short code;
            private String inventorySource;
            private static Map<Short, INVENTORY_SOURCE> map = new HashMap<Short, INVENTORY_SOURCE>();
            static {
                for (INVENTORY_SOURCE val : INVENTORY_SOURCE.values()) {
                    map.put(val.code, val);
                }
            }


            private INVENTORY_SOURCE(short code,String inventorySource)
            {
                this.code = code;
                this.inventorySource = inventorySource;
            }

            public String getInventorySource()
            {
                return this.inventorySource;
            }

            public short getCode()
            {
                return this.code;
            }
            public static INVENTORY_SOURCE getEnum(short i)
            {
                return map.get(i);
            }
}
