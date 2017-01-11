package com.kritter.kritterui.api.site;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;
import java.util.Map.Entry;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.kritter.api.entity.response.msg.Message;
import com.kritter.api.entity.site.NoFillPassBack;
import com.kritter.api.entity.site.Site;
import com.kritter.api.entity.site.SiteList;
import com.kritter.api.entity.site.SiteListEntity;
import com.kritter.constants.CategoryTier;
import com.kritter.constants.Payout;
import com.kritter.constants.SITE_PASSBACK_CONTENT_TYPE;
import com.kritter.constants.SITE_PASSBACK_TYPE;
import com.kritter.constants.SITE_PLATFORM;
import com.kritter.constants.error.ErrorEnum;
import com.kritter.entity.native_props.NativeProps;
import com.kritter.entity.video_supply_props.VideoSupplyProps;
import com.kritter.kritterui.api.utils.InQueryPrepareStmnt;
import com.kritter.utils.uuid.mac.SingletonUUIDGenerator;

public class SiteCrud {
    private static final Logger LOG = LoggerFactory.getLogger(SiteCrud.class);
    
    public static String payout_percentage_to_billing_rule_json(String payout_percentage){
        if(payout_percentage != null){
            String tmp_payout_percentage = payout_percentage.trim();
            if(!tmp_payout_percentage.equals("")){
                return "{\"payout\":"+payout_percentage+"}";
            }
        }
        return "{\"payout\":"+Payout.default_payout_percent+"}";
    }
    public static String billing_rule_json_to_payout_percentage(String billing_rule_json){
        if(billing_rule_json != null){
            String tmp_billing_rule_json = billing_rule_json.trim();
            if(!tmp_billing_rule_json.equals("")){
                ObjectMapper mapper = new ObjectMapper();
                try {
                    JsonNode node = mapper.readValue(tmp_billing_rule_json,JsonNode.class);
                    JsonNode payout = node.get("payout");
                    if(payout != null){
                        return payout.getValueAsText();
                    }
                } catch (Exception e) {
                    LOG.error(e.getMessage(),e);
                }
            }
        }
        return Payout.default_payout_percent_str;
    }
    private static HashSet<Integer> strArrayToHashSet(String str){
    	if(str==null){
    		return null;
    	}
    	String strTrim = str.trim().replaceAll("\\[", "").replaceAll("]", "");
    	if(!"".equals(strTrim)){
        	HashSet<Integer> set = new HashSet<Integer>();
    		String split[]= strTrim.split(",");
    		for(String splitStr:split){
    			try{
    				Integer i = Integer.parseInt(splitStr.trim());
    				set.add(i);
    			}catch(Exception e){
    				LOG.error(e.getMessage(),e);
    			}
    		}
    		if(set.size()>0){
    			return set;
    		}
    	}
    	return null;
    }
    private static String  hashSetToStringArray(HashSet<Integer> set){
    	StringBuffer sBuff = new StringBuffer("[");
    	if(set !=null && set.size()>0){
    		boolean isFirst=true;
    		for(Integer i:set){
    			if(isFirst){
    				isFirst=false;
    			}else{
    				sBuff.append(",");
    			}
    			sBuff.append(i);
    		}
    	}
    	sBuff.append("]");
    	return sBuff.toString();
    }
    public static void populate(Site site, ResultSet rset) throws SQLException{
        if(site != null && rset != null){
            site.setAllow_house_ads(rset.getBoolean("allow_house_ads"));
            site.setApp_id(rset.getString("app_id"));
            site.setApp_store_id(rset.getInt("app_store_id"));
            site.setBilling_rules_json(billing_rule_json_to_payout_percentage(rset.getString("billing_rules_json")));
            String categories_list = rset.getString("categories_list");
            if(categories_list != null){
                ObjectMapper mapper = new ObjectMapper();
                try {
                    JsonNode node = mapper.readValue(categories_list,JsonNode.class);
                    JsonNode tier1node = node.get(CategoryTier.TIER1.getName());
                    if(tier1node != null){
                        site.setCategories_tier_1_list(tier1node.toString());
                    }
                    JsonNode tier2node = node.get(CategoryTier.TIER2.getName());
                    if(tier2node != null){
                        site.setCategories_tier_2_list(tier2node.toString());
                    }
                } catch (Exception e) {
                    LOG.error(e.getMessage(),e);
                }
            }
            String category_list_inc_exc = rset.getString("category_list_inc_exc");
            if(category_list_inc_exc != null){
                ObjectMapper mapper = new ObjectMapper();
                try {
                    JsonNode node = mapper.readValue(category_list_inc_exc,JsonNode.class);
                    JsonNode tier1node = node.get(CategoryTier.TIER1.getName());
                    if(tier1node != null){
                        site.setCategory_list_inc_exc_tier_1(tier1node.toString());
                    }
                    JsonNode tier2node = node.get(CategoryTier.TIER2.getName());
                    if(tier2node != null){
                        site.setCategory_list_inc_exc_tier_2(tier2node.toString());
                    }
                } catch (Exception  e) {
                    LOG.error(e.getMessage(),e);
                }
            }
            site.setCreative_attr_inc_exc(rset.getString("creative_attr_inc_exc"));
            site.setGuid(rset.getString("guid"));
            site.setHygiene_list(db_to_ui_hygiene_list(rset.getString("hygiene_list")));
            site.setModified_by(rset.getInt("modified_by"));
            site.setName(rset.getString("name"));
            site.setOpt_in_hygiene_list(rset.getString("opt_in_hygiene_list"));
            site.setPub_guid(rset.getString("pub_guid"));
            site.setPub_id(rset.getInt("pub_id"));
            site.setSite_platform_id(SITE_PLATFORM.getEnum((short)rset.getInt("site_platform_id")));
            site.setSite_url(rset.getString("site_url"));
            site.setStatus_id(rset.getInt("status_id"));
            String url_exclusion = rset.getString("url_exclusion");
            if(url_exclusion != null){
            	url_exclusion = url_exclusion.trim().replaceAll("\\[", "").replaceAll("]", "").replaceAll("\"", "");
            }
            site.setUrl_exclusion(url_exclusion);
            site.setIs_category_list_excluded(rset.getBoolean("is_category_list_excluded"));
            site.setIs_creative_attr_exc(rset.getBoolean("is_creative_attr_exc"));
            site.setComments(rset.getString("comments"));
            site.setId(rset.getInt("id"));
            site.setCreated_on(rset.getTimestamp("created_on").getTime());
            site.setFloor(rset.getDouble("floor"));
            site.setIs_advertiser_excluded(rset.getBoolean("is_advertiser_excluded"));
            site.setPassback_type(rset.getInt("passback_type"));
            populatePassBack(site, rset.getString("nofill_backup_content"));
            populateAdvCampaign(site, rset.getString("campaign_inc_exc_schema"));
            site.setIs_richmedia_allowed(rset.getBoolean("is_richmedia_allowed"));
            boolean is_native = rset.getBoolean("is_native");
            site.setIs_native(is_native);
            populateNativeProps(site, rset.getString("native_props"), is_native);
            boolean is_video = rset.getBoolean("is_video");
            site.setIs_video(is_video);
            populateVideoSupplyProps(site, rset.getString("video_supply_props"), is_video);
        }
    }
    private static void populateVideoSupplyProps(Site site, String video_supply_props, boolean is_video){
    	if(site != null && video_supply_props != null && is_video){
    		site.setIs_video(is_video);
    		String vTrim = video_supply_props.trim();
    		if(!"".equals(vTrim)){
    		try{
    			VideoSupplyProps vProps = VideoSupplyProps.getObject(video_supply_props);
    			if(vProps!=null){
    				if(vProps.getMimes() != null){
    					site.setStrmimes(hashSetToStringArray(vProps.getMimes()));
    				}
    				if(vProps.getProtocols() != null){
    					site.setStrprotocols(hashSetToStringArray(vProps.getProtocols()));
    				}
    				if(vProps.getLinearity() != null){
    					site.setLinearity(vProps.getLinearity());
    				}
    				if(vProps.getStartDelay() != null){
    					site.setStartDelay(vProps.getStartDelay());
    				}
    				if(vProps.getPlaybackmethod() != null){
    					site.setStrplaybackmethod(hashSetToStringArray(vProps.getPlaybackmethod()));
    				}
    				if(vProps.getMinDurationSec() != null){
    					site.setMinDurationSec(vProps.getMinDurationSec());
    				}
    				if(vProps.getMaxDurationSec() != null){
    					site.setMaxDurationSec(vProps.getMaxDurationSec());
    				}
    				if(vProps.getWidthPixel() != null){
    					site.setWidthPixel(vProps.getWidthPixel());
    				}
    				if(vProps.getHeightPixel() != null){
    					site.setHeightPixel(vProps.getHeightPixel());
    				}
    				if(vProps.getDelivery() != null){
    					site.setStrdelivery(hashSetToStringArray(vProps.getDelivery()));
    				}
    				if(vProps.getPos()!= null){
    					site.setPos(vProps.getPos());
    				}
    				if(vProps.getApi() != null){
    					site.setStrapi(hashSetToStringArray(vProps.getApi()));
    				}
    				if(vProps.getCompaniontype() != null){
    					site.setStrcompaniontype(hashSetToStringArray(vProps.getCompaniontype()));
    				}
    			}
    		}catch(Exception e){
    			LOG.error(e.getMessage(),e);
    		}
    		}
    	}
        
    }
    private static void populateNativeProps(Site site, String native_props, boolean is_native){
        if(is_native && native_props!= null){
            try{
                NativeProps objNativeProps = NativeProps.getObject(native_props);
                if(objNativeProps != null && objNativeProps.getLayout() != null){
                    if(objNativeProps.getCall_to_action_keyname() != null){
                        site.setNative_call_to_action_keyname(objNativeProps.getCall_to_action_keyname() );
                    }
                    if(objNativeProps.getDescription_keyname() != null){
                        site.setNative_description_keyname(objNativeProps.getDescription_keyname() );
                    }
                    if(objNativeProps.getDescription_maxchars() != null){
                        site.setNative_description_maxchars(objNativeProps.getDescription_maxchars());
                    }
                        if(objNativeProps.getIcon_imagesize() != null){
                    site.setNative_icon_imagesize(objNativeProps.getIcon_imagesize());
                    }
                    if(objNativeProps.getIcon_keyname() != null){
                        site.setNative_icon_keyname(objNativeProps.getIcon_keyname());
                    }
                    if(objNativeProps.getLandingurl_keyname() != null){
                        site.setNative_landingurl_keyname(objNativeProps.getLandingurl_keyname());
                    }
                    if(objNativeProps.getLayout() != null){
                        site.setNative_layout(objNativeProps.getLayout());
                    }
                    if(objNativeProps.getRating_count_keyname() != null){
                        site.setNative_rating_count_keyname(objNativeProps.getRating_count_keyname());
                    }
                    if(objNativeProps.getScreenshot_imagesize() != null){
                        site.setNative_screenshot_imagesize(objNativeProps.getScreenshot_imagesize());
                    }
                    if(objNativeProps.getScreenshot_keyname() != null){
                        site.setNative_screenshot_keyname(objNativeProps.getScreenshot_keyname());
                    }
                    if(objNativeProps.getTitle_keyname() != null){
                        site.setNative_title_keyname(objNativeProps.getTitle_keyname());
                    }
                    if(objNativeProps.getTitle_maxchars() != null){
                        site.setNative_title_maxchars(objNativeProps.getTitle_maxchars());
                    }
                }
            }catch(Exception e){
                
            }
        }
    }
    private static void populatePassBack(Site site, String nofill_backup_content){
        if(site == null || nofill_backup_content == null || "".equals(nofill_backup_content)
                || "[]".equals(nofill_backup_content)){
            return;
        }
        if(site.getPassback_type() == (int)SITE_PASSBACK_TYPE.NONE.getCode()){
            return;
        }
        try{
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readValue(nofill_backup_content,JsonNode.class);
            JsonNode firstElement = node.get(0);
            site.setNofill_backup_content(firstElement.get("content").getValueAsText());
            site.setPassback_content_type(Integer.parseInt(firstElement.get("type").getValueAsText()));
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
        }
    }
    private static String generateNoFillPassBackContent(Site site){
        if(site == null){
            return "";
        }
        if(site.getPassback_type() == (int)SITE_PASSBACK_TYPE.NONE.getCode()){
            return "";
        }
        if(site.getPassback_content_type() == (int)SITE_PASSBACK_CONTENT_TYPE.NONE.getCode()){
            return "";
        }
        NoFillPassBack nfpb = new NoFillPassBack();
        nfpb.setPriority(1);
        nfpb.setContent(site.getNofill_backup_content().trim());
        nfpb.setType(site.getPassback_content_type());
        return "["+nfpb.toJson().toString()+"]";
    }
    private static String generateNativeProps(Site site){
        if(site == null){
            return null;
        }
        if(site.isIs_native()){
            NativeProps nativeProps = new NativeProps();
            if(site.getNative_layout() != null){
                nativeProps.setLayout(site.getNative_layout());
            }else{
                return null;
            }
            if(site.getNative_call_to_action_keyname() != null){
                nativeProps.setCall_to_action_keyname(site.getNative_call_to_action_keyname() );
            }
            if(site.getNative_description_keyname() != null){
                nativeProps.setDescription_keyname(site.getNative_description_keyname());
            }
            if(site.getNative_description_maxchars() != null){
                nativeProps.setDescription_maxchars(site.getNative_description_maxchars());
            }
            if(site.getNative_icon_imagesize() != null){
                nativeProps.setIcon_imagesize(site.getNative_icon_imagesize());
            }
            if(site.getNative_icon_keyname() != null){
                nativeProps.setIcon_keyname(site.getNative_icon_keyname());
            }
            if(site.getNative_landingurl_keyname() != null){
                nativeProps.setLandingurl_keyname(site.getNative_landingurl_keyname());
            }
            if(site.getNative_rating_count_keyname() != null){
                nativeProps.setRating_count_keyname(site.getNative_rating_count_keyname());
            }
            if(site.getNative_screenshot_imagesize() != null){
                nativeProps.setScreenshot_imagesize(site.getNative_screenshot_imagesize());
            }
            if(site.getNative_screenshot_keyname() != null){
                nativeProps.setScreenshot_keyname(site.getNative_screenshot_keyname());
            }
            if(site.getNative_title_keyname() != null){
                nativeProps.setTitle_keyname(site.getNative_title_keyname() );
            }
            if(site.getNative_title_maxchars() != null){
                nativeProps.setTitle_maxchars(site.getNative_title_maxchars());
            }
            return nativeProps.toJson().toString();
        }
        return null;
    }
    
