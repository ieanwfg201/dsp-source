package com.kritter.kritterui.api.db_query_def;

public class RetargetingSegment {
    public static final String insert_retargeting_segment = "insert into retargeting_segment(name, tag, is_deprecated, account_guid,"+ 
            "last_modified, modified_by) values(?,?,?,?,?,?)";
    public static final String update_retargeting_segment = "update retargeting_segment set name=?, tag=?, is_deprecated=?, account_guid=?,"+ 
            "last_modified=?, modified_by=? where id=?";
    public static final String get_retargeting_segments_by_ids  = "select * from retargeting_segment where id in (<id>)";
    public static final String get_retargeting_segments_by_accounts  = "select * from retargeting_segment where account_guid in (<id>) order by created_on desc";
}
