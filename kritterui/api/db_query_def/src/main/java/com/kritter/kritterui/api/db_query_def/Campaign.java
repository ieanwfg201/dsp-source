package com.kritter.kritterui.api.db_query_def;

public class Campaign {
    public static final String insert_campaign = "insert into campaign(id,guid,name,account_guid,status_id," +
    		"start_date,end_date,created_on,modified_by,last_modified,is_frequency_capped,freqcap_json) values(null,?,?,?,?,?,?,?,?,?,?,?)";
    
    public static final String update_campaign = "update campaign set name=?,status_id=?," +
            "start_date=?,end_date=?,modified_by=?,last_modified=?,is_frequency_capped=?,freqcap_json=?  where id=? and account_guid=?";
    
    public static final String get_campaign_of_account = "select * from campaign where id=? and account_guid = ?";
    
    public static final String get_campaign_by_id = "select * from campaign where id=?";
    
    public static final String list_campaign_of_account = "select * from campaign where account_guid = ? order by last_modified desc limit ?,?";
    
    public static final String list_campaign_of_accounts = "select * from campaign where account_guid in (<id>) order by last_modified desc limit ?,?";

    public static final String list_campaign_by_account_ids =  "select  c.id as id, c.guid as guid, c.name as name, " +
    		"c.account_guid as account_guid, c.status_id as status_id, c.start_date as start_date, c.end_date as " +
    		"end_date,c.created_on as created_on,c.modified_by as modified_by, c.last_modified as last_modified,"
    		+ "c.is_frequency_capped as is_frequency_capped,c.freqcap_json as freqcap_json " +
    		"from campaign as c,account as a where c.account_guid=a.guid and a.id in (<id>)  order by c.last_modified desc limit  ?,?";
    
    public static final String list_campaign_by_account_ids_with_account_id =  "select  c.id as id, c.guid as guid, c.name as name, " +
            "c.account_guid as account_guid, a.id as account_id, c.status_id as status_id, c.start_date as start_date, c.end_date as " +
            "end_date,c.created_on as created_on,c.modified_by as modified_by, c.last_modified as last_modified,"
            + "c.is_frequency_capped as is_frequency_capped,c.freqcap_json as freqcap_json " +
            "from campaign as c,account as a where c.account_guid=a.guid and a.id in (<id>)  order by c.last_modified desc limit  ?,?";

    public static final String list_campaign_of_all_accounts = "select * from campaign order by last_modified desc limit ?,?";
    
    public static final String list_non_expired_campaign_of_account_by_status = "select * from campaign where account_guid = ? and status_id=? and status_id != 6 order by last_modified desc limit ?,?";
    
    public static final String list_non_expired_campaign_by_status = "select * from campaign where status_id=? and status_id != 6 order by last_modified desc limit ?,?";
    
    public static final String list_all_non_expired_campaign_of_account = 
            "select a.id as id, a.guid as guid, a.name as name, a.account_guid as account_guid, " +
    		"a.status_id as status_id, a.start_date as start_date, a.end_date as end_date,a.created_on as created_on, a.modified_by as modified_by," +
    		" a.last_modified as last_modified,a.is_frequency_capped as is_frequency_capped,a.freqcap_json as freqcap_json,b.internal_total_budget as internal_total_budget," +
    		"b.adv_total_budget as adv_total_budget,b.internal_total_burn as internal_total_burn," +
    		"b.adv_total_burn as adv_total_burn," +
    		"b.internal_daily_budget as internal_daily_budget,b.adv_daily_budget as adv_daily_budget," +
    		"b.internal_daily_burn as internal_daily_burn, b.adv_daily_burn as adv_daily_burn,c.internal_balance as internal_balance," +
    		"c.adv_balance as adv_balance" +
    		" from campaign as a LEFT JOIN campaign_budget as b ON a.id=b.campaign_id LEFT JOIN account_budget as c  ON a.account_guid=c.account_guid " +
    		"where a.account_guid = ? and  status_id != 6 order by last_modified desc limit ?,?";
    
    public static final String list_all_expired_campaign_of_account = " select * from campaign where account_guid=? and  (status_id=6 or NOW() >= end_date) order by last_modified desc limit ?,?";

    public static final String update_campaign_status = "update campaign set status_id=?,modified_by=?,last_modified=? where id=? and account_guid=?";

    public static final String get_campaign_by_guid = "select * from campaign where guid=?";

    public static final String insert_campaign_impression_budget =
            "insert into campaign_impressions_budget (campaign_guid,impression_cap,time_window_hours,modified_by,created_on,last_modified) " +
                    "values (?,?,?,?,?,?)";
    public static final String update_campaign_impression_budget =
            "update campaign_impressions_budget set impression_cap = ? ,time_window_hours = ? where campaign_guid = ?";
    public static final String get_campaign_impression_budget =
            "select count(*) as count from campaign_impressions_budget where campaign_guid = ?";
}
