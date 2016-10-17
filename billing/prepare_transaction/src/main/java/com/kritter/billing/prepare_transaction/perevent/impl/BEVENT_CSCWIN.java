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
import com.kritter.utils.pub_payout_serde.ReadPayout;

public class BEVENT_CSCWIN implements IPrepareTransaction {

    private static final Logger LOG = LoggerFactory.getLogger(BEVENT_CSCWIN.class);

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

    private String siteSelectString = "select s.billing_rules_json as rules,s.pub_id as pub_id from site as s where s.id=?;";
    
    private String payoutSelectString = "select s.billing_rules_json as rules,p.pub_id as pub_id,p.payout as payout, " +
    		"p.monthly_payout as monthly_payout, p.yearly_payout as yearly_payout, p.daily_payout as daily_payout from payout as p, site as s where"+
    		" p.ad_id=? and s.id=? and p.pub_id=s.pub_id;";
    
    
    private String campaignUpdateString = "update campaign_budget set  internal_total_burn=?,adv_total_burn=?,"+
            "internal_daily_burn=?, adv_daily_burn=?,last_modified=? where campaign_id=?;";
    private String  accountUpdateString = "update account_budget set internal_balance=?, internal_burn=?,adv_balance=?, adv_burn=?,last_modified=? " +
    		" where account_guid=?";
    private String payoutUpdateString = "update payout set payout=?, monthly_payout=?, yearly_payout=?, last_modified=?, daily_payout=?, campaign_id=? where pub_id=? and ad_id=?";
    private String payoutInsertString = "insert into payout(pub_id,ad_id,payout,monthly_payout,yearly_payout,last_modified,daily_payout,campaign_id) values(?,?,?,?,?,?,?,?)";
    
    private boolean payoutPresent = true;
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
    
    private String rules = null;
    private int pub_id = 0;
    private double payout = 0;
    private double monthly_payout = 0;
    private double yearly_payout = 0;
    private double daily_payout = 0;
    
    private boolean preventTransation = false;
    private boolean preventDemandTransation = false;
    
    private double auction_price = 0.0;

    private double eventPayOut = 0.0;
    
    private int marketplace_id = 0;
    
    public double getEventPayOut() {
        return eventPayOut;
    }


    public void setEventPayOut(double eventPayOut) {
        this.eventPayOut = eventPayOut;
    }

    public int getMarketPlaceId() {
        return marketplace_id;
    }

