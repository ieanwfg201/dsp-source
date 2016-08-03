package com.kritter.kritterui.api.logevent;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import com.kritter.api.entity.log.LogEntity;


public class LogEventHelper {
    

    
    public LinkedHashMap<String, String> projectionMap = new LinkedHashMap<String, String>();
    private LinkedHashSet<String> whereSet = new LinkedHashSet<String>();
    
    public String createQueryString(LogEntity logEntity){
        whereSet.add("time >= '" + logEntity.getStart_time_str() +"'");
        whereSet.add("time <= '" + logEntity.getEnd_time_str() +"'");
        if(logEntity.isProcessing_time()){
            populateNonWhere("processing_time", "processing_time");
        }
        if(logEntity.isTime()){
            populateNonWhere("time", "time");
        }
        if(logEntity.isEventTime()){
            populateNonWhere("eventTime", "eventTime");
        }
        if(logEntity.getStatus() != null){
            populateLLString("status", "status", logEntity.getStatus() );
        }
        if(logEntity.getEvent() != null){
            populateLLString("event", "event", logEntity.getEvent() );
        }
        if(logEntity.getCampaignId() != null){
            populateLLInt("campaignId", "campaignId", logEntity.getCampaignId());
        }
        if(logEntity.getAdId() != null){
            populateLLInt("adId", "adId", logEntity.getAdId());
        }
        if(logEntity.getMarketplace_id() != null){
            populateLLInt("marketplace_id", "marketplace_id", logEntity.getMarketplace_id());
        }
        if(logEntity.getExchangeId() != null){
            populateLLInt("exchangeId", "exchangeId", logEntity.getExchangeId());
        }
        if(logEntity.getSiteId() != null){
            populateLLInt("siteId", "siteId", logEntity.getSiteId());
        }
        if(logEntity.getExt_supply_attr_internal_id() != null){
            populateLLInt("ext_supply_attr_internal_id", "ext_supply_attr_internal_id", logEntity.getExt_supply_attr_internal_id());
        }
        if(logEntity.getInventorySource() != null){
            populateLLInt("inventorySource", "inventorySource", logEntity.getInventorySource());
        }
        if(logEntity.getSupply_source_type() != null){
            populateLLInt("supply_source_type", "supply_source_type", logEntity.getSupply_source_type());
        }
        if(logEntity.isAdvertiser_bid()){
            populateNonWhere("advertiser_bid", "advertiser_bid");
        }
        if(logEntity.isInternal_max_bid()){
            populateNonWhere("internal_max_bid", "internal_max_bid");
        }
        if(logEntity.isBidprice_to_exchange()){
            populateNonWhere("bidprice_to_exchange", "bidprice_to_exchange");
        }
        if(logEntity.isCpa_goal()){
            populateNonWhere("cpa_goal", "cpa_goal");
        }
        if(logEntity.getCountryId() != null){
            populateLLInt("countryId", "countryId", logEntity.getCountryId());
        }
        if(logEntity.getCountryCarrierId() != null){
            populateLLInt("countryCarrierId", "countryCarrierId", logEntity.getCountryCarrierId());
        }
        if(logEntity.isDeviceId()){
            populateNonWhere("deviceId", "deviceId");
        }
        if(logEntity.getDeviceManufacturerId() != null){
            populateLLInt("deviceManufacturerId", "deviceManufacturerId", logEntity.getDeviceManufacturerId());
        }
        if(logEntity.getDeviceModelId() != null){
            populateLLInt("deviceModelId", "deviceModelId", logEntity.getDeviceModelId());
        }
        if(logEntity.getDeviceOsId() != null){
            populateLLInt("deviceOsId", "deviceOsId", logEntity.getDeviceOsId());
        }
        if(logEntity.getDeviceBrowserId() != null){
            populateLLInt("deviceBrowserId", "deviceBrowserId", logEntity.getDeviceBrowserId());
        }
        if(logEntity.isIpAddress()){
            populateNonWhere("ipAddress", "ipAddress");
        }
        if(logEntity.isUserAgent()){
            populateNonWhere("userAgent", "userAgent");
        }
        if(logEntity.isxForwardedFor()){
            populateNonWhere("xForwardedFor", "xForwardedFor");
        }
        if(logEntity.isUrlExtraParameters()){
            populateNonWhere("urlExtraParameters", "urlExtraParameters");
        }
        if(logEntity.isReferer()){
            populateNonWhere("referer", "referer");
        }
        if(logEntity.isVersion()){
            populateNonWhere("version", "version");
        }
        if(logEntity.isRequestId()){
            populateNonWhere("requestId", "requestId");
        }
        if(logEntity.isSrcRequestId()){
            populateNonWhere("srcRequestId", "srcRequestId");
        }
        if(logEntity.isImpressionId()){
            populateNonWhere("impressionId", "impressionId");
        }
        if(logEntity.isSrcUrlHash()){
            populateNonWhere("srcUrlHash", "srcUrlHash");
        }
        if(logEntity.isAliasUrlId()){
            populateNonWhere("aliasUrlId", "aliasUrlId");
        }
        if(logEntity.isThirdPartyTrackingUrlEventId()){
            populateNonWhere("thirdPartyTrackingUrlEventId", "thirdPartyTrackingUrlEventId");
        }
        if(logEntity.isThirdPartyId()){
            populateNonWhere("thirdPartyId", "thirdPartyId");
        }
        if(logEntity.isThirdPartyUrlHash()){
            populateNonWhere("thirdPartyUrlHash", "thirdPartyUrlHash");
        }
        if(logEntity.isAuction_price()){
            populateNonWhere("auction_price", "auction_price");
        }
        if(logEntity.isAuction_currency()){
            populateNonWhere("auction_currency", "auction_currency");
        }
        if(logEntity.isAuction_seat_id()){
            populateNonWhere("auction_seat_id", "auction_seat_id");
        }
        if(logEntity.isAuction_bid_id()){
            populateNonWhere("auction_bid_id", "auction_bid_id");
        }
        if(logEntity.isAuction_id()){
            populateNonWhere("auction_id", "auction_id");
        }
        if(logEntity.isAuction_imp_id()){
            populateNonWhere("auction_imp_id", "auction_imp_id");
        }
        if(logEntity.getCountryRegionId() != null){
            populateLLInt("countryRegionId", "countryRegionId", logEntity.getCountryRegionId());
        }
        if(logEntity.getSelectedSiteCategoryId() != null){
            populateLLInt("selectedSiteCategoryId", "selectedSiteCategoryId", logEntity.getSelectedSiteCategoryId());
        }
        if(logEntity.isUrlVersion()){
            populateNonWhere("urlVersion", "urlVersion");
        }
        if(logEntity.isBidderModelId()){
            populateNonWhere("bidderModelId", "bidderModelId");
        }
        if(logEntity.getSlotId() != null){
            populateLLInt("slotId", "slotId", logEntity.getSlotId());
        }
        if(logEntity.isBuyerUid()){
            populateNonWhere("buyerUid", "buyerUid");
        }
        if(logEntity.isConnectionTypeId()){
            populateNonWhere("connectionTypeId", "connectionTypeId");
        }
        if(logEntity.isAdv_inc_id()){
            populateNonWhere("adv_inc_id", "adv_inc_id");
        }
        if(logEntity.isPub_inc_id()){
            populateNonWhere("pub_inc_id", "pub_inc_id");
        }
        if(logEntity.isTevent()){
            populateNonWhere("tevent", "tevent");
        }
        if(logEntity.isTeventtype()){
            populateNonWhere("teventtype", "teventtype");
        }
        if(logEntity.isDeviceType()){
            populateNonWhere("deviceType", "deviceType");
        }
        if(logEntity.isBidFloor()){
            populateNonWhere("bidFloor", "bidFloor");
        }
        if(logEntity.isExchangeUserId()){
            populateNonWhere("exchangeUserId", "exchangeUserId");
        }
        if(logEntity.isKritterUserId()){
            populateNonWhere("kritterUserId", "kritterUserId");
        }
        if(logEntity.isExternalSiteAppId()){
            populateNonWhere("externalSiteAppId", "externalSiteAppId");
        }
        return createQuery(logEntity);
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
    public String createStringFromLLString(String name,LinkedList<String> ll){
        if(ll.size() < 1){
            return "";
        }
        StringBuffer sbuff=new StringBuffer();
        boolean isFirst=true;
        for(String i:ll){
            if(!isFirst){
                sbuff.append(",");
            }else{
                sbuff.append(name);
                sbuff.append(" in (");
                isFirst = false;
            }
            sbuff.append("'");sbuff.append(i);sbuff.append("'");
        }
        sbuff.append(")");
        return sbuff.toString();
    }
    public void populateLLInt(String name,String uiName, LinkedList<Integer> ll){
        if(name == null || uiName == null || ll == null){
            return;
        }
        projectionMap.put(name, uiName);
        if(ll.size()>0){
            whereSet.add(createStringFromLLInt(name,ll));
        }
    }
    public void populateLLString(String name,String uiName, LinkedList<String> ll){
        if(name == null || uiName == null || ll == null){
            return;
        }
        projectionMap.put(name, uiName);
        if(ll.size()>0){
            whereSet.add(createStringFromLLString(name,ll));
        }
    }

    public String createQuery(LogEntity logEntity){
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
        sbuff.append(" from logevents ");
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
        sbuff.append(" limit ");sbuff.append(logEntity.getStartindex());sbuff.append(",");sbuff.append(logEntity.getPagesize());
        return sbuff.toString();
    }
    public ArrayNode createColumnNode(ObjectMapper mapper){
        ArrayNode columnNode = mapper.createArrayNode();
        for(String key: projectionMap.keySet()){
            ObjectNode columnObjNode = mapper.createObjectNode();
            columnObjNode.put("title", projectionMap.get(key));
            columnObjNode.put("field", key);
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
