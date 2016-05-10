package com.kritter.kritterui.api.db_query_def;

public class Creative_container {

    public static final String insert_creative_container = "insert into creative_container(guid,account_guid,label,format_id,creative_attr," +
    		"text,resource_uri_ids,html_content,modified_by,last_modified,status_id,ext_resource_url,"
    		+ "native_demand_props,creative_macro,video_props) "
    		+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    public static final String update_creative_container = "update creative_container set label=?,format_id=?,creative_attr=?," +
            "text=?,resource_uri_ids=?,html_content=?,modified_by=?,last_modified=?,status_id=?,ext_resource_url=?,native_demand_props=?,"
            + "creative_macro=?,video_props=? where id = ?";
    public static final String get_creative_container = "select * from creative_container where id=?";
    public static final String list_creative_container_by_account = "select * from creative_container where account_guid=? order by last_modified desc limit ?,?";
    public static final String list_creative_container_by_status = "select * from creative_container where status_id=? order by last_modified desc limit ?,?";
    public static final String update_status = "update creative_container set status_id=? where id = ?";
    public static final String update_multiple_status = "update creative_container set status_id=? where id in (<id>)";
    public static final String get_creative_container_by_guid = "select * from creative_container where guid = ?";
    
    
}
