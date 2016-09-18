package com.kritter.kritterui.api.ad;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kritter.api.entity.ad.Ad;
import com.kritter.api.entity.ad.AdList;
import com.kritter.api.entity.ad.AdListEntity;
import com.kritter.api.entity.response.msg.Message;
import com.kritter.constants.CategoryTier;
import com.kritter.constants.FreqDuration;
import com.kritter.constants.FreqEventType;
import com.kritter.constants.MarketPlace;
import com.kritter.constants.StatusIdEnum;
import com.kritter.constants.error.ErrorEnum;
import com.kritter.constants.tracking_partner.TrackingPartner;
import com.kritter.entity.ad_ext.AdExt;
import com.kritter.entity.external_tracker.ExtTracker;
import com.kritter.entity.freqcap_entity.FreqCap;
import com.kritter.entity.freqcap_entity.FreqDef;
import com.kritter.kritterui.api.utils.InQueryPrepareStmnt;
import com.kritter.utils.uuid.mac.SingletonUUIDGenerator;

public class AdCrud {
    private static final Logger LOG = LoggerFactory.getLogger(AdCrud.class);
    public static void populate(Ad ad, ResultSet rset, boolean tp_name) throws SQLException{
        if(ad != null && rset != null){
            ad.setAdvertiser_bid(rset.getDouble("advertiser_bid"));
            ad.setAllocation_ids(rset.getString("allocation_ids"));
            ad.setCampaign_guid(rset.getString("campaign_guid"));
            ad.setCampaign_id(rset.getInt("campaign_id"));
            String categories_list = rset.getString("categories_list");
            if(categories_list != null){
                ObjectMapper mapper = new ObjectMapper();
                try {
                    JsonNode node = mapper.readValue(categories_list,JsonNode.class);
                    JsonNode tier1node = node.get(CategoryTier.TIER1.getName());
                    if(tier1node != null){
                        ad.setCategories_tier_1_list(tier1node.toString());
                    }
                    JsonNode tier2node = node.get(CategoryTier.TIER2.getName());
                    if(tier2node != null){
                        ad.setCategories_tier_2_list(tier2node.toString());
                    }
                } catch (Exception  e) {
                    LOG.error(e.getMessage(),e);
                }
            }

            ad.setCreative_guid(rset.getString("creative_guid"));
            ad.setCreative_id(rset.getInt("creative_id"));
            ad.setGuid(rset.getString("guid"));
            ad.setHygiene_list(rset.getString("hygiene_list"));
            ad.setId(rset.getInt("id"));
            ad.setInternal_max_bid(rset.getDouble("internal_max_bid"));
            ad.setLanding_url(rset.getString("landing_url"));
            ad.setMarketplace_id(MarketPlace.getMarketPlace(rset.getInt("marketplace_id")));
            ad.setModified_by(rset.getInt("modified_by"));
            ad.setName(rset.getString("name"));
            ad.setStatus_id(StatusIdEnum.getEnum(rset.getInt("status_id")));
            ad.setTargeting_guid(rset.getString("targeting_guid"));
            ad.setComment(rset.getString("comment"));
            ad.setCreated_on(rset.getTimestamp("created_on").getTime());
            ad.setTracking_partner(TrackingPartner.getEnum(rset.getInt("tracking_partner")));
            if(tp_name){
                ad.setTargeting_profile_name(rset.getString("targeting_profile_name"));
            }
            ad.setCpa_goal(rset.getDouble("cpa_goal"));
            String adv_domain = rset.getString("adv_domain");
            if(adv_domain != null){
                ad.setAdv_domain(adv_domain.replaceAll("\\[", "").replaceAll("]", "").replaceAll("\"",""));
            }
            boolean is_frequency_capped = rset.getBoolean("is_frequency_capped");
            ad.setIs_frequency_capped(is_frequency_capped);
            ad.setFrequency_cap(rset.getInt("frequency_cap"));
            ad.setTime_window(rset.getInt("time_window"));
            ad.setBidtype(rset.getInt("bidtype"));
            populateExternalTracker(ad, rset.getString("external_tracker"));
            populateExt(ad, rset.getString("ext"));
            populateFreqCap(ad, rset.getString("freqcap_json"));
        }
    }
    private static void populateFreqCap(Ad ad,String freqcap){
    	if(freqcap == null || ad == null){
    		return;
    	}
    	if(!ad.isIs_frequency_capped()){
    		return;
    	}
    	String freqcapTrim = freqcap.trim();
    	if("".equals(freqcapTrim)){
    		return;
    	}
    	try{
    		FreqCap freqCapObj = FreqCap.getObject(freqcapTrim);
    		if(freqCapObj != null && freqCapObj.getFDef()!=null){
    			Set<FreqDef> clickDef = freqCapObj.getFDef().get(FreqEventType.CLK);
    			if(clickDef != null){
    				ad.setClick_freq_cap(true);
    				for(FreqDef f:clickDef){
    					switch(f.getDuration()){
    					case LIFE:
    						ad.setClick_freq_cap_type(FreqDuration.LIFE.getCode());
    						ad.setClick_freq_cap_count(f.getCount());
    						ad.setClick_freq_time_window(-1);
    						break;
    					case BYHOUR:
    						ad.setClick_freq_cap_type(FreqDuration.BYHOUR.getCode());
    						ad.setClick_freq_cap_count(f.getCount());
    						ad.setClick_freq_time_window(f.getHour());
    						break;
    					case BYDAY:
    						ad.setClick_freq_cap_type(FreqDuration.BYDAY.getCode());
    						ad.setClick_freq_cap_count(f.getCount());
    						ad.setClick_freq_time_window(24);
    					default:
    						break;
    					}
    				}
    			}
				Set<FreqDef> impDef = freqCapObj.getFDef().get(FreqEventType.IMP);
				if(impDef != null){
					ad.setImp_freq_cap(true);
					for(FreqDef f:impDef){
						switch(f.getDuration()){
						case LIFE:
							ad.setImp_freq_cap_type(FreqDuration.LIFE.getCode());
							ad.setImp_freq_cap_count(f.getCount());
							ad.setImp_freq_time_window(-1);
							break;
						case BYHOUR:
							ad.setImp_freq_cap_type(FreqDuration.BYHOUR.getCode());
							ad.setImp_freq_cap_count(f.getCount());
							ad.setImp_freq_time_window(f.getHour());
							break;
						case BYDAY:
							ad.setImp_freq_cap_type(FreqDuration.BYDAY.getCode());
							ad.setImp_freq_cap_count(f.getCount());
							ad.setImp_freq_time_window(24);
							break;
						default:
							break;
						}
					}
				}
    		}
    	}catch(Exception e){
    		LOG.error(e.getMessage(),e);
    	}
    }
    private static String generateFreqCap(Ad ad){
    	if(ad==null || !ad.isIs_frequency_capped()){
    		return "{}";
    	}
    	FreqCap f = new FreqCap();
    	Map<FreqEventType, Set<FreqDef>> map = new HashMap<FreqEventType, Set<FreqDef>>();
    	if(ad.isClick_freq_cap()){
    		Set<FreqDef> set = new HashSet<FreqDef>();
    		FreqDef fdef= new FreqDef();
    		
    		FreqDuration fDur = FreqDuration.getEnum(ad.getClick_freq_cap_type());
    		if(fDur==null){
    			fDur=FreqDuration.BYHOUR;
    		}
    		fdef.setDuration(fDur);
    		int count = ad.getClick_freq_cap_count();
    		fdef.setCount(count);
    		if(fDur != FreqDuration.LIFE){
    			fdef.setHour(ad.getClick_freq_time_window());
    		}
    		if(fDur == FreqDuration.BYDAY){
    			fdef.setHour(24);
    		}
    		set.add(fdef);
    		map.put(FreqEventType.CLK, set);
    	}
    	if(ad.isImp_freq_cap()){
    		Set<FreqDef> set = new HashSet<FreqDef>();
    		FreqDef fdef= new FreqDef();
    		FreqDuration fDur = FreqDuration.getEnum(ad.getImp_freq_cap_type());
    		if(fDur==null){
    			fDur=FreqDuration.BYHOUR;
    		}
    		fdef.setDuration(fDur);
    		int count = ad.getImp_freq_cap_count();
    		fdef.setCount(count);
    		if(fDur != FreqDuration.LIFE){
    			fdef.setHour(ad.getImp_freq_time_window());
    		}
    		if(fDur == FreqDuration.BYDAY){
    			fdef.setHour(24);
    		}
    		set.add(fdef);
    		map.put(FreqEventType.IMP, set);
    	}
    	f.setFDef(map);
    	return f.toJson().toString();
    }
    private static void populateExt(Ad ad,String ext){
    	if(ext == null){
    		return;
    	}
    	String extTrim = ext.trim();
    	try{
    		AdExt adExt = AdExt.getObject(extTrim);
    		if(adExt != null){
    			if(adExt.getMma_tier1() != null){
    				StringBuffer sBuff = new StringBuffer("[");
    				boolean isFirst = true;
    				for(Integer i:adExt.getMma_tier1() ){
    					if(isFirst){
    						isFirst = false;
    					}else{
    						sBuff.append(",");
    					}
    					sBuff.append(i);
    				}
    				sBuff.append("]");
    				ad.setMma_tier_1_list(sBuff.toString());
    			}
    			if(adExt.getMma_tier2() != null){
    				StringBuffer sBuff = new StringBuffer("[");
    				boolean isFirst = true;
    				for(Integer i:adExt.getMma_tier2() ){
    					if(isFirst){
    						isFirst = false;
    					}else{
    						sBuff.append(",");
    					}
    					sBuff.append(i);
    				}
    				sBuff.append("]");
    				ad.setMma_tier_2_list(sBuff.toString());
    			}
    		}
    	}catch(Exception e){
    		LOG.error(e.getMessage(),e);
    	}
    	
    }
    
