package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

public enum MetadataType {
    STATUS(1,"STATUS"),
    //CATEGORY(2,"CATEGORY"),
    APP_STORE_ID(3,"APP_STORE_ID"),
    COUNTRY(4,"COUNTRY"),
    ALLISP(5,"ALLISP"),
    ISP_BY_COUNTRY(6,"ISP_BY_COUNTRY"),
    CREATIVE_SLOTS(7,"CREATIVE_SLOTS"),
    CREATIVE_FORMATS(8,"CREATIVE_FORMATS"),
    CREATIVE_ATTRIBUTES(9,"CREATIVE_ATTRIBUTES"),
    HANDSET_MANUFACTURER(10,"HANDSET_MANUFACTURER"),
    HANDSET_OS(11,"HANDSET_OS"),
    HANDSET_MODEL(12,"HANDSET_MODEL"),
    HANDSET_BROWSER(13,"HANDSET_BROWSER"),
    /*HANDSET_OS_VERSION(14,"HANDSET_OS_VERSION"),
    HANDSET_BROWSER_VERSION(15,"HANDSET_BROWSER_VERSION"),*/
    STATUS_BY_ID(16,"STATUS_BY_ID"),
    //CATEGORY_BY_ID(17,"CATEGORY_BY_ID"),
    APP_STORE_ID_BY_ID(18,"APP_STORE_ID_BY_ID"),
    COUNTRY_BY_ID(19,"COUNTRY_BY_ID"),
    ALLISP_BY_ID(20,"ALLISP_BY_ID"),
    /*ISP_BY_COUNTRY_BY_ID(21,"ISP_BY_COUNTRY"),*/
    CREATIVE_SLOTS_BY_ID(22,"CREATIVE_SLOTS_BY_ID"),
    CREATIVE_FORMATS_BY_ID(23,"CREATIVE_FORMATS_BY_ID"),
    CREATIVE_ATTRIBUTES_BY_ID(24,"CREATIVE_ATTRIBUTES_BY_ID"),
    HANDSET_MANUFACTURER_BY_ID(25,"HANDSET_MANUFACTURER_BY_ID"),
    HANDSET_OS_BY_ID(26,"HANDSET_OS_BY_ID"),
    HANDSET_MODEL_BY_ID(27,"HANDSET_MODEL_BY_ID"),
    HANDSET_BROWSER_BY_ID(28,"HANDSET_BROWSER_BY_ID"),
    CREATIVE_ATTRIBUTES_BY_FORMAT_ID(29,"CREATIVE_ATTRIBUTES_BY_FORMAT_ID"),
    /*COUNTRY_FROM_COUNTRY_TABLE(30,"COUNTRY_FROM_COUNTRY_TABLE"),
    ISP_BY_COUNTRY_CODE(31,"ISP_BY_COUNTRY_CODE"),*/
    HANDSET_MANUFACTURER_BY_OS(32,"HANDSET_MANUFACTURER_BY_OS"),
    ISP_MAPPINGS_DATA_FETCH_FOR_INSERTION(33,"ISP_MAPPINGS_DATA_FETCH_FOR_INSERTION"),
    ISP_MAPPINGS_ALREADY_PRESENT_DATA_FETCH(34,"ISP_MAPPINGS_ALREADY_PRESENT_DATA_FETCH"),
    INSERT_INTO_ISP_MAPPINGS(35,"INSERT_INTO_ISP_MAPPINGS"),
    UPDATE_ISP_MAPPINGS_ROW(36,"UPDATE_ISP_MAPPINGS_ROW"),
    CATEGORY_TIER_1(37,"CATEGORY_TIER_1"),
    CATEGORY_TIER_2(38,"CATEGORY_TIER_2"),
    CATEGORY_BY_ID(39,"CATEGORY_BY_ID"),
    SITE_BY_ID(40,"SITE_BY_ID"),
    ACTIVE_ADVERTISER_LIST(41,"ACTIVE_ADVERTISER_LIST"),
    ACCOUNT_AS_META_BY_ID(42,"ACCOUNT_AS_META_BY_ID"),
    PUB_BY_ID(43,"PUB_BY_ID"),
    CAMPAIGN_AS_META_BY_ID(44,"CAMPAIGN_AS_META_BY_ID"),
    EXT_SITE_BY_ID(45,"EXT_SITE_BY_ID"),
    PUB_BY_GUID(46,"PUB_BY_GUID");

    private static Map<Integer, MetadataType> map = new HashMap<Integer, MetadataType>();
    static {
        for (MetadataType val : MetadataType.values()) {
            map.put(val.code, val);
        }
    }

    private int code;
    private String name;

    private MetadataType(int code,String name)
    {
        this.code = code;
    }

    public String getName()
    {
        return this.name;
    }

    public int getCode()
    {
        return this.code;
    }
    public static MetadataType getEnum(int i)
    {
        return map.get(i);
    }
}