    private static String generateVideoSupplyProps(Site site){
    	if(site==null || site.isIs_video()){
    		VideoSupplyProps vProps= new VideoSupplyProps();
    		vProps.setMimes(strArrayToHashSet(site.getStrmimes()));
    		vProps.setProtocols(strArrayToHashSet(site.getStrprotocols()));
    		vProps.setPlaybackmethod(strArrayToHashSet(site.getStrplaybackmethod()));
    		vProps.setMinDurationSec(site.getMinDurationSec());
    		vProps.setMaxDurationSec(site.getMaxDurationSec());
    		vProps.setWidthPixel(site.getWidthPixel());
    		vProps.setHeightPixel(site.getHeightPixel());
    		vProps.setStartDelay(site.getStartDelay());
    		vProps.setLinearity(site.getLinearity());
    		vProps.setDelivery(strArrayToHashSet(site.getStrdelivery()));
    		vProps.setPos(site.getPos());
    		vProps.setApi(strArrayToHashSet(site.getStrapi()));
    		vProps.setCompaniontype(strArrayToHashSet(site.getStrcompaniontype()));
    		return vProps.toJson().toString();
    	}
    	return "";
    }

    
    private static String generateAdvCampaign(Site site){
        if(site == null ){
            return "";
        }
        String advjsonarray = site.getAdvertiser_json_array().replaceAll("\\[", "").replaceAll("]", "");
        HashMap<String, LinkedList<Integer>> map = new HashMap<String, LinkedList<Integer>>();
        String campaignjsonArray = site.getCampaign_json_array().replaceAll("\\[", "").replaceAll("]", "").replace("\"", "");
        String[] campaignjsonArraySplit = campaignjsonArray.split(",");
        for(String key:campaignjsonArraySplit){
            String[] keySplit = key.split("\\|");
            if(keySplit.length>1){
                if (map.get(keySplit[0]) == null){
                    LinkedList<Integer> ll = new LinkedList<Integer>();
                    ll.add(Integer.parseInt(keySplit[1]));
                    map.put(keySplit[0], ll);
                }else{
                    map.get(keySplit[0]).add(Integer.parseInt(keySplit[1]));
                }
            }
        }
        String[] advjsonarraySplit = advjsonarray.split(",");
        for(String key:advjsonarraySplit){
            if(!"".equals(key) && map.get(key)==null){
                map.put(key, new LinkedList<Integer>());
            }
        }
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.valueToTree(map);
        return jsonNode.toString();

        
    }
    private static void populateAdvCampaign(Site site, String campaign_inc_exc_schema){
        if(site != null && campaign_inc_exc_schema != null && !"".equals(campaign_inc_exc_schema)){
            try{
                ObjectMapper mapper = new ObjectMapper();
                JsonNode node = mapper.readValue(campaign_inc_exc_schema,JsonNode.class);
                Iterator<Entry<String, JsonNode>> itr = node.getFields();
                StringBuffer advBuff = new StringBuffer("[");
                StringBuffer campaigBuff = new StringBuffer("[");
                boolean advIsFirst = true;
                boolean campIsFirst = true;
                while(itr.hasNext()){
                    if(advIsFirst){
                        advIsFirst=false;
                    }else{
                        advBuff.append(",");
                    }
                    Entry<String, JsonNode> elem = itr.next();
                    String key = elem.getKey();
                    advBuff.append(key);
                    Iterator<JsonNode> values = elem.getValue().getElements();
                    while(values.hasNext()){
                        if(campIsFirst){
                            campIsFirst=false;
                        }else{
                            campaigBuff.append(",");
                        }
                        campaigBuff.append("\""+key+"|"+values.next().getValueAsText()+"\"");
                    }
                }
                advBuff.append("]");
                campaigBuff.append("]");
                site.setAdvertiser_json_array(advBuff.toString());
                site.setCampaign_json_array(campaigBuff.toString());
            }catch(Exception e){
                LOG.error(e.getMessage(),e);
                site.setAdvertiser_json_array("[]");
                site.setCampaign_json_array("[]");
            }
        }else{
            site.setAdvertiser_json_array("[]");
            site.setCampaign_json_array("[]");
        }
    }
    