    private static String generateExt(Ad ad){
		AdExt adExt = new AdExt();
    	if(ad.getMma_tier_1_list() != null){
    		HashSet<Integer> t1Set = new HashSet<Integer>();
    		String t1 = ad.getMma_tier_1_list().trim().replaceAll("\\[", "").replaceAll("]", "").replaceAll("\"", "");
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
    		adExt.setMma_tier1(t1Set);
        	if(ad.getMma_tier_2_list() != null){
        		HashSet<Integer> t2Set = new HashSet<Integer>();
        		String t2 = ad.getMma_tier_2_list().trim().replaceAll("\\[", "").replaceAll("]", "").replaceAll("\"", "");
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
        		adExt.setMma_tier2(t2Set);
        	}
    	}
    	return adExt.toJson().toString();
    }
    private static void populateExternalTracker(Ad ad, String external_Tracker){
        try{
        if(external_Tracker != null){
            String external_TrackerTrim = external_Tracker.trim();
            if(!"".equals(external_TrackerTrim)){
                ExtTracker extTracker = ExtTracker.getObject(external_Tracker);
                if(extTracker != null){
                    if(extTracker.getImpTracker() !=null && extTracker.getImpTracker().size()>0){
                        StringBuffer sBuff = new StringBuffer("");
                        boolean isFirst = true;
                        for(String str:extTracker.getImpTracker()){
                            if(isFirst){
                                isFirst=false;
                            }else{
                                sBuff.append("\n");
                            }
                            sBuff.append(str);
                        }
                        ad.setExternal_imp_tracker(sBuff.toString());
                    }
                    if(extTracker.getClickTracker() !=null && extTracker.getClickTracker().size()>0){
                        StringBuffer sBuff = new StringBuffer("");
                        boolean isFirst = true;
                        for(String str:extTracker.getClickTracker()){
                            if(isFirst){
                                isFirst=false;
                            }else{
                                sBuff.append("\n");
                            }
                            sBuff.append(str);
                        }
                        ad.setExternal_click_tracker(sBuff.toString());
                    }
                    Set<Integer> set = extTracker.getImpMacro();
                    if(set != null){
                        StringBuffer sbuff = new StringBuffer("[");
                        boolean isFirst=true;
                        for(Integer i:set){
                            if(isFirst){
                                isFirst=false;
                            }else{
                                sbuff.append(",");
                            }
                            sbuff.append(i);
                        }
                        sbuff.append("]");
                        ad.setImpMacro(sbuff.toString());
                    }
                    if(extTracker.getImpMacroQuote() != null){
                  	  ad.setImpMacroQuote(extTracker.getImpMacroQuote());
                    }
                    Set<Integer> clickSet = extTracker.getClickMacro();
                    if(clickSet != null){
                        StringBuffer sbuff = new StringBuffer("[");
                        boolean isFirst=true;
                        for(Integer i:clickSet){
                            if(isFirst){
                                isFirst=false;
                            }else{
                                sbuff.append(",");
                            }
                            sbuff.append(i);
                        }
                        sbuff.append("]");
                        ad.setClickMacro(sbuff.toString());
                    }
                    if(extTracker.getClickMacroQuote() != null){
                  	  ad.setClickMacroQuote(extTracker.getClickMacroQuote());
                    }
                    if(extTracker.getClickType() != null){
                    	  ad.setExtclickType(extTracker.getClickType());
                      }

                }
            }
        }
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
        }
        
    }
    private static String generateExternalTracker(Ad ad){
        ExtTracker extTracker = new ExtTracker();
        if(ad.getExternal_imp_tracker() != null){
            String external_tracker = ad.getExternal_imp_tracker().trim();
            String split[] = external_tracker.split("\n");
            boolean isFirst = true;
            List<String> extTrackerUriList = null;
            for(String str:split){
                String strTrim = str.trim();
                if(!"".equals(strTrim)){
                    if(isFirst){
                        isFirst=false;
                        extTrackerUriList = new LinkedList<String>();
                    }
                    extTrackerUriList.add(strTrim);
                }
            }
            extTracker.setImpTracker(extTrackerUriList);
            Set<Integer> s = stringtoset(ad.getImpMacro());
            if(s != null){
            	extTracker.setImpMacro(s);
                extTracker.setImpMacroQuote(ad.getImpMacroQuote());
            }
        }
        if(ad.getExternal_click_tracker() != null){
            String external_tracker = ad.getExternal_click_tracker().trim();
            String split[] = external_tracker.split("\n");
            boolean isFirst = true;
            List<String> extTrackerUriList = null;
            for(String str:split){
                String strTrim = str.trim();
                if(!"".equals(strTrim)){
                    if(isFirst){
                        isFirst=false;
                        extTrackerUriList = new LinkedList<String>();
                    }
                    extTrackerUriList.add(strTrim);
                }
            }
            extTracker.setClickTracker(extTrackerUriList);
            Set<Integer> s = stringtoset(ad.getClickMacro());
            extTracker.setClickType(ad.getExtclickType());
            if(s != null){
            	extTracker.setClickMacro(s);
                extTracker.setClickMacroQuote(ad.getClickMacroQuote());
            }
        }
        return extTracker.toJson().toString();
    }
    private static Set<Integer> stringtoset(String str){
        try{
            if(str==null){
                return null;
            }
            String strTrim = str.trim();
            if("".equals(strTrim)){
                return null;
            }
            String strNew = strTrim.replaceAll("\\[", "").replaceAll("]", "");
            String strSplit[] = strNew.split(",");
            if(strSplit.length>0){
                Set<Integer> ll = new HashSet<Integer>();
                for(String s:strSplit){
                    ll.add(Integer.parseInt(s));
                }
                return ll;
            }
            return null;
        }catch(Exception e){
            return null;
        }
    }

