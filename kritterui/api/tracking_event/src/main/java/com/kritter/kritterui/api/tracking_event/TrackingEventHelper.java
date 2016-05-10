package com.kritter.kritterui.api.tracking_event;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import com.kritter.api.entity.tracking_event.TrackingEvent;
import com.kritter.constants.Frequency;


public class TrackingEventHelper {
    

    
    public LinkedHashMap<String, String> projectionMap = new LinkedHashMap<String, String>();
    private LinkedHashSet<String> whereSet = new LinkedHashSet<String>();
    private LinkedHashSet<String> groupBy = new LinkedHashSet<String>();
    private String table_desc_hourly = "tracking_hourly as a";
    private String table_desc_daily = "tracking_daily as a";
    private String table_desc_monthly = "tracking_monthly as a";
    
    public String createQueryString(TrackingEvent trackingEvent){
        whereSet.add("a.impression_time >= '" + trackingEvent.getStart_time_str() +"'");
        whereSet.add("a.impression_time <= '" + trackingEvent.getEnd_time_str() +"'");
        if(trackingEvent.isDate_as_dimension()){
            groupBy.add("a.impression_time");
            projectionMap.put("a.impression_time", "time");
        }
        if(trackingEvent.getPubId() != null){
            populateLLInt("a.pubId", "Pub", trackingEvent.getPubId());
            projectionMap.put("a.pubId", "Pub");
        }
        if(trackingEvent.getSiteId() != null){
            populateLLInt("a.siteId", "Site", trackingEvent.getSiteId());
            projectionMap.put("a.siteId", "Site");
        }
        if(trackingEvent.getExtsiteId() != null){
            populateLLInt("a.extsiteId", "ExtSite", trackingEvent.getExtsiteId());
            projectionMap.put("a.extsiteId", "ExtSite");
        }
        if(trackingEvent.getAdvId() != null){
            populateLLInt("a.advId", "Adv", trackingEvent.getAdvId());
            projectionMap.put("a.advId", "Adv");
        }
        if(trackingEvent.getCampaignId() != null){
            populateLLInt("a.campaignId", "Campaign", trackingEvent.getCampaignId());
            projectionMap.put("a.campaignId", "Campaign");
        }
        if(trackingEvent.getAdId() != null){
            populateLLInt("a.adId", "Ad", trackingEvent.getAdId());
            projectionMap.put("a.adId", "Ad");
        }
        if(trackingEvent.getCountryId() != null){
            populateLLInt("a.countryId", "Country", trackingEvent.getCountryId());
            projectionMap.put("a.countryId", "Country");
        }
        if(trackingEvent.getCountryCarrierId() != null){
            populateLLInt("a.countryCarrierId", "Carrier", trackingEvent.getCountryCarrierId());
            projectionMap.put("a.countryCarrierId", "Carrier");
        }
        if(trackingEvent.getDeviceOsId() != null){
            populateLLInt("a.deviceOsId", "Os", trackingEvent.getDeviceOsId());
            projectionMap.put("a.deviceOsId", "Os");
        }
        if(trackingEvent.getDeviceManufacturerId() != null){
            populateLLInt("a.deviceManufacturerId", "Brand", trackingEvent.getDeviceManufacturerId());
            projectionMap.put("a.deviceManufacturerId", "Brand");
        }
        if(trackingEvent.isTerminationReason()){
            populateLLInt("a.terminationReason", "TerminationReason", new LinkedList<Integer>());
            projectionMap.put("a.terminationReason", "TerminationReason");
        }
        if(trackingEvent.isTevent()){
            populateLLInt("a.tevent", "Tevent", new LinkedList<Integer>());
            projectionMap.put("a.tevent", "Tevent");
        }
        if(trackingEvent.isTeventtype()){
            populateLLInt("a.teventtype", "TeventType", new LinkedList<Integer>());
            projectionMap.put("a.teventtype", "TeventType");
        }
        populateNonWhere("SUM(a.total_event)", "Count");
        
        return createQuery(trackingEvent);
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
            sbuff.append("'");
            sbuff.append(i);
            sbuff.append("'");
        }
        sbuff.append(")");
        return sbuff.toString();
    }
    public String createStringFromLLString(String name,List<String> ll, boolean addQuote){
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
            if(addQuote){
                sbuff.append("'");
            }
            sbuff.append(i);
            if(addQuote){
                sbuff.append("'");
            }
        }
        sbuff.append(")");
        return sbuff.toString();
    }
    public void populateLLInt(String factname,String uiName, LinkedList<Integer> ll){
        if(factname == null || uiName == null || ll == null){
            return;
        }
        if(ll.size()>0){
            whereSet.add(createStringFromLLInt(factname,ll));
        }
        groupBy.add(factname);
    }
    public void populateLLString(String factname,String uiName, LinkedList<String> ll){
        if(factname == null || uiName == null || ll == null){
            return;
        }
        if(ll.size()>0){
            whereSet.add(createStringFromLLString(factname,ll));
        }
        groupBy.add(factname);
    }
    public void populateLLString(String factname,String uiName, List<String> ll){
        populateLLString(factname, uiName, ll,true);
    }
    public void populateLLString(String factname,String uiName, List<String> ll,boolean addQuote){
        if(factname == null || uiName == null || ll == null){
            return;
        }
        if(ll.size()>0){
            whereSet.add(createStringFromLLString(factname,ll,addQuote));
        }
        groupBy.add(factname);
    }

    public String createQuery(TrackingEvent trackingEvent){
        Frequency freq = trackingEvent.getFrequency();
        String table_desc = table_desc_daily;
        if(freq==Frequency.ADMIN_INTERNAL_HOURLY || freq ==Frequency.TODAY){
            table_desc = table_desc_hourly;
        }
        if(freq==Frequency.MONTHLY){
            table_desc = table_desc_monthly;
        }
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
        sbuff.append(" from "+table_desc);
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
        sbuff.append(" limit ");sbuff.append(trackingEvent.getStartindex());sbuff.append(",");sbuff.append(trackingEvent.getPagesize());
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
