package com.kritter.kritterui.api.targeting_profile;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kritter.api.entity.response.msg.Message;
import com.kritter.api.entity.targeting_profile.TargetingProfileList;
import com.kritter.api.entity.targeting_profile.TargetingProfileListEntity;
import com.kritter.api.entity.targeting_profile.Targeting_profile;
import com.kritter.constants.CategoryTier;
import com.kritter.constants.Geo_Targeting_type;
import com.kritter.constants.MidpValue;
import com.kritter.constants.StatusIdEnum;
import com.kritter.constants.SupplySourceEnum;
import com.kritter.constants.SupplySourceTypeEnum;
import com.kritter.constants.error.ErrorEnum;
import com.kritter.entity.targeting_profile.column.Retargeting;
import com.kritter.entity.targeting_profile.column.TPExt;
import com.kritter.kritterui.api.db_query_def.Targeting_Profile;
import com.kritter.utils.uuid.mac.SingletonUUIDGenerator;

public class TargetingProfileCrud {
    
    private static final Logger LOG = LoggerFactory.getLogger(TargetingProfileCrud.class);
    
    private static String new_format_direct_supply_source = "{}";
    private static String new_format_exchange_supply_source = "{}";
    

    public static Targeting_profile getTargetingProfileUseRawCountryJson(String guid,Connection connection)
    {
        PreparedStatement pstmt = null;
        Targeting_profile targeting_profile = null;

        try {
            pstmt = connection.prepareStatement(com.kritter.kritterui.api.db_query_def.Targeting_Profile.get_targeting_profile);
            pstmt.setString(1,guid);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next())
            {
                targeting_profile = new Targeting_profile();
                populateWithRawCountryJson(targeting_profile,rs,true);
                return targeting_profile;
            }
        }
        catch(Exception e){
            LOG.error(e.getMessage(),e);
            return null;
        }finally{
            if(pstmt != null){
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    LOG.error(e.getMessage(),e);
                }
            }
        }

        return null;
    }

    public static String create_country_entity_list_from_country_ids(String country_code_list, Connection con){
        if(country_code_list != null && con != null && !country_code_list.equals("[]") && !country_code_list.equals("{}")){
            String tmp_country_code_list = country_code_list.replaceAll("\\[", "").replaceAll("]", "");
            PreparedStatement pstmt = null;
            try{
                pstmt = con.prepareStatement(Targeting_Profile.convert_country_id_to_entity.replaceAll("<id>", tmp_country_code_list));
                ResultSet rset = pstmt.executeQuery();
                StringBuffer sBuff = new StringBuffer("");
                boolean isFirst = true;
                sBuff.append("{");
                while(rset.next()){
                    if(isFirst){
                        isFirst = false;
                    }else{
                        sBuff.append(",");
                    }
                    sBuff.append("\"");
                    sBuff.append(rset.getInt("id"));
                    sBuff.append("\":");
                    sBuff.append(rset.getString("entity_id_set"));
                }
                sBuff.append("}");
                return sBuff.toString();
            }catch(Exception e){
                LOG.error(e.getMessage(),e);
                return "{}";
            }finally{
                if(pstmt != null){
                    try {
                        pstmt.close();
                    } catch (SQLException e) {
                        LOG.error(e.getMessage(),e);
                    }
                }
            }
        }
        return "{}";
    }
    public static String create_isp_entity_list_from_ids(String is_ui_name_list, Connection con){
        if(is_ui_name_list != null && con != null && !is_ui_name_list.equals("[]") && !is_ui_name_list.equals("{}")){
            String tmp_is_ui_name_list = is_ui_name_list.replaceAll("\\[", "").replaceAll("]", "");
            PreparedStatement pstmt = null;
            try{
                pstmt = con.prepareStatement(Targeting_Profile.convert_isp_ids_to_entity.replaceAll("<id>", tmp_is_ui_name_list));
                ResultSet rset = pstmt.executeQuery();
                StringBuffer sBuff = new StringBuffer("{");
                boolean isFirst = true;
                while(rset.next()){
                    if(isFirst){
                        isFirst = false;
                    }else{
                        sBuff.append(",");
                    }
                    sBuff.append("\"");
                    sBuff.append(rset.getInt("id"));
                    sBuff.append("\":");
                    sBuff.append(rset.getString("entity_id_set"));
                }
                sBuff.append("}");
                return sBuff.toString();
            }catch(Exception e){
                LOG.error(e.getMessage(),e);
                return "{}";
            }finally{
                if(pstmt != null){
                    try {
                        pstmt.close();
                    } catch (SQLException e) {
                        LOG.error(e.getMessage(),e);
                    }
                }
            }
        }
        return "{}";
    }
    private static String generateLocationJson_from_id_list(String strIn){
        if(strIn != null ){
            String str = strIn.trim().replaceAll("\\[", "").replaceAll("]", "");
            if("".equals(str) || "[]".equals(str) || "[all]".equalsIgnoreCase(str)
            		|| "[none]".equalsIgnoreCase(str)){
            	return "{}";
            }
            StringBuffer sBuff = new StringBuffer("{");
            boolean isFirst=true;
            String split[] = str.split(",");
            for(String s:split){
            	String sTrim=s.trim();
            	if(!"".equals(sTrim)){
            		try{
            			Integer i = Integer.parseInt(sTrim);
            			if(isFirst){
            				isFirst=false;
            			}else{
            				sBuff.append(",");
            			}
            			sBuff.append("\"");
            			sBuff.append(i);
            			sBuff.append("\":[]");
            		}catch(Exception e){
            			LOG.error(e.getMessage(),e);
            		}
            	}
            }
            sBuff.append("}");
            return sBuff.toString();
        }
        return "{}";
    }

    public static String create_id_list(String inputStr){
        if(inputStr != null && !inputStr.trim().equals("")){
            StringBuffer sbuff = new StringBuffer("[");
            ObjectMapper mapper = new ObjectMapper();
            try {
                JsonNode node = mapper.readValue(inputStr,JsonNode.class);
                Iterator<String> itr = node.getFieldNames();
                boolean isFirst = true;
                while(itr.hasNext()){
                    String childnode = itr.next();
                    if(isFirst){
                        isFirst = false;
                    }else{
                        sbuff.append(",");
                    }
                    sbuff.append(childnode);
                }
                
            } catch (Exception e) {
                LOG.error(e.getMessage(),e);
            }
            sbuff.append("]");
            return sbuff.toString();
        }
        return "[]";
    }
    private static String ui_to_db_file_set(String incoming){
        if(incoming == null){ 
            return "[]";
        }
        String incomingTrim = incoming.trim();
        if(incomingTrim.equals("") || incomingTrim.equals("[]")){
            return "[]";
        }
        
        String incomingSplit[] = incomingTrim.split(",");
        StringBuffer sbuff = new StringBuffer("[");
        boolean isFirst = true;
        for(String str:incomingSplit){
            if(isFirst){
                isFirst=false;
            }else{
                sbuff.append(",");
            }
            sbuff.append("\"");
            sbuff.append(str);
            sbuff.append("\"");
        }
        sbuff.append("]");
        return sbuff.toString();
    }
    private static String db_to_ui_file_set(String outgoing){
        if(outgoing == null){
            return "";
        }
        String outgoingTrim = outgoing.trim();
        if("".equals(outgoingTrim)){
        	return "";
        }
        return outgoingTrim.replaceAll("\\[", "").replaceAll("]", "").replaceAll("\"", "");
    }
    private static void lat_lon_r(Targeting_profile tp, String str){
        if(tp == null || str == null ){
            return;
        }
        String str_trim = str.trim();
        if("".equals(str_trim) || "[]".equals(str_trim)){
            return;
        }
        ObjectMapper mapper = new ObjectMapper();
        try {
            ArrayNode node = mapper.readValue(str,ArrayNode.class);
            StringBuffer sBuff = new StringBuffer("");
            for(int i =0 ;i<node.size();i++){
                JsonNode jsonNode = node.get(i);
                double lat = 0.0;
                double lon = 0.0;
                double r = 0.0;
                if(i!=0){
                    sBuff.append("\n");
                }
                lat = jsonNode.path("lat").getDoubleValue();
                lon = jsonNode.path("lon").getDoubleValue();
                r = jsonNode.path("r").getDoubleValue();
                sBuff.append(lat);sBuff.append(",");sBuff.append(lon);sBuff.append(",");sBuff.append(r);
            }
            tp.setLat_long(sBuff.toString());
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        } 
    }
    private static String generateDirectSites(Targeting_profile tp){
        StringBuffer new_format_sbuff = new StringBuffer("");
        if(tp == null){
            new_format_direct_supply_source ="{}";
            return "{}";
        }
/*        || tp.getSite_list() ==null || "".equals(tp.getSite_list())
                || "[]".equals(tp.getSite_list()) || "[\"all\"]".equalsIgnoreCase(tp.getSite_list())){
            
        }*/
        if(tp.getSupply_source() == SupplySourceEnum.EXCHANGE){
            new_format_direct_supply_source ="{}";
            return "{}";
        }
        StringBuffer sbuff = new StringBuffer("");
        boolean isFirst = true;
        HashMap<String, LinkedList<String>> keyval = new HashMap<String, LinkedList<String>>();
        if(tp.getSite_list() != null && !"".equals(tp.getSite_list())
                && !"[]".equals(tp.getSite_list()) && !"[\"all\"]".equalsIgnoreCase(tp.getSite_list())){
            String incoming = tp.getSite_list().replaceAll("\\[", "").replaceAll("]", "").replaceAll("\"", ""); 
            String[] firstLevelSplit = incoming.split(",");
            for(String firstLevel:firstLevelSplit){
                String[] secondLevelSplit = firstLevel.split("\\|");
                if(secondLevelSplit.length>1){
                    if(keyval.get(secondLevelSplit[0]) == null){
                        LinkedList<String> ll = new LinkedList<String>();
                        ll.add(secondLevelSplit[1]);
                        keyval.put(secondLevelSplit[0], ll);
                    }else{
                        keyval.get(secondLevelSplit[0]).add(secondLevelSplit[1]);
                    }
                }
            }
        }
        if(tp.getPub_list() != null && !"".equals(tp.getPub_list())
                && !"[]".equals(tp.getPub_list()) && !"[\"all\"]".equalsIgnoreCase(tp.getPub_list())){
            String incomingPub = tp.getPub_list().replaceAll("\\[", "").replaceAll("]", "").replaceAll("\"", ""); 
            String[] pubFirstLevelSplit = incomingPub.split(",");
            for(String firstLevel:pubFirstLevelSplit){
                if(!"".equals(firstLevel) && keyval.get(firstLevel) == null){
                    keyval.put(firstLevel, new LinkedList<String>());
                }
            }
        }
        
        for(String key:keyval.keySet()){
            if(isFirst){
                isFirst=false;
                sbuff.append("{");
                new_format_sbuff.append("{");
            }else{
                sbuff.append(",");
                new_format_sbuff.append(",");
            }
            sbuff.append("\""+key+"\":[");
            new_format_sbuff.append("\""+key+"\":{");
            LinkedList<String> ll = keyval.get(key);
            boolean llisFirst=true;
            for(String val:ll){
                if(llisFirst){
                    llisFirst=false;
                }else{
                    new_format_sbuff.append(",");
                    sbuff.append(",");
                }
                new_format_sbuff.append("\""+val+"\":[]");
                sbuff.append(val);
            }
            new_format_sbuff.append("}");
            sbuff.append("]");
        }
        if(!isFirst){
            new_format_sbuff.append("}");
            sbuff.append("}");
        }
        new_format_direct_supply_source= new_format_sbuff.toString();
        return sbuff.toString();
    }
    private static String generateExtSupplyAttr(Targeting_profile tp){
        if(tp == null){
            return "";
        }
        /*if(tp == null || tp.getExt_supply_attributes() ==null || "".equals(tp.getExt_supply_attributes())
                || "[]".equals(tp.getExt_supply_attributes()) || "[\"all\"]".equalsIgnoreCase(tp.getExt_supply_attributes())){
            return "";
        }*/
        if(tp.getSupply_source() == SupplySourceEnum.NETWORK){
            return "{}";
        }
        HashMap<String, HashMap<String, LinkedList<HashMap<String, Integer>>>> keyval = new HashMap<String, HashMap<String,LinkedList<HashMap<String, Integer>>>>();
        if(tp.getExt_supply_attributes() !=null &&  !"".equals(tp.getExt_supply_attributes())
                && !"[]".equals(tp.getExt_supply_attributes()) && !"[\"all\"]".equalsIgnoreCase(tp.getExt_supply_attributes())){
           
            String incoming = tp.getExt_supply_attributes().replaceAll("\\[", "").replaceAll("]", "").replaceAll("\"", ""); 
            String[] firstLevelSplit = incoming.split(",");
            for(String firstLevel:firstLevelSplit){
                String[] secondLevelSplit = firstLevel.split("\\|");
                if(secondLevelSplit.length>2){
                    if(keyval.get(secondLevelSplit[0]) == null){
                        HashMap<String, Integer> lowest = new HashMap<String, Integer>();
                        lowest.put("intid", Integer.parseInt(secondLevelSplit[2]));
                        LinkedList<HashMap<String, Integer>> ll = new LinkedList<HashMap<String, Integer>>();
                        ll.add(lowest);
                        HashMap<String, LinkedList<HashMap<String, Integer>>> hm = new HashMap<String, LinkedList<HashMap<String, Integer>>>();
                        hm.put(secondLevelSplit[1], ll);
                        keyval.put(secondLevelSplit[0], hm);
                    }else {
                        if(keyval.get(secondLevelSplit[0]).get(secondLevelSplit[1]) == null){
                            HashMap<String, LinkedList<HashMap<String, Integer>>> hm = keyval.get(secondLevelSplit[0]);
                            HashMap<String, Integer> lowest = new HashMap<String, Integer>();
                            lowest.put("intid", Integer.parseInt(secondLevelSplit[2]));
                            LinkedList<HashMap<String, Integer>> ll = new LinkedList<HashMap<String, Integer>>();
                            ll.add(lowest);
                            hm.put(secondLevelSplit[1], ll);
                        }else{
                            HashMap<String, Integer> lowest = new HashMap<String, Integer>();
                            lowest.put("intid", Integer.parseInt(secondLevelSplit[2]));
                            keyval.get(secondLevelSplit[0]).get(secondLevelSplit[1]).add(lowest);
                        }
                    }
                }
            }
        }
        if(tp.getExchange_list() !=null &&  !"".equals(tp.getExchange_list())
                && !"[]".equals(tp.getExchange_list()) && !"[\"all\"]".equalsIgnoreCase(tp.getExchange_list())){
            String incoming = tp.getExchange_list().replaceAll("\\[", "").replaceAll("]", "").replaceAll("\"", ""); 
            String[] firstLevelSplit = incoming.split(",");
            for(String firstLevel:firstLevelSplit){
                if(!"".equals(firstLevel) && keyval.get(firstLevel)==null){
                    HashMap<String, LinkedList<HashMap<String, Integer>>> hm = new HashMap<String, LinkedList<HashMap<String, Integer>>>();
                    keyval.put(firstLevel, hm);
                }
            }
        }
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.valueToTree(keyval);
        new_format_exchange_supply_source = jsonNode.toString();
        return new_format_exchange_supply_source;
    }
    private static String generateSupply_inc_exc(Targeting_profile tp){
        if(tp == null){
            return "{}";
        }
        return generateSupply_inc_exc(tp.getDirect_supply_inc_exc(), tp.getExchange_supply_inc_exc());
    }
    private static String generateSupply_inc_exc(String directSupply, String exchangeSupply){
        String directSupplyTmp = null;
        if(directSupply != null){
            directSupplyTmp = directSupply.trim();
        }
        String exchangeSupplyTmp = null;
        if(exchangeSupply != null){
            exchangeSupplyTmp = exchangeSupply.trim();
        }
        if((directSupplyTmp == null || "".equals(directSupplyTmp) || "[]".equals(directSupplyTmp)) 
                && 
                (exchangeSupplyTmp == null || "".equals(exchangeSupplyTmp) || "[]".equals(exchangeSupplyTmp))){
            return "{}";
        }
        if(directSupplyTmp == null || "".equals(directSupplyTmp) || "[]".equals(directSupplyTmp)){
            return "{\"2\":"+exchangeSupply+"}";
        }
        if(exchangeSupplyTmp == null || "".equals(exchangeSupplyTmp) || "[]".equals(exchangeSupplyTmp)){
            return "{\"1\":"+directSupplyTmp+"}";
        }
        return "{\"2\":"+exchangeSupplyTmp+",\"1\":"+directSupplyTmp+"}";
    }
    private static void populateExtSupplyAttr(Targeting_profile tp, String str){
        if(tp == null || str == null || "".equals(str) || "[]".equals(str)){
            tp.setExt_supply_attributes("[]");
            tp.setExchange_list("[]");
            return;
        }
        if(str != null && !str.trim().equals("")){
            StringBuffer sbuff = new StringBuffer("[");
            StringBuffer exchangeSbuff = new StringBuffer("[");
            ObjectMapper mapper = new ObjectMapper();
            try {
                JsonNode node = mapper.readValue(str,JsonNode.class);
                Iterator<java.util.Map.Entry<String,JsonNode>> itr = node.getFields();
                boolean isFirst = true;
                boolean exchangeIsFirst = true;
                while(itr.hasNext()){
                    java.util.Map.Entry<String,JsonNode> firstLeveldnode = itr.next();
                    String key = firstLeveldnode.getKey();
                    if(exchangeIsFirst){
                        exchangeIsFirst=false;
                    }else{
                        exchangeSbuff.append(",");
                    }
                    exchangeSbuff.append(key);
                    JsonNode secondlevelnode = firstLeveldnode.getValue();
                    Iterator<java.util.Map.Entry<String,JsonNode>> thirdlevelnode = secondlevelnode.getFields();
                    while(thirdlevelnode.hasNext()){
                        java.util.Map.Entry<String,JsonNode> fourthLevel = thirdlevelnode.next();
                        String siteKey=fourthLevel.getKey();
                        JsonNode fifthlevelnode = fourthLevel.getValue();
                        Iterator<JsonNode> sixthlevel =  fifthlevelnode.getElements();
                        while(sixthlevel.hasNext()){
                            JsonNode seventhlevel = sixthlevel.next();
                            if(isFirst){
                                isFirst=false;
                            }else{
                                sbuff.append(",");
                            }
                            sbuff.append("\""+key+"|"+siteKey+"|"+seventhlevel.get("intid").getValueAsText()+"\"");
                        }
                    }
                }
            } catch (Exception e) {
                LOG.error(e.getMessage(),e);
            }
            sbuff.append("]");
            exchangeSbuff.append("]");
            tp.setExt_supply_attributes(sbuff.toString());
            tp.setExchange_list(exchangeSbuff.toString());
            return;
        }
        tp.setExt_supply_attributes("[]");
        tp.setExchange_list("[]");
    }
    private static void populateDirectSites(Targeting_profile tp, String str){
        tp.setDirect_supply_inc_exc(str);
        if(tp == null || str == null || "".equals(str) || "[]".equals(str)){
            tp.setSite_list("[]");
            tp.setPub_list("[]");
            return;
        }
        if(str != null && !str.trim().equals("")){
            StringBuffer sbuff = new StringBuffer("[");
            StringBuffer pubSbuff = new StringBuffer("[");
            ObjectMapper mapper = new ObjectMapper();
            try {
                JsonNode node = mapper.readValue(str,JsonNode.class);
                Iterator<java.util.Map.Entry<String,JsonNode>> itr = node.getFields();
                boolean isFirst = true;
                boolean pubIsFirst = true;
                while(itr.hasNext()){
                    java.util.Map.Entry<String,JsonNode> firstLeveldnode = itr.next();
                    String key = firstLeveldnode.getKey();
                    JsonNode secondlevelnode = firstLeveldnode.getValue();
                    Iterator<JsonNode> thirdlevelnode = secondlevelnode.getElements();
                    if(pubIsFirst){
                        pubIsFirst=false;
                    }else{
                        pubSbuff.append(",");
                    }
                    while(thirdlevelnode.hasNext()){
                        JsonNode fourthLevel = thirdlevelnode.next();
                        if(isFirst){
                            isFirst=false;
                        }else{
                            sbuff.append(",");
                        }
                        sbuff.append("\""+key+"|"+fourthLevel.getValueAsText()+"\"");
                    }
                    pubSbuff.append(key);
                }
            } catch (Exception e) {
                LOG.error(e.getMessage(),e);
            }
            sbuff.append("]");
            pubSbuff.append("]");
            tp.setSite_list(sbuff.toString());
            tp.setPub_list(pubSbuff.toString());
            return;
        }
        tp.setSite_list("[]");
        tp.setPub_list("[]");
    }

    private static String lat_lon_r_to_db(Targeting_profile tp){
        StringBuffer sbuff = new StringBuffer("[");
        try{
            if(!(tp.getLat_long() == null)){
                String lat_longStr = tp.getLat_long().trim();
                String[] lineSplit = lat_longStr.split("\n");
                boolean isFirst = true;
                for(String line:lineSplit){
                    String[] cellSplit=line.split(",");
                    if(cellSplit.length==3 && !("".equals(cellSplit[0]) || "".equals(cellSplit[1]) || "".equals(cellSplit[2]))){
                        double lat = Double.parseDouble(cellSplit[0]);
                        double lon = Double.parseDouble(cellSplit[1]);
                        double r = Double.parseDouble(cellSplit[2]);
                        if(isFirst){
                            isFirst=false;
                        }else{
                            sbuff.append(",");
                        }
                        sbuff.append("{");
                        sbuff.append("\"lat\":");sbuff.append(lat);sbuff.append(",");
                        sbuff.append("\"lon\":");sbuff.append(lon);sbuff.append(",");
                        sbuff.append("\"r\":");sbuff.append(r);
                        sbuff.append("}");
                    }
                }
            }
            sbuff.append("]");
            return sbuff.toString();
        }catch(Exception e){
            LOG.error(e.getMessage(), e);
            sbuff.append("]");
            return sbuff.toString();
        }
    }

    public static void populate(Targeting_profile tp, ResultSet rset) throws SQLException{
        populateWithRawCountryJson(tp,rset,false);
    }

    public static void populateWithRawCountryJson(Targeting_profile tp, ResultSet rset,boolean useRawCountryJson) throws SQLException{
        if(tp != null && rset != null){
            tp.setAccount_guid(rset.getString("account_guid"));
            tp.setBrand_list(rset.getString("brand_list"));
            tp.setBrowser_json(rset.getString("browser_json"));
            String categories_list = rset.getString("category_list");
            if(categories_list != null){
                ObjectMapper mapper = new ObjectMapper();
                try {
                    JsonNode node = mapper.readValue(categories_list,JsonNode.class);
                    JsonNode tier1node = node.get(CategoryTier.TIER1.getName());
                    if(tier1node != null){
                        tp.setCategories_tier_1_list(tier1node.toString());
                    }
                    JsonNode tier2node = node.get(CategoryTier.TIER2.getName());
                    if(tier2node != null){
                        tp.setCategories_tier_2_list(tier2node.toString());
                    }
                } catch (Exception e) {
                    LOG.error(e.getMessage(),e);
                }
            }
            String city_json= rset.getString("city_json");
            if(city_json==null){
            	tp.setCity_json("[]");
            }else{
            	String cityTrim = city_json.trim();
            	if("".equals(cityTrim) || "[]".equals(cityTrim) ||"{}".equals(cityTrim)){
            		tp.setCity_json("[]");
            	}else{
            		tp.setCity_json(create_id_list(cityTrim));
            	}
        	}
            if(useRawCountryJson)
                tp.setCountry_json(rset.getString("country_json"));
            else
                tp.setCountry_json(create_id_list(rset.getString("country_json")) );

            tp.setCarrier_json(create_id_list(rset.getString("carrier_json")));
            tp.setCustom_ip_file_id_set(db_to_ui_file_set(rset.getString("custom_ip_file_id_set")));
            tp.setGuid(rset.getString("guid"));
            tp.setIs_category_list_excluded(rset.getBoolean("is_category_list_excluded"));
            tp.setIs_site_list_excluded(rset.getBoolean("is_site_list_excluded"));
            tp.setModel_list(rset.getString("model_list"));
            tp.setModified_by(rset.getInt("modified_by"));
            tp.setName(rset.getString("name"));
            tp.setOs_json(rset.getString("os_json"));
            String state_json= rset.getString("state_json");
            if(state_json==null){
            	tp.setState_json("[]");
            }else{
            	String stateTrim = state_json.trim();
            	if("".equals(stateTrim) || "[]".equals(stateTrim) || "{}".equals(stateTrim)){
            		tp.setState_json("[]");
            	}else{
            		tp.setState_json(create_id_list(stateTrim));
            	}
        	}
            tp.setStatus_id(StatusIdEnum.getEnum(rset.getInt("status_id")));
            tp.setSupply_source(SupplySourceEnum.getEnum((short)rset.getInt("supply_source")));
            tp.setSupply_source_type(SupplySourceTypeEnum.getEnum((short)rset.getInt("supply_source_type")));
            tp.setGeo_targeting_type(Geo_Targeting_type.getEnum(rset.getInt("geo_targeting_type")));
            tp.setZipcode_file_id_set(db_to_ui_file_set(rset.getString("zipcode_file_id_set")));
            tp.setTablet_targeting(rset.getBoolean("tablet_targeting"));
            String str = rset.getString("hours_list");
            if(str == null){
                tp.setHours_list("[]");
            }else{
                tp.setHours_list(str);
            }
            int midp = rset.getInt("midp");
            if(midp > 0){
                tp.setMidp(MidpValue.fetchMipValue((short)midp));
            }else{
                tp.setMidp(MidpValue.ALL);
            }
        }
        tp.setCreated_on(rset.getTimestamp("created_on").getTime());
        lat_lon_r(tp,rset.getString("lat_long"));
        populateExtSupplyAttr(tp, rset.getString("exchange_supply_inc_exc"));
        populateDirectSites(tp, rset.getString("direct_supply_inc_exc"));
        tp.setSupplyInclusionExclusion(rset.getString("supply_inc_exc"));
        generateSupply_inc_exc(tp);
        String connection_type_str = rset.getString("connection_type_targeting_json");
        if(connection_type_str != null && !"".equals(connection_type_str)){ 
            tp.setConnection_type_targeting_json(connection_type_str);
        }
        String retargeting = rset.getString("retargeting");
        populateRetargeting(retargeting, tp);
        tp.setPmp_deal_json(rset.getString("pmp_deal_json"));
        tp.setDevice_type(rset.getString("device_type"));
        populatePMPDealJson(tp);
        populateExt(tp, rset.getString("ext"));
        tp.setLat_lon_radius_file(db_to_ui_file_set(rset.getString("lat_lon_radius_file")));
    }
    
    private static void populateExt(Targeting_profile tp,String ext){
    	if(ext == null){
    		return;
    	}
    	String extTrim = ext.trim();
    	try{
    		TPExt tpExt = TPExt.getObject(extTrim);
    		if(tpExt != null){
    			tp.setMma_inc_exc(tpExt.isInc());
    			if(tpExt.getMma_tier1() != null){
    				StringBuffer sBuff = new StringBuffer("[");
    				boolean isFirst = true;
    				for(Integer i:tpExt.getMma_tier1() ){
    					if(isFirst){
    						isFirst = false;
    					}else{
    						sBuff.append(",");
    					}
    					sBuff.append(i);
    				}
    				sBuff.append("]");
    				tp.setMma_tier_1_list(sBuff.toString());
    			}
    			if(tpExt.getMma_tier2() != null){
    				StringBuffer sBuff = new StringBuffer("[");
    				boolean isFirst = true;
    				for(Integer i:tpExt.getMma_tier2() ){
    					if(isFirst){
    						isFirst = false;
    					}else{
    						sBuff.append(",");
    					}
    					sBuff.append(i);
    				}
    				sBuff.append("]");
    				tp.setMma_tier_2_list(sBuff.toString());
    			}
    			if(tpExt.getAdposids() != null){
    				StringBuffer sBuff = new StringBuffer("[");
    				boolean isFirst = true;
    				for(Integer i:tpExt.getAdposids() ){
    					if(isFirst){
    						isFirst = false;
    					}else{
    						sBuff.append(",");
    					}
    					sBuff.append(i);
    				}
    				sBuff.append("]");
    				tp.setAdposition_list(sBuff.toString());
    			}
    			tp.setAdposition_inc_exc(tpExt.isAdposids_inc());
    			tp.setChannel_inc_exc(tpExt.isChannel_inc());
    			if(tpExt.getChannel_tier1()!= null){
    				StringBuffer sBuff = new StringBuffer("[");
    				boolean isFirst = true;
    				for(Integer i:tpExt.getChannel_tier1() ){
    					if(isFirst){
    						isFirst = false;
    					}else{
    						sBuff.append(",");
    					}
    					sBuff.append(i);
    				}
    				sBuff.append("]");
    				tp.setChannel_tier_1_list(sBuff.toString());
    			}
    			if(tpExt.getChannel_tier2() != null){
    				StringBuffer sBuff = new StringBuffer("[");
    				boolean isFirst = true;
    				for(Integer i:tpExt.getChannel_tier2() ){
    					if(isFirst){
    						isFirst = false;
    					}else{
    						sBuff.append(",");
    					}
    					sBuff.append(i);
    				}
    				sBuff.append("]");
    				tp.setChannel_tier_2_list(sBuff.toString());
    			}
    		}
    	}catch(Exception e){
    		LOG.error(e.getMessage(),e);
    	}
    	
    }
    
    private static String generateExt(Targeting_profile tp){
		TPExt tpExt = new TPExt();
    	if(tp.getMma_tier_1_list() != null){
    		tpExt.setInc(tp.isMma_inc_exc());
    		HashSet<Integer> t1Set = new HashSet<Integer>();
    		String t1 = tp.getMma_tier_1_list().trim().replaceAll("\\[", "").replaceAll("]", "").replaceAll("\"", "");
    		String t1Split[] = t1.split(",");
    		for(String str:t1Split){
    			String strTrim=str.trim();
    			if(!"".equals(strTrim)){
    				try{
    					int i = Integer.parseInt(strTrim);
    					t1Set.add(i);
    				}catch(Exception e){
    					LOG.error(e.getMessage(),e);
    				}
    			}
    		}
    		tpExt.setMma_tier1(t1Set);
        	if(tp.getMma_tier_2_list() != null){
        		HashSet<Integer> t2Set = new HashSet<Integer>();
        		String t2 = tp.getMma_tier_2_list().trim().replaceAll("\\[", "").replaceAll("]", "").replaceAll("\"", "");
        		String t2Split[] = t2.split(",");
        		for(String str:t2Split){
        			String strTrim=str.trim();
        			if(!"".equals(strTrim)){
        				try{
        					int i = Integer.parseInt(strTrim);
        					t2Set.add(i);
        				}catch(Exception e){
        					LOG.error(e.getMessage(),e);
        				}
        			}
        		}
        		tpExt.setMma_tier2(t2Set);
        	}
        	if(tp.getAdposition_list() != null){
        		HashSet<Integer> adSet = new HashSet<Integer>();
        		String t2 = tp.getAdposition_list().trim().replaceAll("\\[", "").replaceAll("]", "").replaceAll("\"", "");
        		String t2Split[] = t2.split(",");
        		for(String str:t2Split){
        			String strTrim=str.trim();
        			if(!"".equals(strTrim)){
        				try{
        					int i = Integer.parseInt(strTrim);
        					adSet.add(i);
        				}catch(Exception e){
        					LOG.error(e.getMessage(),e);
        				}
        			}
        		}
        		tpExt.setAdposids(adSet);
        	}
    	}
    	tpExt.setInc(tp.isMma_inc_exc());
    	tpExt.setAdposids_inc(tp.isAdposition_inc_exc());
    	if(tp.getAdposition_list() != null){
    		HashSet<Integer> adSet = new HashSet<Integer>();
    		String t2 = tp.getAdposition_list().trim().replaceAll("\\[", "").replaceAll("]", "").replaceAll("\"", "");
    		String t2Split[] = t2.split(",");
    		for(String str:t2Split){
    			String strTrim=str.trim();
    			if(!"".equals(strTrim)){
    				try{
    					int i = Integer.parseInt(strTrim);
    					adSet.add(i);
    				}catch(Exception e){
    					LOG.error(e.getMessage(),e);
    				}
    			}
    		}
    		tpExt.setAdposids(adSet);
    	}
    	if(tp.getChannel_tier_1_list() != null){
    		tpExt.setChannel_inc(tp.isChannel_inc_exc());
    		HashSet<Integer> t1Set = new HashSet<Integer>();
    		String t1 = tp.getChannel_tier_1_list().trim().replaceAll("\\[", "").replaceAll("]", "").replaceAll("\"", "");
    		String t1Split[] = t1.split(",");
    		for(String str:t1Split){
    			String strTrim=str.trim();
    			if(!"".equals(strTrim)){
    				try{
    					int i = Integer.parseInt(strTrim);
    					t1Set.add(i);
    				}catch(Exception e){
    					LOG.error(e.getMessage(),e);
    				}
    			}
    		}
    		tpExt.setChannel_tier1(t1Set);
        	if(tp.getChannel_tier_2_list() != null){
        		HashSet<Integer> t2Set = new HashSet<Integer>();
        		String t2 = tp.getChannel_tier_2_list().trim().replaceAll("\\[", "").replaceAll("]", "").replaceAll("\"", "");
        		String t2Split[] = t2.split(",");
        		for(String str:t2Split){
        			String strTrim=str.trim();
        			if(!"".equals(strTrim)){
        				try{
        					int i = Integer.parseInt(strTrim);
        					t2Set.add(i);
        				}catch(Exception e){
        					LOG.error(e.getMessage(),e);
        				}
        			}
        		}
        		tpExt.setChannel_tier2(t2Set);
        	}
    	}
    	return tpExt.toJson().toString();
    }

    
    private static void populateRetargeting(String retargeting, Targeting_profile tp){
        if(retargeting != null && !retargeting.trim().equals("")){
            try {
                Retargeting  retargetingColumn = Retargeting.getObject(retargeting);
                if(retargetingColumn != null && retargetingColumn.getSegment() != null){
                    StringBuffer sbuff = new StringBuffer("[");
                    boolean isFirst=true;
                    for(Integer i: retargetingColumn.getSegment()){
                        if(isFirst){
                            isFirst=false;
                        }else{
                            sbuff.append(",");
                        }
                        sbuff.append(i);
                    }
                    sbuff.append("]");
                    tp.setRetargeting(sbuff.toString());
                }
                
            } catch (Exception e) {
                LOG.error(e.getMessage(),e);
            } 
        }
    }
    
    private static String generateRetargeting(String retargeting){
        if(retargeting != null){
            String tmp_retargeting = retargeting.replaceAll("\\[", "").replaceAll("]", "").trim();
            if(!tmp_retargeting.equals("") &&  !tmp_retargeting.equals("[]")){
                String[] retargetingSplit = tmp_retargeting.split(",");
                List<Integer> list = new LinkedList<Integer>();
                for(String idStr:retargetingSplit){
                    list.add(Integer.parseInt(idStr));
                }
                Retargeting retargetingCol = new Retargeting();
                retargetingCol.setSegment(list);
                return retargetingCol.toJson().toString();
            }
        }
        return "";
    }
    private static void generatePMPDealJson(Targeting_profile tp){
        String exchangeList= tp.getPmp_exchange();
        String dealIdList= tp.getPmp_dealid();
        if(exchangeList != null && dealIdList != null){
            exchangeList = exchangeList.trim().replaceAll("\\[", "").replaceAll("]", "");
            dealIdList= dealIdList.trim().replaceAll("\\[", "").replaceAll("]", "");
            if(!"".equals(exchangeList) && !"".equals(dealIdList)){
                StringBuffer sbuff = new StringBuffer("{");
                boolean isFirst = true;
                String[] dealArray = dealIdList.split(",");
                if(dealArray.length > 0){
                    for(String deal:dealArray){
                        String dealTrim=deal.trim();
                        if(!"".equals(dealTrim)){
                            if(isFirst){
                                sbuff.append("\"");
                                sbuff.append(exchangeList);
                                sbuff.append("\":[");
                                isFirst=false;
                            }else{
                                sbuff.append(",");
                            }
                            sbuff.append("\"");
                            sbuff.append(dealTrim);
                            sbuff.append("\"");
                        }
                    }
                    if(!isFirst){
                        sbuff.append("]");
                    }
                }
                sbuff.append("}");
                tp.setPmp_deal_json(sbuff.toString());
            }
        }
    }
    private static String generateJsonArray(String inputStr){
    	if(inputStr == null){
    		return "[]";
    	}
    	String inputStrTrim = inputStr.trim().replaceAll("\\[", "").replaceAll("]","");
    	if("".equals(inputStrTrim)){
    		return "[]";
    	}
    	String strSplit[] = inputStrTrim.split(",");
    	boolean isFirst=true;
    	StringBuffer sBuff = new StringBuffer("[");
    	for(String str:strSplit){
    		String strTrim = str.trim();
    		if(!"".equals(strTrim)){
    			try{
    				int i = Integer.parseInt(strTrim);
    				if(isFirst){
    					isFirst=false;
    				}else{
    					sBuff.append(",");
    				}
    				sBuff.append(i);
    			}catch(Exception e ){
    				LOG.error(e.getMessage(),e);
    			}
    		}
    	}
    	sBuff.append("]");
    	return sBuff.toString();
    }
    private static void populatePMPDealJson(Targeting_profile tp){
        String pmp = tp.getPmp_deal_json();
        if(pmp == null){
            return;
        }
        pmp=pmp.trim();
        if("".equals(pmp) || "{}".equals(pmp) || "[]".equals(pmp)){
            return;
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readValue(pmp,JsonNode.class);
            Iterator<java.util.Map.Entry<String,JsonNode>> itr = node.getFields();
            StringBuffer exchangeList = new StringBuffer("");
            StringBuffer dealList = new StringBuffer("");
            boolean isFirstExch = true;
            while(itr.hasNext()){
                boolean isFirst=true;
                java.util.Map.Entry<String,JsonNode> firstElement = itr.next();
                String key = firstElement.getKey().trim();
                if(!"".equals(key)){
                    JsonNode value = firstElement.getValue();
                    Iterator<JsonNode> valueItr = value.iterator();
                    while(valueItr.hasNext()){
                        String elementNode = valueItr.next().getTextValue().trim();
                        if(!"".equals(elementNode)){
                            if(isFirst){
                                isFirst = false;
                            }else{
                                dealList.append(",");
                            }
                            dealList.append(elementNode);
                        }
                    }
                    if(!isFirst){
                        if(isFirstExch){
                            isFirstExch = false;
                        }else{
                            exchangeList.append(",");
                        }
                        exchangeList.append(key);
                    }
                }
            }
            tp.setPmp_exchange(exchangeList.toString());
            tp.setPmp_dealid(dealList.toString());
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
        }
    }
    public static JsonNode insert_targeting_profile(Connection con, JsonNode jsonNode){
        if(jsonNode == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.TARGETING_PROFILE_NULL.getId());
            msg.setMsg(ErrorEnum.TARGETING_PROFILE_NULL.getName());
            return msg.toJson();
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            Targeting_profile tp = objectMapper.treeToValue(jsonNode, Targeting_profile.class);
            return insert_targeting_profile(con, tp, true).toJson();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            return msg.toJson();
        }
    }
    public static Message insert_targeting_profile(Connection con, Targeting_profile tp, boolean createTransaction){
        if(con == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            return msg;
        }
        if(tp == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.TARGETING_PROFILE_NULL.getId());
            msg.setMsg(ErrorEnum.TARGETING_PROFILE_NULL.getName());
            return msg;
        }
        PreparedStatement pstmt = null;
        boolean autoCommitFlag = false;
        try{
            if(createTransaction){
                autoCommitFlag = con.getAutoCommit();
                con.setAutoCommit(false);
            }
            String tmp_country_json = create_country_entity_list_from_country_ids(tp.getCountry_json(), con);
            String tmp_isp_list = create_isp_entity_list_from_ids(tp.getCarrier_json(), con);
            pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Targeting_Profile.insert_targeting_profile);
            String guid = SingletonUUIDGenerator.getSingletonUUIDGenerator().generateUniversallyUniqueIdentifier().toString();
            tp.setGuid(guid);
            pstmt.setString(1, tp.getGuid());
            pstmt.setString(2, tp.getName());
            pstmt.setString(3, tp.getAccount_guid());
            pstmt.setInt(4, tp.getStatus_id().getCode());
            pstmt.setString(5, tp.getBrand_list());
            pstmt.setString(6, tp.getModel_list());
            pstmt.setString(7, tp.getOs_json());
            pstmt.setString(8, tp.getBrowser_json());
            pstmt.setString(9, tmp_country_json);
            pstmt.setString(10, tmp_isp_list);
            pstmt.setString(11, generateLocationJson_from_id_list(tp.getState_json()));
            pstmt.setString(12, generateLocationJson_from_id_list(tp.getCity_json()));
            pstmt.setString(13, ui_to_db_file_set(tp.getZipcode_file_id_set()));
            String directsiteexcinc = generateDirectSites(tp);
            pstmt.setString(14, directsiteexcinc);
            pstmt.setBoolean(15, tp.isIs_site_list_excluded());
            pstmt.setString(16, "{\"TIER1\":"+tp.getCategories_tier_1_list()+",\"TIER2\":"+tp.getCategories_tier_2_list()+"}");
            pstmt.setBoolean(17, tp.isIs_category_list_excluded());
            pstmt.setString(18, ui_to_db_file_set(tp.getCustom_ip_file_id_set()));
            pstmt.setInt(19, tp.getModified_by());
            pstmt.setTimestamp(20,new Timestamp((new Date()).getTime()));
            pstmt.setInt(21, tp.getSupply_source_type().getCode());
            pstmt.setInt(22, tp.getSupply_source().getCode());
            pstmt.setInt(23, tp.getGeo_targeting_type().getCode());
            pstmt.setString(24, tp.getHours_list());
            pstmt.setInt(25,tp.getMidp().getCode());
            pstmt.setString(26,lat_lon_r_to_db(tp));
            String exchangeexcinc = generateExtSupplyAttr(tp);
            pstmt.setString(27,exchangeexcinc);
            pstmt.setString(28, tp.getConnection_type_targeting_json());
            pstmt.setBoolean(29, tp.isTablet_targeting());
            pstmt.setString(30,generateSupply_inc_exc(new_format_direct_supply_source,new_format_exchange_supply_source));
            pstmt.setString(31, generateRetargeting(tp.getRetargeting()));
            generatePMPDealJson(tp);
            pstmt.setString(32, tp.getPmp_deal_json());

            String deviceType = tp.getDevice_type();
            if(deviceType == null)
                deviceType = "[]";
            pstmt.setString(33, deviceType);
            pstmt.setString(34, generateExt(tp));
            pstmt.setString(35, ui_to_db_file_set(tp.getLat_lon_radius_file()));
            int returnCode = pstmt.executeUpdate();
            if(createTransaction){
                con.commit();
            }
            if(returnCode == 0){
                Message msg = new Message();
                msg.setError_code(ErrorEnum.TARGETING_PROFILE_NOT_INSERTED.getId());
                msg.setMsg(ErrorEnum.TARGETING_PROFILE_NOT_INSERTED.getName());
                return msg;
            }
            Message msg = new Message();
            msg.setError_code(ErrorEnum.NO_ERROR.getId());
            msg.setMsg(ErrorEnum.NO_ERROR.getName());
            msg.setId(guid);
            return msg;
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            if(createTransaction){
                try {
                    con.rollback();
                } catch (SQLException e1) {
                    LOG.error(e1.getMessage(),e1);
                }
            }
            Message msg = new Message();
            msg.setError_code(ErrorEnum.SQL_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.SQL_EXCEPTION.getName());
            return msg;
        }finally{
            if(pstmt != null){
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    LOG.error(e.getMessage(),e);
                }
            }
            if(createTransaction){
                try {
                    con.setAutoCommit(autoCommitFlag);
                } catch (SQLException e1) {
                    LOG.error(e1.getMessage(),e1);
                }
            }
        } 
    }
    public static Message insert_targeting_profile_limited_parameters(Connection con, Targeting_profile tp, boolean createTransaction){
        if(con == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            return msg;
        }
        if(tp == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.TARGETING_PROFILE_NULL.getId());
            msg.setMsg(ErrorEnum.TARGETING_PROFILE_NULL.getName());
            return msg;
        }
        PreparedStatement pstmt = null;
        boolean autoCommitFlag = false;
        try{
            if(createTransaction){
                autoCommitFlag = con.getAutoCommit();
                con.setAutoCommit(false);
            }
            String tmp_country_json = tp.getCountry_json();
            String tmp_isp_list = create_isp_entity_list_from_ids(tp.getCarrier_json(), con);
            pstmt = con.prepareStatement(Targeting_Profile.insert_targeting_profile_limited_parameters);
            pstmt.setString(1, tp.getGuid());
            pstmt.setString(2, tp.getName());
            pstmt.setString(3, tp.getAccount_guid());
            pstmt.setInt(4, tp.getStatus_id().getCode());
            pstmt.setString(5, tp.getBrand_list());
            pstmt.setString(6, tp.getModel_list());
            pstmt.setString(7, tp.getOs_json());
            pstmt.setString(8, tp.getBrowser_json());
            pstmt.setString(9, tmp_country_json);
            pstmt.setString(10, tmp_isp_list);
            pstmt.setString(11, generateLocationJson_from_id_list(tp.getState_json()));
            pstmt.setString(12, generateLocationJson_from_id_list(tp.getCity_json()));
            pstmt.setString(13, ui_to_db_file_set(tp.getZipcode_file_id_set()));
            String directsiteexcinc = generateDirectSites(tp);
            pstmt.setString(14, directsiteexcinc);
            pstmt.setBoolean(15, tp.isIs_site_list_excluded());
            pstmt.setString(16, "{\"TIER1\":"+tp.getCategories_tier_1_list()+",\"TIER2\":"+tp.getCategories_tier_2_list()+"}");
            pstmt.setBoolean(17, tp.isIs_category_list_excluded());
            pstmt.setString(18, ui_to_db_file_set(tp.getCustom_ip_file_id_set()));
            pstmt.setInt(19, tp.getModified_by());
            pstmt.setTimestamp(20,new Timestamp((new Date()).getTime()));
            pstmt.setInt(21, tp.getSupply_source_type().getCode());
            pstmt.setInt(22, tp.getSupply_source().getCode());
            pstmt.setInt(23, tp.getGeo_targeting_type().getCode());
            pstmt.setString(24, tp.getHours_list());
            pstmt.setInt(25,tp.getMidp().getCode());
            pstmt.setString(26,lat_lon_r_to_db(tp));
            String exchangeexcinc = generateExtSupplyAttr(tp);
            pstmt.setString(27,exchangeexcinc);
            pstmt.setString(28, tp.getConnection_type_targeting_json());
            pstmt.setBoolean(29, tp.isTablet_targeting());
            pstmt.setString(30,tp.getSupplyInclusionExclusion());
            pstmt.setString(31, generateRetargeting(tp.getRetargeting()));
            generatePMPDealJson(tp);
            pstmt.setString(32, tp.getPmp_deal_json());
            int returnCode = pstmt.executeUpdate();
            if(createTransaction){
                con.commit();
            }
            if(returnCode == 0){
                Message msg = new Message();
                msg.setError_code(ErrorEnum.TARGETING_PROFILE_NOT_INSERTED.getId());
                msg.setMsg(ErrorEnum.TARGETING_PROFILE_NOT_INSERTED.getName());
                return msg;
            }
            Message msg = new Message();
            msg.setError_code(ErrorEnum.NO_ERROR.getId());
            msg.setMsg(ErrorEnum.NO_ERROR.getName());
            return msg;
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            if(createTransaction){
                try {
                    con.rollback();
                } catch (SQLException e1) {
                    LOG.error(e1.getMessage(),e1);
                }
            }
            Message msg = new Message();
            msg.setError_code(ErrorEnum.SQL_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.SQL_EXCEPTION.getName());
            return msg;
        }finally{
            if(pstmt != null){
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    LOG.error(e.getMessage(),e);
                }
            }
            if(createTransaction){
                try {
                    con.setAutoCommit(autoCommitFlag);
                } catch (SQLException e1) {
                    LOG.error(e1.getMessage(),e1);
                }
            }
        }
    }

    public static JsonNode update_targeting_profile(Connection con, JsonNode jsonNode){
        if(jsonNode == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.TARGETING_PROFILE_NULL.getId());
            msg.setMsg(ErrorEnum.TARGETING_PROFILE_NULL.getName());
            return msg.toJson();
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            Targeting_profile tp = objectMapper.treeToValue(jsonNode, Targeting_profile.class);
            return update_targeting_profile(con, tp, true).toJson();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            return msg.toJson();
        }
    }

    public static Message update_targeting_profile(Connection con, Targeting_profile tp, boolean createTransaction)
    {
        return update_targeting_profile_using_provided_country_json(con,tp,createTransaction,false);
    }

    public static Message update_targeting_profile_using_provided_country_json(Connection con, Targeting_profile tp,
                                                                               boolean createTransaction,
                                                                               boolean
                                                                                   useprovidedCountrySupplyIncExcJson){
        if(con == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            return msg;
        }
        if(tp == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.TARGETING_PROFILE_NULL.getId());
            msg.setMsg(ErrorEnum.TARGETING_PROFILE_NULL.getName());
            return msg;
        }
        PreparedStatement pstmt = null;
        boolean autoCommitFlag = false;
        try{
            if(createTransaction){
                autoCommitFlag = con.getAutoCommit();
                con.setAutoCommit(false);
            }
            String tmp_country_json = null;

            if(useprovidedCountrySupplyIncExcJson)
                tmp_country_json = tp.getCountry_json();
            else
                tmp_country_json =  create_country_entity_list_from_country_ids(tp.getCountry_json(), con);

            String tmp_isp_json = create_isp_entity_list_from_ids(tp.getCarrier_json(), con);
            pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Targeting_Profile.update_targeting_profile);
            pstmt.setString(1, tp.getName());
            pstmt.setInt(2, tp.getStatus_id().getCode());
            pstmt.setString(3, tp.getBrand_list());
            pstmt.setString(4, tp.getModel_list());
            pstmt.setString(5, tp.getOs_json());
            pstmt.setString(6, tp.getBrowser_json());
            pstmt.setString(7, tmp_country_json);
            pstmt.setString(8, tmp_isp_json);
            pstmt.setString(9, generateLocationJson_from_id_list(tp.getState_json()));
            pstmt.setString(10, generateLocationJson_from_id_list(tp.getCity_json()));
            pstmt.setString(11, ui_to_db_file_set(tp.getZipcode_file_id_set()));
            String directsiteexcinc = generateDirectSites(tp);
            pstmt.setString(12, directsiteexcinc);
            pstmt.setBoolean(13, tp.isIs_site_list_excluded());
            pstmt.setString(14, "{\"TIER1\":"+tp.getCategories_tier_1_list()+",\"TIER2\":"+tp.getCategories_tier_2_list()+"}");
            pstmt.setBoolean(15, tp.isIs_category_list_excluded());
            pstmt.setString(16, ui_to_db_file_set(tp.getCustom_ip_file_id_set()));
            pstmt.setInt(17, tp.getModified_by());
            pstmt.setTimestamp(18,new Timestamp((new Date()).getTime()));
            pstmt.setInt(19, tp.getSupply_source_type().getCode());
            pstmt.setInt(20, tp.getSupply_source().getCode());
            pstmt.setInt(21, tp.getGeo_targeting_type().getCode());
            pstmt.setString(22, tp.getHours_list());
            pstmt.setInt(23, tp.getMidp().getCode());
            pstmt.setString(24, lat_lon_r_to_db(tp));
            String exchangeexcinc = generateExtSupplyAttr(tp);
            pstmt.setString(25, exchangeexcinc);
            pstmt.setString(26, tp.getConnection_type_targeting_json());
            pstmt.setBoolean(27, tp.isTablet_targeting());

            if(useprovidedCountrySupplyIncExcJson)
                pstmt.setString(28,tp.getSupplyInclusionExclusion());
            else
                pstmt.setString(28,generateSupply_inc_exc(new_format_direct_supply_source,new_format_exchange_supply_source));

            pstmt.setString(29, generateRetargeting(tp.getRetargeting()));
            generatePMPDealJson(tp);
            pstmt.setString(30, tp.getPmp_deal_json());

            String deviceType = tp.getDevice_type();
            if(deviceType == null)
                deviceType = "[]";
            pstmt.setString(31, deviceType);
            pstmt.setString(32, generateExt(tp));
            pstmt.setString(33, ui_to_db_file_set(tp.getLat_lon_radius_file()));
            pstmt.setString(34, tp.getGuid());
            int returnCode = pstmt.executeUpdate();
            if(createTransaction){
                con.commit();
            }
            if(returnCode == 0){
                Message msg = new Message();
                msg.setError_code(ErrorEnum.TARGETING_PROFILE_NOT_UPDATED.getId());
                msg.setMsg(ErrorEnum.TARGETING_PROFILE_NOT_UPDATED.getName());
                return msg;
            }
            Message msg = new Message();
            msg.setError_code(ErrorEnum.NO_ERROR.getId());
            msg.setMsg(ErrorEnum.NO_ERROR.getName());
            return msg;
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            if(createTransaction){
                try {
                    con.rollback();
                } catch (SQLException e1) {
                    LOG.error(e1.getMessage(),e1);
                }
            }
            Message msg = new Message();
            msg.setError_code(ErrorEnum.SQL_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.SQL_EXCEPTION.getName());
            return msg;
        }finally{
            if(pstmt != null){
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    LOG.error(e.getMessage(),e);
                }
            }
            if(createTransaction){
                try {
                    con.setAutoCommit(autoCommitFlag);
                } catch (SQLException e1) {
                    LOG.error(e1.getMessage(),e1);
                }
            }
        } 
    }
    public static JsonNode various_get_targeting_profile(Connection con, JsonNode jsonNode){
        if(jsonNode == null){
            TargetingProfileList tplist = new TargetingProfileList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.TARGETING_PROFILE_LIST_ENTITY_NULL.getId());
            msg.setMsg(ErrorEnum.TARGETING_PROFILE_LIST_ENTITY_NULL.getName());
            tplist.setMsg(msg);
            return tplist.toJson();
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            TargetingProfileListEntity tplistEntity = objectMapper.treeToValue(jsonNode, TargetingProfileListEntity.class);
            return  various_get_targeting_profile(con, tplistEntity).toJson();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            TargetingProfileList tplist = new TargetingProfileList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            tplist.setMsg(msg);
            return tplist.toJson();
        }
    }
    public static TargetingProfileList various_get_targeting_profile(Connection con, TargetingProfileListEntity tplistEntity){
        return various_get_targeting_profile(con, tplistEntity,1);
    }
    public static TargetingProfileList various_get_targeting_profile(Connection con, TargetingProfileListEntity tplistEntity, int version){
        if(con == null){
            TargetingProfileList tplist = new TargetingProfileList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            tplist.setMsg(msg);
            return tplist;
        }
        if(tplistEntity == null){
            TargetingProfileList tplist = new TargetingProfileList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.TARGETING_PROFILE_LIST_ENTITY_NULL.getId());
            msg.setMsg(ErrorEnum.TARGETING_PROFILE_LIST_ENTITY_NULL.getName());
            tplist.setMsg(msg);
            return tplist;
        }
        PreparedStatement pstmt = null;
        try{
            switch (tplistEntity.getTpEnum()){
                case get_targeting_profile:
                    pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Targeting_Profile.get_targeting_profile);
                    pstmt.setString(1, tplistEntity.getGuid());
                    break;
                case list_active_targeting_profile_by_account:
                    pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Targeting_Profile.list_active_targeting_profile_by_account);
                    pstmt.setString(1, tplistEntity.getAccount_guid());
                    break;
               default:
                    break;
            }
            ResultSet rset = pstmt.executeQuery();
            TargetingProfileList tplist = new TargetingProfileList();
            List<Targeting_profile> tps = new LinkedList<Targeting_profile>();
            while(rset.next()){
                Targeting_profile tp = new Targeting_profile();
                populate(tp, rset);
                tps.add(tp);
            }
            tplist.setTplist(tps);
            Message msg = new Message();
            if(tps.size()>0){
                msg.setError_code(ErrorEnum.NO_ERROR.getId());
                msg.setMsg(ErrorEnum.NO_ERROR.getName());
            }else{
                msg.setError_code(ErrorEnum.TARGETING_PROFILE_NOT_FOUND.getId());
                msg.setMsg(ErrorEnum.TARGETING_PROFILE_NOT_FOUND.getName());
            }
            tplist.setMsg(msg);
            return tplist;
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            TargetingProfileList tplist = new TargetingProfileList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.SQL_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.SQL_EXCEPTION.getName());
            tplist.setMsg(msg);
            return tplist;
        }finally{
            if(pstmt != null){
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    LOG.error(e.getMessage(),e);
                }
            }
        } 
    }
    public static JsonNode deactivate_targeting_profile(Connection con, JsonNode jsonNode){
        if(jsonNode == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.TARGETING_PROFILE_NULL.getId());
            msg.setMsg(ErrorEnum.TARGETING_PROFILE_NULL.getName());
            return msg.toJson();
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            Targeting_profile tp = objectMapper.treeToValue(jsonNode, Targeting_profile.class);
            return deactivate_targeting_profile(con, tp, true).toJson();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            return msg.toJson();
        }
    }
    
    public static Message deactivate_targeting_profile(Connection con, Targeting_profile tp, boolean createTransaction){
        if(con == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            return msg;
        }
        if(tp == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.TARGETING_PROFILE_NULL.getId());
            msg.setMsg(ErrorEnum.TARGETING_PROFILE_NULL.getName());
            return msg;
        }
        PreparedStatement pstmt = null;
        boolean autoCommitFlag = false;
        try{
            if(createTransaction){
                autoCommitFlag = con.getAutoCommit();
                con.setAutoCommit(false);
            }
            pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Targeting_Profile.deactive_targeting_profile);
            pstmt.setTimestamp(1, new Timestamp((new Date()).getTime()));
            pstmt.setString(2, tp.getGuid());
            int returnCode = pstmt.executeUpdate();
            if(createTransaction){
                con.commit();
            }
            if(returnCode == 0){
                Message msg = new Message();
                msg.setError_code(ErrorEnum.TARGETING_PROFILE_NOT_UPDATED.getId());
                msg.setMsg(ErrorEnum.TARGETING_PROFILE_NOT_UPDATED.getName());
                return msg;
            }
            Message msg = new Message();
            msg.setError_code(ErrorEnum.NO_ERROR.getId());
            msg.setMsg(ErrorEnum.NO_ERROR.getName());
            return msg;
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            if(createTransaction){
                try {
                    con.rollback();
                } catch (SQLException e1) {
                    LOG.error(e1.getMessage(),e1);
                }
            }
            Message msg = new Message();
            msg.setError_code(ErrorEnum.SQL_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.SQL_EXCEPTION.getName());
            return msg;
        }finally{
            if(pstmt != null){
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    LOG.error(e.getMessage(),e);
                }
            }
            if(createTransaction){
                try {
                    con.setAutoCommit(autoCommitFlag);
                } catch (SQLException e1) {
                    LOG.error(e1.getMessage(),e1);
                }
            }
        } 
    }
    public static void main(String args[]){
        Targeting_profile tp = new Targeting_profile();
        String str = "{\"34\":[\"kwjdbq\",\"akjkqs\"]}";
        tp.setPmp_deal_json(str);
        populatePMPDealJson(tp);
        System.out.println(str);
        System.out.println(tp.getPmp_exchange());
        System.out.println(tp.getPmp_dealid());
        /*        Targeting_profile tp = new Targeting_profile();
        populateExtSupplyAttr(tp, "{\"1\":[{\"intid\":1,\"id\":\"123\",\"name\":\"name\",\"domain\":\"domain\"}," +
                "{\"intid\":2,\"id\":\"456\",\"name\":\"name2\",\"domain\":\"domain2\"},"+
                "{\"intid\":3,\"id\":\"789\",\"name\":\"name3\",\"domain\":\"domain3\"}],\"2\":[{\"intid\":4," +
                "\"id\":\"456\",\"name\":\"name2\",\"domain\":\"domain2\"}]})");
        System.out.println(tp.getExt_supply_attributes());
        */
    }
}
