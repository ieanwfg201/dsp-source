package com.kritter.kritterui.api.db_query_def;

/**
 * Created by zhangyan on 2/3/17.
 */
public class AudienceSql {
    public static final String get_audience_by_id_and_deleted = "SELECT * FROM audience WHERE id = ? AND deleted = 0";
    public static final String insert_audience_tags = "insert into audience (name,source_id,tags,type,account_guid) values(?,?,?,?,?)";
    public static final String update_audience = "update audience set name=?,source_id=?,tags=?,type=? where id=? and account_guid=?";

    public static final String get_audience_of_account = "select * from audience where id=? and account_guid = ? and deleted = 0";

    public static final String get_audience_by_id = "select * from audience where id=? and deleted = 0";

    public static final String list_audience_of_account = "select * from audience where account_guid = ? order by last_modified desc limit ?,? and deleted = 0";

    public static final String list_audience_of_accounts = "select * from audience where account_guid in (<id>) order by last_modified desc limit ?,? and deleted = 0";

    public static final String list_audience_by_account_ids =  "select  c.id as id, c.name as name, c.tags as tags, c.type as type, c.file_path as file_path" +
            "c.account_guid as account_guid, c.source_id as source_id, c.created_on as created_on, c.last_modified as ,last_modified, c.last_modified as last_modified," +
            "from audience as c,account as a where deleted = 0 and c.account_guid=a.guid and a.id in (<id>)  order by c.last_modified desc limit  ?,?";
    public static final String list_audience_by_account_ids_with_account_id =  "select  c.id as id, c.name as name, c.tags as tags, c.type as type, c.file_path as file_path" +
            "c.account_guid as account_guid,a.id as account_id ,c.source_id as source_id, c.created_on as created_on, c.last_modified as ,last_modified, c.last_modified as last_modified," +
            "from audience as c,account as a where deleted = 0 and c.account_guid=a.guid and a.id in (<id>)  order by c.last_modified desc limit  ?,?";

    public static final String list_audience_of_all_accounts = "select * from audience order by last_modified desc limit ?,? where deleted = 0 ";

    public static final String get_audience_by_guid = "select * from audience where guid=?";

    public static final String insert_audience_impression_budget =
            "insert into audience_impressions_budget (audience_guid,impression_cap,time_window_hours,modified_by,created_on,last_modified) " +
                    "values (?,?,?,?,?,?)";
    public static final String update_audience_impression_budget =
            "update audience_impressions_budget set impression_cap = ? ,time_window_hours = ? where audience_guid = ?";
    public static final String get_audience_impression_budget =
            "select count(*) as count from audience_impressions_budget where audience_guid = ?";
    public static final String payout_threshold_metadata =
            "select * from payout_threshold_metadata";


    public static java.lang.String get_audience_list_of_account="select * from audience where account_guid = ? and deleted = 0";
}
