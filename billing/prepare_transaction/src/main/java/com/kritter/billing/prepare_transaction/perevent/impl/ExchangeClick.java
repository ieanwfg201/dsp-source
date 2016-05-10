package com.kritter.billing.prepare_transaction.perevent.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kritter.billing.prepare_transaction.log.AddBilling;
import com.kritter.billing.prepare_transaction.perevent.IPrepareTransaction;
import com.kritter.constants.MarketPlace;
import com.kritter.postimpression.thrift.struct.Billing;
import com.kritter.postimpression.thrift.struct.BillingTerminationReason;
import com.kritter.postimpression.thrift.struct.BillingType;
import com.kritter.postimpression.thrift.struct.PostImpressionRequestResponse;

public class ExchangeClick implements IPrepareTransaction {

    private static final Logger LOG = LoggerFactory.getLogger(ExchangeClick.class);

    private String account_id = null;

    private String marketPlaceSelectString = "select marketplace_id  from ad where id=? and campaign_id = ?";

    private String campaignSelectString = "select b.internal_total_budget as internal_total_budget," +
    		"b.adv_total_budget as adv_total_budget," +
    		"b.internal_total_burn as internal_total_burn," +
    		" b.adv_total_burn as adv_total_burn, "+ 
            "b.internal_daily_budget as internal_daily_budget,"+ 
            "b.adv_daily_budget as adv_daily_budget," +
            "b.internal_daily_burn as internal_daily_burn,"+
            "b.adv_daily_burn as adv_daily_burn,"+ 
            "c.account_guid as account_id"+
            " from campaign_budget as b,campaign as c where b.campaign_id=? and c.id=? and b.campaign_id=c.id;";
    
    private String accountSelectString = "select internal_balance as internal_balance, internal_burn as internal_burn, adv_balance as adv_balance, " +
    		"adv_burn as adv_burn from account_budget where account_guid=?";

    private String campaignUpdateString = "update campaign_budget set  internal_total_burn=?,adv_total_burn=?,"+
            "internal_daily_burn=?, adv_daily_burn=?,last_modified=? where campaign_id=?;";
    private String  accountUpdateString = "update account_budget set internal_balance=?, internal_burn=?,adv_balance=?, adv_burn=?,last_modified=? " +
            " where account_guid=?";
    private double internal_total_budget = 0;
    private double adv_total_budget = 0;
    private double internal_total_burn = 0;
    private double adv_total_burn = 0;
    private double internal_daily_budget = 0;
    private double adv_daily_budget = 0;
    private double internal_daily_burn = 0;
    private double adv_daily_burn = 0;
    
    private double internal_balance = 0;
    private double internal_burn = 0;
    private double adv_balance = 0;
    private double adv_burn = 0;
    
    private boolean preventTransation = false;
    
