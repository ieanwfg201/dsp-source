package com.kritter.kritterui.api.db_query_def;

/**
 * This class has database query related to pmp deals.
 */
public class PrivateMarketPlaceDeal
{
    public static final String INSERT_NEW_DEAL = "insert into pmp_deals(deal_id,deal_name,ad_id_list," +
            "site_id_list,bcat,third_party_conn_list,dsp_id_list,adv_id_list,wadomain,auction_type,request_cap," +
            "start_date,end_date,deal_cpm,last_modified) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
}
