package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

public enum AdAPIEnum
{
            get_ad(0,"get_ad"),
            list_ad_by_campaign(1,"list_ad_by_campaign"),
            list_ad_by_status(2,"list_ad_by_status"),
            list_ad_by_campaigns(3,"list_ad_by_campaign"),
            approve_ad_again_on_tp_update(4,"approve_ad_again_on_tp_update"),
            activate_ad_by_ids(5,"activate_ad_by_ids"),
            pause_ad_by_ids(6,"pause_ad_by_ids"),
            list_ad_by_campaign_with_tp_name(7,"list_ad_by_campaign_with_tp_name"),
            approve_multiple_ads(8,"approve_multiple_ads"),
            reject_multiple_ads(9,"reject_multiple_ads"),
            approve_ad_again_on_creative_update(10,"approve_ad_again_on_creative_update"),
            list_ad_by_status_of_non_expired_campaign(11,"list_ad_by_status_of_non_expired_campaign");
            
            private int code;
            private String name;
            private static Map<Integer, AdAPIEnum> map = new HashMap<Integer, AdAPIEnum>();
            static {
                for (AdAPIEnum val : AdAPIEnum.values()) {
                    map.put(val.code, val);
                }
            }

            
            private AdAPIEnum(int code,String name)
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
            
            public static AdAPIEnum getEnum(int i)
            {
                return map.get(i);
            }
}
