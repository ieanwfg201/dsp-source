package com.kritter.kritterui.api.db_query_def;

public class NativeScreenshot {
    public static final String insert_native_screenshot = "insert into native_screenshot(guid,account_guid,ss_size," +
    		"resource_uri,modified_by,last_modified) values(?,?,?,?,?,?)";
    public static final String update_native_screenshot = "update native_screenshot set ss_size=?," +
            "resource_uri=?,modified_by=?,last_modified=? where id = ?";
    public static final String get_native_screenshot_by_id = "select * from native_screenshot where id=?";
    public static final String list_native_screenshot_by_account = "select * from native_screenshot where account_guid=?";
    public static final String get_native_screenshot_by_ids = "select * from native_screenshot where id in (<id>)";
    
}
