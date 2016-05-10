package com.kritter.kritterui.api.db_query_def;

public class NativeIcon {
    public static final String insert_native_icon = "insert into native_icon(guid,account_guid,icon_size," +
    		"resource_uri,modified_by,last_modified) values(?,?,?,?,?,?)";
    public static final String update_native_icon = "update native_icon set icon_size=?," +
            "resource_uri=?,modified_by=?,last_modified=? where id = ?";
    public static final String get_native_icon_by_id = "select * from native_icon where id=?";
    public static final String list_native_icon_by_account = "select * from native_icon where account_guid=?";
    public static final String get_native_icon_by_ids = "select * from native_icon where id in (<id>)";
    
}
