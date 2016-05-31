package com.kritter.kritterui.api.db_query_def;

public class ReqLoggingQueryDef {
    public static final String get_all_req_logging_entities  = "select * from req_logging";
    public static final String get_specific_req_logging_entities  = "select * from req_logging where pubId=?";
    public static final String update_req_logging  = "update req_logging set enable=?, time_period=?,last_modified=? where pubId=?";
    public static final String insert_req_logging  = "insert  into req_logging(pubId,enable,time_period,created_on,last_modified) values(?,?,?,?,?)";
}
