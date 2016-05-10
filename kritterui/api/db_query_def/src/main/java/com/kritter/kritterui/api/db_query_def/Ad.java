package com.kritter.kritterui.api.db_query_def;

public class Ad {
    public static final String insert_ad = "insert into ad(guid,name,creative_id,creative_guid,landing_url,targeting_guid,campaign_id,campaign_guid," +
    		"categories_list,hygiene_list,status_id,marketplace_id,internal_max_bid,advertiser_bid," +
    		"allocation_ids,last_modified,modified_by,tracking_partner,cpa_goal,adv_domain,is_frequency_capped,"
    		+ "frequency_cap,time_window,bidtype) "
    		+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    public static final String update_ad = "update ad set name=?,creative_id=?,creative_guid=?,landing_url=?,targeting_guid=?," +
            "categories_list=?,hygiene_list=?,status_id=?,marketplace_id=?,internal_max_bid=?,advertiser_bid=?,allocation_ids=?," +
            "last_modified=?,modified_by=?,comment=?,tracking_partner=?,cpa_goal=?,adv_domain=?,is_frequency_capped=?,frequency_cap=?,"
            + "time_window=?,bidtype=? where id=?";
    public static final String get_ad = "select * from ad where id=?";
    public static final String list_ad_by_campaign = "select * from ad where campaign_id=?";
    public static final String list_ad_by_campaign_with_tp_name = "select a.id as id, a.guid as guid, a.name as name, a.creative_id as creative_id, " +
    		"a.creative_guid as creative_guid, a.landing_url as landing_url, a.targeting_guid as targeting_guid, a.campaign_id as campaign_id, " +
    		"a.campaign_guid as campaign_guid, a.categories_list as categories_list , a.hygiene_list as hygiene_list, a.status_id as status_id, " +
    		"a.marketplace_id as marketplace_id, a.internal_max_bid as internal_max_bid, a.advertiser_bid as advertiser_bid, " +
    		"a.allocation_ids as allocation_ids, a.created_on as created_on, a.last_modified as last_modified, a.modified_by as modified_by, " +
    		"a.comment as comment, a.tracking_partner as tracking_partner, a.cpa_goal as cpa_goal,a.adv_domain as adv_domain, "
    		+ "b.name as targeting_profile_name,a.is_frequency_capped as is_frequency_capped,a.frequency_cap as frequency_cap,"
    		+ "a.time_window as time_window,a.bidtype as bidtype from ad as a LEFT JOIN targeting_profile as b  " +
    		"ON a.targeting_guid=b.guid where a.campaign_id=?";
    public static final String list_ad_by_campaigns = "select * from ad where campaign_id in (<id>) limit ?,?";
    public static final String list_ad_by_all_campaigns = "select * from ad limit ?,?";
    public static final String list_ad_by_status = "select * from ad where status_id=? order by last_modified desc limit ?,?";
    public static final String activate_ad = "update ad set status_id=1, last_modified=? where id=?";
    public static final String reject_ad = "update ad set status_id=4,comment=?, last_modified=? where id=?";
    public static final String pause_ad = "update ad set status_id=2,comment=?, last_modified=? where id=?";
    public static final String approve_ad = "update ad set status_id=9, last_modified=? where id=?";
    public static final String approve_ad_again_on_tp_update = "update ad set status_id=5, last_modified=? where targeting_guid=? and status_id in (1,2,9)";
    public static final String approve_ad_again_on_creative_update = "update ad set status_id=5, last_modified=? where creative_id=? and status_id in (1,2,9)";
    public static final String activate_ad_by_ids = "update ad set status_id=1, last_modified=? where (status_id=2 or status_id=1 or status_id=9) and id in (<id>)";
    public static final String pause_ad_by_ids = "update ad set status_id=2, last_modified=? where (status_id=2 or status_id=1 or status_id=9) and id in (<id>)";
    public static final String reject_multiple_ads = "update ad set status_id=4, last_modified=? where id in (<id>)";
    public static final String approve_multiple_ads = "update ad set status_id=9, last_modified=? where id in (<id>)";
    
    public static final String list_ad_by_status_of_non_expired_campaign = "select a.id as id, a.guid as guid, a.name as name, " +
    		"a.creative_id as creative_id, a.creative_guid as creative_guid, a.landing_url as landing_url, a.targeting_guid as targeting_guid," +
    		"a.campaign_id as campaign_id, a.campaign_guid as campaign_guid, a.categories_list as categories_list, a.hygiene_list as hygiene_list," +
    		"a.status_id as status_id, a.marketplace_id as marketplace_id, a.internal_max_bid as internal_max_bid, a.advertiser_bid as advertiser_bid," +
    		"a.allocation_ids as allocation_ids, a.created_on as created_on, a.last_modified as last_modified, a.modified_by  as modified_by," +
    		"a.comment as comment, a.tracking_partner as tracking_partner,a.cpa_goal as cpa_goal, a.adv_domain as adv_domain,"
    		+ "a.is_frequency_capped as is_frequency_capped,a.frequency_cap as frequency_cap,a.time_window as time_window,a.bidtype as bidtype from ad as a, "
    		+ "campaign as b where a.status_id=? and a.campaign_id=b.id and b.end_date>now() order by a.last_modified desc limit ?,?";

    public static final String get_ad_by_guid = "select * from ad where guid = ?";
    public static final String insert_ad_impression_budget =
            "insert into ad_budget (ad_guid,impression_cap,time_window_hours,modified_by,created_on,last_modified) " +
            "values (?,?,?,?,?,?)";
    public static final String update_ad_impression_budget =
            "update ad_budget set impression_cap = ? ,time_window_hours = ? where ad_guid = ?";
    public static final String get_ad_impression_budget =
        "select count(*) as count from ad_budget where ad_guid = ?";
}
