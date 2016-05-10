package com.kritter.kritterui.api.extsitereport;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import com.kritter.api.entity.extsitereport.ExtSiteReportEntity;
import com.kritter.constants.Frequency;

public class ExtSiteReportHelper {
    

    
    public LinkedHashMap<String, String> projectionMap = new LinkedHashMap<String, String>();
    private LinkedHashSet<String> whereSet = new LinkedHashSet<String>();
    private LinkedHashSet<String> groupBy = new LinkedHashSet<String>();
    private LinkedHashSet<String> joinOn = new LinkedHashSet<String>();
    
    public String createQueryString(ExtSiteReportEntity extEntity){
        whereSet.add("a.impression_time >= '" + extEntity.getStart_time_str() +"'");
        whereSet.add("a.impression_time <= '" + extEntity.getEnd_time_str() +"'");
        if(extEntity.isDate_as_dimension()){
            groupBy.add("a.impression_time");
        }
        if(extEntity.isDate_as_dimension()){
            projectionMap.put("a.impression_time", "time");
        }
        if(extEntity.getFrequency() == Frequency.TODAY){
            joinOn.add("first_level_ext_site as a");
        }else if(extEntity.getFrequency() == Frequency.MONTHLY){
            joinOn.add("first_level_ext_site_monthly as a");
        }else {
            joinOn.add("first_level_ext_site_daily as a");
        }
        if(extEntity.getExchangeId() != null){
            joinOn.add("account as b");
            populateLLInt("a.exchangeId", "exchange", extEntity.getExchangeId(), "b.id","b.name");
        }
        if(extEntity.getExt_site() != null){
            joinOn.add("ext_supply_attr as c");
            populateLLInt("a.ext_site", "extsite", extEntity.getExt_site(), "c.id","c.ext_supply_name");
        }
        if(extEntity.getAdvertiserId() != null){
            if(extEntity.getCampaignId() == null){
                extEntity.setCampaignId(new LinkedList<Integer>());
            }
        }
        if(extEntity.getCampaignId() != null){
            joinOn.add("campaign as e");
            populateLLInt("a.campaignId", "campaign", extEntity.getCampaignId(), "e.id","e.name");
        }
        if(extEntity.getAdId() != null){
            joinOn.add("ad as d");
            populateLLInt("a.adId", "ad", extEntity.getAdId(), "d.id","d.name");
        }
        if(extEntity.getCountryId() != null){
            joinOn.add("ui_targeting_country as f");
            populateLLInt("a.countryId", "country", extEntity.getCountryId(), "f.id","f.country_name");
        }
        if(extEntity.isTotal_request()){
            populateNonWhere(createKey("SUM(a.total_request)",extEntity), "TotalRequest");
        }
        if(extEntity.isTotal_impression()){
            populateNonWhere(createKey("SUM(a.total_impression)",extEntity), "TotalImp");
        }
        if(extEntity.isTotal_csc()){
            populateNonWhere(createKey("SUM(a.total_csc)",extEntity), "TotalCSC");
        }
        if(extEntity.isBilledcsc()){
            populateNonWhere(createKey("SUM(a.billedcsc)",extEntity), "BilledCSC");
        }
        if(extEntity.isTotal_win()){
            populateNonWhere(createKey("SUM(a.total_win)",extEntity), "TotalWin");
        }
        if(extEntity.isTotal_click()){
            populateNonWhere(createKey("SUM(a.total_click)",extEntity), "TotalClick");
        }
        if(extEntity.isBilledclicks()){
            populateNonWhere(createKey("SUM(a.billedclicks)",extEntity), "BilledClicks");
        }
        if(extEntity.isConversion()){
            populateNonWhere(createKey("SUM(a.conversion)",extEntity), "Conversion");
        }
        if(extEntity.isDemandCharges()){
            populateNonWhere(createKey("SUM(a.demandCharges)",extEntity), "Revenue");
        }
        if(extEntity.isNetworkrevenue()){
            populateNonWhere(createKey("SUM(a.networkrevenue)",extEntity), "NetworkRevenue");
        }
        if(extEntity.isExchangerevenue()){
            populateNonWhere(createKey("SUM(a.exchangerevenue)",extEntity), "ExchangeRevenue");
        }
        if(extEntity.isSupplyCost()){
            populateNonWhere(createKey("SUM(a.supplyCost)",extEntity), "PubIncome");
        }
        if(extEntity.isNetworkpayout()){
            populateNonWhere(createKey("SUM(a.networkpayout)",extEntity), "NetworkIncome");
        }
        if(extEntity.isExchangepayout()){
            populateNonWhere(createKey("SUM(a.exchangepayout)",extEntity), "ExchangeIncome");
        }
        if(extEntity.isCpa_goal()){
            populateNonWhere(createKey("SUM(a.cpa_goal)",extEntity), "CPARevenue");
        }
        if(extEntity.isTotal_bidValue()){
            populateNonWhere(createKey("SUM(a.total_bidValue)",extEntity), "TotalBidValue");
        }
        if(extEntity.isTotal_win_bidValue()){
            populateNonWhere(createKey("SUM(a.total_win_bidValue)",extEntity), "TotalWinBidValue");
        }
        return createQuery(extEntity);
    }
    public String createKey(String input, ExtSiteReportEntity extsite){
        if(extsite.isRoundoffmetric()){
            return "ROUND("+input+","+extsite.getRoundoffmetriclength()+")";
        }else{
            return input;
        }
    }
    public void populateNonWhere(String name,String uiName){
        if(name == null || uiName == null){
            return;
        }
        projectionMap.put(name, uiName);
    }
    public String createStringFromLLInt(String name,LinkedList<Integer> ll){
        if(ll.size() < 1){
            return "";
        }
        StringBuffer sbuff=new StringBuffer();
        boolean isFirst=true;
        for(Integer i:ll){
            if(!isFirst){
                sbuff.append(",");
            }else{
                sbuff.append(name);
                sbuff.append(" in (");
                isFirst = false;
            }
            sbuff.append(i);
        }
        sbuff.append(")");
        return sbuff.toString();
    }
    public void populateLLInt(String factname,String uiName, LinkedList<Integer> ll,String dimension_field,String dim_ui_col){
        if(factname == null || uiName == null || ll == null){
            return;
        }
        projectionMap.put(dim_ui_col, uiName);
        if(ll.size()>0){
            whereSet.add(createStringFromLLInt(factname,ll));
        }
        whereSet.add(factname+"="+dimension_field);
        groupBy.add(factname);
    }

