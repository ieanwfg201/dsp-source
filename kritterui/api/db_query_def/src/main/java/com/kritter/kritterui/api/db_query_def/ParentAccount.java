package com.kritter.kritterui.api.db_query_def;

/**
 * This class keeps any database related mysql queries for parent_account table.
 */
public class ParentAccount
{
    public static final String QUERY_INSERT_PARENT_ACCOUNT =
            "insert into parent_account(guid,status,type_id,name,created_on,last_modified) " +
            "values (?,?,?,?,?,?)";

    public static final String QUERY_UPDATE_PARENT_ACCOUNT =
            "update parent_account set status = ?,type_id = ?,name = ?,last_modified = ? where guid = ?";

    public static final String QUERY_GET_PARENT_ACCOUNT_BY_GUID =
            "select * from parent_account where guid = ?";
}

