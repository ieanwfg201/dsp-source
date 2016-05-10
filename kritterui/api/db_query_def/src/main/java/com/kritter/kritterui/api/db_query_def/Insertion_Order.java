package com.kritter.kritterui.api.db_query_def;

public class Insertion_Order {
    public static final String insert_io = "insert into insertion_order(order_number,account_guid,name,total_value," +
    		"modified_by,last_modified,status,comment,created_by,belongs_to) values(?,?,?,?,?,?,?,?,?,?)";
            
    public static final String update_io = "update insertion_order set name=?,total_value=?," +
            "modified_by=?,last_modified=?,status=?,comment=? where account_guid = ? and order_number=?";
    
    public static final String list_io = "select * from insertion_order order by last_modified desc limit ?,?";
    
    public static final String list_io_by_status = "select a.order_number as order_number, a.account_guid as account_guid, a.name as name," +
    		"a.total_value as total_value, a.modified_by as modified_by, a.created_on as created_on, a.last_modified as last_modified, a.status as status," +
    		"a.comment as comment, a.created_by as created_by, a.belongs_to as belongs_to, b.userid as created_by_name,c.userid as belongs_to_name " +
    		"from insertion_order as a, account as b, account as c where " +
    		"a.created_by=b.id and a.belongs_to=c.id and a.status = ? order by a.last_modified desc limit ?,?";
    
    public static final String list_io_by_account_guid = "select * from insertion_order where account_guid=? order by last_modified desc limit ?,?";
    public static final String list_io_by_account_guid_by_status = "select a.order_number as order_number, a.account_guid as account_guid, a.name as name," +
            "a.total_value as total_value, a.modified_by as modified_by, a.created_on as created_on, a.last_modified as last_modified, a.status as status," +
            "a.comment as comment, a.created_by as created_by, a.belongs_to as belongs_to, b.userid as created_by_name,c.userid as belongs_to_name" +
            " from insertion_order as a, account as b, account as c where " +
            "a.created_by=b.id and a.belongs_to=c.id and  a.account_guid=? and a.status=? order by last_modified desc limit ?,?";
    
    public static final String check_io = "select * from insertion_order where order_number=? and account_guid=?";
    
    public static final String get_io = "select * from insertion_order where order_number=? and account_guid=?";
    
    public static final String reject_io = "update insertion_order set " +
            "modified_by=?,last_modified=?,status=?,comment=? where account_guid = ? and order_number=?";
    
    
    
}
