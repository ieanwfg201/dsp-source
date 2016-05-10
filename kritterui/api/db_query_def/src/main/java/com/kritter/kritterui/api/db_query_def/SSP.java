package com.kritter.kritterui.api.db_query_def;

public class SSP {

    public static final String list_ssp_global_rules = "select * from ssp_global_rules";

    public static final String insert_ssp_global_rules = "insert into ssp_global_rules(id,rule_def) values(?,?)";
    
    public static final String update_ssp_global_rules = "update ssp_global_rules set rule_def=? where id=?";
    
    public static final String list_api_advertiser = "select guid as guid, name as name from  account where type_id = 3 and demandtype=1";
}
