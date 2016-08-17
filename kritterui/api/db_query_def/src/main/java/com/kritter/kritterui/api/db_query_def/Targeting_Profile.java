package com.kritter.kritterui.api.db_query_def;



public class Targeting_Profile {

    public static final String insert_targeting_profile = "insert into targeting_profile(guid,name,account_guid,status_id,brand_list," +
    		"model_list,os_json,browser_json,country_json,carrier_json,state_json,city_json,zipcode_file_id_set,direct_supply_inc_exc,is_site_list_excluded," +
    		"category_list,is_category_list_excluded,custom_ip_file_id_set,modified_by," +
    		"last_modified,supply_source_type,supply_source,geo_targeting_type," +
    		"hours_list,midp,lat_long,exchange_supply_inc_exc,connection_type_targeting_json,tablet_targeting,supply_inc_exc, retargeting,"
    		+ "pmp_deal_json,device_type,ext,lat_lon_radius_file)" +
    		" values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    
    public static final String update_targeting_profile = "update targeting_profile set name=?,status_id=?,brand_list=?," +
            "model_list=?,os_json=?,browser_json=?,country_json=?,carrier_json=?,state_json=?,city_json=?,zipcode_file_id_set=?,direct_supply_inc_exc=?," +
            "is_site_list_excluded=?,category_list=?,is_category_list_excluded=?,custom_ip_file_id_set=?,modified_by=?,last_modified=?," +
            "supply_source_type=?,supply_source=?,geo_targeting_type=?,hours_list=?,midp=?,lat_long=?,exchange_supply_inc_exc=?," +
            "connection_type_targeting_json=?,tablet_targeting=?,supply_inc_exc=?,retargeting=?,pmp_deal_json=?,device_type=?,ext=?,"
            + "lat_lon_radius_file=? where guid=?";
    
    public static final String list_active_targeting_profile_by_account = "select * from targeting_profile where account_guid=? and status_id=1";
    
    public static final String get_targeting_profile = "select * from targeting_profile where guid = ?";
    
    public static final String deactive_targeting_profile = "update targeting_profile set status_id=3,last_modified=? where guid=?";
    
    public static final String convert_country_id_to_entity = "select id as id, entity_id_set as entity_id_set from ui_targeting_country where id in (<id>)";
    public static final String convert_isp_ids_to_entity = "select id as id,entity_id_set as entity_id_set from ui_targeting_isp where id in (<id>)";

    public static final String insert_targeting_profile_limited_parameters = "insert into targeting_profile(guid,name,account_guid,status_id,brand_list," +
            "model_list,os_json,browser_json,country_json,carrier_json,state_json,city_json,zipcode_file_id_set,direct_supply_inc_exc,is_site_list_excluded," +
            "category_list,is_category_list_excluded,custom_ip_file_id_set,modified_by," +
            "last_modified,supply_source_type,supply_source,geo_targeting_type," +
            "hours_list,midp,lat_long,exchange_supply_inc_exc,connection_type_targeting_json,tablet_targeting,supply_inc_exc, retargeting,pmp_deal_json)" +
            " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

}
