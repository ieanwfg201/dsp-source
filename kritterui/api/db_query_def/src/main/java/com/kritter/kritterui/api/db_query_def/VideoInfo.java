package com.kritter.kritterui.api.db_query_def;

public class VideoInfo {
    public static final String insert_video_info = "insert into video_info(guid,account_guid,video_size," +
    		"resource_uri,modified_by,last_modified, ext) values(?,?,?,?,?,?,?)";
    public static final String update_video_info = "update video_info set video_size=?," +
            "resource_uri=?,modified_by=?,last_modified=?,ext=? where id = ?";
    public static final String get_video_info_by_id = "select * from video_info where id=?";
    public static final String list_video_info_by_account = "select * from video_info where account_guid=?";
    public static final String get_video_info_by_ids = "select * from video_info where id in (<id>)";
    public static final String get_video_info_by_pub = 
    		"select e.direct_supply_inc_exc as direct_supply_inc_exc,d.video_props as video_props from account as a, campaign as b,ad as c, "
    		+ "creative_container as d,targeting_profile as e  where a.guid=b.account_guid and b.id=c.campaign_id "
    		+ "and c.creative_id=d.id and c.targeting_guid=e.guid  and a.type_id=3 and e.supply_inc_exc is not null "
    		+ "and d.format_id=4 and e.is_site_list_excluded=false;";
    
}
