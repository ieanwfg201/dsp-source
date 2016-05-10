package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

public enum CampaignQueryEnum
{
            get_campaign_of_account(0,"get_campaign_of_account"),
            list_campaign_of_account(1,"list_campaign_of_account"),
            list_non_expired_campaign_of_account_by_status(2,"list_campaign_of_account_by_status"),
            list_non_expired_campaign_by_status(3,"list_campaign_by_status"),
            list_all_non_expired_campaign_of_account(4,"list_all_campaign_of_account"),
            get_campaign_by_id(5,"get_campaign_of_account"),
            list_all_expired_campaign_of_account(6,"list_all_expired_campaign_of_account"),
            list_campaign_of_accounts(7,"list_campaign_of_accounts"),
            list_campaign_by_account_ids(8,"list_campaign_by_account_ids"),
            list_campaign_by_account_ids_with_account_id(9,"list_campaign_by_account_ids_with_account_id");

            private int code;
            private String name;
            private static Map<Integer, CampaignQueryEnum> map = new HashMap<Integer, CampaignQueryEnum>();
            static {
                for (CampaignQueryEnum val : CampaignQueryEnum.values()) {
                    map.put(val.code, val);
                }
            }

            
            private CampaignQueryEnum(int code,String name)
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
            
            public static CampaignQueryEnum getEnum(int i)
            {
                return map.get(i);
            }
}
