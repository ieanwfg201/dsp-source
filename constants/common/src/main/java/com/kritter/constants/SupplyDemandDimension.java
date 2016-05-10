package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

/**
 * This class keeps constants for supply and demand dimension names.
 */
public enum  SupplyDemandDimension
{
    DIM_INVENTORY_SRC_ID("invsrc","inventory source"),
    DIM_SITE_ID("siteid","request site id"),
    DIM_DEVICE_ID("deviceid","internal integer id of requesting device"),
    DIM_MANUFACTURER_ID("manufacturerid","internal manufacturer id of the requesting device"),
    DIM_MODEL_ID("modelid","internal model id of the requesting device"),
    DIM_OS_ID("osid","internal operating system id of the requesting device"),
    DIM_COUNTRY_ID("country","internal ui country id of the requesting user"),
    DIM_CARRIER_ID("carrier","internal ui carrier id of the requesting user"),
    DIM_SELECTED_SITE_CATEGORY_ID("scatid","selected site category id"),
    DIM_TIME_WINDOW_ID("time","time window id"),
    DIM_IS_WEEKEND("isweekend","if the day is weekend or not"),
    DIM_AD_ID("adid","ad id shortlisted for serving"),
    DIM_CAMPAIGN_ID("campaignid","campaign id shortlisted for serving"),
    DIM_CREATIVE_ID("creativeid","creative id shortlisted for serving"),
    DIM_CREATIVE_TYPE("creativetype","creative type shortlisted for serving");

    private String code;
    private String name;

    private static Map<String, SupplyDemandDimension> map = new HashMap<String, SupplyDemandDimension>();

    static
    {
        for (SupplyDemandDimension val : SupplyDemandDimension.values())
        {
            map.put(val.code, val);
        }
    }

    private SupplyDemandDimension(String code,String name)
    {
        this.code = code;
        this.name = name;
    }

    public String getName()
    {
        return this.name;
    }

    public String getCode()
    {
        return this.code;
    }

    public static SupplyDemandDimension getEnum(short i)
    {
        return map.get(i);
    }
}
