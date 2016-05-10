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


public class WinNotification implements IPrepareTransaction {

    private static final Logger LOG = LoggerFactory.getLogger(WinNotification.class);


    private MarketPlace marketplace = MarketPlace.CPM;


    private String siteSelectString = "select s.pub_id as pub_id from site as s where s.id=?;";
    
    private String payoutSelectString = "select p.pub_id as pub_id,p.payout as payout, " +
    		"p.monthly_payout as monthly_payout, p.yearly_payout as yearly_payout, p.daily_payout as daily_payout from payout as p, site as s where"+
    		" p.ad_id=? and s.id=? and p.pub_id=s.pub_id;";
    
    
    private String payoutUpdateString = "update payout set payout=?, monthly_payout=?, yearly_payout=?, last_modified=?, daily_payout=?, campaign_id=? where pub_id=? and ad_id=?";
    private String payoutInsertString = "insert into payout(pub_id,ad_id,payout,monthly_payout,yearly_payout,last_modified,daily_payout,campaign_id) values(?,?,?,?,?,?,?,?)";
    
    private boolean payoutPresent = true;
    
    private int pub_id = 0;
    private double payout = 0;
    private double monthly_payout = 0;
    private double yearly_payout = 0;
    private double daily_payout = 0;
    
    private boolean preventTransation = false;
    
    private double eventPayOut = 0.0;
   
    public double getEventPayOut() {
        return eventPayOut;
    }


    public void setEventPayOut(double eventPayOut) {
        this.eventPayOut = eventPayOut;
    }


    @Override
    public void prepare_campaign_budget(List<Billing> billingLogList, Connection con,
            PostImpressionRequestResponse pirr) throws Exception {
        return;
    }

    
    @Override
    public void prepare_account_budget(List<Billing> billingLogList, Connection con,
            PostImpressionRequestResponse pirr) throws Exception {
        return;
    }


    @Override
    public void prepare_payout_network(List<Billing> billingLogList, Connection con,
            PostImpressionRequestResponse pirr) throws Exception {
        return;
    }

    @Override
    public void prepare_payout_exchange(List<Billing> billingLogList, Connection con,
            PostImpressionRequestResponse pirr) throws Exception {

        if(preventTransation || con == null || pirr == null){
            return;
        }
        eventPayOut = pirr.getAuction_price()/1000;
        PreparedStatement payoutPstmt = null;
        PreparedStatement sitePstmt = null;
        try{
            payoutPstmt = con.prepareStatement(payoutSelectString);
            payoutPstmt.setInt(1, pirr.getAdId());
            payoutPstmt.setInt(2, pirr.getSiteId());
            LOG.debug(payoutPstmt.toString());
            ResultSet payoutRset = payoutPstmt.executeQuery();
            if(payoutRset.next()){
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
                    pub_id = siteRset.getInt("pub_id");
                    payout = 0;
                    monthly_payout = 0;
                    yearly_payout = 0;
                    daily_payout = 0;
                }else{
                    /*Site Id Not Found*/
                    AddBilling.createNaddTerminatedBillingObject(billingLogList, pirr, BillingTerminationReason.EP_SITE_ID_NF, BillingType.EXCHANGE_PAYOUT);
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
    
    public void prepare_updates(List<Billing> billingLogList, Connection con, PostImpressionRequestResponse pirr) throws Exception{
        if(preventTransation || con == null || pirr == null){
            return;
        }
        PreparedStatement campaignStmt = null;
        PreparedStatement accountStmt = null;
        PreparedStatement payoutStmt = null;
        try{
            Timestamp  ts = new Timestamp(new Date().getTime());
            if(payoutPresent){
            /**
             * "update payout set payout=?, monthly_payout=?, yearly_payout=?,last_modified=? where pub_id=? and ad_id=?";
             */
                payoutStmt = con.prepareStatement(payoutUpdateString);
                payoutStmt.setDouble(1, payout + eventPayOut);
                payoutStmt.setDouble(2, monthly_payout + eventPayOut);
                payoutStmt.setDouble(3, yearly_payout + eventPayOut);
                payoutStmt.setTimestamp(4, ts);
                payoutStmt.setDouble(5, daily_payout + eventPayOut);
                payoutStmt.setInt(6, pirr.getCampaignId());
                payoutStmt.setInt(7, pub_id);
                payoutStmt.setInt(8, pirr.getAdId());
                LOG.debug(payoutStmt.toString());
                payoutStmt.executeUpdate();
                AddBilling.createNaddNonTerminatedBillingObject(billingLogList, pirr, BillingTerminationReason.HEALTHY_REQUEST, BillingType.EXCHANGE_PAYOUT, this, marketplace);
            }else{
                /**
                 * insert into payout(pub_id,ad_id,payout,monthly_payout,yearly_payout,last_modified) values(?,?,?,?,?,?)";
                 */
                payoutStmt = con.prepareStatement(payoutInsertString);
                payoutStmt.setInt(1, pub_id);
                payoutStmt.setInt(2, pirr.getAdId());
                payoutStmt.setDouble(3, payout + eventPayOut);
                payoutStmt.setDouble(4, monthly_payout + eventPayOut);
                payoutStmt.setDouble(5, yearly_payout + eventPayOut);
                payoutStmt.setTimestamp(6, ts);
                payoutStmt.setDouble(7, daily_payout + eventPayOut);
                payoutStmt.setInt(8, pirr.getCampaignId());
                LOG.debug(payoutStmt.toString());
                payoutStmt.executeUpdate();
                AddBilling.createNaddNonTerminatedBillingObject(billingLogList, pirr, BillingTerminationReason.HEALTHY_REQUEST, BillingType.EXCHANGE_PAYOUT, this,marketplace);
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
