package com.kritter.kritterui.api.db_query_def;

public class PayoutThresholdDef {
	public static final String insert_campaign_payout_threshold = "insert into campaign_payout_threshold"
			+ "(campaign_id,absolute_threshold,percentage_threshold,last_modified) "
			+ "values(?,?,?,?)";
	
	public static final String update_campaign_payout_threshold = "update campaign_payout_threshold"
			+ " set absolute_threshold=?,percentage_threshold=?,last_modified=? "
			+ " where campaign_id=?";
	
	public static final String update_default_payout_threshold = "update payout_threshold_metadata"
			+ " set value=?, last_modified=? "
			+ " where name=?";
	public static final String select_campaign_payout_threshold = "select * from campaign_payout_threshold"
			+ " where campaign_id in (<id>) ";
	public static final String select_default_payout_threshold = "select * from payout_threshold_metadata";
	

}