    public static List<Ad> geAdByCampaignGuid(String adGuid,Connection connection)
    {
        PreparedStatement pstmt = null;
        List<Ad> adList = new ArrayList<Ad>();
        try {

            pstmt = connection.prepareStatement(com.kritter.kritterui.api.db_query_def.Ad.get_ad_by_guid);
            pstmt.setString(1,adGuid);

            ResultSet rs = pstmt.executeQuery();



            while(rs.next())
            {
                Ad ad = new Ad();
                populate(ad,rs,false);
                adList.add(ad);
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

        return adList;
    }

    public static Ad geAdByGuid(String adGuid,Connection connection)
    {
        PreparedStatement pstmt = null;

        try {

            pstmt = connection.prepareStatement(com.kritter.kritterui.api.db_query_def.Ad.get_ad_by_guid);
            pstmt.setString(1,adGuid);

            ResultSet rs = pstmt.executeQuery();

            Ad ad = null;

            while(rs.next())
            {
                ad = new Ad();
                populate(ad,rs,false);
                return ad;
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

    public static String generateAdvDomain(Ad ad){
        if(ad == null || ad.getAdv_domain() == null){
            return "";
        }
        StringBuffer sbuff = new StringBuffer("[");
        String strSplit[] = ad.getAdv_domain().split(",");
        boolean isFirst  = true;
        for(String str:strSplit){
            if(!"".equals(str)){
                if(isFirst){
                    isFirst=false;
                }else{
                    sbuff.append(",");
                }
                sbuff.append("\"");sbuff.append(str);sbuff.append("\"");
            }
        }
        sbuff.append("]");
        return sbuff.toString();
    }
    
    public static JsonNode insert_ad(Connection con, JsonNode jsonNode){
        if(jsonNode == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.AD_NULL.getId());
            msg.setMsg(ErrorEnum.AD_NULL.getName());
            return msg.toJson();
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            Ad ad = objectMapper.treeToValue(jsonNode, Ad.class);
            return insert_ad(con, ad, true).toJson();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            return msg.toJson();
        }
    }

    public static Message insert_ad(Connection con, Ad ad , boolean createTransaction){
        return insert_ad_using_provided_guid(con,ad,createTransaction,false);
    }

    public static Message insert_ad_using_provided_guid(Connection con, Ad ad , boolean createTransaction,
                                                        boolean useProvidedGuid){
        if(con == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            return msg;
        }
        if(ad == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.AD_NULL.getId());
            msg.setMsg(ErrorEnum.AD_NULL.getName());
            return msg;
        }
        PreparedStatement pstmt = null;
        boolean autoCommitFlag = false;
        try{
            if(createTransaction){
                autoCommitFlag = con.getAutoCommit();
                con.setAutoCommit(false);
            }
            pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Ad.insert_ad, PreparedStatement.RETURN_GENERATED_KEYS);
            String adGuid = SingletonUUIDGenerator.getSingletonUUIDGenerator().generateUniversallyUniqueIdentifier().toString();
            if(useProvidedGuid)
                adGuid = ad.getGuid();

            pstmt.setString(1,adGuid);
            pstmt.setString(2, ad.getName());
            pstmt.setInt(3, ad.getCreative_id());
            pstmt.setString(4, ad.getCreative_guid());
            pstmt.setString(5, ad.getLanding_url());
            pstmt.setString(6, ad.getTargeting_guid());
            pstmt.setInt(7, ad.getCampaign_id());
            pstmt.setString(8, ad.getCampaign_guid());
            pstmt.setString(9, "{\"TIER1\":"+ad.getCategories_tier_1_list()+",\"TIER2\":"+ad.getCategories_tier_2_list()+"}");
            pstmt.setString(10, ad.getHygiene_list());
            pstmt.setInt(11, ad.getStatus_id().getCode());
            pstmt.setInt(12, ad.getMarketplace_id().getCode());
            pstmt.setDouble(13, ad.getInternal_max_bid());
            pstmt.setDouble(14, ad.getAdvertiser_bid());
            pstmt.setString(15, ad.getAllocation_ids());
            pstmt.setTimestamp(16, new Timestamp(new Date().getTime()));
            pstmt.setInt(17, ad.getModified_by());
            pstmt.setInt(18, ad.getTracking_partner().getCode());
            pstmt.setDouble(19, ad.getCpa_goal());
            pstmt.setString(20, generateAdvDomain(ad));
            pstmt.setBoolean(21, ad.isIs_frequency_capped());
            pstmt.setInt(22, ad.getFrequency_cap());
            pstmt.setInt(23, ad.getTime_window());
            pstmt.setInt(24, ad.getBidtype());
            pstmt.setString(25, generateExternalTracker(ad));
            pstmt.setString(26, generateExt(ad));
            pstmt.setString(27, generateFreqCap(ad));
            int returnCode = pstmt.executeUpdate();
            if(createTransaction){
                con.commit();
            }
            if(returnCode == 0){
                Message msg = new Message();
                msg.setError_code(ErrorEnum.AD_NOT_INSERTED.getId());
                msg.setMsg(ErrorEnum.AD_NOT_INSERTED.getName());
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
    
    
    
    public static JsonNode various_get_ad(Connection con, JsonNode jsonNode){
        if(jsonNode == null){
            AdList adlist = new AdList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.AD_LIST_ENTITY_NULL.getId());
            msg.setMsg(ErrorEnum.AD_LIST_ENTITY_NULL.getName());
            adlist.setMsg(msg);
            return adlist.toJson();
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            AdListEntity adlistEntity = objectMapper.treeToValue(jsonNode, AdListEntity.class);
            return various_get_ad(con, adlistEntity).toJson();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            AdList adlist = new AdList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            adlist.setMsg(msg);
            return adlist.toJson();
        }
    }
    
    public static AdList various_get_ad(Connection con, AdListEntity adlistEntity){
        if(con == null){
            AdList adlist = new AdList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            adlist.setMsg(msg);
            return adlist;
        }
        if(adlistEntity == null){
            AdList adlist = new AdList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.AD_LIST_ENTITY_NULL.getId());
            msg.setMsg(ErrorEnum.AD_LIST_ENTITY_NULL.getName());
            adlist.setMsg(msg);
            return adlist;
        }
        boolean tp_name = false;
        PreparedStatement pstmt = null;
        try{
            switch (adlistEntity.getAdenum()){
                case get_ad:
                    pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Ad.get_ad);
                    pstmt.setInt(1, adlistEntity.getId());
                    break;
                case list_ad_by_campaign:
                    pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Ad.list_ad_by_campaign);
                    pstmt.setInt(1, adlistEntity.getCampaign_id());
                    break;
                case list_ad_by_campaign_with_tp_name:
                    pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Ad.list_ad_by_campaign_with_tp_name);
                    pstmt.setInt(1, adlistEntity.getCampaign_id());
                    tp_name = true;
                    break;
                case list_ad_by_campaigns:
                    if("none".equalsIgnoreCase(adlistEntity.getId_list()) || "[none]".equalsIgnoreCase(adlistEntity.getId_list())){
                        AdList adlist = new AdList();
                        Message msg = new Message();
                        msg.setError_code(ErrorEnum.NO_ERROR.getId());
                        msg.setMsg(ErrorEnum.NO_ERROR.getName());
                        adlist.setMsg(msg);
                        return adlist;
                    }else if("ALL".equalsIgnoreCase(adlistEntity.getId_list())){
                        pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Ad.list_ad_by_all_campaigns);
                    }else{
                        pstmt = con.prepareStatement(InQueryPrepareStmnt.createInQueryPrepareStatement(
                            com.kritter.kritterui.api.db_query_def.Ad.list_ad_by_campaigns, "<id>", adlistEntity.getId_list(), 
                            ",", false));
                    }
                    pstmt.setInt(1, adlistEntity.getPage_no() * adlistEntity.getPage_size());
                    pstmt.setInt(2, adlistEntity.getPage_size());
                    break;
                case list_ad_by_status:
                    pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Ad.list_ad_by_status);
                    pstmt.setInt(1, adlistEntity.getStatudIdEnum().getCode());
                    pstmt.setInt(2, adlistEntity.getPage_no()*adlistEntity.getPage_size());
                    pstmt.setInt(3, adlistEntity.getPage_size());
                    break;
                case list_ad_by_status_of_non_expired_campaign:
                    pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Ad.list_ad_by_status_of_non_expired_campaign);
                    pstmt.setInt(1, adlistEntity.getStatudIdEnum().getCode());
                    pstmt.setInt(2, adlistEntity.getPage_no()*adlistEntity.getPage_size());
                    pstmt.setInt(3, adlistEntity.getPage_size());
                    break;
                default:
                    break;
            }
            ResultSet rset = pstmt.executeQuery();
            AdList adlist = new AdList();
            List<Ad> ads = new LinkedList<Ad>();
            while(rset.next()){
                Ad ad = new Ad();
                populate(ad, rset, tp_name);
                ads.add(ad);
            }
            adlist.setAdlist(ads);
            Message msg = new Message();
            if(ads.size()>0){
                msg.setError_code(ErrorEnum.NO_ERROR.getId());
                msg.setMsg(ErrorEnum.NO_ERROR.getName());
            }else{
                msg.setError_code(ErrorEnum.AD_NOT_FOUND.getId());
                msg.setMsg(ErrorEnum.AD_NOT_FOUND.getName());
            }
            adlist.setMsg(msg);
            return adlist;
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            AdList adlist = new AdList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.SQL_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.SQL_EXCEPTION.getName());
            adlist.setMsg(msg);
            return adlist;
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
    
    public static JsonNode update_ad(Connection con, JsonNode jsonNode){
        if(jsonNode == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.AD_NULL.getId());
            msg.setMsg(ErrorEnum.AD_NULL.getName());
            return msg.toJson();
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            Ad ad = objectMapper.treeToValue(jsonNode, Ad.class);
            return update_ad(con, ad, true).toJson();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            return msg.toJson();
        }
    }
    
    public static Message update_ad(Connection con, Ad ad, boolean createTransaction ){
        if(con == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            return msg;
        }
        if(ad == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.AD_NULL.getId());
            msg.setMsg(ErrorEnum.AD_NULL.getName());
            return msg;
        }
        PreparedStatement pstmt = null;
        boolean autoCommitFlag = false;
        try{
            if(createTransaction){
                autoCommitFlag = con.getAutoCommit();
                con.setAutoCommit(false);
            }
            pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Ad.update_ad);
            pstmt.setString(1, ad.getName());
            pstmt.setInt(2, ad.getCreative_id());
            pstmt.setString(3, ad.getCreative_guid());
            pstmt.setString(4, ad.getLanding_url());
            pstmt.setString(5, ad.getTargeting_guid());
            pstmt.setString(6, "{\"TIER1\":"+ad.getCategories_tier_1_list()+",\"TIER2\":"+ad.getCategories_tier_2_list()+"}");
            pstmt.setString(7, ad.getHygiene_list());
            pstmt.setInt(8, ad.getStatus_id().getCode());
            pstmt.setInt(9, ad.getMarketplace_id().getCode());
            pstmt.setDouble(10, ad.getInternal_max_bid());
            pstmt.setDouble(11, ad.getAdvertiser_bid());
            pstmt.setString(12, ad.getAllocation_ids());
            pstmt.setTimestamp(13, new Timestamp(new Date().getTime()));
            pstmt.setInt(14, ad.getModified_by());
            pstmt.setString(15, ad.getComment());
            pstmt.setInt(16, ad.getTracking_partner().getCode());
            pstmt.setDouble(17, ad.getCpa_goal());
            pstmt.setString(18, generateAdvDomain(ad));
            pstmt.setBoolean(19, ad.isIs_frequency_capped());
            pstmt.setInt(20, ad.getFrequency_cap());
            pstmt.setInt(21, ad.getTime_window());
            pstmt.setInt(22, ad.getBidtype());
            pstmt.setString(23, generateExternalTracker(ad));
            pstmt.setString(24, generateExt(ad));
            pstmt.setString(25, generateFreqCap(ad));
            pstmt.setInt(26, ad.getId());
            int returnCode = pstmt.executeUpdate();
            if(createTransaction){
                con.commit();
            }
            if(returnCode == 0){
                Message msg = new Message();
                msg.setError_code(ErrorEnum.AD_NOT_UPDATED.getId());
                msg.setMsg(ErrorEnum.AD_NOT_UPDATED.getName());
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
    

    
    public static JsonNode activate_ad(Connection con, JsonNode jsonNode){
        if(jsonNode == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.AD_NULL.getId());
            msg.setMsg(ErrorEnum.AD_NULL.getName());
            return msg.toJson();
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            Ad ad = objectMapper.treeToValue(jsonNode, Ad.class);
            return activate_ad(con, ad, true).toJson();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            return msg.toJson();
        }
    }
    
    public static Message activate_ad(Connection con, Ad ad , boolean createTransaction){
        if(con == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            return msg;
        }
        if(ad == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.AD_NULL.getId());
            msg.setMsg(ErrorEnum.AD_NULL.getName());
            return msg;
        }
        PreparedStatement pstmt = null;
        boolean autoCommitFlag = false;
        try{
            if(createTransaction){
                autoCommitFlag = con.getAutoCommit();
                con.setAutoCommit(false);
            }
            pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Ad.activate_ad);
            pstmt.setTimestamp(1,  new Timestamp(new Date().getTime()));
            pstmt.setInt(2, ad.getId());
            int returnCode = pstmt.executeUpdate();
            if(createTransaction){
                con.commit();
            }
            if(returnCode == 0){
                Message msg = new Message();
                msg.setError_code(ErrorEnum.AD_NOT_UPDATED.getId());
                msg.setMsg(ErrorEnum.AD_NOT_UPDATED.getName());
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

    public static JsonNode approve_ad(Connection con, JsonNode jsonNode){
        if(jsonNode == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.AD_NULL.getId());
            msg.setMsg(ErrorEnum.AD_NULL.getName());
            return msg.toJson();
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            Ad ad = objectMapper.treeToValue(jsonNode, Ad.class);
            return approve_ad(con, ad, true).toJson();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            return msg.toJson();
        }
    }
    
    public static Message approve_ad(Connection con, Ad ad , boolean createTransaction){
        if(con == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            return msg;
        }
        if(ad == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.AD_NULL.getId());
            msg.setMsg(ErrorEnum.AD_NULL.getName());
            return msg;
        }
        PreparedStatement pstmt = null;
        boolean autoCommitFlag = false;
        try{
            if(createTransaction){
                autoCommitFlag = con.getAutoCommit();
                con.setAutoCommit(false);
            }
            pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Ad.approve_ad);
            pstmt.setTimestamp(1, new Timestamp(new Date().getTime()));
            pstmt.setInt(2, ad.getId());
            int returnCode = pstmt.executeUpdate();
            if(createTransaction){
                con.commit();
            }
            if(returnCode == 0){
                Message msg = new Message();
                msg.setError_code(ErrorEnum.AD_NOT_UPDATED.getId());
                msg.setMsg(ErrorEnum.AD_NOT_UPDATED.getName());
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

    public static JsonNode reject_ad(Connection con, JsonNode jsonNode){
        if(jsonNode == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.AD_NULL.getId());
            msg.setMsg(ErrorEnum.AD_NULL.getName());
            return msg.toJson();
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            Ad ad = objectMapper.treeToValue(jsonNode, Ad.class);
            return reject_ad(con, ad, true).toJson();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            return msg.toJson();
        }
    }
    
    public static Message reject_ad(Connection con, Ad ad , boolean createTransaction){
        if(con == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            return msg;
        }
        if(ad == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.AD_NULL.getId());
            msg.setMsg(ErrorEnum.AD_NULL.getName());
            return msg;
        }
        PreparedStatement pstmt = null;
        boolean autoCommitFlag = false;
        try{
            if(createTransaction){
                autoCommitFlag = con.getAutoCommit();
                con.setAutoCommit(false);
            }
            pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Ad.reject_ad);
            pstmt.setString(1, ad.getComment());
            pstmt.setTimestamp(2, new Timestamp(new Date().getTime()));
            pstmt.setInt(3, ad.getId());
            
            int returnCode = pstmt.executeUpdate();
            if(createTransaction){
                con.commit();
            }
            if(returnCode == 0){
                Message msg = new Message();
                msg.setError_code(ErrorEnum.AD_NOT_UPDATED.getId());
                msg.setMsg(ErrorEnum.AD_NOT_UPDATED.getName());
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
    public static JsonNode pause_ad(Connection con, JsonNode jsonNode){
        if(jsonNode == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.AD_NULL.getId());
            msg.setMsg(ErrorEnum.AD_NULL.getName());
            return msg.toJson();
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            Ad ad = objectMapper.treeToValue(jsonNode, Ad.class);
            return pause_ad(con, ad, true).toJson();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            return msg.toJson();
        }
    }
    
    public static Message pause_ad(Connection con, Ad ad , boolean createTransaction){
        if(con == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            return msg;
        }
        if(ad == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.AD_NULL.getId());
            msg.setMsg(ErrorEnum.AD_NULL.getName());
            return msg;
        }
        PreparedStatement pstmt = null;
        boolean autoCommitFlag = false;
        try{
            if(createTransaction){
                autoCommitFlag = con.getAutoCommit();
                con.setAutoCommit(false);
            }
            pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Ad.pause_ad);
            pstmt.setString(1, ad.getComment());
            pstmt.setTimestamp(2, new Timestamp(new Date().getTime()));
            pstmt.setInt(3, ad.getId());
            
            int returnCode = pstmt.executeUpdate();
            if(createTransaction){
                con.commit();
            }
            if(returnCode == 0){
                Message msg = new Message();
                msg.setError_code(ErrorEnum.AD_NOT_UPDATED.getId());
                msg.setMsg(ErrorEnum.AD_NOT_UPDATED.getName());
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
    
    public static JsonNode change_status_ad(Connection con, JsonNode jsonNode){
        if(jsonNode == null){
            AdList adlist = new AdList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.AD_LIST_ENTITY_NULL.getId());
            msg.setMsg(ErrorEnum.AD_LIST_ENTITY_NULL.getName());
            adlist.setMsg(msg);
            return adlist.toJson();
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            AdListEntity adlistEntity = objectMapper.treeToValue(jsonNode, AdListEntity.class);
            return change_status_ad(con, adlistEntity, true).toJson();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            AdList adlist = new AdList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            adlist.setMsg(msg);
            return adlist.toJson();
        }
    }
    
    public static Message change_status_ad(Connection con, AdListEntity adlistEntity, boolean createTransaction){
        if(con == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            return msg;
        }
        if(adlistEntity == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.AD_LIST_ENTITY_NULL.getId());
            msg.setMsg(ErrorEnum.AD_LIST_ENTITY_NULL.getName());
            return msg;
        }
        PreparedStatement pstmt = null;
        boolean autoCommitFlag = false;
        try{
            if(createTransaction){
                autoCommitFlag = con.getAutoCommit();
                con.setAutoCommit(false);
            }
            boolean success_when_zero_rows_affected=false;
            switch (adlistEntity.getAdenum()){
                case approve_ad_again_on_tp_update:
                    pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Ad.approve_ad_again_on_tp_update);
                    pstmt.setTimestamp(1, new Timestamp(new Date().getTime()));
                    pstmt.setString(2, adlistEntity.getId_list());
                    break;
                case approve_ad_again_on_creative_update:
                    success_when_zero_rows_affected=true;
                    pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Ad.approve_ad_again_on_creative_update);
                    pstmt.setTimestamp(1, new Timestamp(new Date().getTime()));
                    pstmt.setInt(2, adlistEntity.getId());
                    break;
                case activate_ad_by_ids:
                    String str = adlistEntity.getId_list(); 
                    if(str == null || "ALL".equalsIgnoreCase(str) || "".equalsIgnoreCase(str)){
                        Message msg = new Message();
                        msg.setError_code(ErrorEnum.AD_NOT_UPDATED.getId());
                        msg.setMsg(ErrorEnum.AD_NOT_UPDATED.getName());
                        return msg;
                    }else{
                        pstmt = con.prepareStatement(InQueryPrepareStmnt.createInQueryPrepareStatement(
                            com.kritter.kritterui.api.db_query_def.Ad.activate_ad_by_ids, "<id>", adlistEntity.getId_list(), 
                            ",", false));
                    }
                    pstmt.setTimestamp(1, new Timestamp(new Date().getTime()));
                    break;
                case pause_ad_by_ids:
                    String str1 = adlistEntity.getId_list(); 
                    if(str1 == null || "ALL".equalsIgnoreCase(str1) || "".equalsIgnoreCase(str1)){
                        Message msg = new Message();
                        msg.setError_code(ErrorEnum.AD_NOT_UPDATED.getId());
                        msg.setMsg(ErrorEnum.AD_NOT_UPDATED.getName());
                        return msg;
                    }else{
                        pstmt = con.prepareStatement(InQueryPrepareStmnt.createInQueryPrepareStatement(
                            com.kritter.kritterui.api.db_query_def.Ad.pause_ad_by_ids, "<id>", adlistEntity.getId_list(), 
                            ",", false));
                    }
                    pstmt.setTimestamp(1, new Timestamp(new Date().getTime()));
                    break;
                case approve_multiple_ads:
                    String str2 = adlistEntity.getId_list(); 
                    if(str2 == null || "ALL".equalsIgnoreCase(str2) || "".equalsIgnoreCase(str2)){
                        Message msg = new Message();
                        msg.setError_code(ErrorEnum.AD_NOT_UPDATED.getId());
                        msg.setMsg(ErrorEnum.AD_NOT_UPDATED.getName());
                        return msg;
                    }else{
                        pstmt = con.prepareStatement(InQueryPrepareStmnt.createInQueryPrepareStatement(
                            com.kritter.kritterui.api.db_query_def.Ad.approve_multiple_ads, "<id>", adlistEntity.getId_list(), 
                            ",", false));
                    }
                    pstmt.setTimestamp(1, new Timestamp(new Date().getTime()));
                    break;
                case reject_multiple_ads:
                    String str3 = adlistEntity.getId_list(); 
                    if(str3 == null || "ALL".equalsIgnoreCase(str3) || "".equalsIgnoreCase(str3)){
                        Message msg = new Message();
                        msg.setError_code(ErrorEnum.AD_NOT_UPDATED.getId());
                        msg.setMsg(ErrorEnum.AD_NOT_UPDATED.getName());
                        return msg;
                    }else{
                        pstmt = con.prepareStatement(InQueryPrepareStmnt.createInQueryPrepareStatement(
                            com.kritter.kritterui.api.db_query_def.Ad.reject_multiple_ads, "<id>", adlistEntity.getId_list(), 
                            ",", false));
                    }
                    pstmt.setTimestamp(1, new Timestamp(new Date().getTime()));
                    break;
                default:
                    Message msg = new Message();
                    msg.setError_code(ErrorEnum.AD_NOT_UPDATED.getId());
                    msg.setMsg(ErrorEnum.AD_NOT_UPDATED.getName());
                    return msg;
            }
            int returnCode = pstmt.executeUpdate();
            if(createTransaction){
                con.commit();
            }
            if(returnCode == 0 && !success_when_zero_rows_affected){
                Message msg = new Message();
                msg.setError_code(ErrorEnum.AD_NOT_UPDATED.getId());
                msg.setMsg(ErrorEnum.AD_NOT_UPDATED.getName());
                return msg;
            }
            Message msg = new Message();
            msg.setError_code(ErrorEnum.NO_ERROR.getId());
            msg.setMsg(ErrorEnum.NO_ERROR.getName());
            return msg;
        }catch(Exception e){
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

    public static Message insertUpdateAdImpressionsBudget(Connection connection,
                                                          String adGuid,
                                                          int impressionCap,
                                                          int timeWindowHours,
                                                          int modifiedBy)
    {
        PreparedStatement pstmt = null;

        try
        {
            pstmt = connection.prepareStatement(com.kritter.kritterui.api.db_query_def.Ad.get_ad_impression_budget);
            pstmt.setString(1,adGuid);

            ResultSet rs = pstmt.executeQuery();
            int count = 0;
            while (rs.next())
            {
                count = rs.getInt("count");
                break;
            }

            if(count > 0)
            {
                pstmt = connection.prepareStatement(com.kritter.kritterui.api.db_query_def.Ad
                                                        .update_ad_impression_budget);
                pstmt.setInt(1,impressionCap);
                pstmt.setInt(2,timeWindowHours);
                pstmt.setString(3,adGuid);
            }
            /*****Insert if not already present.**********/
            else
            {
                pstmt = connection.prepareStatement(com.kritter.kritterui.api.db_query_def.Ad
                                                        .insert_ad_impression_budget);
                pstmt.setString(1,adGuid);
                pstmt.setInt(2,impressionCap);
                pstmt.setInt(3,timeWindowHours);
                pstmt.setInt(4,modifiedBy);
                pstmt.setTimestamp(5,new Timestamp(System.currentTimeMillis()));
                pstmt.setTimestamp(6,new Timestamp(System.currentTimeMillis()));
            }

            int result = pstmt.executeUpdate();

            Message msg = new Message();

            if(result <= 0)
            {
                msg.setError_code(ErrorEnum.SQL_EXCEPTION.getId());
                msg.setMsg(ErrorEnum.SQL_EXCEPTION.getName());
            }
            else
            {
                msg.setMsg(ErrorEnum.NO_ERROR.getName());
                msg.setError_code(ErrorEnum.NO_ERROR.getId());
            }

            return msg;
        }
        catch (Exception e)
        {
            Message msg = new Message();
            msg.setError_code(ErrorEnum.SQL_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.SQL_EXCEPTION.getName());
            return msg;
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
    }
}