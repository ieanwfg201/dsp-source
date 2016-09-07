package com.kritter.kritterui.api.campaign;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
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

import com.kritter.api.entity.campaign.Campaign;
import com.kritter.api.entity.campaign.CampaignList;
import com.kritter.api.entity.campaign.CampaignListEntity;
import com.kritter.api.entity.response.msg.Message;
import com.kritter.constants.Budget;
import com.kritter.constants.FreqDuration;
import com.kritter.constants.FreqEventType;
import com.kritter.constants.StatusIdEnum;
import com.kritter.constants.error.ErrorEnum;
import com.kritter.entity.freqcap_entity.FreqCap;
import com.kritter.entity.freqcap_entity.FreqDef;
import com.kritter.kritterui.api.utils.InQueryPrepareStmnt;
import com.kritter.utils.uuid.mac.SingletonUUIDGenerator;

public class CampaignCrud {
    
    private static final Logger LOG = LoggerFactory.getLogger(CampaignCrud.class);
    
    public static Campaign populate( ResultSet rset, boolean showExpiry, boolean budgetStatus, boolean accpunt_id) throws SQLException{
        Campaign campaign = null;
        if(rset != null){
            Timestamp ts =  rset.getTimestamp("end_date");
            java.util.Date now = new Date();
            if(!showExpiry){
                if(now.getTime() >= ts.getTime()){
                    return null;
                }
            }
            campaign = new Campaign();
            if(now.getTime() < ts.getTime()){
                campaign.setStatus_id(rset.getInt("status_id"));
            }else{
                campaign.setStatus_id(StatusIdEnum.Expired.getCode());
            }
            campaign.setAccount_guid(rset.getString("account_guid"));
            campaign.setEnd_date(ts.getTime());
            campaign.setGuid(rset.getString("guid"));
            campaign.setId(rset.getInt("id"));
            campaign.setModified_by(rset.getInt("modified_by"));
            campaign.setName(rset.getString("name"));
            campaign.setStart_date(rset.getTimestamp("start_date").getTime());
            campaign.setCreated_on(rset.getTimestamp("created_on").getTime());
            if(budgetStatus){
                double internal_daily_budget = rset.getDouble("internal_daily_budget");
                double internal_daily_burn = rset.getDouble("internal_daily_burn") ;
                double internal_daily_remaining = internal_daily_budget - internal_daily_burn;
                double internal_total_budget = rset.getDouble("internal_total_budget") ;
                double internal_total_burn = rset.getDouble("internal_total_burn") ;
                double internal_total_remaining = internal_total_budget - internal_total_burn;
                double internal_balance = rset.getDouble("internal_balance") ;
                
                double adv_daily_budget = rset.getDouble("adv_daily_budget");
                double adv_daily_burn = rset.getDouble("adv_daily_burn") ;
                double adv_daily_remaining = adv_daily_budget - adv_daily_burn;
                double adv_total_budget = rset.getDouble("adv_total_budget") ;
                double adv_total_burn = rset.getDouble("adv_total_burn") ;
                double adv_total_remaining = adv_total_budget - adv_total_burn;
                double adv_balance = rset.getDouble("adv_balance") ;
                
                
                if(campaign.getStatus_id() == StatusIdEnum.Active.getCode() ){
                    if(internal_daily_remaining <= Budget.min_budget){
                        campaign.setStatus_id(StatusIdEnum.CAMP_DAILY_BUDGET_OVER.getCode());
                    }else if(adv_daily_remaining <= Budget.min_budget){
                        campaign.setStatus_id(StatusIdEnum.CAMP_DAILY_BUDGET_OVER.getCode());
                    }else if(internal_total_remaining <= Budget.min_budget){
                        campaign.setStatus_id(StatusIdEnum.CAMP_TOTAL_BUDGET_OVER.getCode());
                    }else if(adv_total_remaining <= Budget.min_budget){
                        campaign.setStatus_id(StatusIdEnum.CAMP_TOTAL_BUDGET_OVER.getCode());
                    }else if(internal_balance <= Budget.min_budget){
                        campaign.setStatus_id(StatusIdEnum.ACCOUNT_BALANCE_OVER.getCode());
                    }else if(adv_balance <= Budget.min_budget){
                        campaign.setStatus_id(StatusIdEnum.ACCOUNT_BALANCE_OVER.getCode());
                    }
                }
            }
            if(accpunt_id){
                campaign.setAccount_id(rset.getInt("account_id"));
            }
            
        }
        campaign.setIs_frequency_capped(rset.getBoolean("is_frequency_capped"));
        populateFreqCap(campaign, rset.getString("freqcap_json"));
        return campaign;
    }
    private static void populateFreqCap(Campaign campaign,String freqcap){
    	if(freqcap == null || campaign == null){
    		return;
    	}
    	if(!campaign.isIs_frequency_capped()){
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
    				campaign.setClick_freq_cap(true);
    				for(FreqDef f:clickDef){
    					switch(f.getDuration()){
    					case LIFE:
    						campaign.setClick_freq_cap_type(FreqDuration.LIFE.getCode());
    						campaign.setClick_freq_cap_count(f.getCount());
    						campaign.setClick_freq_time_window(-1);
    						break;
    					case BYHOUR:
    						campaign.setClick_freq_cap_type(FreqDuration.BYHOUR.getCode());
    						campaign.setClick_freq_cap_count(f.getCount());
    						campaign.setClick_freq_time_window(f.getHour());
    						break;
    					case BYDAY:
    						campaign.setClick_freq_cap_type(FreqDuration.BYDAY.getCode());
    						campaign.setClick_freq_cap_count(f.getCount());
    						campaign.setClick_freq_time_window(24);
    						break;
    					default:
    						break;
    					}
    				}
    			}
				Set<FreqDef> impDef = freqCapObj.getFDef().get(FreqEventType.IMP);
				if(impDef != null){
					campaign.setImp_freq_cap(true);
					for(FreqDef f:impDef){
						switch(f.getDuration()){
						case LIFE:
							campaign.setImp_freq_cap_type(FreqDuration.LIFE.getCode());
							campaign.setImp_freq_cap_count(f.getCount());
							campaign.setImp_freq_time_window(-1);
							break;
						case BYHOUR:
							campaign.setImp_freq_cap_type(FreqDuration.BYHOUR.getCode());
							campaign.setImp_freq_cap_count(f.getCount());
							campaign.setImp_freq_time_window(f.getHour());
							break;
						case BYDAY:
							campaign.setImp_freq_cap_type(FreqDuration.BYDAY.getCode());
							campaign.setImp_freq_cap_count(f.getCount());
							campaign.setImp_freq_time_window(24);
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
    private static String generateFreqCap(Campaign campaign){
    	if(campaign==null || !campaign.isIs_frequency_capped()){
    		return "{}";
    	}
    	FreqCap f = new FreqCap();
    	Map<FreqEventType, Set<FreqDef>> map = new HashMap<FreqEventType, Set<FreqDef>>();
    	if(campaign.isClick_freq_cap()){
    		Set<FreqDef> set = new HashSet<FreqDef>();
    		FreqDef fdef= new FreqDef();
    		
    		FreqDuration fDur = FreqDuration.getEnum(campaign.getClick_freq_cap_type());
    		if(fDur==null){
    			fDur=FreqDuration.BYHOUR;
    		}
    		fdef.setDuration(fDur);
    		int count = campaign.getClick_freq_cap_count();
    		fdef.setCount(count);
    		if(fDur != FreqDuration.LIFE){
    			fdef.setHour(campaign.getClick_freq_time_window());
    		}
    		if(fDur == FreqDuration.BYDAY){
    			fdef.setHour(24);
    		}
    		set.add(fdef);
    		map.put(FreqEventType.CLK, set);
    	}
    	if(campaign.isImp_freq_cap()){
    		Set<FreqDef> set = new HashSet<FreqDef>();
    		FreqDef fdef= new FreqDef();
    		FreqDuration fDur = FreqDuration.getEnum(campaign.getImp_freq_cap_type());
    		if(fDur==null){
    			fDur=FreqDuration.BYHOUR;
    		}
    		fdef.setDuration(fDur);
    		int count = campaign.getImp_freq_cap_count();
    		fdef.setCount(count);
    		if(fDur != FreqDuration.LIFE){
    			fdef.setHour(campaign.getImp_freq_time_window());
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

    
    public static JsonNode insert_campaign(Connection con, JsonNode jsonNode){
        if(jsonNode == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.CAMPAIGN_NULL.getId());
            msg.setMsg(ErrorEnum.CAMPAIGN_NULL.getName());
            return msg.toJson();
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            Campaign campaign = objectMapper.treeToValue(jsonNode, Campaign.class);
            return insert_campaign(con, campaign, true).toJson();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            return msg.toJson();
        }
    }

    public static Message insert_campaign(Connection con, Campaign campaign, boolean createTransaction){
        return insert_campaign(con,campaign,createTransaction,false);
    }

    public static Message insert_campaign(Connection con, Campaign campaign, boolean createTransaction,boolean useExternalGuid){
        if(con == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            return msg;
        }
        if(campaign == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.CAMPAIGN_NULL.getId());
            msg.setMsg(ErrorEnum.CAMPAIGN_NULL.getName());
            return msg;
        }
        PreparedStatement pstmt = null;
        boolean autoCommitFlag = false;
        try{
            if(createTransaction){
                autoCommitFlag = con.getAutoCommit();
                con.setAutoCommit(false);
            }
            pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Campaign.insert_campaign,PreparedStatement.RETURN_GENERATED_KEYS);
            if(useExternalGuid)
                pstmt.setString(1,campaign.getGuid());
            else
                pstmt.setString(1, SingletonUUIDGenerator.getSingletonUUIDGenerator().generateUniversallyUniqueIdentifier().toString());
            pstmt.setString(2, campaign.getName());
            pstmt.setString(3, campaign.getAccount_guid());
            pstmt.setInt(4, campaign.getStatus_id());
            pstmt.setTimestamp(5, new Timestamp(campaign.getStart_date()));
            pstmt.setTimestamp(6, new Timestamp(campaign.getEnd_date()));
            pstmt.setTimestamp(7, new Timestamp((new Date()).getTime()));
            pstmt.setInt(8,campaign.getModified_by());
            pstmt.setTimestamp(9, new Timestamp((new Date()).getTime()));
            pstmt.setBoolean(10, campaign.isIs_frequency_capped());
            pstmt.setString(11, generateFreqCap(campaign));
            int returnCode = pstmt.executeUpdate();
            if(createTransaction){
                con.commit();
            }
            if(returnCode == 0){
                Message msg = new Message();
                msg.setError_code(ErrorEnum.CAMPAIGN_NOT_INSERTED.getId());
                msg.setMsg(ErrorEnum.CAMPAIGN_NOT_INSERTED.getName());
                return msg;
            }
            ResultSet keyResultSet = pstmt.getGeneratedKeys();
            int campaign_id = -1;
            if (keyResultSet.next()) {
                campaign_id = keyResultSet.getInt(1);
            }
            campaign.setId(campaign_id);
            Message msg = new Message();
            msg.setError_code(ErrorEnum.NO_ERROR.getId());
            msg.setMsg(ErrorEnum.NO_ERROR.getName());
            msg.setId(campaign_id+"");
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
    
    
    public static JsonNode update_campaign(Connection con, JsonNode jsonNode){
        if(jsonNode == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.CAMPAIGN_NULL.getId());
            msg.setMsg(ErrorEnum.CAMPAIGN_NULL.getName());
            return msg.toJson();
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            Campaign campaign = objectMapper.treeToValue(jsonNode, Campaign.class);
            return update_campaign(con, campaign, true).toJson();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            return msg.toJson();
        }
    }
    public static Message update_campaign(Connection con, Campaign campaign, boolean createTransaction){
        if(con == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            return msg;
        }
        if(campaign == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.CAMPAIGN_NULL.getId());
            msg.setMsg(ErrorEnum.CAMPAIGN_NULL.getName());
            return msg;
        }
        PreparedStatement pstmt = null;
        boolean autoCommitFlag = false;
        try{
            java.util.Date now = new Date();
            if(createTransaction){
                autoCommitFlag = con.getAutoCommit();
                con.setAutoCommit(false);
            }
            if(now.getTime() < campaign.getEnd_date() && campaign.getStatus_id() == StatusIdEnum.Expired.getCode()){
                campaign.setStatus_id(StatusIdEnum.Paused.getCode());
            }
            pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Campaign.update_campaign);
            pstmt.setString(1, campaign.getName());
            pstmt.setInt(2, campaign.getStatus_id());
            pstmt.setTimestamp(3, new Timestamp(campaign.getStart_date()));
            pstmt.setTimestamp(4, new Timestamp(campaign.getEnd_date()));
            pstmt.setInt(5,campaign.getModified_by());
            pstmt.setTimestamp(6, new Timestamp(now.getTime()));
            pstmt.setBoolean(7, campaign.isIs_frequency_capped());
            pstmt.setString(8, generateFreqCap(campaign));
            pstmt.setInt(9, campaign.getId());
            pstmt.setString(10, campaign.getAccount_guid());
            
            int returnCode = pstmt.executeUpdate();
            if(createTransaction){
                con.commit();
            }
            if(returnCode == 0){
                Message msg = new Message();
                msg.setError_code(ErrorEnum.CAMPAIGN_NOT_UPDATED.getId());
                msg.setMsg(ErrorEnum.CAMPAIGN_NOT_UPDATED.getName());
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
    
    public static JsonNode list_campaign(Connection con, JsonNode jsonNode){
        if(jsonNode == null){
            CampaignList campaignlist = new CampaignList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.CAMPAIGNLIST_ENTITY_NULL.getId());
            msg.setMsg(ErrorEnum.CAMPAIGNLIST_ENTITY_NULL.getName());
            campaignlist.setMsg(msg);
            return campaignlist.toJson();
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            CampaignListEntity campaignlistEntity = objectMapper.treeToValue(jsonNode, CampaignListEntity.class);
            return list_campaign(con, campaignlistEntity).toJson();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            CampaignList campaignlist = new CampaignList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            campaignlist.setMsg(msg);
            return campaignlist.toJson();
        }
    }

    public static Campaign get_campaign(Connection con, String campaignGuid)
    {
        if(con == null || null == campaignGuid)
            return null;

        PreparedStatement pstmt = null;
        try
        {
            pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Campaign.get_campaign_by_guid);
            pstmt.setString(1, campaignGuid);

            ResultSet rset = pstmt.executeQuery();

            while (rset.next())
            {
                return populate(rset, true, false, false);
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

        return null;
    }

    public static Campaign get_campaign(Connection con, int campaignId)
    {
        if(con == null || campaignId <= 0)
            return null;

        PreparedStatement pstmt = null;
        try
        {
            pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Campaign.get_campaign_by_id);
            pstmt.setInt(1, campaignId);

            ResultSet rset = pstmt.executeQuery();

            while (rset.next())
            {
                return populate(rset, true, false, false);
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

        return null;
    }

    public static CampaignList list_campaign(Connection con, CampaignListEntity campaignlistEntity){
        if(con == null){
            CampaignList campaignlist = new CampaignList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            campaignlist.setMsg(msg);
            return campaignlist;
        }
        if(campaignlistEntity == null){
            CampaignList campaignlist = new CampaignList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.CAMPAIGNLIST_ENTITY_NULL.getId());
            msg.setMsg(ErrorEnum.CAMPAIGNLIST_ENTITY_NULL.getName());
            campaignlist.setMsg(msg);
            return campaignlist;
        }
        PreparedStatement pstmt = null;
        boolean showExpiry=false;
        boolean budgetStatus=false;
        boolean account_id=false;
        try{
            switch (campaignlistEntity.getCampaignQueryEnum()){
                case get_campaign_of_account:
                    pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Campaign.get_campaign_of_account);
                    pstmt.setInt(1, campaignlistEntity.getCampaign_id());
                    pstmt.setString(2, campaignlistEntity.getAccount_guid());
                    showExpiry=true;
                    budgetStatus=false;
                    break;
                case get_campaign_by_id:
                    pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Campaign.get_campaign_by_id);
                    pstmt.setInt(1, campaignlistEntity.getCampaign_id());
                    showExpiry=true;
                    budgetStatus=false;
                    break;
                case list_campaign_of_account:
                    pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Campaign.list_campaign_of_account);
                    pstmt.setString(1, campaignlistEntity.getAccount_guid());
                    pstmt.setInt(2, campaignlistEntity.getPage_no()*campaignlistEntity.getPage_size());
                    pstmt.setInt(3, campaignlistEntity.getPage_size());
                    showExpiry=true;
                    budgetStatus=false;
                    break;
                case list_campaign_of_accounts:
                    if("ALL".equalsIgnoreCase(campaignlistEntity.getId_list())){
                        pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Campaign.list_campaign_of_all_accounts);
                    }else{
                        pstmt = con.prepareStatement(InQueryPrepareStmnt.createInQueryPrepareStatement(
                            com.kritter.kritterui.api.db_query_def.Campaign.list_campaign_of_accounts, "<id>", campaignlistEntity.getId_list(), 
                            ",", false));
                    }
                    pstmt.setInt(1, campaignlistEntity.getPage_no()*campaignlistEntity.getPage_size());
                    pstmt.setInt(2, campaignlistEntity.getPage_size());
                    showExpiry=true;
                    budgetStatus=false;
                    break;
                case list_campaign_by_account_ids:
                    if("ALL".equalsIgnoreCase(campaignlistEntity.getId_list())){
                        pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Campaign.list_campaign_of_all_accounts);
                    }else{
                        pstmt = con.prepareStatement(InQueryPrepareStmnt.createInQueryPrepareStatement(
                            com.kritter.kritterui.api.db_query_def.Campaign.list_campaign_by_account_ids, "<id>", campaignlistEntity.getId_list(), 
                            ",", false));
                    }
                    pstmt.setInt(1, campaignlistEntity.getPage_no()*campaignlistEntity.getPage_size());
                    pstmt.setInt(2, campaignlistEntity.getPage_size());
                    showExpiry=true;
                    budgetStatus=false;
                    break;
                case list_campaign_by_account_ids_with_account_id:
                    if("ALL".equalsIgnoreCase(campaignlistEntity.getId_list())){
                        pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Campaign.list_campaign_of_all_accounts);
                    }else{
                        pstmt = con.prepareStatement(InQueryPrepareStmnt.createInQueryPrepareStatement(
                            com.kritter.kritterui.api.db_query_def.Campaign.list_campaign_by_account_ids_with_account_id, "<id>", campaignlistEntity.getId_list(), 
                            ",", false));
                        account_id=true;
                    }
                    pstmt.setInt(1, campaignlistEntity.getPage_no()*campaignlistEntity.getPage_size());
                    pstmt.setInt(2, campaignlistEntity.getPage_size());
                    showExpiry=true;
                    budgetStatus=false;
                    break;
                case list_non_expired_campaign_of_account_by_status:
                    pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Campaign.list_non_expired_campaign_of_account_by_status);
                    pstmt.setString(1, campaignlistEntity.getAccount_guid());
                    pstmt.setInt(2, campaignlistEntity.getStatusIdEnum().getCode());
                    pstmt.setInt(3, campaignlistEntity.getPage_no()*campaignlistEntity.getPage_size());
                    pstmt.setInt(4, campaignlistEntity.getPage_size());
                    showExpiry=false;
                    budgetStatus=false;
                    break;
                case list_non_expired_campaign_by_status:
                    pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Campaign.list_non_expired_campaign_by_status);
                    pstmt.setInt(1, campaignlistEntity.getStatusIdEnum().getCode());
                    pstmt.setInt(2, campaignlistEntity.getPage_no()*campaignlistEntity.getPage_size());
                    pstmt.setInt(3, campaignlistEntity.getPage_size());
                    showExpiry=false;
                    budgetStatus=false;
                    break;
                case list_all_non_expired_campaign_of_account:
                    pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Campaign.list_all_non_expired_campaign_of_account);
                    pstmt.setString(1, campaignlistEntity.getAccount_guid());
                    pstmt.setInt(2, campaignlistEntity.getPage_no()*campaignlistEntity.getPage_size());
                    pstmt.setInt(3, campaignlistEntity.getPage_size());
                    showExpiry=false;
                    budgetStatus=true;
                    break;
                case list_all_expired_campaign_of_account:
                    pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Campaign.list_all_expired_campaign_of_account);
                    pstmt.setString(1, campaignlistEntity.getAccount_guid());
                    pstmt.setInt(2, campaignlistEntity.getPage_no()*campaignlistEntity.getPage_size());
                    pstmt.setInt(3, campaignlistEntity.getPage_size());
                    showExpiry=true;
                    budgetStatus=false;
                    break;
                default:
                    break;
            }
            ResultSet rset = pstmt.executeQuery();
            CampaignList campaignlist = new CampaignList();
            List<Campaign> campaigns = new LinkedList<Campaign>();
            while(rset.next()){
                Campaign campaign = populate(rset,showExpiry, budgetStatus,account_id);
                if(campaign != null){
                    campaigns.add(campaign);
                }
            }
            campaignlist.setCampaign_list(campaigns);
            Message msg = new Message();
            if(campaigns.size()>0){
                msg.setError_code(ErrorEnum.NO_ERROR.getId());
                msg.setMsg(ErrorEnum.NO_ERROR.getName());
            }else{
                msg.setError_code(ErrorEnum.CAMPAIGN_NOT_FOUND.getId());
                msg.setMsg(ErrorEnum.CAMPAIGN_NOT_FOUND.getName());
            }
            campaignlist.setMsg(msg);
            return campaignlist;
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            CampaignList campaignlist = new CampaignList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.SQL_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.SQL_EXCEPTION.getName());
            campaignlist.setMsg(msg);
            return campaignlist;
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
    
    public static JsonNode pause_campaign(Connection con, JsonNode jsonNode){
        if(jsonNode == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.CAMPAIGN_NULL.getId());
            msg.setMsg(ErrorEnum.CAMPAIGN_NULL.getName());
            return msg.toJson();
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            Campaign campaign = objectMapper.treeToValue(jsonNode, Campaign.class);
            return update_campaign(con, campaign, true).toJson();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            return msg.toJson();
        }
    }

    public static Message pause_campaign(Connection con, Campaign campaign, boolean createTransaction){
        if(campaign != null){
            campaign.setStatus_id(StatusIdEnum.Paused.getCode());
        }
        return update_campaign_status(con, campaign, createTransaction);
    }
    public static JsonNode activate_campaign(Connection con, JsonNode jsonNode){
        if(jsonNode == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.CAMPAIGN_NULL.getId());
            msg.setMsg(ErrorEnum.CAMPAIGN_NULL.getName());
            return msg.toJson();
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            Campaign campaign = objectMapper.treeToValue(jsonNode, Campaign.class);
            return update_campaign(con, campaign, true).toJson();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            return msg.toJson();
        }
    }

    public static Message activate_campaign(Connection con, Campaign campaign, boolean createTransaction){
        if(campaign != null){
            campaign.setStatus_id(StatusIdEnum.Active.getCode());
        }
        return update_campaign_status(con, campaign, createTransaction);
    }
    public static Message update_campaign_status(Connection con, Campaign campaign, boolean createTransaction){
        if(con == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            return msg;
        }
        if(campaign == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.CAMPAIGN_NULL.getId());
            msg.setMsg(ErrorEnum.CAMPAIGN_NULL.getName());
            return msg;
        }
        PreparedStatement pstmt = null;
        boolean autoCommitFlag = false;
        try{
            if(createTransaction){
                autoCommitFlag = con.getAutoCommit();
                con.setAutoCommit(false);
            }
            pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Campaign.update_campaign_status);
            pstmt.setInt(1, campaign.getStatus_id());
            pstmt.setInt(2,campaign.getModified_by());
            pstmt.setTimestamp(3, new Timestamp((new Date()).getTime()));
            pstmt.setInt(4, campaign.getId());
            pstmt.setString(5, campaign.getAccount_guid());
            
            int returnCode = pstmt.executeUpdate();
            if(createTransaction){
                con.commit();
            }
            if(returnCode == 0){
                Message msg = new Message();
                msg.setError_code(ErrorEnum.CAMPAIGN_NOT_UPDATED.getId());
                msg.setMsg(ErrorEnum.CAMPAIGN_NOT_UPDATED.getName());
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

    public static Message insertUpdateCampaignImpressionsBudget(Connection connection,
                                                                String campaignGuid,
                                                                int impressionCap,
                                                                int timeWindowHours,
                                                                int modifiedBy)
    {
        PreparedStatement pstmt = null;

        try
        {
            pstmt = connection.prepareStatement(com.kritter.kritterui.api.db_query_def.Campaign.get_campaign_impression_budget);
            pstmt.setString(1,campaignGuid);

            ResultSet rs = pstmt.executeQuery();
            int count = 0;
            while (rs.next())
            {
                count = rs.getInt("count");
                break;
            }

            if(count > 0)
            {
                pstmt = connection.prepareStatement(com.kritter.kritterui.api.db_query_def.Campaign
                        .update_campaign_impression_budget);
                pstmt.setInt(1,impressionCap);
                pstmt.setInt(2,timeWindowHours);
                pstmt.setString(3,campaignGuid);
            }
            /*****Insert if not already present.**********/
            else
            {
                pstmt = connection.prepareStatement(com.kritter.kritterui.api.db_query_def.Campaign
                        .insert_campaign_impression_budget);
                pstmt.setString(1,campaignGuid);
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