    public static String ui_to_db_hygiene_list(String str){
        StringBuffer sbuff = new StringBuffer("[");
        if(str != null){
            sbuff.append(str);
        }
        sbuff.append("]");
        return sbuff.toString();
    }
    public static String db_to_ui_hygiene_list(String str){
        if(str != null){
            return str.replaceAll("\\[", "").replaceAll("]", "");
        }
        return null;
    }
    private static String generateUrlExclusion(String str){
    	if(str == null){
    		return "[]";
    	}
    	String strTrim = str.trim();
    	if("".equals(strTrim)){
    		return "[]";
    	}
    	StringBuffer sBuff = new StringBuffer("[");
    	String split[] = strTrim.split(",");
    	boolean isFirst = true;
    	for(String s:split){
    		String sTrim =s.trim();
    		if(!"".equals(sTrim)){
    			if(isFirst){
    				isFirst=false;
    			}else{
    				sBuff.append(",");
    			}
    			sBuff.append("\"");
    			sBuff.append(sTrim);
    			sBuff.append("\"");
    		}
    	}
    	sBuff.append("]");
    	return sBuff.toString();
    }
    
    public static JsonNode insert_site(Connection con, JsonNode jsonNode){
        if(jsonNode == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.SITE_OBJECT_NULL.getId());
            msg.setMsg(ErrorEnum.SITE_OBJECT_NULL.getName());
            return msg.toJson();
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            Site site = objectMapper.treeToValue(jsonNode, Site.class);
            return insert_site(con, site, true).toJson();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            return msg.toJson();
        }
    }

    public static Message insert_site(Connection con, Site site, boolean createTransaction){
        return insert_site(con,site,createTransaction,false);
    }

    public static Message insert_site(Connection con, Site site, boolean createTransaction, boolean userSpecifiedGuid){
        if(con == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            return msg;
        }
        if(site == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.SITE_OBJECT_NULL.getId());
            msg.setMsg(ErrorEnum.SITE_OBJECT_NULL.getName());
            return msg;
        }
        PreparedStatement pstmt = null;
        boolean autoCommitFlag = false;
        try{
            if(createTransaction){
                autoCommitFlag = con.getAutoCommit();
                con.setAutoCommit(false);
            }
            pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Site.insert_site,PreparedStatement.RETURN_GENERATED_KEYS);
            if(userSpecifiedGuid)
                pstmt.setString(1, site.getGuid());
            else
                pstmt.setString(1, SingletonUUIDGenerator.getSingletonUUIDGenerator().generateUniversallyUniqueIdentifier().toString());

            pstmt.setString(2, site.getName());
            pstmt.setInt(3, site.getPub_id());
            pstmt.setString(4, site.getPub_guid());
            pstmt.setString(5, site.getSite_url());
            pstmt.setString(6, site.getApp_id());
            pstmt.setInt(7, site.getApp_store_id());
            pstmt.setString(8, "{\"TIER1\":"+site.getCategories_tier_1_list()+",\"TIER2\":"+site.getCategories_tier_2_list()+"}");
            pstmt.setString(9, "{\"TIER1\":"+site.getCategory_list_inc_exc_tier_1()+",\"TIER2\":"+site.getCategory_list_inc_exc_tier_2()+"}");
            pstmt.setBoolean(10, site.isIs_category_list_excluded());
            pstmt.setString(11,ui_to_db_hygiene_list(site.getHygiene_list()));
            pstmt.setString(12, site.getOpt_in_hygiene_list());
            pstmt.setString(13, site.getCreative_attr_inc_exc());
            pstmt.setBoolean(14, site.isIs_creative_attr_exc());
            pstmt.setInt(15, site.getSite_platform_id().getPlatform());
            pstmt.setInt(16, site.getStatus_id());
            Timestamp ts = new Timestamp((new Date()).getTime());
            pstmt.setTimestamp(17, ts);
            pstmt.setInt(18, site.getModified_by());
            pstmt.setString(19, payout_percentage_to_billing_rule_json(site.getBilling_rules_json()));
            pstmt.setString(20, generateUrlExclusion(site.getUrl_exclusion()));
            pstmt.setBoolean(21, site.isAllow_house_ads());
            pstmt.setString(22, site.getComments());
            pstmt.setDouble(23, site.getFloor());
            pstmt.setBoolean(24, site.isIs_advertiser_excluded());
            pstmt.setString(25, generateAdvCampaign(site));
            pstmt.setString(26, generateNoFillPassBackContent(site));
            pstmt.setInt(27, site.getPassback_type());
            pstmt.setBoolean(28, site.isIs_richmedia_allowed());
            pstmt.setBoolean(29, site.isIs_native());
            pstmt.setString(30, generateNativeProps(site));
            pstmt.setBoolean(31, site.isIs_video());
            pstmt.setString(32, generateVideoSupplyProps(site));
            int returnCode = pstmt.executeUpdate();
            if(createTransaction){
                con.commit();
            }
            if(returnCode == 0){
                Message msg = new Message();
                msg.setError_code(ErrorEnum.SITE_NOT_INSERTED.getId());
                msg.setMsg(ErrorEnum.SITE_NOT_INSERTED.getName());
                return msg;
            }
            ResultSet keyResultSet = pstmt.getGeneratedKeys();
            int db_id = -1;
            if (keyResultSet.next()) {
                db_id = keyResultSet.getInt(1);
            }
            Message msg = new Message();
            msg.setError_code(ErrorEnum.NO_ERROR.getId());
            msg.setMsg(ErrorEnum.NO_ERROR.getName());
            msg.setId(db_id+"");
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
    
    public static JsonNode update_site(Connection con, JsonNode jsonNode){
        if(jsonNode == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.SITE_OBJECT_NULL.getId());
            msg.setMsg(ErrorEnum.SITE_OBJECT_NULL.getName());
            return msg.toJson();
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            Site site = objectMapper.treeToValue(jsonNode, Site.class);
            return update_site(con, site, true).toJson();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            return msg.toJson();
        }
    }

    public static Message update_site(Connection con, Site site, boolean createTransaction){
        if(con == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            return msg;
        }
        if(site == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.SITE_OBJECT_NULL.getId());
            msg.setMsg(ErrorEnum.SITE_OBJECT_NULL.getName());
            return msg;
        }
        PreparedStatement pstmt = null;
        boolean autoCommitFlag = false;
        try{
            if(createTransaction){
                autoCommitFlag = con.getAutoCommit();
                con.setAutoCommit(false);
            }
            pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Site.update_site);
            pstmt.setString(1, site.getName());
            pstmt.setString(2, site.getSite_url());
            pstmt.setString(3, site.getApp_id());
            pstmt.setInt(4, site.getApp_store_id());
            pstmt.setString(5, "{\"TIER1\":"+site.getCategories_tier_1_list()+",\"TIER2\":"+site.getCategories_tier_2_list()+"}");
            pstmt.setString(6, "{\"TIER1\":"+site.getCategory_list_inc_exc_tier_1()+",\"TIER2\":"+site.getCategory_list_inc_exc_tier_2()+"}");
            pstmt.setBoolean(7, site.isIs_category_list_excluded());
            pstmt.setString(8,ui_to_db_hygiene_list(site.getHygiene_list()));
            pstmt.setString(9, site.getOpt_in_hygiene_list());
            pstmt.setString(10, site.getCreative_attr_inc_exc());
            pstmt.setBoolean(11, site.isIs_creative_attr_exc());
            pstmt.setInt(12, site.getSite_platform_id().getPlatform());
            pstmt.setInt(13, site.getStatus_id());
            Timestamp ts = new Timestamp((new Date()).getTime());
            pstmt.setTimestamp(14, ts);
            pstmt.setInt(15, site.getModified_by());
            pstmt.setString(16, payout_percentage_to_billing_rule_json(site.getBilling_rules_json()));
            pstmt.setString(17, generateUrlExclusion(site.getUrl_exclusion()));
            pstmt.setBoolean(18, site.isAllow_house_ads());
            pstmt.setString(19, site.getComments());
            pstmt.setDouble(20, site.getFloor());
            pstmt.setBoolean(21, site.isIs_advertiser_excluded());
            pstmt.setString(22, generateAdvCampaign(site));
            pstmt.setString(23, generateNoFillPassBackContent(site));
            pstmt.setInt(24, site.getPassback_type());
            pstmt.setBoolean(25, site.isIs_richmedia_allowed());
            pstmt.setBoolean(26, site.isIs_native());
            pstmt.setString(27, generateNativeProps(site));
            pstmt.setBoolean(28, site.isIs_video());
            pstmt.setString(29, generateVideoSupplyProps(site));
            pstmt.setInt(30, site.getId());
            int returnCode = pstmt.executeUpdate();
            if(createTransaction){
                con.commit();
            }
            if(returnCode == 0){
                Message msg = new Message();
                msg.setError_code(ErrorEnum.SITE_NOT_UPDATED.getId());
                msg.setMsg(ErrorEnum.SITE_NOT_UPDATED.getName());
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

    public static JsonNode list_site(Connection con, JsonNode jsonNode){
        if(jsonNode == null){
            SiteList sitelist = new SiteList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.SITE_LIST_ENTITY_NULL.getId());
            msg.setMsg(ErrorEnum.SITE_LIST_ENTITY_NULL.getName());
            sitelist.setMsg(msg);
            return sitelist.toJson();
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            SiteListEntity sitelistEntity = objectMapper.treeToValue(jsonNode, SiteListEntity.class);
            return list_site(con, sitelistEntity).toJson();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            SiteList sitelist = new SiteList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            sitelist.setMsg(msg);
            return sitelist.toJson();
        }
    }
    
    public static SiteList list_site(Connection con, SiteListEntity sitelistEntity){
        if(con == null){
            SiteList sitelist = new SiteList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            sitelist.setMsg(msg);
            return sitelist;
        }
        if(sitelistEntity == null){
            SiteList sitelist = new SiteList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.SITE_LIST_ENTITY_NULL.getId());
            msg.setMsg(ErrorEnum.SITE_LIST_ENTITY_NULL.getName());
            sitelist.setMsg(msg);
            return sitelist;
        }
        PreparedStatement pstmt = null;
        try{
            
            if(sitelistEntity.getStatus_id() != -1){
                pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Site.list_sites_by_status);
                pstmt.setInt(1, sitelistEntity.getStatus_id());
                pstmt.setInt(2, sitelistEntity.getPage_no()*sitelistEntity.getPage_size());
                pstmt.setInt(3, sitelistEntity.getPage_size());
            }else{
                pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Site.list_sites);
                pstmt.setInt(1, sitelistEntity.getPage_no()*sitelistEntity.getPage_size());
                pstmt.setInt(2, sitelistEntity.getPage_size());
            }
            ResultSet rset = pstmt.executeQuery();
            SiteList sitelist = new SiteList();
            List<Site> sites = new LinkedList<Site>();
            while(rset.next()){
                Site site = new Site();
                populate(site, rset);
                sites.add(site);
            }
            sitelist.setSite_list(sites);
            Message msg = new Message();
            if(sites.size()>0){
                msg.setError_code(ErrorEnum.NO_ERROR.getId());
                msg.setMsg(ErrorEnum.NO_ERROR.getName());
            }else{
                msg.setError_code(ErrorEnum.SITE_NOT_FOUND.getId());
                msg.setMsg(ErrorEnum.SITE_NOT_FOUND.getName());
            }
            sitelist.setMsg(msg);
            return sitelist;
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            SiteList sitelist = new SiteList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.SQL_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.SQL_EXCEPTION.getName());
            sitelist.setMsg(msg);
            return sitelist;
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
    
    public static JsonNode get_site(Connection con, JsonNode jsonNode){
        if(con == null){
            SiteList sitelist = new SiteList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            sitelist.setMsg(msg);
            return sitelist.toJson();
        }
        if(jsonNode == null){
            SiteList sitelist = new SiteList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.SITE_LIST_ENTITY_NULL.getId());
            msg.setMsg(ErrorEnum.SITE_LIST_ENTITY_NULL.getName());
            sitelist.setMsg(msg);
            return sitelist.toJson();
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            SiteListEntity sitelistEntity = objectMapper.treeToValue(jsonNode, SiteListEntity.class);
            return list_site(con, sitelistEntity).toJson();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            SiteList sitelist = new SiteList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            sitelist.setMsg(msg);
            return sitelist.toJson();
        }
    }
    
    public static SiteList get_site(Connection con, SiteListEntity sitelistEntity){
        if(con == null){
            SiteList sitelist = new SiteList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            sitelist.setMsg(msg);
            return sitelist;
        }
        if(sitelistEntity == null){
            SiteList sitelist = new SiteList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.SITE_LIST_ENTITY_NULL.getId());
            msg.setMsg(ErrorEnum.SITE_LIST_ENTITY_NULL.getName());
            sitelist.setMsg(msg);
            return sitelist;
        }
        PreparedStatement pstmt = null;
        try{
            
            pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Site.get_Site);
            pstmt.setInt(1, sitelistEntity.getSite_int_id());
            ResultSet rset = pstmt.executeQuery();
            SiteList sitelist = new SiteList();
            List<Site> sites = new LinkedList<Site>();
            while(rset.next()){
                Site site = new Site();
                populate(site, rset);
                sites.add(site);
            }
            sitelist.setSite_list(sites);
            Message msg = new Message();
            if(sites.size()>0){
                msg.setError_code(ErrorEnum.NO_ERROR.getId());
                msg.setMsg(ErrorEnum.NO_ERROR.getName());
            }else{
                msg.setError_code(ErrorEnum.SITE_NOT_FOUND.getId());
                msg.setMsg(ErrorEnum.SITE_NOT_FOUND.getName());
            }
            sitelist.setMsg(msg);
            return sitelist;
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            SiteList sitelist = new SiteList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.SQL_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.SQL_EXCEPTION.getName());
            sitelist.setMsg(msg);
            return sitelist;
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

    public static Site get_site(Connection con, String siteGuid)
    {
        if(con == null || null == siteGuid)
            return null;

        PreparedStatement pstmt = null;
        Site site = null;
        try
        {
            pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Site.get_Site_Guid);
            pstmt.setString(1, siteGuid);

            ResultSet rset = pstmt.executeQuery();

            while(rset.next())
            {
                site = new Site();
                populate(site, rset);
            }

        }
        catch(Exception e)
        {
            LOG.error(e.getMessage(),e);
            return null;
        }
        finally
        {
            if(pstmt != null)
            {
                try
                {
                    pstmt.close();
                }
                catch (SQLException e)
                {
                    LOG.error(e.getMessage(),e);
                }
            }
        }

        return site;
    }

    public static Site get_site(Connection con, Integer siteIncId)
    {
        if(con == null || null == siteIncId)
            return null;

        PreparedStatement pstmt = null;
        Site site = null;
        try
        {
            pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Site.get_Site_Inc_id);
            pstmt.setInt(1, siteIncId);

            ResultSet rset = pstmt.executeQuery();

            while(rset.next())
            {
                site = new Site();
                populate(site, rset);
            }

        }
        catch(Exception e)
        {
            LOG.error(e.getMessage(),e);
            return null;
        }
        finally
        {
            if(pstmt != null)
            {
                try
                {
                    pstmt.close();
                }
                catch (SQLException e)
                {
                    LOG.error(e.getMessage(),e);
                }
            }
        }

        return site;
    }

    public static List<Site> get_site_by_pub_guid(Connection con, String pubGuid)
    {
        if(con == null || null == pubGuid)
            return null;

        List<Site> siteList = new ArrayList<Site>();

        PreparedStatement pstmt = null;
        Site site = null;
        try
        {
            pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Site.get_Site_by_pub_guid);
            pstmt.setString(1, pubGuid);

            ResultSet rset = pstmt.executeQuery();

            while(rset.next())
            {
                site = new Site();
                populate(site, rset);
                siteList.add(site);
            }

        }
        catch(Exception e)
        {
            LOG.error(e.getMessage(),e);
            return null;
        }
        finally
        {
            if(pstmt != null)
            {
                try
                {
                    pstmt.close();
                }
                catch (SQLException e)
                {
                    LOG.error(e.getMessage(),e);
                }
            }
        }

        return siteList;
    }

    public static JsonNode approve_site(Connection con, JsonNode jsonNode){
        if(jsonNode == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.SITE_OBJECT_NULL.getId());
            msg.setMsg(ErrorEnum.SITE_OBJECT_NULL.getName());
            return msg.toJson();
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            Site site = objectMapper.treeToValue(jsonNode, Site.class);
            return approve_site(con, site, true).toJson();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            return msg.toJson();
        }
    }

    public static Message approve_site(Connection con, Site site, boolean createTransaction){
        if(con == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            return msg;
        }
        if(site == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.SITE_OBJECT_NULL.getId());
            msg.setMsg(ErrorEnum.SITE_OBJECT_NULL.getName());
            return msg;
        }
        PreparedStatement pstmt = null;
        boolean autoCommitFlag = false;
        try{
            if(createTransaction){
                autoCommitFlag = con.getAutoCommit();
                con.setAutoCommit(false);
            }
            pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Site.approve_site);
            pstmt.setTimestamp(1, new Timestamp((new Date()).getTime()));
            pstmt.setInt(2, site.getId());
            int returnCode = pstmt.executeUpdate();
            if(createTransaction){
                con.commit();
            }
            if(returnCode == 0){
                Message msg = new Message();
                msg.setError_code(ErrorEnum.SITE_NOT_UPDATED.getId());
                msg.setMsg(ErrorEnum.SITE_NOT_UPDATED.getName());
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
    
    public static JsonNode reject_site(Connection con, JsonNode jsonNode){
        if(jsonNode == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.SITE_OBJECT_NULL.getId());
            msg.setMsg(ErrorEnum.SITE_OBJECT_NULL.getName());
            return msg.toJson();
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            Site site = objectMapper.treeToValue(jsonNode, Site.class);
            return reject_site(con, site, true).toJson();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            return msg.toJson();
        }
    }

    public static Message reject_site(Connection con, Site site, boolean createTransaction){
        if(con == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            return msg;
        }
        if(site == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.SITE_OBJECT_NULL.getId());
            msg.setMsg(ErrorEnum.SITE_OBJECT_NULL.getName());
            return msg;
        }
        PreparedStatement pstmt = null;
        boolean autoCommitFlag = false;
        try{
            if(createTransaction){
                autoCommitFlag = con.getAutoCommit();
                con.setAutoCommit(false);
            }
            pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Site.reject_site);
            pstmt.setString(1, site.getComments());
            pstmt.setTimestamp(2, new Timestamp((new Date()).getTime()));
            pstmt.setInt(3, site.getId());
            int returnCode = pstmt.executeUpdate();
            if(createTransaction){
                con.commit();
            }
            if(returnCode == 0){
                Message msg = new Message();
                msg.setError_code(ErrorEnum.SITE_NOT_UPDATED.getId());
                msg.setMsg(ErrorEnum.SITE_NOT_UPDATED.getName());
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
    public static JsonNode pause_site(Connection con, JsonNode jsonNode){
        if(jsonNode == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.SITE_OBJECT_NULL.getId());
            msg.setMsg(ErrorEnum.SITE_OBJECT_NULL.getName());
            return msg.toJson();
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            Site site = objectMapper.treeToValue(jsonNode, Site.class);
            return pause_site(con, site, true).toJson();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            return msg.toJson();
        }
    }

    public static Message pause_site(Connection con, Site site, boolean createTransaction){
        if(con == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            return msg;
        }
        if(site == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.SITE_OBJECT_NULL.getId());
            msg.setMsg(ErrorEnum.SITE_OBJECT_NULL.getName());
            return msg;
        }
        PreparedStatement pstmt = null;
        boolean autoCommitFlag = false;
        try{
            if(createTransaction){
                autoCommitFlag = con.getAutoCommit();
                con.setAutoCommit(false);
            }
            pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Site.pause_site);
            pstmt.setString(1, site.getComments());
            pstmt.setTimestamp(2, new Timestamp((new Date()).getTime()));
            pstmt.setInt(3, site.getId());
            int returnCode = pstmt.executeUpdate();
            if(createTransaction){
                con.commit();
            }
            if(returnCode == 0){
                Message msg = new Message();
                msg.setError_code(ErrorEnum.SITE_NOT_UPDATED.getId());
                msg.setMsg(ErrorEnum.SITE_NOT_UPDATED.getName());
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

    public static JsonNode list_site_by_account_guid(Connection con, JsonNode jsonNode){
        if(jsonNode == null){
            SiteList sitelist = new SiteList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.SITE_LIST_ENTITY_NULL.getId());
            msg.setMsg(ErrorEnum.SITE_LIST_ENTITY_NULL.getName());
            sitelist.setMsg(msg);
            return sitelist.toJson();
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            SiteListEntity sitelistEntity = objectMapper.treeToValue(jsonNode, SiteListEntity.class);
            return list_site_by_account_guid(con, sitelistEntity).toJson();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            SiteList sitelist = new SiteList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            sitelist.setMsg(msg);
            return sitelist.toJson();
        }
    }
    
    public static SiteList list_site_by_account_guid(Connection con, SiteListEntity sitelistEntity){
        if(con == null){
            SiteList sitelist = new SiteList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            sitelist.setMsg(msg);
            return sitelist;
        }
        if(sitelistEntity == null){
            SiteList sitelist = new SiteList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.SITE_LIST_ENTITY_NULL.getId());
            msg.setMsg(ErrorEnum.SITE_LIST_ENTITY_NULL.getName());
            sitelist.setMsg(msg);
            return sitelist;
        }
        PreparedStatement pstmt = null;
        try{
            
            pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Site.list_sites_by_account_guid);
            pstmt.setString(1, sitelistEntity.getPub_guid());
            pstmt.setInt(2, sitelistEntity.getPage_no()*sitelistEntity.getPage_size());
            pstmt.setInt(3, sitelistEntity.getPage_size());
            ResultSet rset = pstmt.executeQuery();
            SiteList sitelist = new SiteList();
            List<Site> sites = new LinkedList<Site>();
            while(rset.next()){
                Site site = new Site();
                populate(site, rset);
                sites.add(site);
            }
            sitelist.setSite_list(sites);
            Message msg = new Message();
            if(sites.size()>0){
                msg.setError_code(ErrorEnum.NO_ERROR.getId());
                msg.setMsg(ErrorEnum.NO_ERROR.getName());
            }else{
                msg.setError_code(ErrorEnum.SITE_NOT_FOUND.getId());
                msg.setMsg(ErrorEnum.SITE_NOT_FOUND.getName());
            }
            sitelist.setMsg(msg);
            return sitelist;
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            SiteList sitelist = new SiteList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.SQL_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.SQL_EXCEPTION.getName());
            sitelist.setMsg(msg);
            return sitelist;
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
    public static JsonNode list_site_by_account_id(Connection con, JsonNode jsonNode){
        if(jsonNode == null){
            SiteList sitelist = new SiteList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.SITE_LIST_ENTITY_NULL.getId());
            msg.setMsg(ErrorEnum.SITE_LIST_ENTITY_NULL.getName());
            sitelist.setMsg(msg);
            return sitelist.toJson();
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            SiteListEntity sitelistEntity = objectMapper.treeToValue(jsonNode, SiteListEntity.class);
            return list_site_by_account_id(con, sitelistEntity).toJson();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            SiteList sitelist = new SiteList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            sitelist.setMsg(msg);
            return sitelist.toJson();
        }
    }
    
    public static SiteList list_site_by_account_id(Connection con, SiteListEntity sitelistEntity){
        if(con == null){
            SiteList sitelist = new SiteList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            sitelist.setMsg(msg);
            return sitelist;
        }
        if(sitelistEntity == null){
            SiteList sitelist = new SiteList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.SITE_LIST_ENTITY_NULL.getId());
            msg.setMsg(ErrorEnum.SITE_LIST_ENTITY_NULL.getName());
            sitelist.setMsg(msg);
            return sitelist;
        }
        PreparedStatement pstmt = null;
        try{
            
            pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Site.list_sites_by_account_id);
            pstmt.setInt(1, sitelistEntity.getPub_id());
            pstmt.setInt(2, sitelistEntity.getPage_no()*sitelistEntity.getPage_size());
            pstmt.setInt(3, sitelistEntity.getPage_size());
            ResultSet rset = pstmt.executeQuery();
            SiteList sitelist = new SiteList();
            List<Site> sites = new LinkedList<Site>();
            while(rset.next()){
                Site site = new Site();
                populate(site, rset);
                sites.add(site);
            }
            sitelist.setSite_list(sites);
            Message msg = new Message();
            if(sites.size()>0){
                msg.setError_code(ErrorEnum.NO_ERROR.getId());
                msg.setMsg(ErrorEnum.NO_ERROR.getName());
            }else{
                msg.setError_code(ErrorEnum.SITE_NOT_FOUND.getId());
                msg.setMsg(ErrorEnum.SITE_NOT_FOUND.getName());
            }
            sitelist.setMsg(msg);
            return sitelist;
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            SiteList sitelist = new SiteList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.SQL_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.SQL_EXCEPTION.getName());
            sitelist.setMsg(msg);
            return sitelist;
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

    public static JsonNode list_site_by_account_ids(Connection con, JsonNode jsonNode){
        if(jsonNode == null){
            SiteList sitelist = new SiteList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.SITE_LIST_ENTITY_NULL.getId());
            msg.setMsg(ErrorEnum.SITE_LIST_ENTITY_NULL.getName());
            sitelist.setMsg(msg);
            return sitelist.toJson();
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            SiteListEntity sitelistEntity = objectMapper.treeToValue(jsonNode, SiteListEntity.class);
            return list_site_by_account_ids(con, sitelistEntity).toJson();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            SiteList sitelist = new SiteList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            sitelist.setMsg(msg);
            return sitelist.toJson();
        }
    }
    
    public static SiteList list_site_by_account_ids(Connection con, SiteListEntity sitelistEntity){
        if(con == null){
            SiteList sitelist = new SiteList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            sitelist.setMsg(msg);
            return sitelist;
        }
        if(sitelistEntity == null){
            SiteList sitelist = new SiteList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.SITE_LIST_ENTITY_NULL.getId());
            msg.setMsg(ErrorEnum.SITE_LIST_ENTITY_NULL.getName());
            sitelist.setMsg(msg);
            return sitelist;
        }
        PreparedStatement pstmt = null;
        try{
            if("ALL".equalsIgnoreCase(sitelistEntity.getId_list())){
                pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Site.list_sites_by_all_account_ids);
            }else{
                pstmt = con.prepareStatement(InQueryPrepareStmnt.createInQueryPrepareStatement(
                    com.kritter.kritterui.api.db_query_def.Site.list_sites_by_account_ids, "<id>", sitelistEntity.getId_list(), 
                    ",", false));
            }
            pstmt.setInt(1, sitelistEntity.getPage_no()*sitelistEntity.getPage_size());
            pstmt.setInt(2, sitelistEntity.getPage_size());
            ResultSet rset = pstmt.executeQuery();
            SiteList sitelist = new SiteList();
            List<Site> sites = new LinkedList<Site>();
            while(rset.next()){
                Site site = new Site();
                populate(site, rset);
                sites.add(site);
            }
            sitelist.setSite_list(sites);
            Message msg = new Message();
            if(sites.size()>0){
                msg.setError_code(ErrorEnum.NO_ERROR.getId());
                msg.setMsg(ErrorEnum.NO_ERROR.getName());
            }else{
                msg.setError_code(ErrorEnum.SITE_NOT_FOUND.getId());
                msg.setMsg(ErrorEnum.SITE_NOT_FOUND.getName());
            }
            sitelist.setMsg(msg);
            return sitelist;
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            SiteList sitelist = new SiteList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.SQL_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.SQL_EXCEPTION.getName());
            sitelist.setMsg(msg);
            return sitelist;
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
    
    public static JsonNode change_site_status(Connection con, JsonNode jsonNode){
        if(jsonNode == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.SITE_LIST_ENTITY_NULL.getId());
            msg.setMsg(ErrorEnum.SITE_LIST_ENTITY_NULL.getName());
            return msg.toJson();
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            SiteListEntity siteListEntity = objectMapper.treeToValue(jsonNode, SiteListEntity.class);
            return change_site_status(con, siteListEntity, true).toJson();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            return msg.toJson();
        }
    }

    public static Message change_site_status(Connection con, SiteListEntity siteListEntity, boolean createTransaction){
        if(con == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            return msg;
        }
        if(siteListEntity == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.SITE_LIST_ENTITY_NULL.getId());
            msg.setMsg(ErrorEnum.SITE_LIST_ENTITY_NULL.getName());
            return msg;
        }
        PreparedStatement pstmt = null;
        boolean autoCommitFlag = false;
        try{
            if(createTransaction){
                autoCommitFlag = con.getAutoCommit();
                con.setAutoCommit(false);
            }
            switch(siteListEntity.getSiteApiEnum()){
            case approve_multiple_site:
                String str = siteListEntity.getId_list(); 
                if(str == null || "ALL".equalsIgnoreCase(str) || "".equalsIgnoreCase(str)){
                    Message msg = new Message();
                    msg.setError_code(ErrorEnum.SITE_NOT_UPDATED.getId());
                    msg.setMsg(ErrorEnum.SITE_NOT_UPDATED.getName());
                    return msg;
                }else{
                    pstmt = con.prepareStatement(InQueryPrepareStmnt.createInQueryPrepareStatement(
                        com.kritter.kritterui.api.db_query_def.Site.approve_multiple_site, "<id>", siteListEntity.getId_list(), 
                        ",", false));
                }
                pstmt.setTimestamp(1, new Timestamp(new Date().getTime()));
                break;
            case reject_multiple_site:
                String str1 = siteListEntity.getId_list(); 
                if(str1 == null || "ALL".equalsIgnoreCase(str1) || "".equalsIgnoreCase(str1)){
                    Message msg = new Message();
                    msg.setError_code(ErrorEnum.SITE_NOT_UPDATED.getId());
                    msg.setMsg(ErrorEnum.SITE_NOT_UPDATED.getName());
                    return msg;
                }else{
                    pstmt = con.prepareStatement(InQueryPrepareStmnt.createInQueryPrepareStatement(
                        com.kritter.kritterui.api.db_query_def.Site.reject_multiple_site, "<id>", siteListEntity.getId_list(), 
                        ",", false));
                }
                pstmt.setTimestamp(1, new Timestamp(new Date().getTime()));
                break;
            case pause_multiple_site:
                String str2 = siteListEntity.getId_list(); 
                if(str2 == null || "ALL".equalsIgnoreCase(str2) || "".equalsIgnoreCase(str2)){
                    Message msg = new Message();
                    msg.setError_code(ErrorEnum.SITE_NOT_UPDATED.getId());
                    msg.setMsg(ErrorEnum.SITE_NOT_UPDATED.getName());
                    return msg;
                }else{
                    pstmt = con.prepareStatement(InQueryPrepareStmnt.createInQueryPrepareStatement(
                        com.kritter.kritterui.api.db_query_def.Site.pause_multiple_site, "<id>", siteListEntity.getId_list(), 
                        ",", false));
                }
                pstmt.setTimestamp(1, new Timestamp(new Date().getTime()));
                break;
            default:
                break;
            }
            int returnCode = pstmt.executeUpdate();
            if(createTransaction){
                con.commit();
            }
            if(returnCode == 0){
                Message msg = new Message();
                msg.setError_code(ErrorEnum.SITE_NOT_UPDATED.getId());
                msg.setMsg(ErrorEnum.SITE_NOT_UPDATED.getName());
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
    public static JsonNode change_site_payout(Connection con, JsonNode jsonNode){
        if(jsonNode == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.SITE_LIST_ENTITY_NULL.getId());
            msg.setMsg(ErrorEnum.SITE_LIST_ENTITY_NULL.getName());
            return msg.toJson();
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            SiteListEntity siteListEntity = objectMapper.treeToValue(jsonNode, SiteListEntity.class);
            return change_site_payout(con, siteListEntity, true).toJson();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            return msg.toJson();
        }
    }

    public static Message change_site_payout(Connection con, SiteListEntity siteListEntity, boolean createTransaction){
        if(con == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            return msg;
        }
        if(siteListEntity == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.SITE_LIST_ENTITY_NULL.getId());
            msg.setMsg(ErrorEnum.SITE_LIST_ENTITY_NULL.getName());
            return msg;
        }
        PreparedStatement pstmt = null;
        boolean autoCommitFlag = false;
        try{
            if(createTransaction){
                autoCommitFlag = con.getAutoCommit();
                con.setAutoCommit(false);
            }
            switch(siteListEntity.getSiteApiEnum()){
            case change_site_payout:
                pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Site.change_site_payout);
                pstmt.setString(1, siteListEntity.getBilling_rules_json());
                pstmt.setTimestamp(2, new Timestamp(new Date().getTime()));
                pstmt.setInt(3, siteListEntity.getPub_id());
                pstmt.setString(4, "{\"payout\":"+Payout.default_payout_percent_str+"}");
                break;
            default:
                break;
            }
            int returnCode = pstmt.executeUpdate();
            if(createTransaction){
                con.commit();
            }
            if(returnCode == 0){
                Message msg = new Message();
                msg.setError_code(ErrorEnum.SITE_NOT_UPDATED.getId());
                msg.setMsg(ErrorEnum.SITE_NOT_UPDATED.getName());
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
    public static JsonNode list_site_by_url(Connection con, JsonNode jsonNode){
        if(jsonNode == null){
            SiteList sitelist = new SiteList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.SITE_LIST_ENTITY_NULL.getId());
            msg.setMsg(ErrorEnum.SITE_LIST_ENTITY_NULL.getName());
            sitelist.setMsg(msg);
            return sitelist.toJson();
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            SiteListEntity sitelistEntity = objectMapper.treeToValue(jsonNode, SiteListEntity.class);
            return list_site_by_url(con, sitelistEntity).toJson();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            SiteList sitelist = new SiteList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            sitelist.setMsg(msg);
            return sitelist.toJson();
        }
    }
    
    public static SiteList list_site_by_url(Connection con, SiteListEntity sitelistEntity){
        if(con == null){
            SiteList sitelist = new SiteList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            sitelist.setMsg(msg);
            return sitelist;
        }
        if(sitelistEntity == null){
            SiteList sitelist = new SiteList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.SITE_LIST_ENTITY_NULL.getId());
            msg.setMsg(ErrorEnum.SITE_LIST_ENTITY_NULL.getName());
            sitelist.setMsg(msg);
            return sitelist;
        }
        PreparedStatement pstmt = null;
        try{
            
            pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Site.list_sites_by_url);
            pstmt.setString(1, "%"+sitelistEntity.getUrl()+"%");
            ResultSet rset = pstmt.executeQuery();
            SiteList sitelist = new SiteList();
            List<Site> sites = new LinkedList<Site>();
            while(rset.next()){
                Site site = new Site();
                populate(site, rset);
                sites.add(site);
            }
            sitelist.setSite_list(sites);
            Message msg = new Message();
            if(sites.size()>0){
                msg.setError_code(ErrorEnum.NO_ERROR.getId());
                msg.setMsg(ErrorEnum.NO_ERROR.getName());
            }else{
                msg.setError_code(ErrorEnum.SITE_NOT_FOUND.getId());
                msg.setMsg(ErrorEnum.SITE_NOT_FOUND.getName());
            }
            sitelist.setMsg(msg);
            return sitelist;
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            SiteList sitelist = new SiteList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.SQL_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.SQL_EXCEPTION.getName());
            sitelist.setMsg(msg);
            return sitelist;
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


}
