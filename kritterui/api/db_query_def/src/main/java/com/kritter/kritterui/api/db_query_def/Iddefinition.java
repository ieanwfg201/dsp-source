package com.kritter.kritterui.api.db_query_def;

public class Iddefinition {
    public static String GET_PUB_BY_ID = "select id as id, guid as guid, name as name from account where id in (<id>) and type_id=2";
    public static String GET_PUB_BY_GUID = "select id as id, guid as guid, name as name from account where guid in (<id>)  and type_id=2";
    public static String GET_SITE_BY_ID = "select id as id, guid as guid, name as name from site where id in (<id>)";
    public static String GET_SITE_BY_GUID = "select id as id, guid as guid, name as name from site where guid in (<id>)";
    public static String GET_EXT_SITE_BY_ID = "select id as id, '' as guid, ext_supply_name as name from ext_supply_attr where id in (<id>)";
    public static String GET_ADV_BY_ID = "select id as id, guid as guid, name as name from account where id in (<id>)  and type_id=3";
    public static String GET_ADV_BY_GUID = "select id as id, guid as guid, name as name from account where guid in (<id>)  and type_id=3";
    public static String GET_CAMPAIGN_BY_ID = "select id as id, guid as guid, name as name from campaign where id in (<id>)";
    public static String GET_CAMPAIGN_BY_GUID = "select id as id, guid as guid, name as name from campaign where guid in (<id>)";
    public static String GET_AD_BY_ID = "select id as id, guid as guid, name as name from ad where id in (<id>)";
    public static String GET_AD_BY_GUID = "select id as id, guid as guid, name as name from ad where guid in (<id>)";
    public static String GET_CREATIVE_BY_ID = "select id as id, guid as guid, label as name from  creative_container where id in (<id>)";
    public static String GET_CREATIVE_BY_GUID = "select id as id, guid as guid, label as name from creative_container where guid in (<id>)";
    public static String GET_TP_BY_GUID = "select -1 as id, guid as guid, name as name from targeting_profile where guid in (<id>)";
    public static String GET_OS_BY_ID = "select os_id as id, '' as guid, os_name as name from handset_os where os_id in (<id>)";
    public static String GET_BRAND_BY_ID = "select  manufacturer_id as id, '' as guid, manufacturer_name as name from handset_manufacturer where manufacturer_id in (<id>)";
    public static String GET_MODEL_BY_ID = "select model_id as id, '' as guid, model_name as name from handset_model where model_id in (<id>)";
    public static String GET_BROWSER_BY_ID = "select browser_id as id, '' as guid, browser_name as name from handset_browser where browser_id in (<id>)";
}