    public String createQuery(ExtSiteReportEntity extEntity){
        StringBuffer sbuff = new StringBuffer("select ");
        boolean isFirst=true;
        for(String key : projectionMap.keySet()){
            if(isFirst){
                isFirst=false;
            }else{
                sbuff.append(",");
            }
            sbuff.append(key); sbuff.append(" as ");sbuff.append(projectionMap.get(key));
        }
        isFirst=true;
        for(String joinName:joinOn){
            if(isFirst){
                isFirst=false;
                sbuff.append(" from ");
            }else{
                sbuff.append(", ");
            }
            sbuff.append(joinName);
        }
        isFirst=true;
        for(String key : whereSet){
            if(isFirst){
                sbuff.append(" where ");
                isFirst=false;
            }else{
                sbuff.append(" and ");
            }
            sbuff.append(key);
        }
        isFirst=true;
        for(String key : groupBy){
            if(isFirst){
                sbuff.append(" group by ");
                isFirst=false;
            }else{
                sbuff.append(", ");
            }
            sbuff.append(key);
        }
        sbuff.append(" limit ");sbuff.append(extEntity.getStartindex());sbuff.append(",");sbuff.append(extEntity.getPagesize());
        return sbuff.toString();
    }
    public ArrayNode createColumnNode(ObjectMapper mapper){
        ArrayNode columnNode = mapper.createArrayNode();
        for(String key: projectionMap.keySet()){
            ObjectNode columnObjNode = mapper.createObjectNode();
            columnObjNode.put("title", projectionMap.get(key));
            columnObjNode.put("field", projectionMap.get(key));
            columnObjNode.put("visible", true);
            columnNode.add(columnObjNode);
        }
        return columnNode;
    }
    public String creatHeadereCsv(String delimiter){
        StringBuffer sbuff = new StringBuffer("");
        boolean isFirst = true;
        for(String key: projectionMap.keySet()){
            if(isFirst){
                isFirst = false;
            }else{
                sbuff.append(delimiter);
            }
            sbuff.append(projectionMap.get(key));
        }
        return sbuff.toString();
    }
}
