package com.kritter.kritterui.api.userreport;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import com.kritter.entity.userreports.UserReport;

public class UserReportHelper {
    

    
    public LinkedHashMap<String, String> projectionMap = new LinkedHashMap<String, String>();
    private LinkedHashSet<String> whereSet = new LinkedHashSet<String>();
    private String table_desc = "uu_daily as a";
    private String ad_table = "ad as b";
    private String campaign_table = "campaign as c";
    private String account_table = "account as d";
    
    public String createQueryString(UserReport userReport){
        whereSet.add("a.processing_time >= '" + userReport.getStart_time_str() +"'");
        whereSet.add("a.processing_time <= '" + userReport.getEnd_time_str() +"'");
        projectionMap.put("a.processing_time", "time");
        if(userReport.getAdvId() != null){
            populateLLInt("a.advId", "Advertiser", userReport.getAdvId());
        }
        if(userReport.getCampaignId() != null){
            populateLLInt("a.campaignId", "Campaign", userReport.getCampaignId());
        }
        if(userReport.getAdId() != null){
            populateLLInt("a.adId", "Ad", userReport.getAdId());
        }else{
            userReport.setAdId(new LinkedList<Integer>());
        }
        whereSet.add("a.adId=b.id");
        whereSet.add("a.campaignId=c.id");
        whereSet.add("a.advId = d.id");
        projectionMap.put("d.name", "Advertiser");
        projectionMap.put("c.name", "Campaign");
        projectionMap.put("b.name", "Ad");
        populateNonWhere("uu_count", "UniqueUser");
        
        return createQuery(userReport);
    }
    public void populateNonWhere(String name,String uiName){
        if(name == null || uiName == null){
            return;
        }
        projectionMap.put(name, uiName);
    }
    public String createStringFromLLInt(String name,List<Integer> ll){
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
    public void populateLLInt(String factname,String uiName, List<Integer> ll){
        if(factname == null || uiName == null || ll == null){
            return;
        }
        if(ll.size()>0){
            whereSet.add(createStringFromLLInt(factname,ll));
        }
    }
    public void populateLLString(String factname,String uiName, LinkedList<String> ll){
        if(factname == null || uiName == null || ll == null){
            return;
        }
        if(ll.size()>0){
            whereSet.add(createStringFromLLString(factname,ll));
        }
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
    }

    public String createQuery(UserReport userReport){
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
        sbuff.append(",");
        sbuff.append(ad_table);
        sbuff.append(",");
        sbuff.append(campaign_table);
        sbuff.append(",");
        sbuff.append(account_table);
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
        sbuff.append(" limit ");sbuff.append(userReport.getStartindex());sbuff.append(",");sbuff.append(userReport.getPagesize());
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
