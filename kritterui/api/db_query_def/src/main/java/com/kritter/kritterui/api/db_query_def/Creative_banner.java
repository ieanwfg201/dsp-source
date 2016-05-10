package com.kritter.kritterui.api.db_query_def;

public class Creative_banner {
    public static final String insert_creative_banner = "insert into creative_banner(guid,account_guid,slot_id," +
    		"resource_uri,modified_by,last_modified) values(?,?,?,?,?,?)";
    public static final String update_creative_banner = "update creative_banner set slot_id=?," +
            "resource_uri=?,modified_by=?,last_modified=? where id = ?";
    public static final String get_creative_banner = "select * from creative_banner where id=?";
    public static final String list_creative_banner_by_account = "select * from creative_banner where account_guid=?";
    public static final String get_creative_banner_by_ids = "select * from creative_banner where id in (<id>)";
    
}
