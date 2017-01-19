package com.kritter.kritterui.api.db_query_def;

public class Account {
    public static String verify_userId = "select * from account where userid=?";
    public static String verify_userId_email = "select * from account where userid=? or email=?";
    public static String create_account = "insert into account(guid,status,type_id,name,userid,password,email,address,country,city,phone,company_name," +
    		"modified_by,last_modified," +
            "payment_type,bank_transfer_beneficiary_name,bank_transfer_account_number,bank_transfer_bank_name,bank_transfer_bank_add," +
            "bank_transfer_branch_number,bank_transfer_vat_number,wire_beneficiary_name,wire_account_number,wire_bank_name,wire_transfer_bank_add," +
            "wire_swift_code,wire_iban,paypal_id,im,comment,api_key,inventory_source,billing_rules_json,demandtype,"
            + "demandpreference,qps,timeout,demand_props,billing_name,billing_email,ext,adxbased,"
            + "open_rtb_ver_required,third_party_demand_channel_type,contactdetail,brand,firstind,secondind)" +
    		" values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    
    public static String list_accounts_by_type = "select * from  account where type_id=? order by name limit ?,?";
    public static String list_accounts_by_type_by_status = "select * from  account where type_id=? and status=? order by name limit ?,?";
    public static String list_exchanges_by_status = "select * from  account where type_id=2 and status=? and inventory_source = 2 order by name limit ?,?";
    public static String list_directpublisher_by_status = "select * from  account where type_id=2 and status=? and inventory_source != 2 order by name limit ?,?";
    public static String list_all_accounts = "select * from  account where type_id != 1 order by name limit ?,?";
    public static String list_all_accounts_by_status = "select * from  account where type_id != 1 and status=? order by name limit ?,?";
    public static String list_active_advertiser_by_demandtype = "select * from  account where type_id = 3 and status=1 and demandtype=?";
    
    public static String update_accounts = "update account set status=?,name=?,email=?,address=?,country=?,city=?," +
    		"phone=?,company_name=?,modified_by=?,last_modified=?,payment_type=?,bank_transfer_beneficiary_name=?," +
    		"bank_transfer_account_number=?,bank_transfer_bank_name=?,bank_transfer_bank_add=?," +
            "bank_transfer_branch_number=?,bank_transfer_vat_number=?,wire_beneficiary_name=?,wire_account_number=?," +
            "wire_bank_name=?,wire_transfer_bank_add=?," +
            "wire_swift_code=?,wire_iban=?,paypal_id=?,im=?,comment=?,api_key=?,inventory_source=?,billing_rules_json=?," +
            "password=?,demandtype=?,demandpreference=?,qps=?,timeout=?, demand_props=?,billing_name=?,billing_email=?,ext=?," +
            "adxbased=?,open_rtb_ver_required = ?,third_party_demand_channel_type = ?,contactdetail =?,brand =?,"
            + "firstind=?,secondind=? where id=? ";
    
    public static String update_status = "update account set status=?,comment=?,last_modified=? where id=? ";
    
    public static String get_Account = "select * from  account where userid=?";
    
    public static String get_Account_By_Guid = "select * from  account where guid=?";
    public static String get_Account_By_Id = "select * from  account where id=?";
    public static String get_Account_By_Guid_Apikey = "select * from  account where guid=? and api_key=?";
    public static String get_Account_By_UserId_Pwd = "select * from  account where userid=?";
    public static String list_accounts_by_email = "select * from  account where email=?";

}
