package com.kritter.kritterui.api.reporting;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.lang.time.DateUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kritter.constants.PageConstants;
import com.kritter.kritterui.api.db_query_def.Dashboard;

public class DashBoardCrud {
    private static final Logger LOG = LoggerFactory.getLogger(DashBoardCrud.class);
    
    public static void getPie(Connection con,PreparedStatement pstmt, String metric, String dimension, String yesterDayStr, String queryStr) throws Exception{
        pstmt = con.prepareStatement(queryStr);
        pstmt.setString(1, yesterDayStr);
        pstmt.setInt(2, PageConstants.dashboard_pie_size);
        ResultSet rset = pstmt.executeQuery();
        while(rset.next()){
            
        }
    }
    
    
    
    public static JsonNode get_data(Connection con, String tz){
        if(con == null){
            return null;
        }
        PreparedStatement pStmt = null;
        TimeZone.setDefault(TimeZone.getTimeZone(tz));
        Date currentDate = new Date();
        SimpleDateFormat currentDateFormat = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        String currentDateStr = currentDateFormat.format(currentDate);
        Date date7days = DateUtils.addDays(currentDate, 0-PageConstants.dashboard_bar_size);
        String date7daysStr = currentDateFormat.format(date7days);
        Date yesterday = DateUtils.addDays(currentDate, -1);
        String yesterdayStr = currentDateFormat.format(yesterday);
        Date dayb4yes = DateUtils.addDays(currentDate, -2);
        String dayb4yesStr = currentDateFormat.format(dayb4yes);
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode rootNode = mapper.createObjectNode();
        try{
            pStmt = con.prepareStatement(Dashboard.total_metric);
            pStmt.setString(1, date7daysStr);
            pStmt.setString(2, currentDateStr);
            ResultSet rset=pStmt.executeQuery();
            ArrayNode RequestImpressionRenderArrayNode = mapper.createArrayNode();
            ArrayNode RevenuePubIncomeArrayNode = mapper.createArrayNode();
            ArrayNode ClickConversionArrayNode = mapper.createArrayNode();
            while(rset.next()){
                RequestImpressionRenderArrayNode.add(DashBoardHelper.RequestImpressionRenderObjectNode(rset, mapper));
                RevenuePubIncomeArrayNode.add(DashBoardHelper.RevenuePubIncomeObjectNode(rset, mapper));
                ClickConversionArrayNode.add(DashBoardHelper.ClickConversionObjectNode(rset, mapper));
            }
            ArrayNode headerRequestImpressionRenderArrayNode = DashBoardHelper.columnRequestImpressionRender(mapper);
            ArrayNode headerRevenuePubIncomeArrayNode = DashBoardHelper.columnRevenuePubIncome(mapper);
            ArrayNode headerClickConversionArrayNode = DashBoardHelper.columnClickConversion(mapper);
            ObjectNode RequestImpressionRenderObjectNode = mapper.createObjectNode();
                RequestImpressionRenderObjectNode.put("column", headerRequestImpressionRenderArrayNode);
                RequestImpressionRenderObjectNode.put("data", RequestImpressionRenderArrayNode);
            ObjectNode RevenuePubIncomeObjectNode = mapper.createObjectNode();
                RevenuePubIncomeObjectNode.put("column", headerRevenuePubIncomeArrayNode);
                RevenuePubIncomeObjectNode.put("data", RevenuePubIncomeArrayNode);
            ObjectNode ClickConversionObjectNode = mapper.createObjectNode();
                ClickConversionObjectNode.put("column", headerClickConversionArrayNode);
                ClickConversionObjectNode.put("data", ClickConversionArrayNode);
            rootNode.put("RequestImpressionRender", RequestImpressionRenderObjectNode);
            rootNode.put("RevenuePubIncome", RevenuePubIncomeObjectNode);
            rootNode.put("ClickConversion", ClickConversionObjectNode);
            rootNode.put("topAdvertiserByRevenueYes",DashBoardHelper.topAdvertiserByRevenueYes(con, pStmt, mapper, yesterdayStr));
            rootNode.put("topCampaignByRevenueYes",DashBoardHelper.topCampaignByRevenueYes(con, pStmt, mapper, yesterdayStr));
            rootNode.put("topPublisherByIncomeYes",DashBoardHelper.topPublisherByIncomeYes(con, pStmt, mapper, yesterdayStr));
            rootNode.put("topSiteByIncomeYes",DashBoardHelper.topSiteByIncomeYes(con, pStmt, mapper, yesterdayStr));
            rootNode.put("topExchangeByIncomeYes",DashBoardHelper.topExchangeByIncomeYes(con, pStmt, mapper, yesterdayStr));
            rootNode.put("topGainerAdvertiserByRevenueYesDayBefore", DashBoardHelper.gainloss(con, pStmt, mapper, yesterdayStr, 
                    dayb4yesStr, "topGainerAdvertiserByRevenueYesDayBefore", "gain", "advertiser_name")); 
            rootNode.put("topGainerPublisherByPubIncomeYesDayBefore", DashBoardHelper.gainloss(con, pStmt, mapper, yesterdayStr, 
                    dayb4yesStr, "topGainerPublisherByPubIncomeYesDayBefore", "gain", "pub_name")); 
            rootNode.put("topGainerCampaignByRevenueYesDayBefore", DashBoardHelper.gainloss(con, pStmt, mapper, yesterdayStr, 
                    dayb4yesStr, "topGainerCampaignByRevenueYesDayBefore", "gain", "campaign_name")); 
            rootNode.put("topGainerSiteByIncomeYesDayBefore", DashBoardHelper.gainloss(con, pStmt, mapper, yesterdayStr, 
                    dayb4yesStr, "topGainerSiteByIncomeYesDayBefore", "gain", "site_name")); 
            rootNode.put("topLooserAdvertiserByRevenueYesDayBefore", DashBoardHelper.gainloss(con, pStmt, mapper, yesterdayStr, 
                    dayb4yesStr, "topLooserAdvertiserByRevenueYesDayBefore", "loss", "advertiser_name")); 
            rootNode.put("topLooserPublisherByIncomeYesDayBefore", DashBoardHelper.gainloss(con, pStmt, mapper, yesterdayStr, 
                    dayb4yesStr, "topLooserPublisherByIncomeYesDayBefore", "loss", "pub_name")); 
            rootNode.put("topLooserCampaignByRevenueYesDayBefore", DashBoardHelper.gainloss(con, pStmt, mapper, yesterdayStr, 
                    dayb4yesStr, "topLooserCampaignByRevenueYesDayBefore", "loss", "campaign_name")); 
            rootNode.put("topLooserSiteByIncomeYesDayBefore", DashBoardHelper.gainloss(con, pStmt, mapper, yesterdayStr, 
                    dayb4yesStr, "topLooserSiteByIncomeYesDayBefore", "loss", "site_name")); 
            rootNode.put("topCountryByRequestYes", DashBoardHelper.pie(con, pStmt, mapper, yesterdayStr, 
                     "topCountryByRequestYes", "total_request_name", "country_name")); 
            rootNode.put("topOsByRequestYes", DashBoardHelper.pie(con, pStmt, mapper, yesterdayStr, 
                    "topOsByRequestYes", "total_request_name", "os_name")); 
            rootNode.put("topManufacturerByRequestYes", DashBoardHelper.pie(con, pStmt, mapper, yesterdayStr, 
                    "topManufacturerByRequestYes", "total_request_name", "manufacturer_name")); 
            rootNode.put("topBrowserByRequestYes", DashBoardHelper.pie(con, pStmt, mapper, yesterdayStr, 
                    "topBrowserByRequestYes", "total_request_name", "browser_name")); 
            rootNode.put("ImpressionWin", DashBoardHelper.impression_win(con, pStmt, mapper, date7daysStr, currentDateStr));
            return rootNode;
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            return rootNode;
        }finally{
            if(pStmt != null){
                try {
                    pStmt.close();
                } catch (SQLException e) {
                    LOG.error(e.getMessage(),e);
                }
            }  
        }
    }
    public static JsonNode get_adv_dashboard(Connection con, String tz,String guid){
        if(con == null || tz ==null || guid == null){
            return null;
        }
        PreparedStatement pStmt = null;
        TimeZone.setDefault(TimeZone.getTimeZone(tz));
        Date currentDate = new Date();
        SimpleDateFormat currentDateFormat = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        String currentDateStr = currentDateFormat.format(currentDate);
        Date date7days = DateUtils.addDays(currentDate, 0-PageConstants.dashboard_bar_size);
        String date7daysStr = currentDateFormat.format(date7days);
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode rootNode = mapper.createObjectNode();
        try{
            pStmt = con.prepareStatement(Dashboard.dashboard_advertisers_v1);
            pStmt.setString(1, date7daysStr);
            pStmt.setString(2, currentDateStr);
            pStmt.setString(3, guid);
            ResultSet rset=pStmt.executeQuery();
            ArrayNode dataNode = mapper.createArrayNode();
            while(rset.next()){
                dataNode.add(DashBoardHelper.advertiser_v1(rset, mapper));
            }
            ArrayNode headerNode = DashBoardHelper.columnAdvertiserV1Render(mapper);
            ObjectNode RequestImpressionRenderObjectNode = mapper.createObjectNode();
                RequestImpressionRenderObjectNode.put("column", headerNode);
                RequestImpressionRenderObjectNode.put("data", dataNode);
            rootNode.put("advertiser", RequestImpressionRenderObjectNode);
            return rootNode;
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            return rootNode;
        }finally{
            if(pStmt != null){
                try {
                    pStmt.close();
                } catch (SQLException e) {
                    LOG.error(e.getMessage(),e);
                }
            }  
        }
    }
    public static JsonNode get_pub_dashboard(Connection con, String tz,String guid){
        if(con == null || tz ==null || guid == null){
            return null;
        }
        PreparedStatement pStmt = null;
        TimeZone.setDefault(TimeZone.getTimeZone(tz));
        Date currentDate = new Date();
        SimpleDateFormat currentDateFormat = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        String currentDateStr = currentDateFormat.format(currentDate);
        Date date7days = DateUtils.addDays(currentDate, 0-PageConstants.dashboard_bar_size);
        String date7daysStr = currentDateFormat.format(date7days);
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode rootNode = mapper.createObjectNode();
        try{
            pStmt = con.prepareStatement(Dashboard.dashboard_publisher_v1);
            pStmt.setString(1, date7daysStr);
            pStmt.setString(2, currentDateStr);
            pStmt.setString(3, guid);
            ResultSet rset=pStmt.executeQuery();
            ArrayNode dataNode = mapper.createArrayNode();
            while(rset.next()){
                dataNode.add(DashBoardHelper.publisher_v1(rset, mapper));
            }
            ArrayNode headerNode = DashBoardHelper.columnPublisherV1Render(mapper);
            ObjectNode RequestImpressionRenderObjectNode = mapper.createObjectNode();
                RequestImpressionRenderObjectNode.put("column", headerNode);
                RequestImpressionRenderObjectNode.put("data", dataNode);
            rootNode.put("publisher", RequestImpressionRenderObjectNode);
            return rootNode;
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            return rootNode;
        }finally{
            if(pStmt != null){
                try {
                    pStmt.close();
                } catch (SQLException e) {
                    LOG.error(e.getMessage(),e);
                }
            }  
        }
    }
}
