package com.kritter.kritterui.api.db_query_def;

public class Campaign_Budget {
    
    public static final String insert_campaign_budget = "insert into campaign_budget(campaign_id,campaign_guid,internal_total_budget,adv_total_budget," +
    		"internal_total_burn,adv_total_burn,internal_daily_budget,adv_daily_budget," +
    		"internal_daily_burn,adv_daily_burn,modified_by,last_modified) " +
    		"values(?,?,?,?,?,?,?,?,?,?,?,?)";
    
    public static final String update_campaign_budget = "update campaign_budget set internal_total_budget=?,adv_total_budget=?," +
            "internal_total_burn=?,adv_total_burn=?,internal_daily_budget=?,adv_daily_budget=?," +
            "internal_daily_burn=?,adv_daily_burn=?,modified_by=?,last_modified=? " +
            "where campaign_id=?";
    
    public static final String get_campaign_budget = "select * from campaign_budget where campaign_id=?";
    public static final String get_campaign_budget_by_guid = "select * from campaign_budget where campaign_guid=?";
    
}
