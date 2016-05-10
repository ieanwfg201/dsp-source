package com.kritter.kritterui.api.db_query_def;

public class Account_Budget {
    public static final String insert_account_budget = "insert into account_budget(account_guid,internal_balance,internal_burn,adv_balance,adv_burn," +
    		"modified_by,last_modified) values(?,?,?,?,?,?,?)";
    public static final String update_account_budget = "update account_budget set internal_balance=?,internal_burn=?,adv_balance=?" +
    		",adv_burn=?,modified_by=?,last_modified=? where account_guid=? ";
    public static final String get_account_budget = "select * from account_budget where account_guid=?";
}