    @Override
    public void prepare_campaign_budget(List<Billing> billingLogList, Connection con,
            PostImpressionRequestResponse pirr) throws Exception {
        if(con == null || pirr == null){
            return ;
        }
        PreparedStatement marketPlaceStmt = null;
        PreparedStatement selectStmt = null;
        PreparedStatement updateStmt = null;
        try{
            marketPlaceStmt = con.prepareStatement(marketPlaceSelectString);
            marketPlaceStmt.setInt(1, pirr.getAdId());
            marketPlaceStmt.setInt(2, pirr.getCampaignId());
            LOG.debug(marketPlaceStmt.toString());
            ResultSet marketPlaceRset = marketPlaceStmt.executeQuery();
            
            int marketplace_id = 0;
            if(marketPlaceRset.next()){
                marketplace_id = marketPlaceRset.getInt("marketplace_id");
            }else{
                preventTransation = true;
                /*Markeplace Id not found*/
                AddBilling.createNaddTerminatedBillingObject(billingLogList, pirr, BillingTerminationReason.ECLK_MP_NF, BillingType.CPC);
                return;
            }
            if(!(marketplace_id == MarketPlace.CPC.getCode())){
                /*Simply return as the Campaign is not CPC*/
                AddBilling.createNaddTerminatedBillingObject(billingLogList, pirr, BillingTerminationReason.ECLK_SERVED_CAMP_NOT_CPC, BillingType.CPC);
                preventTransation = true;
                return;
            }
           
            selectStmt = con.prepareStatement(campaignSelectString);
            selectStmt.setInt(1, pirr.getCampaignId());
            selectStmt.setInt(2, pirr.getCampaignId());
            LOG.debug(selectStmt.toString());
            ResultSet rset = selectStmt.executeQuery();
            if(rset.next()){
                internal_total_budget = rset.getDouble("internal_total_budget");
                adv_total_budget = rset.getDouble("adv_total_budget");
                internal_total_burn = rset.getDouble("internal_total_burn");
                adv_total_burn = rset.getDouble("adv_total_burn");
                internal_daily_budget = rset.getDouble("internal_daily_budget");
                adv_daily_budget = rset.getDouble("adv_daily_budget");
                internal_daily_burn = rset.getDouble("internal_daily_burn");
                adv_daily_burn = rset.getDouble("adv_daily_burn");
                account_id = rset.getString("account_id");
            }else{
                /*Campiagn Id not found*/
                AddBilling.createNaddTerminatedBillingObject(billingLogList, pirr, BillingTerminationReason.DPCLCK_CAMP_ID_NF, BillingType.CPC);
                preventTransation = true;
                return;
            }
            double internal_total_remaining_budget= internal_total_budget - internal_total_burn;
            double adv_total_remaining_budget = adv_total_budget - adv_total_burn;
            double internal_daily_remaining_budget = internal_daily_budget - internal_daily_burn;
            double adv_daily_remaining_budget = adv_daily_budget - adv_daily_burn;
            if(internal_total_remaining_budget <= 0){
                /*Internal Total Remaining Budget NO FUND*/
                AddBilling.createNaddTerminatedBillingObject(billingLogList, pirr, BillingTerminationReason.ECLK_INTR_Tbudget_NoF, BillingType.CPC);
                preventTransation = true;
                return;
            }else if(pirr.getInternal_max_bid() > internal_total_remaining_budget){
                /*Max Bid > internal total remaining*/
                AddBilling.createNaddTerminatedBillingObject(billingLogList, pirr, BillingTerminationReason.ECLK_MAX_BID_GT_INTR_Tbudget, BillingType.CPC);
                preventTransation = true;
                return;
            }
            if(adv_total_remaining_budget <= 0){
                /*Adv Total Remaining Budget NO FUND*/
                AddBilling.createNaddTerminatedBillingObject(billingLogList, pirr, BillingTerminationReason.ECLK_ADV_Tbudget_NoF, BillingType.CPC);
                preventTransation = true;
                return;
            }else if(pirr.getAdvertiser_bid() > adv_total_remaining_budget ){
                /*Max Bid > advertiser total remaining*/
                AddBilling.createNaddTerminatedBillingObject(billingLogList, pirr, BillingTerminationReason.ECLK_MAX_BID_GT_ADV_Tbudget, BillingType.CPC);
                preventTransation = true;
                return;
            }
            if(internal_daily_remaining_budget <=0) {
                /*Internal Daily Remaining Budget NO FUND*/
                AddBilling.createNaddTerminatedBillingObject(billingLogList, pirr, BillingTerminationReason.ECLK_INTR_Dbudget_NoF, BillingType.CPC);
                preventTransation = true;
                return;
            }else if(pirr.getInternal_max_bid() > internal_daily_remaining_budget){
                /*Max Bid > internal daily remaining*/
                AddBilling.createNaddTerminatedBillingObject(billingLogList, pirr, BillingTerminationReason.ECLK_MAX_BID_GT_INTR_Dbudget, BillingType.CPC);
                preventTransation = true;
                return;
            }
            if(adv_daily_remaining_budget <=0 ){
                /*Adv Daily Remaining Budget NO FUND*/
                AddBilling.createNaddTerminatedBillingObject(billingLogList, pirr, BillingTerminationReason.ECLK_ADV_Dbudget_NoF, BillingType.CPC);
                preventTransation = true;
                return;
            }else if(pirr.getAdvertiser_bid() > adv_daily_remaining_budget ){
                /*Max Bid > adv daily remaining*/
                AddBilling.createNaddTerminatedBillingObject(billingLogList, pirr, BillingTerminationReason.ECLK_MAX_BID_GT_ADV_Dbudget, BillingType.CPC);
                preventTransation = true;
                return;
            }
            
        }catch(Exception e){
            preventTransation = true;
            throw new Exception (e);
        }finally{
            if(selectStmt != null){
                selectStmt.close();
            }
            if(updateStmt != null){
                updateStmt.close();
            }
            if(marketPlaceStmt != null){
                marketPlaceStmt.close();
            }
        }
        
    }

    
    @Override
    public void prepare_account_budget(List<Billing> billingLogList, Connection con,
            PostImpressionRequestResponse pirr) throws Exception {
        if(preventTransation || con == null || pirr == null || account_id == null){
            return;
        }
        PreparedStatement accountSelectStmt = null;
        try{
            accountSelectStmt = con.prepareStatement(accountSelectString);
            accountSelectStmt.setString(1, account_id);
            LOG.debug(accountSelectStmt.toString());
            ResultSet accountResultSet = accountSelectStmt.executeQuery();
            if(accountResultSet.next()){
                internal_balance = accountResultSet.getDouble("internal_balance");
                internal_burn = accountResultSet.getDouble("internal_burn");
                adv_balance = accountResultSet.getDouble("adv_balance");
                adv_burn = accountResultSet.getDouble("adv_burn");
            }else{
                /*Account info not found*/
                AddBilling.createNaddTerminatedBillingObject(billingLogList, pirr, BillingTerminationReason.ECLK_ACCOUNT_NF, BillingType.CPC);
                preventTransation = true;
                return;
            }
            if(internal_balance <= 0){
                /*Account NoF*/
                AddBilling.createNaddTerminatedBillingObject(billingLogList, pirr, BillingTerminationReason.ECLK_ACCOUNT_NoF, BillingType.CPC);
                preventTransation = true;
                return;
            }
            if(pirr.getAdvertiser_bid() > adv_balance){
                /*Advertiser Bid Greater than adv bal no fund*/
                AddBilling.createNaddTerminatedBillingObject(billingLogList, pirr, BillingTerminationReason.ECLK_ADV_BID_GT_ADV_BAL, BillingType.CPC);
                preventTransation = true;
                return;
            }
            if(pirr.getInternal_max_bid() > internal_balance){
                /*Internal Bid Greater than adv bal no fund*/
                AddBilling.createNaddTerminatedBillingObject(billingLogList, pirr, BillingTerminationReason.ECLK_ADV_BID_GT_INT_BAL, BillingType.CPC);
                preventTransation = true;
                return;
            }
        }catch(Exception e){
            preventTransation = true;
            throw new Exception (e);
        }finally{
            if(accountSelectStmt != null){
                accountSelectStmt.close();
            }
        }
        
    }


