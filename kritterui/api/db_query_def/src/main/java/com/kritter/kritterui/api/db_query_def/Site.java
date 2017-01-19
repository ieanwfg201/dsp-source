package com.kritter.kritterui.api.db_query_def;

public class Site {

    public static final String insert_site = "insert into site(guid,name,pub_id,pub_guid,site_url," +
    		"app_id,app_store_id,categories_list,category_list_inc_exc,is_category_list_excluded," +
    		"hygiene_list,opt_in_hygiene_list,creative_attr_inc_exc,is_creative_attr_exc,site_platform_id," +
    		"status_id,last_modified,modified_by,billing_rules_json,url_exclusion,allow_house_ads,comments,floor," +
    		"is_advertiser_excluded,campaign_inc_exc_schema,nofill_backup_content,passback_type,is_richmedia_allowed,"
    		+ "is_native,native_props,is_video,video_supply_props) values(" +
    		"?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

    
    public static final String update_site = "update site set name=?,site_url=?," +
            "app_id=?,app_store_id=?,categories_list=?,category_list_inc_exc=?,is_category_list_excluded=?," +
            "hygiene_list=?,opt_in_hygiene_list=?,creative_attr_inc_exc=?,is_creative_attr_exc=?," +
            "site_platform_id=?,status_id=?,last_modified=?,modified_by=?,billing_rules_json=?," +
            "url_exclusion=?,allow_house_ads=?,comments=?,floor=?,is_advertiser_excluded=?,campaign_inc_exc_schema=?," +
            "nofill_backup_content=?,passback_type=?,is_richmedia_allowed=?,is_native=?,native_props=?,"
            + "is_video=?,video_supply_props=?  where id=?";
    
    public static final String list_sites = "select * from site order by last_modified desc limit ?,?";
    
    public static final String list_sites_by_status = "select * from site where status_id= ? " +
    		"order by last_modified desc limit ?,?";
    
    public static final String list_sites_by_account_guid = "select * from site where pub_guid=? " +
            "order by name limit ?,?";
    public static final String list_sites_by_url= "select * from site where site_url like ? ";
    
    public static final String list_sites_by_account_id = "select * from site where pub_id=? " +
            "order by last_modified desc limit ?,?";
    
    public static final String list_sites_by_account_ids = "select * from site where pub_id in (<id>) " +
            "order by name limit ?,?";
    
    public static final String list_sites_by_all_account_ids = "select * from site  " +
            "order by name limit ?,?";
    
    public static final String get_Site = "select * from site where id= ?";

    public static final String get_Site_Guid = "select * from site where guid= ?";

    public static final String get_Site_Inc_id = "select * from site where id= ?";

    public static final String get_Site_by_pub_guid = "select * from site where pub_guid = ?";

    public static final String approve_site = "update site set status_id=1,last_modified=? where id=?";
    public static final String approve_multiple_site = "update site set status_id=1,last_modified=? where id in (<id>)";
    public static final String reject_site = "update site set status_id=4,comments=?,last_modified=? where id=?";
    public static final String reject_multiple_site = "update site set status_id=4,last_modified=? where id in (<id>)";
    public static final String pause_site = "update site set status_id=2,comments=?,last_modified=? where id=?";
    public static final String pause_multiple_site = "update site set status_id=2,last_modified=? where id in (<id>)";
    
    public static final String change_site_payout = "update site set billing_rules_json=?,last_modified=? where pub_id=? and billing_rules_json = ?";
    
}
