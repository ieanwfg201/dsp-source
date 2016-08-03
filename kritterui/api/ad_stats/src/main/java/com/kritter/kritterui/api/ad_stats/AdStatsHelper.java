package com.kritter.kritterui.api.ad_stats;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import com.kritter.constants.Frequency;
import com.kritter.entity.ad_stats.AdStats;

public class AdStatsHelper {
    

    
    public LinkedHashMap<String, String> projectionMap = new LinkedHashMap<String, String>();
    private LinkedHashSet<String> whereSet = new LinkedHashSet<String>();
    private LinkedHashSet<String> groupBy = new LinkedHashSet<String>();
    private LinkedHashSet<String> joinOn = new LinkedHashSet<String>();
    
    public String createQueryString(AdStats adStats){
        whereSet.add("a.request_time >= '" + adStats.getStart_time_str() +"'");
        whereSet.add("a.request_time <= '" + adStats.getEnd_time_str() +"'");
        if(adStats.isDate_as_dimension()){
            groupBy.add("a.request_time");
        }
        if(adStats.isDate_as_dimension()){
            projectionMap.put("a.request_time", "time");
        }
        if(adStats.getFrequency() == Frequency.TODAY){
            joinOn.add("ad_no_fill_reason as a");
        }else {
            joinOn.add("ad_no_fill_reason_daily as a");
        }
        
        if(adStats.getAdId() != null){
            joinOn.add("ad as b");
            populateLLInt("a.ad_id", "ad", adStats.getAdId(), "b.id","b.name");
        }
        if(adStats.isCount()){
            populateNonWhere("SUM(a.count)", "count");
        }
        if(adStats.isNo_fill_reason_name()){
        	populateNonWhere("a.no_fill_reason_name", "nfr");
        	groupBy.add("a.no_fill_reason_name");
        }
        return createQuery(adStats);
    }
    public void populateNonWhere(String name,String uiName){
        if(name == null || uiName == null){
            return;
        }
        projectionMap.put(name, uiName);
    }
    public String createStringFromLLInt(String name, List<Integer> ll){
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
    public void populateLLInt(String factname,String uiName, List<Integer> ll,String dimension_field,String dim_ui_col){
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

    public String createQuery(AdStats adStats){
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
        sbuff.append(" limit ");sbuff.append(adStats.getStartindex());sbuff.append(",");sbuff.append(adStats.getPagesize());
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