    @Override
    public void prepare_payout_network(List<Billing> billingLogList, Connection con,
            PostImpressionRequestResponse pirr) throws Exception {
        return;
    }

    @Override
    public void prepare_payout_exchange(List<Billing> billingLogList, Connection con,
            PostImpressionRequestResponse pirr) throws Exception {
        return;
    }
    
    public void prepare_updates(List<Billing> billingLogList, Connection con, PostImpressionRequestResponse pirr) throws Exception{
        if(preventTransation || con == null || pirr == null){
            return;
        }
        PreparedStatement campaignStmt = null;
        PreparedStatement accountStmt = null;
        PreparedStatement payoutStmt = null;
        try{
            campaignStmt = con.prepareStatement(campaignUpdateString);
            campaignStmt.setDouble(1, internal_total_burn + pirr.getInternal_max_bid());
            campaignStmt.setDouble(2, adv_total_burn + pirr.getAdvertiser_bid());
            campaignStmt.setDouble(3, internal_daily_burn + pirr.getInternal_max_bid());
            campaignStmt.setDouble(4, adv_daily_burn + pirr.getAdvertiser_bid());
            campaignStmt.setTimestamp(5, new Timestamp((new Date()).getTime()));
            campaignStmt.setInt(6, pirr.getCampaignId());
            LOG.debug(campaignStmt.toString());
            campaignStmt.executeUpdate();
            
            accountStmt = con.prepareStatement(accountUpdateString);
            accountStmt.setDouble(1, internal_balance - pirr.getInternal_max_bid());
            accountStmt.setDouble(2, internal_burn + pirr.getInternal_max_bid());
            accountStmt.setDouble(3, adv_balance - pirr.getAdvertiser_bid());
            accountStmt.setDouble(4, adv_burn + pirr.getAdvertiser_bid());
            accountStmt.setTimestamp(5, new Timestamp((new Date()).getTime()));
            accountStmt.setString(6, account_id);
            LOG.debug(accountStmt.toString());
            accountStmt.executeUpdate();
            AddBilling.createNaddNonTerminatedBillingObject(billingLogList, pirr, BillingTerminationReason.HEALTHY_REQUEST, BillingType.CPC, this);
        }catch(Exception e){
            preventTransation = true;
            throw new Exception(e);
        }finally{
            if(campaignStmt != null){
                campaignStmt.close();
            }
            if(accountStmt != null){
                accountStmt.close();
            }
            if(payoutStmt != null){
                payoutStmt.close();
            }
        }
    }

}
