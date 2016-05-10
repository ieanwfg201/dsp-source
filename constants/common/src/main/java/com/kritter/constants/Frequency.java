package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

public enum Frequency
{
            /*Directly correlates with TABLE enum of com.kritter.kumbaya.libraries.data_structs.common.KumbayaReportingConfiguration
             * implemented in getTABLE(Frequency frequency) of com.kritter.kumbaya.libraries.query_planner.helper.HelperKumbayaQueryPlanner*/
            YESTERDAY(0,"YESTERDAY"),
            TODAY(1,"TODAY"),
            LAST7DAYS(2,"LAST7DAYS"),
            CURRENTMONTH(3,"CURRENTMONTH"),
            DATERANGE(4,"DATERANGE"),
            ADMIN_INTERNAL_HOURLY(5,"ADMIN_INTERNAL_HOURLY"),
            LASTMONTH(6,"LASTMONTH"),
            MONTHLY(7,"MONTHLY"),;

            private int code;
            private String name;
            private static Map<Integer, Frequency> map = new HashMap<Integer, Frequency>();
            static {
                for (Frequency val : Frequency.values()) {
                    map.put(val.code, val);
                }
            }

            private Frequency(int code,String name)
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
            public static Frequency getEnum(int i)
            {
                return map.get(i);
            }
}
