package com.kritter.kritterui.api.fraud;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import com.kritter.api.entity.fraud.FraudReportEntity;

public class FraudReportHelper {
    

    
    public LinkedHashMap<String, String> projectionMap = new LinkedHashMap<String, String>();
    private LinkedHashSet<String> whereSet = new LinkedHashSet<String>();
    private LinkedHashSet<String> groupBy = new LinkedHashSet<String>();
    private String table_desc = "fraud_daily as a";
    
    public String createQueryString(FraudReportEntity fraudEntity){
        whereSet.add("a.impression_time >= '" + fraudEntity.getStart_time_str() +"'");
        whereSet.add("a.impression_time <= '" + fraudEntity.getEnd_time_str() +"'");
        if(fraudEntity.isDate_as_dimension()){
            groupBy.add("a.impression_time");
            projectionMap.put("a.impression_time", "time");
        }
        if(fraudEntity.getPubId() != null){
            if(fraudEntity.getSiteId() == null){
                fraudEntity.setSiteId(new LinkedList<Integer>());
            }
        }
        if(fraudEntity.getAdvertiserId() != null){
            if(fraudEntity.getAdId() == null || fraudEntity.getCampaignId() == null){
                fraudEntity.setAdId(new LinkedList<Integer>());
            }
        }
        if(fraudEntity.getAdId() != null){
            populateLLInt("a.adId", "Ad", fraudEntity.getAdId());
            projectionMap.put("a.adId", "Ad");
        }
        if(fraudEntity.getSiteId() != null){
            populateLLInt("a.siteId", "Site", fraudEntity.getSiteId());
            projectionMap.put("a.siteId", "Site");
        }
        if(fraudEntity.getEvent() != null){
            populateLLString("a.event", "Event", fraudEntity.getEvent(), false);
            projectionMap.put("a.event", "Event");
        }
        populateLLString("a.terminationReason", "Fraud", new LinkedList<String>());
        projectionMap.put("a.terminationReason", "Fraud");
        populateNonWhere("SUM(a.count)", "Total");
        
        return createQuery(fraudEntity);
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

    public String createQuery(FraudReportEntity fraudEntity){
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
        sbuff.append(" limit ");sbuff.append(fraudEntity.getStartindex());sbuff.append(",");sbuff.append(fraudEntity.getPagesize());
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
