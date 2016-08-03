package com.kritter.kritterui.api.db_query_def;

public class VideoInfo {
    public static final String insert_video_info = "insert into video_info(guid,account_guid,video_size," +
    		"resource_uri,modified_by,last_modified, ext) values(?,?,?,?,?,?,?)";
    public static final String update_video_info = "update video_info set video_size=?," +
            "resource_uri=?,modified_by=?,last_modified=?,ext=? where id = ?";
    public static final String get_video_info_by_id = "select * from video_info where id=?";
    public static final String list_video_info_by_account = "select * from video_info where account_guid=?";
    public static final String get_video_info_by_ids = "select * from video_info where id in (<id>)";
    
}
