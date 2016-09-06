package com.kritter.kritterui.api.db_query_def;

/**
 * This class has database query related to pmp deals.
 */
public class PrivateMarketPlaceDeal
{
    public static final String INSERT_NEW_DEAL = "insert into pmp_deals(deal_id,deal_name,campaign_id_list," +
            "ad_id_list,pub_id_list,site_id_list,bcat,third_party_conn_list,dsp_id_list,adv_id_list,wadomain," +
            "auction_type,request_cap," +
            "start_date,end_date,deal_cpm,last_modified,status_id) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

    public static final String UPDATE_DEAL = "update pmp_deals set deal_name = ?,campaign_id_list = ?," +
            "ad_id_list = ?,pub_id_list = ?,site_id_list = ?,bcat = ?,third_party_conn_list = ?,dsp_id_list = ?," +
            "adv_id_list = ?,wadomain = ?,auction_type = ?,request_cap = ?," +
            "start_date = ?,end_date = ?,deal_cpm = ?,last_modified = ?,status_id = ? where deal_id = ?";

    public static final String UPDATE_DEAL_STATUS = "update pmp_deals set status_id = ?,last_modified = now() " +
                                                    "where deal_id = ?";

    public static final String GET_DEAL_BY_GUID = "select * from pmp_deals where deal_id = ?";

    public static final String GET_DEALS = "select * from pmp_deals";
}
