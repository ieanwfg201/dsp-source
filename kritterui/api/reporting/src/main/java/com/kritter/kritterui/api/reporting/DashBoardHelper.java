package com.kritter.kritterui.api.reporting;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import com.kritter.constants.Naming;
import com.kritter.constants.PageConstants;
import com.kritter.kritterui.api.db_query_def.Dashboard;

public class DashBoardHelper {
    public static ArrayNode addHeader(ArrayNode columnNode,ObjectMapper mapper, String fieldName){
        ObjectNode columnObjNode = mapper.createObjectNode();
        columnObjNode.put("field", fieldName);
        columnObjNode.put("visible", "true");
        columnNode.add(columnObjNode);
        return columnNode;
    }
    public static ObjectNode RequestImpressionRenderObjectNode(ResultSet rset,ObjectMapper mapper) throws Exception{
        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("time", rset.getString("time"));
        objectNode.put("total_request_name", rset.getInt("total_request_name"));
        objectNode.put("total_impression_name", rset.getInt("total_impression_name"));
        objectNode.put("total_csc_name", rset.getInt("total_csc_name"));
        return objectNode;
    }
    public static ObjectNode advertiser_v1(ResultSet rset,ObjectMapper mapper) throws Exception{
        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("time", rset.getString("time"));
        objectNode.put("account_name", rset.getString("account_name"));
        objectNode.put("total_impression_name", rset.getInt("total_impression_name"));
        objectNode.put("total_click_name", rset.getInt("total_click_name"));
        objectNode.put("total_win_name", rset.getInt("total_win_name"));
        objectNode.put("conversion_name", rset.getInt("conversion_name"));
        objectNode.put("demandCharges_name", rset.getDouble("demandCharges_name"));
        objectNode.put("total_csc_name", rset.getDouble("total_csc_name"));
        return objectNode;
    }
    public static ObjectNode publisher_v1(ResultSet rset,ObjectMapper mapper) throws Exception{
        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("time", rset.getString("time"));
        objectNode.put("account_name", rset.getString("account_name"));
        objectNode.put("total_request_name", rset.getInt("total_request_name"));
        objectNode.put("total_impression_name", rset.getInt("total_impression_name"));
        objectNode.put("total_click_name", rset.getInt("total_click_name"));
        objectNode.put("total_csc_name", rset.getDouble("total_csc_name"));
        objectNode.put("total_win_name", rset.getInt("total_win_name"));
        objectNode.put("supplyCost_name", rset.getDouble("supplyCost_name"));
        return objectNode;
    }
    public static ArrayNode columnRequestImpressionRender(ObjectMapper mapper){
        ArrayNode columnNode = mapper.createArrayNode();
        addHeader(columnNode, mapper, "time");
        addHeader(columnNode, mapper, "total_request_name");
        addHeader(columnNode, mapper, "total_impression_name");
        addHeader(columnNode, mapper, "total_csc_name");
        return columnNode;
    }
    public static ArrayNode columnAdvertiserV1Render(ObjectMapper mapper){
        ArrayNode columnNode = mapper.createArrayNode();
        addHeader(columnNode, mapper, "time");
        addHeader(columnNode, mapper, "account_name");
        addHeader(columnNode, mapper, "total_impression_name");
        addHeader(columnNode, mapper, "total_click_name");
        addHeader(columnNode, mapper, "total_win_name");
        addHeader(columnNode, mapper, "conversion_name");
        addHeader(columnNode, mapper, "demandCharges_name");
        addHeader(columnNode, mapper, "total_csc_name");
        return columnNode;
    }
    public static ArrayNode columnPublisherV1Render(ObjectMapper mapper){
        ArrayNode columnNode = mapper.createArrayNode();
        addHeader(columnNode, mapper, "time");
        addHeader(columnNode, mapper, "account_name");
        addHeader(columnNode, mapper, "total_request_name");
        addHeader(columnNode, mapper, "total_impression_name");
        addHeader(columnNode, mapper, "total_click_name");
        addHeader(columnNode, mapper, "total_csc_name");
        addHeader(columnNode, mapper, "total_win_name");
        addHeader(columnNode, mapper, "supplyCost_name");
        return columnNode;
    }
    public static ObjectNode RevenuePubIncomeObjectNode(ResultSet rset,ObjectMapper mapper) throws Exception{
        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("time", rset.getString("time"));
        objectNode.put("demandCharges_name", rset.getInt("demandCharges_name"));
        objectNode.put("supplyCost_name", rset.getInt("supplyCost_name"));
        return objectNode;
    }
    public static ArrayNode columnRevenuePubIncome(ObjectMapper mapper){
        ArrayNode columnNode = mapper.createArrayNode();
        addHeader(columnNode, mapper, "time");
        addHeader(columnNode, mapper, "demandCharges_name");
        addHeader(columnNode, mapper, "supplyCost_name");
        return columnNode;
    }
    public static ObjectNode ClickConversionObjectNode(ResultSet rset,ObjectMapper mapper) throws Exception{
        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("time", rset.getString("time"));
        objectNode.put("total_click_name", rset.getInt("total_click_name"));
        objectNode.put("conversion_name", rset.getInt("conversion_name"));
        return objectNode;
    }
    public static ArrayNode columnClickConversion(ObjectMapper mapper){
        ArrayNode columnNode = mapper.createArrayNode();
        addHeader(columnNode, mapper, "time");
        addHeader(columnNode, mapper, "total_click_name");
        addHeader(columnNode, mapper, "conversion_name");
        return columnNode;
    }
    public static ArrayNode columntopAdvertiserByRevenueYes(ObjectMapper mapper) throws Exception{
        ArrayNode columnNode = mapper.createArrayNode();
        addHeader(columnNode, mapper, "account_name");
        addHeader(columnNode, mapper, "demandCharges_name");
        return columnNode;
    }
    public static ObjectNode topAdvertiserByRevenueYes(Connection con, PreparedStatement pstmt, ObjectMapper mapper, String dateStr) throws Exception{
        ObjectNode objectNode = mapper.createObjectNode();
        pstmt = con.prepareStatement(Dashboard.dashboard_advertisers);
        pstmt.setString(1, dateStr);
        pstmt.setInt(2, PageConstants.dashboard_table_size);
        ResultSet rset = pstmt.executeQuery();
        ArrayNode arrayNode = mapper.createArrayNode();
        while(rset.next()){
            ObjectNode dataNode = mapper.createObjectNode();
            dataNode.put("account_name", rset.getString("account_name"));
            dataNode.put("demandCharges_name", rset.getDouble("demandCharges_name"));
            arrayNode.add(dataNode);
        }
        objectNode.put("data", arrayNode);
        objectNode.put("column", columntopAdvertiserByRevenueYes(mapper));
        return objectNode;
    }
    public static ArrayNode columntopCampaignByRevenueYes(ObjectMapper mapper) throws Exception{
        ArrayNode columnNode = mapper.createArrayNode();
        addHeader(columnNode, mapper, "campaign_name");
        addHeader(columnNode, mapper, "demandCharges_name");
        return columnNode;
    }
    public static ObjectNode topCampaignByRevenueYes(Connection con, PreparedStatement pstmt, ObjectMapper mapper, String dateStr) throws Exception{
        ObjectNode objectNode = mapper.createObjectNode();
        pstmt = con.prepareStatement(Dashboard.dashboard_campaign);
        pstmt.setString(1, dateStr);
        pstmt.setInt(2, PageConstants.dashboard_table_size);
        ResultSet rset = pstmt.executeQuery();
        ArrayNode arrayNode = mapper.createArrayNode();
        while(rset.next()){
            ObjectNode dataNode = mapper.createObjectNode();
            dataNode.put("campaign_name", rset.getString("campaign_name"));
            dataNode.put("demandCharges_name", rset.getDouble("demandCharges_name"));
            arrayNode.add(dataNode);
        }
        objectNode.put("data", arrayNode);
        objectNode.put("column", columntopCampaignByRevenueYes(mapper));
        return objectNode;
    }
    public static ArrayNode columntopPublisherByIncomeYes(ObjectMapper mapper) throws Exception{
        ArrayNode columnNode = mapper.createArrayNode();
        addHeader(columnNode, mapper, "account_name");
        addHeader(columnNode, mapper, "supplyCost_name");
        return columnNode;
    }
    public static ObjectNode topPublisherByIncomeYes(Connection con, PreparedStatement pstmt, ObjectMapper mapper, String dateStr) throws Exception{
        ObjectNode objectNode = mapper.createObjectNode();
        pstmt = con.prepareStatement(Dashboard.dashboard_publisher);
        pstmt.setString(1, dateStr);
        pstmt.setInt(2, PageConstants.dashboard_table_size);
        ResultSet rset = pstmt.executeQuery();
        ArrayNode arrayNode = mapper.createArrayNode();
        while(rset.next()){
            ObjectNode dataNode = mapper.createObjectNode();
            dataNode.put("account_name", rset.getString("account_name"));
            dataNode.put("supplyCost_name", rset.getDouble("supplyCost_name"));
            arrayNode.add(dataNode);
        }
        objectNode.put("data", arrayNode);
        objectNode.put("column", columntopPublisherByIncomeYes(mapper));
        return objectNode;
    }
    public static ArrayNode columntopSiteByIncomeYes(ObjectMapper mapper) throws Exception{
        ArrayNode columnNode = mapper.createArrayNode();
        addHeader(columnNode, mapper, "site_name");
        addHeader(columnNode, mapper, "supplyCost_name");
        return columnNode;
    }
    public static ObjectNode topSiteByIncomeYes(Connection con, PreparedStatement pstmt, ObjectMapper mapper, String dateStr) throws Exception{
        ObjectNode objectNode = mapper.createObjectNode();
        pstmt = con.prepareStatement(Dashboard.dashboard_site);
        pstmt.setString(1, dateStr);
        pstmt.setInt(2, PageConstants.dashboard_table_size);
        ResultSet rset = pstmt.executeQuery();
        ArrayNode arrayNode = mapper.createArrayNode();
        while(rset.next()){
            ObjectNode dataNode = mapper.createObjectNode();
            dataNode.put("site_name", rset.getString("site_name"));
            dataNode.put("supplyCost_name", rset.getDouble("supplyCost_name"));
            arrayNode.add(dataNode);
        }
        objectNode.put("data", arrayNode);
        objectNode.put("column", columntopSiteByIncomeYes(mapper));
        return objectNode;
    }
    public static ArrayNode columntopExchangeByIncomeYes(ObjectMapper mapper) throws Exception{
        ArrayNode columnNode = mapper.createArrayNode();
        addHeader(columnNode, mapper, "account_name");
        addHeader(columnNode, mapper, "exchangepayout_name");
        return columnNode;
    }
    public static ObjectNode topExchangeByIncomeYes(Connection con, PreparedStatement pstmt, ObjectMapper mapper, String dateStr) throws Exception{
        ObjectNode objectNode = mapper.createObjectNode();
        pstmt = con.prepareStatement(Dashboard.dashboard_exchange);
        pstmt.setString(1, dateStr);
        pstmt.setInt(2, PageConstants.dashboard_table_size);
        ResultSet rset = pstmt.executeQuery();
        ArrayNode arrayNode = mapper.createArrayNode();
        while(rset.next()){
            ObjectNode dataNode = mapper.createObjectNode();
            dataNode.put("account_name", rset.getString("account_name"));
            dataNode.put("exchangepayout_name", rset.getDouble("exchangepayout_name"));
            arrayNode.add(dataNode);
        }
        objectNode.put("data", arrayNode);
        objectNode.put("column", columntopExchangeByIncomeYes(mapper));
        return objectNode;
    }
    public static ArrayNode columnChange(ObjectMapper mapper,String...columnName) throws Exception{
        ArrayNode columnNode = mapper.createArrayNode();
        for(String str:columnName){
            addHeader(columnNode, mapper, str);
        }
        return columnNode;
    }
    public static ObjectNode gainloss(Connection con, PreparedStatement pstmt, ObjectMapper mapper, String dateStr1,String dateStr2,
            String dataname,String metricname, String dimname ) throws Exception{
        ObjectNode objectNode = mapper.createObjectNode();
        if("topGainerAdvertiserByRevenueYesDayBefore".equals(dataname)){
            pstmt = con.prepareStatement(Dashboard.gain_advertisers);
        }else if("topGainerPublisherByPubIncomeYesDayBefore".equals(dataname)){
            pstmt = con.prepareStatement(Dashboard.gain_publisher);
        }else if("topGainerCampaignByRevenueYesDayBefore".equals(dataname)){
            pstmt = con.prepareStatement(Dashboard.gain_campaign);
        }else if("topGainerSiteByIncomeYesDayBefore".equals(dataname)){
            pstmt = con.prepareStatement(Dashboard.gain_site);
        }if("topLooserAdvertiserByRevenueYesDayBefore".equals(dataname)){
            pstmt = con.prepareStatement(Dashboard.looser_advertisers);
        }else if("topLooserPublisherByIncomeYesDayBefore".equals(dataname)){
            pstmt = con.prepareStatement(Dashboard.looser_publisher);
        }else if("topLooserCampaignByRevenueYesDayBefore".equals(dataname)){
            pstmt = con.prepareStatement(Dashboard.looser_campaign);
        }else if("topLooserSiteByIncomeYesDayBefore".equals(dataname)){
            pstmt = con.prepareStatement(Dashboard.looser_site);
        } 
        pstmt.setString(1, dateStr1);
        pstmt.setString(2, dateStr2);
        pstmt.setInt(3, PageConstants.dashboard_table_size);
        ResultSet rset = pstmt.executeQuery();
        ArrayNode arrayNode = mapper.createArrayNode();
        while(rset.next()){
            ObjectNode dataNode = mapper.createObjectNode();
            dataNode.put(dimname, rset.getString(dimname));
            dataNode.put(metricname, rset.getDouble(metricname));
            arrayNode.add(dataNode);
        }
        objectNode.put("data", arrayNode);
        objectNode.put("column", columnChange(mapper, dimname, metricname));
        return objectNode;
    }
    public static JsonNode pie(Connection con, PreparedStatement pstmt, ObjectMapper mapper, String dateStr,
            String dataname,String metricname, String dimname ) throws Exception{
        if("topCountryByRequestYes".equals(dataname)){
            pstmt = con.prepareStatement(Dashboard.dashboard_country);
        }else if("topOsByRequestYes".equals(dataname)){
            pstmt = con.prepareStatement(Dashboard.dashboard_os);
        }else if("topManufacturerByRequestYes".equals(dataname)){
            pstmt = con.prepareStatement(Dashboard.dashboard_manufacturer);
        }else if("topBrowserByRequestYes".equals(dataname)){
            pstmt = con.prepareStatement(Dashboard.dashboard_browser);
        }
        pstmt.setString(1, dateStr);
        pstmt.setString(2, dateStr);
        pstmt.setInt(3, PageConstants.dashboard_pie_size);
        ResultSet rset = pstmt.executeQuery();
        ArrayNode arrayNode = mapper.createArrayNode();
        double total = 0.0;
        while(rset.next()){
            ObjectNode dataNode = mapper.createObjectNode();
            dataNode.put("label", rset.getString(dimname));
            double d = rset.getDouble(metricname);
            dataNode.put("value", d);
            arrayNode.add(dataNode);
            total =total + d;
        }
        ObjectNode dataNode = mapper.createObjectNode();
        dataNode.put("label", Naming.report_dashboard_others );
        dataNode.put("value", 100-total);
        arrayNode.add(dataNode);
        return arrayNode;
    }
    public static ArrayNode columnImpressionWin(ObjectMapper mapper){
        ArrayNode columnNode = mapper.createArrayNode();
        addHeader(columnNode, mapper, "time");
        addHeader(columnNode, mapper, "total_impression_name");
        addHeader(columnNode, mapper, "total_win_name");
        return columnNode;
    }
    public static ObjectNode impression_win(Connection con, PreparedStatement pstmt, ObjectMapper mapper, 
            String dateStr1,String dateStr2) throws Exception{
        ObjectNode objectNode = mapper.createObjectNode();
        pstmt = con.prepareStatement(Dashboard.impression_win);
        pstmt.setString(1, dateStr1);
        pstmt.setString(2, dateStr2);
        ResultSet rset = pstmt.executeQuery();
        ArrayNode arrayNode = mapper.createArrayNode();
        while(rset.next()){
            ObjectNode dataNode = mapper.createObjectNode();
            dataNode.put("time", rset.getString("time"));
            dataNode.put("total_impression_name", rset.getInt("total_impression_name"));
            dataNode.put("total_win_name", rset.getInt("total_win_name"));
            arrayNode.add(dataNode);
        }
        objectNode.put("data", arrayNode);
        objectNode.put("column", columnImpressionWin(mapper));
        return objectNode;
    }


}
