package com.kritter.constants.tracking_partner;

import java.util.HashMap;
import java.util.Map;

public enum TrackingPartner
{

            NONE(0,"NONE",null);

            private int code;
            private String name;
            private String applicationInstanceId;

            private static Map<Integer, TrackingPartner> map = new HashMap<Integer, TrackingPartner>();
            static {
                for (TrackingPartner val : TrackingPartner.values()) {
                    map.put(val.code, val);
                }
            }
            private TrackingPartner(int code,String name,String applicationInstanceId)
            {
                this.code = code;
                this.name = name;
                this.applicationInstanceId = applicationInstanceId;
            }
            public String getName()
            {
                return this.name;
            }

            public int getCode()
            {
                return this.code;
            }

            public String getApplicationInstanceId()
            {
                return this.applicationInstanceId;
            }
            
            public static TrackingPartner getEnum(int i)
            {
                return map.get(i);
            }
}