    @Override
    public void prepare_campaign_budget(List<Billing> billingLogList, Connection con,
            PostImpressionRequestResponse pirr) throws Exception {
        if(con == null || pirr == null){
            return ;
        }
        auction_price = pirr.getAuction_price();

        PreparedStatement marketPlaceStmt = null;
        PreparedStatement selectStmt = null;
        PreparedStatement updateStmt = null;
        try{
            marketPlaceStmt = con.prepareStatement(marketPlaceSelectString);
            marketPlaceStmt.setInt(1, pirr.getAdId());
            marketPlaceStmt.setInt(2, pirr.getCampaignId());
            LOG.debug(marketPlaceStmt.toString());
            ResultSet marketPlaceRset = marketPlaceStmt.executeQuery();
            
            
            if(marketPlaceRset.next()){
                marketplace_id = marketPlaceRset.getInt("marketplace_id");
            }else{
                preventTransation = true;
                /*Markeplace Id not found*/
                AddBilling.createNaddTerminatedBillingObject(billingLogList, pirr, BillingTerminationReason.BEVENT_CSCWIN_MP_NF, BillingType.BEVENT_CSCWIN);
                return;
            }
            if(!(marketplace_id == MarketPlace.CPM.getCode())){
                preventDemandTransation=true;
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
                AddBilling.createNaddTerminatedBillingObject(billingLogList, pirr, BillingTerminationReason.BEVENT_CSCWIN_CAMP_ID_NF, BillingType.BEVENT_CSCWIN);
                preventTransation = true;
                return;
            }
            double internal_total_remaining_budget= internal_total_budget - internal_total_burn;
            double adv_total_remaining_budget = adv_total_budget - adv_total_burn;
            double internal_daily_remaining_budget = internal_daily_budget - internal_daily_burn;
            double adv_daily_remaining_budget = adv_daily_budget - adv_daily_burn;
            if(internal_total_remaining_budget <= 0){
                /*Internal Total Remaining Budget NO FUND*/
                AddBilling.createNaddTerminatedBillingObject(billingLogList, pirr, BillingTerminationReason.BEVENT_CSCWIN_INTR_Tbudget_NoF, BillingType.BEVENT_CSCWIN);
                preventTransation = true;
                preventDemandTransation=true;
                return;
            }else if(auction_price/1000 > internal_total_remaining_budget){
                /*Max Bid > internal total remaining*/
                AddBilling.createNaddTerminatedBillingObject(billingLogList, pirr, BillingTerminationReason.BEVENT_CSCWIN_MAX_BID_GT_INTR_Tbudget, BillingType.BEVENT_CSCWIN);
                preventTransation = true;
                return;
            }
            if(adv_total_remaining_budget <= 0){
                /*Adv Total Remaining Budget NO FUND*/
                AddBilling.createNaddTerminatedBillingObject(billingLogList, pirr, BillingTerminationReason.BEVENT_CSCWIN_ADV_Tbudget_NoF, BillingType.BEVENT_CSCWIN);
                preventTransation = true;
                preventDemandTransation=true;
                return;
            }else if(auction_price/1000 > adv_total_remaining_budget ){
                /*Max Bid > advertiser total remaining*/
                AddBilling.createNaddTerminatedBillingObject(billingLogList, pirr, BillingTerminationReason.BEVENT_CSCWIN_MAX_BID_GT_ADV_Tbudget, BillingType.BEVENT_CSCWIN);
                preventTransation = true;
                preventDemandTransation=true;
                return;
            }
            if(internal_daily_remaining_budget <=0) {
                /*Internal Daily Remaining Budget NO FUND*/
                AddBilling.createNaddTerminatedBillingObject(billingLogList, pirr, BillingTerminationReason.BEVENT_CSCWIN_INTR_Dbudget_NoF, BillingType.BEVENT_CSCWIN);
                preventTransation = true;
                preventDemandTransation=true;
                return;
            }else if(auction_price/1000 > internal_daily_remaining_budget){
                /*Max Bid > internal daily remaining*/
                AddBilling.createNaddTerminatedBillingObject(billingLogList, pirr, BillingTerminationReason.BEVENT_CSCWIN_MAX_BID_GT_INTR_Dbudget, BillingType.BEVENT_CSCWIN);
                preventTransation = true;
                preventDemandTransation=true;
                return;
            }
            if(adv_daily_remaining_budget <=0 ){
                /*Adv Daily Remaining Budget NO FUND*/
                AddBilling.createNaddTerminatedBillingObject(billingLogList, pirr, BillingTerminationReason.BEVENT_CSCWIN_ADV_Dbudget_NoF, BillingType.BEVENT_CSCWIN);
                preventTransation = true;
                preventDemandTransation=true;
                return;
            }else if(auction_price/1000 > adv_daily_remaining_budget ){
                /*Max Bid > adv daily remaining*/
                AddBilling.createNaddTerminatedBillingObject(billingLogList, pirr, BillingTerminationReason.BEVENT_CSCWIN_MAX_BID_GT_ADV_Dbudget, BillingType.BEVENT_CSCWIN);
                preventTransation = true;
                preventDemandTransation=true;
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
        if(preventTransation || preventDemandTransation || con == null || pirr == null || account_id == null){
            return;
        }
        if(!(marketplace_id == MarketPlace.CPM.getCode())){
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
                AddBilling.createNaddTerminatedBillingObject(billingLogList, pirr, BillingTerminationReason.BEVENT_CSCWIN_ACCOUNT_NF, BillingType.BEVENT_CSCWIN);
                preventTransation = true;
                return;
            }
            if(internal_balance <= 0){
                /*Account NoF*/
                AddBilling.createNaddTerminatedBillingObject(billingLogList, pirr, BillingTerminationReason.BEVENT_CSCWIN_ACCOUNT_NoF, BillingType.BEVENT_CSCWIN);
                preventTransation = true;
                preventDemandTransation=true;
                return;
            }
            if(auction_price/1000 > adv_balance){
                /*Advertiser Bid Greater than adv bal no fund*/
                AddBilling.createNaddTerminatedBillingObject(billingLogList, pirr, BillingTerminationReason.BEVENT_CSCWIN_ADV_BID_GT_ADV_BAL, BillingType.BEVENT_CSCWIN);
                preventTransation = true;
                preventDemandTransation=true;
                return;
            }
            if(auction_price/1000 > internal_balance){
                /*Internal Bid Greater than adv bal no fund*/
                AddBilling.createNaddTerminatedBillingObject(billingLogList, pirr, BillingTerminationReason.BEVENT_CSCWIN_ADV_BID_GT_INT_BAL, BillingType.BEVENT_CSCWIN);
                preventTransation = true;
                preventDemandTransation=true;
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
        if(preventTransation || con == null || pirr == null){
            return;
        }
        PreparedStatement payoutPstmt = null;
        PreparedStatement sitePstmt = null;
        try{
            payoutPstmt = con.prepareStatement(payoutSelectString);
            payoutPstmt.setInt(1, pirr.getAdId());
            payoutPstmt.setInt(2, pirr.getSiteId());
            LOG.debug(payoutPstmt.toString());
            ResultSet payoutRset = payoutPstmt.executeQuery();
            if(payoutRset.next()){
                rules = payoutRset.getString("rules");
                pub_id = payoutRset.getInt("pub_id");
                payout = payoutRset.getDouble("payout");
                monthly_payout = payoutRset.getDouble("monthly_payout");
                yearly_payout = payoutRset.getDouble("yearly_payout");
                daily_payout = payoutRset.getDouble("daily_payout");
            }else{
                payoutPresent = false;
                sitePstmt = con.prepareStatement(siteSelectString);
                sitePstmt.setInt(1, pirr.getSiteId());
                LOG.debug(sitePstmt.toString());
                ResultSet siteRset = sitePstmt.executeQuery();
                if(siteRset.next()){
                    rules = siteRset.getString("rules");
                    pub_id = siteRset.getInt("pub_id");
                    payout = 0;
                    monthly_payout = 0;
                    yearly_payout = 0;
                    daily_payout = 0;
                }else{
                    /*Site Id Not Found*/
                    AddBilling.createNaddTerminatedBillingObject(billingLogList, pirr, BillingTerminationReason.BEVENT_CSCWIN_SITE_ID_NF, BillingType.BEVENT_CSCWIN);
                    preventTransation = true;
                    return;
                }
            }
            
        }catch(Exception e){
            preventTransation = true;
            throw new Exception(e);
        }finally{
            if(payoutPstmt != null){
                payoutPstmt.close();
            }
            if(sitePstmt != null){
                sitePstmt.close();
            }
        }

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
        boolean updateDemand=false;
        BillingType localBillingType = BillingType.BEVENT_CSCWIN;
        try{
            if(marketplace_id == MarketPlace.CPM.getCode() && !preventDemandTransation){

                campaignStmt = con.prepareStatement(campaignUpdateString);
                campaignStmt.setDouble(1, internal_total_burn + (auction_price/1000));
                campaignStmt.setDouble(2, adv_total_burn + (auction_price/1000));
                campaignStmt.setDouble(3, internal_daily_burn + (auction_price/1000));
                campaignStmt.setDouble(4, adv_daily_burn + (auction_price/1000));
                campaignStmt.setTimestamp(5, new Timestamp((new Date()).getTime()));
                campaignStmt.setInt(6, pirr.getCampaignId());
                LOG.debug(campaignStmt.toString());
                campaignStmt.executeUpdate();
                
                accountStmt = con.prepareStatement(accountUpdateString);
                accountStmt.setDouble(1, internal_balance - (auction_price/1000));
                accountStmt.setDouble(2, internal_burn + (auction_price/1000));
                accountStmt.setDouble(3, adv_balance - (auction_price/1000));
                accountStmt.setDouble(4, adv_burn + (auction_price/1000));
                accountStmt.setTimestamp(5, new Timestamp((new Date()).getTime()));
                accountStmt.setString(6, account_id);
                LOG.debug(accountStmt.toString());
                accountStmt.executeUpdate();
                updateDemand=true;
                localBillingType = BillingType.BEVENT_CSCWIN_DEM;
            }
            if(payoutPresent){
            /**
             * "update payout set payout=?, monthly_payout=?, yearly_payout=?,last_modified=? where pub_id=? and ad_id=?";
             */
                payoutStmt = con.prepareStatement(payoutUpdateString);
                double payoutPercent = ReadPayout.getPayoutPercent(rules);
                eventPayOut = (payoutPercent * auction_price)/100000;
                payoutStmt.setDouble(1, payout + eventPayOut);
                payoutStmt.setDouble(2, monthly_payout + eventPayOut);
                payoutStmt.setDouble(3, yearly_payout + eventPayOut);
                payoutStmt.setTimestamp(4, new Timestamp((new Date()).getTime()));
                payoutStmt.setDouble(5, daily_payout + eventPayOut);
                payoutStmt.setInt(6, pirr.getCampaignId());
                payoutStmt.setInt(7, pub_id);
                payoutStmt.setInt(8, pirr.getAdId());
                LOG.debug(payoutStmt.toString());
                payoutStmt.executeUpdate();
                AddBilling.createNaddNonTerminatedBillingObject(billingLogList, pirr, BillingTerminationReason.HEALTHY_REQUEST, localBillingType, this, updateDemand);
            }else{
                /**
                 * insert into payout(pub_id,ad_id,payout,monthly_payout,yearly_payout,last_modified) values(?,?,?,?,?,?)";
                 */
                payoutStmt = con.prepareStatement(payoutInsertString);
                double payoutPercent = ReadPayout.getPayoutPercent(rules);
                eventPayOut = (payoutPercent * auction_price)/100000;
                payoutStmt.setInt(1, pub_id);
                payoutStmt.setInt(2, pirr.getAdId());
                payoutStmt.setDouble(3, payout + eventPayOut);
                payoutStmt.setDouble(4, monthly_payout + eventPayOut);
                payoutStmt.setDouble(5, yearly_payout + eventPayOut);
                payoutStmt.setTimestamp(6, new Timestamp((new Date()).getTime()));
                payoutStmt.setDouble(7, daily_payout + eventPayOut);
                payoutStmt.setInt(8, pirr.getCampaignId());
                LOG.debug(payoutStmt.toString());
                payoutStmt.executeUpdate();
                AddBilling.createNaddNonTerminatedBillingObject(billingLogList, pirr, BillingTerminationReason.HEALTHY_REQUEST, localBillingType, this, updateDemand);
            }
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
