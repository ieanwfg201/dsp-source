package com.kritter.kritterui.api.db_query_def;

public class SavedQueryDef {
    public static final String list_saved_query_entity_by_account_guids = "select * from saved_query where account_guid in (<id>) and status_id=1";
    public static final String list_saved_query_entity_by_entity_id = "select * from saved_query where id in (<id>)";
    
    public static final String insert_saved_query_entity = "insert into saved_query(name,reporting_entity,status_id,account_guid,modified_on,reporting_type)" +
    		" values(?,?,?,?,?,?)";
    public static final String update_saved_query_entity = "update saved_query set name=?,reporting_entity=?,status_id=?,account_guid=?" +
    		",modified_on=?,reporting_type=? where id=?";
    public static final String change_status_saved_query_entity = "update saved_query set status_id=? where id in (<id>)";
}
