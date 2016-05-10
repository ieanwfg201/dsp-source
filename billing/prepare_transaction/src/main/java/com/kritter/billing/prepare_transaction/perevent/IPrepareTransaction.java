package com.kritter.billing.prepare_transaction.perevent;

import java.sql.Connection;
import java.util.List;



import com.kritter.postimpression.thrift.struct.Billing;
import com.kritter.postimpression.thrift.struct.PostImpressionRequestResponse;

public interface IPrepareTransaction {

    public void prepare_campaign_budget(List<Billing> billingLogList,Connection con, PostImpressionRequestResponse pirr) throws Exception;
    
    public void prepare_account_budget(List<Billing> billingLogList,Connection con, PostImpressionRequestResponse pirr) throws Exception;
    
    public void prepare_payout_network(List<Billing> billingLogList,Connection con, PostImpressionRequestResponse pirr) throws Exception;
    
    /**
     * 
     * select payout as payout, monthly_payout as monthly_payout, yearly_payout as yearly_payout from payout where
     * ad_inc_id="?" and publisher_inc_id=?;
     * update payout set payout=?, monthly_payout=?, yearly_payout=? where publisher_inc_id=? and ad_inc_id=?;
     * @param con
     * @param pirr
     * @throws Exception
     */
    public void prepare_payout_exchange(List<Billing> billingLogList,Connection con, PostImpressionRequestResponse pirr) throws Exception;
    
    public void prepare_updates(List<Billing> billingLogList,Connection con, PostImpressionRequestResponse pirr) throws Exception;  
    
}
